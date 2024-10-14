package com.example.icons_uwb_app.serial

import android.app.Application
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbManager
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.icons_uwb_app.data.environments.Anchor
import com.example.icons_uwb_app.data.environments.getPoint
import com.example.icons_uwb_app.data.rainging.FileManager
import com.example.icons_uwb_app.data.rainging.RangingData
import com.example.icons_uwb_app.data.rainging.RangingDistance
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.annotations.SerializedName
import com.hoho.android.usbserial.BuildConfig
import com.hoho.android.usbserial.driver.CdcAcmSerialDriver
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.util.SerialInputOutputManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Date


class SerialViewModel(application: Application): AndroidViewModel(application), SerialInputOutputManager.Listener {
    private val fileManager = FileManager(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + "/SerialLog")
    //data to Update
    var anchorList = listOf(Anchor())
    var nowRangingData : MutableState<RangingData> = mutableStateOf(RangingData())
    //

    //for Moving Average Filter
    private val distanceBuffers = mutableMapOf<Int, MutableList<Float>>()
    private val windowSize = 10  // 이동 평균 필터의 윈도우 크기
    //


    /*setUp Parameter*/
    var baudRate = 115200
    /*setUp Parameter*/
     //JSON Parsing and handle for DWM3001CDK CLI build
    private fun blockHandler(blockString: String){
        viewModelScope.launch{ fileManager.writeTextFile(blockString + "\n") }
        try {
            val data = Gson().fromJson(blockString, Data::class.java)
            val distanceList:List<RangingDistance> = data.results.map{ result ->
                //Use not adjusted data(raw)
                /*RangingDistance(
                    id = if(result.status == "Err") -1 else result.addr.substring(3).toInt(),
                    distance = result.dCm.toFloat() / 100,
                    PDOA = if (result.lPDoADeg != 0.0f) result.lPDoADeg else null,
                    AOA = if (result.lAoADeg != 0.0f) result.lAoADeg else null
                )*/
                //

                //Insert Moving Average Filter
                val id = if (result.status == "Err") -1 else result.addr.substring(3).toInt()
                val rawDistance = result.dCm.toFloat() / 100

                // 이동 평균 필터 적용
                val filteredDistance = if (id != -1) {
                    val buffer = distanceBuffers.getOrPut(id) { mutableListOf() }

                    // 새로운 거리값 추가
                    buffer.add(rawDistance)

                    // 윈도우 크기 초과 시 가장 오래된 값 제거
                    if (buffer.size > windowSize) {
                        buffer.removeAt(0)
                    }

                    // 버퍼의 평균값 계산
                    buffer.average().toFloat()
                } else {
                    rawDistance
                }

                RangingDistance(
                    id = id,
                    distance = filteredDistance,
                    PDOA = if (result.lPDoADeg != 0.0f) result.lPDoADeg else null,
                    AOA = if (result.lAoADeg != 0.0f) result.lAoADeg else null
                )

            }
            val blockData = RangingData(
                blockNum = data.block,
                distanceList = distanceList,
                time = SimpleDateFormat("dd HH:mm:ss").format(Date())
            )


            val validInput = distanceList.filter{it.id != -1}
            Log.e("asdf","$validInput")
            blockData.coordinates =
                when(validInput.size){
                    4 -> calcMiddleBy4Side(validInput.map{it.distance}, validInput.map{validDistance -> anchorList.find{ it.id == validDistance.id }?.getPoint() ?: Point()})
                    3 -> calcBy3Side(validInput.map{it.distance}, validInput.map{validDistance -> anchorList.find{ it.id == validDistance.id }?.getPoint() ?: Point()})
                    2 -> calcByDoubleAnchor(validInput.map{it.distance}, validInput.map{validDistance -> anchorList.find{ it.id == validDistance.id }?.getPoint() ?: Point()}, anchorList.map{it.getPoint()})
                    else -> blockData.coordinates
                }
            nowRangingData.value = blockData
            viewModelScope.launch{
                fileManager.writeCSVFile(blockData)
            }
        }catch(e: JsonSyntaxException){
            Log.e("SerialViewModel", "signal error")
        }catch(e: NullPointerException){
            Log.e("SerialViewModel","nullPointer -")
        }
    }

