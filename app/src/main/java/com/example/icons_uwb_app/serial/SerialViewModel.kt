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
import androidx.compose.runtime.remember
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.icons_uwb_app.data.environments.Anchor
import com.example.icons_uwb_app.data.environments.getPoint
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
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Date


class SerialViewModel(application: Application): AndroidViewModel(application), SerialInputOutputManager.Listener {
    val foldername: String =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + "/SerialLog" //Save Path
    var rawfilename: String = "temp.txt"
    var filename: String = "temp.csv"
    var anchorList = listOf(Anchor())
    var nowRangingData : MutableState<RangingData> = mutableStateOf(RangingData())



    fun blockHandler(blockString: String){
        viewModelScope.launch{
            writeTextFile(blockString + "\n") // write Raw Serial Data
        }
        try {
            val data = Gson().fromJson(blockString, Data::class.java)
            val distanceList:List<RangingDistance> = data.results.map{ result ->
                RangingDistance(
                     id = if(result.status == "Err") -1 else result.addr.substring(3).toInt(),
                    distance = result.dCm.toFloat() / 100,
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
                    else -> Point()
                }
            nowRangingData.value = blockData
            viewModelScope.launch{
                writeCSVFile(blockData)
            }
        }catch(e: JsonSyntaxException){
            Log.d("error", "signal error")
        }catch(e: NullPointerException){
            Log.e("error", "nullPointer -")
        }
    }
    fun updateAnchorList(newAnchorList: List<Anchor>){
        anchorList = newAnchorList
    }


    fun setFileTime(){
        rawfilename = "rawSerial_" + SimpleDateFormat("yyyy_MM_dd HH_mm_ss").format(Date()) + ".txt"
        filename = SimpleDateFormat("yyyy_MM_dd HH_mm_ss").format(Date()) + ".csv"
    }
    private suspend fun writeTextFile(contents: String) {
        withContext(Dispatchers.IO) {
            try {
                val dir = File(foldername)
                if (!dir.exists()) {
                    dir.mkdir()
                }
                val fos = FileOutputStream(foldername + "/" + rawfilename, true)
                fos.write(contents.encodeToByteArray())
                fos.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
    private suspend fun writeCSVFile(data: RangingData) {
        withContext(Dispatchers.IO) {
            try {
                var contents = ""
                contents += "${data.blockNum},"
                data.distanceList.forEach{ rangingDistance ->
                    contents +="${rangingDistance.id},${rangingDistance.distance},${rangingDistance.PDOA},${rangingDistance.AOA},"
                }
                contents +="/,${"%.2f".format(data.coordinates.x)},${"%.2f".format(data.coordinates.y)},${"%.2f".format(data.coordinates.z)},"
                contents += "\n"


                val dir = File(foldername)
                if (!dir.exists()) {
                    dir.mkdir()
                }
                val fos = FileOutputStream(foldername + "/" + filename, true)
                fos.write(contents.encodeToByteArray())
                fos.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    var connectedUSBItem = MutableStateFlow<USBItem?>(null)
    private enum class USBPermission {UnKnown, Requested, Granted, Denied}
    var baudRate = 115200
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
            Log.d("asdf", "${connectedUSBItem.value == null}")
            while (connectedUSBItem.value == null) {
                Log.d("button", "try to Connect")
                for (device in usbManager.deviceList.values) {
                    val driver = CdcAcmSerialDriver(device)
                    if (driver.ports.size == 1) {
                        connectedUSBItem.update {
                            USBItem(device, driver.ports[0], driver)
                        }
                        Log.d("asdf", "device Connected")
                    }
                }
                delay(1000L) //by 1 sec
                count ++
                if(count>5) {
                    cancel()
                    disConnectSerialDevice()
                } //more than 5 sec
            }
            val device: UsbDevice = connectedUSBItem.value!!.device

            Log.d("asdf", "usb connection try")
            var usbConnection: UsbDeviceConnection? = null
            if (usbPermission == USBPermission.UnKnown && !usbManager.hasPermission(device)) {
                usbPermission = USBPermission.Requested
                val intent: Intent = Intent(INTENT_ACTION_GRANT_USB)
                intent.setPackage(getApplication<Application>().packageName)
                Log.d("asdf", "request Permission")
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
                Log.d("asdf", "Port open try")
                usbConnection = usbManager.openDevice(device)
                connectedUSBItem.value!!.port.open(usbConnection)
            }catch(e:IllegalArgumentException){
                disConnectSerialDevice()
                return@launch
            }catch(e: IOException){
                if(e.message != "Already Open") throw IOException()
            }
            Log.d("asdf", "Port open")
            connectedUSBItem.value!!.port.setParameters(baudRate, 8, 1, UsbSerialPort.PARITY_NONE)
            usbIOManager = SerialInputOutputManager(connectedUSBItem.value!!.port, this@SerialViewModel)
            usbIOManager!!.start()
            Log.d("qwrwe","dtr On")
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

    override fun onNewData(data: ByteArray?) {
        viewModelScope.launch{
            receive(data)
        }
    }
    override fun onRunError(e: Exception) {
        viewModelScope.launch() {
            Log.d("SerialDevice", "Disconnected: ${e.message}")
            disConnectSerialDevice()
        }
    }

    private fun receive(data: ByteArray?){
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
        setFileTime()
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