    /*
    //parsing custom data format for DWM3001CDK, which is "{ID(int), BlockNum(int), Distance(float)}"
    // 새 포맷을 위한 데이터 클래스 선언
    data class ParsedData(val id: Int, val blockNum: Int, val distance: Float)
    var blockBuffer = RangingData()

    // ViewModel 내에 버퍼 변수를 선언
    private val dataBuffer = mutableMapOf<Int, MutableList<ParsedData>>()
    private var currentBlockNum: Int? = null

    // blockHandler 함수
    private fun blockHandler(blockString: String) {
        viewModelScope.launch { fileManager.writeTextFile(blockString + "\n") }

        // 새 포맷의 데이터 파싱
        val parsedData = parseData(blockString)

        if (parsedData != null) {
            val blockNum = parsedData.blockNum

            // 현재 블록 번호가 다른 블록 번호인지 확인
            if (currentBlockNum != null && blockNum != currentBlockNum) {
                // 현재 모인 데이터가 있으면 처리
                processCurrentBlockData(currentBlockNum!!)
            }

            currentBlockNum = blockNum
            val currentBlockData = dataBuffer.getOrPut(blockNum) { mutableListOf() }

            // 블록 데이터 모으기
            currentBlockData.add(parsedData)

            // 블록 번호에 대해 4개의 데이터가 모두 수집된 경우 즉시 처리
            if (currentBlockData.size == 4) {
                processCurrentBlockData(blockNum)
            }
        } else {
            Log.e("SerialViewModel", "Invalid data format")
        }
    }

    // 현재 블록 데이터를 처리하는 함수
    private fun processCurrentBlockData(blockNum: Int) {
        val currentBlockData = dataBuffer[blockNum]
        if (currentBlockData != null && currentBlockData.isNotEmpty()) {
            try {
                val distanceList: List<RangingDistance> = currentBlockData.map { data ->
                    RangingDistance(
                        id = data.id,
                        distance = data.distance,
                        PDOA = null, // AOA 제거
                        AOA = null
                    )
                }

                val blockData = RangingData(
                    blockNum = blockNum,
                    distanceList = distanceList,
                    time = SimpleDateFormat("dd HH:mm:ss").format(Date())
                )

                val validInput = distanceList.filter { it.id != -1 }
                Log.e("asdf",anchorList.toString())
                Log.e("asdf", "$validInput")

                blockData.coordinates =
                    when (validInput.size) {
                        4 -> calcMiddleBy4Side(validInput.map { it.distance }, validInput.map { validDistance -> anchorList.find { it.id == validDistance.id }?.getPoint() ?: Point() })
                        3 -> calcBy3Side(validInput.map { it.distance }, validInput.map { validDistance -> anchorList.find { it.id == validDistance.id }?.getPoint() ?: Point() })
                        2 -> calcByDoubleAnchor(validInput.map { it.distance }, validInput.map { validDistance -> anchorList.find { it.id == validDistance.id }?.getPoint() ?: Point() }, anchorList.map { it.getPoint() })
                        else -> Point()
                    }

                nowRangingData.value = blockData

                // CSV 파일에 데이터 기록
                viewModelScope.launch {
                    fileManager.writeCSVFile(blockData)
                }

                // 처리 후 해당 블록 데이터 초기화
                dataBuffer[blockNum] = mutableListOf()
            } catch (e: Exception) {
                Log.e("SerialViewModel", "Error processing block data: ${e.message}")
            }
        }
    }

    // 데이터 파싱 함수
    fun parseData(input: String): ParsedData? {
        // 공백을 제거하고, 데이터 양 끝의 '{', '}'를 제거
        val cleanedInput = input.trim().removeSurrounding("{", "}").trim()
        val parts = cleanedInput.split(",").map { it.trim() } // 각 부분의 공백도 제거

        return if (parts.size == 3) {
            try {
                val id = parts[0].toInt()
                val blockNum = parts[1].toInt()
                val distance = parts[2].toFloat()
                ParsedData(id, blockNum, distance)
            } catch (e: NumberFormatException) {
                Log.e("SerialViewModel", "Number format error: ${e.message}")
                null
            }
        } else {
            Log.e("SerialViewModel", "Data format error: expected 3 parts but got ${parts.size}")
            null
        }
    }

     */




















    fun updateAnchorList(newAnchorList: List<Anchor>){
        anchorList = newAnchorList
    }


    private var connectedUSBItem = MutableStateFlow<USBItem?>(null)
    private enum class USBPermission {UnKnown, Requested, Granted, Denied}
    private var usbPermission: USBPermission = USBPermission.UnKnown
    private val INTENT_ACTION_GRANT_USB: String = BuildConfig.LIBRARY_PACKAGE_NAME + ".GRANT_USB"
    private var usbIOManager: SerialInputOutputManager? = null
    private val usbPermissionReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (INTENT_ACTION_GRANT_USB == intent.action) {
                usbPermission = if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                    USBPermission.Granted
                } else {
                    USBPermission.Denied
                }
                connectSerialDevice(context)
            }
        }
    }

    fun connectSerialDevice(context: Context){
        var count = 0
        viewModelScope.launch(Dispatchers.IO) {
            val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
            while (connectedUSBItem.value == null) {
                Log.d("SerialViewModel", "try to Connect")
                for (device in usbManager.deviceList.values) {
                    val driver = CdcAcmSerialDriver(device)
                    if (driver.ports.size == 1) {
                        connectedUSBItem.update {
                            USBItem(device, driver.ports[0], driver)
                        }
                        Log.d("SerialViewModel", "device Connected")
                    }
                }
                delay(1000L) //by 1 sec
                count ++
                if(count>5) {
                    disConnectSerialDevice()
                    cancel()
                } //more than 5 sec
            }
            val device: UsbDevice = connectedUSBItem.value!!.device

            Log.d("SerialViewModel", "usb connection try")
            var usbConnection: UsbDeviceConnection? = null
            if (usbPermission == USBPermission.UnKnown && !usbManager.hasPermission(device)) {
                usbPermission = USBPermission.Requested
                val intent: Intent = Intent(INTENT_ACTION_GRANT_USB)
                intent.setPackage(getApplication<Application>().packageName)
                Log.d("SerialViewModel", "request Permission")
                usbManager.requestPermission(
                    device,
                    PendingIntent.getBroadcast(
                        getApplication(),
                        0,
                        intent,
                        PendingIntent.FLAG_IMMUTABLE
                    )
                )
                return@launch
            }
            delay(1000L)
            try {
                Log.d("SerialViewModel", "Port open try")
                usbConnection = usbManager.openDevice(device)
                connectedUSBItem.value!!.port.open(usbConnection)
            }catch(e:IllegalArgumentException){
                disConnectSerialDevice()
                return@launch
            }catch(e: IOException){
                if(e.message != "Already Open") throw IOException()
            }
            Log.d("SerialViewModel", "Port open")
            connectedUSBItem.value!!.port.setParameters(baudRate, 8, 1, UsbSerialPort.PARITY_NONE)
            usbIOManager = SerialInputOutputManager(connectedUSBItem.value!!.port, this@SerialViewModel)
            usbIOManager!!.start()
            Log.d("SerialViewModel","dtr On")
            connectedUSBItem.value?.port?.dtr = true
        }
    }
    fun disConnectSerialDevice(){
        usbPermission = USBPermission.UnKnown
        usbIOManager?.listener = null
        usbIOManager?.stop()
        if(connectedUSBItem.value == null) return
        if(connectedUSBItem.value!!.port.isOpen()){
            connectedUSBItem.value?.port?.close()
        }
        connectedUSBItem.update{ null }
    }

    val blockString: StateFlow<String?> get() = _blockString
    private val _blockString = MutableStateFlow<String?>("")
    private var _buffer = mutableStateOf("")

    //for JSON

    override fun onNewData(data: ByteArray?) { // called when get data
        viewModelScope.launch{
            if(data != null) {
                if (data.isNotEmpty()) {
                    val result : String = getLineString(data, data.size)
                    if (_buffer.value.isEmpty()) {
                        _buffer.value += result
                    }else{
                        if(result.length >=3 && result.substring(0,3) == "{\"B"){ //메시지를 받다말고 새로운 메시지가 들어옴
                            _buffer.value = result
                        }else if(result.length >=3 && result.substring(result.length - 3).equals("}  ")){ //메시지의 끝
                            _buffer.value += result.substring(0,result.length-2)
                            _blockString.value = _buffer.value
                            blockHandler(_buffer.value)
                            _buffer.value = ""
                        }else{
                            _buffer.value += result
                        }
                    }
                }
            }
        }
    }

    /*
    override fun onNewData(data: ByteArray?) { // For Custom Data Format ( {~~~~~} )
        viewModelScope.launch{
            if(data != null) {
                if (data.isNotEmpty()) {
                    val result : String = getLineString(data, data.size)
                    if (_buffer.value.isEmpty()) {
                        _buffer.value += result
                    }else{
                        result.replace(" ","")
                        if(result.contains("}")){
                            _buffer.value += result
                            Log.d("blockHandle", _buffer.value)
                            blockHandler(_buffer.value)
                            _buffer.value = ""
                        }else{
                            _buffer.value += result
                        }
                    }
                }
            }
        }
    }
     */


    override fun onRunError(e: Exception) {
        viewModelScope.launch() {
            Log.e("SerialViewModel", "Disconnected: ${e.message}")
            disConnectSerialDevice()
        }
    }

    private fun getLineString(array: ByteArray, length: Int): String {
        val result = StringBuilder()
        val line = ByteArray(8)
        var lineIndex = 0
        for (i in 0 until 0 + length) {
            if (lineIndex == line.size) {
                for (j in line.indices) {
                    if (line[j] > ' '.code.toByte() && line[j] < '~'.code.toByte()) {
                        result.append(String(line, j, 1))
                    } else {
                        result.append(" ")
                    }
                }
                lineIndex = 0
            }
            val b = array[i]
            line[lineIndex++] = b
        }
        for (i in 0 until lineIndex) {
            if (line[i] > ' '.code.toByte() && line[i] < '~'.code.toByte()) {
                result.append(String(line, i, 1))
            } else {
                result.append(" ")
            }
        }
        return result.toString()
    }
    init{
        val filter = IntentFilter(INTENT_ACTION_GRANT_USB)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Android 12 이상일 경우, 명시적으로 플래그를 지정
            getApplication<Application>().registerReceiver(usbPermissionReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            // Android 12 미만일 경우, 기존 방식으로 등록
            getApplication<Application>().registerReceiver(usbPermissionReceiver, filter)
        }
        fileManager.setFileTime()
        viewModelScope.launch{
            blockString.collect{
                it?.let{
                    blockHandler(it)
                    //todo: ProcessData
                }

            }
        }
    }
}

data class Result(
    @SerializedName("Addr") val addr: String,
    @SerializedName("Status") val status: String,
    @SerializedName("D_cm") val dCm: Int,
    @SerializedName("LPDoA_deg") val lPDoADeg: Float,
    @SerializedName("LAoA_deg") val lAoADeg: Float,
    @SerializedName("LFoM") val lfom: Int,
    @SerializedName("RAoA_deg") val raDoADeg: Float,
    @SerializedName("CFO_100ppm") val cfo100ppm: Int
)

data class Data(
    @SerializedName("Block") val block: Int,
    @SerializedName("results") val results: List<Result>
)