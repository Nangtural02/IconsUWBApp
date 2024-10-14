package com.example.icons_uwb_app.ui.screens.homescreen

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.icons_uwb_app.R
import com.example.icons_uwb_app.data.environments.Anchor
import com.example.icons_uwb_app.data.environments.UWBEnvironment
import com.example.icons_uwb_app.ui.theme.ICONS_UWB_APPTheme
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

@Composable
fun EnvironmentDetailScreen(
    onNavigateUp : () -> Unit,
    selectedEnvironment : UWBEnvironment?,
    environmentEditViewModel: EnvironmentEditViewModel
){
    selectedEnvironment?.let {
        environmentEditViewModel.updateUiState(selectedEnvironment)
    }

    val context = LocalContext.current
    val contentResolver = context.contentResolver
    val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if(uri != null) {
            try {
                val inputStream = contentResolver.openInputStream(uri)
                val file = File(context.cacheDir, "selected_image.jpg")
                val outputStream = FileOutputStream(file)

                inputStream?.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }

                val newUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                Log.d("ImageAccess", "File copied to local storage: $newUri")
                environmentEditViewModel.editChangeImage(newUri)
                // 이 newUri를 데이터베이스에 저장하여 사용
            } catch (e: Exception) {
                Log.e("ImageAccess", "Failed to copy URI content to local storage", e)
            }

        }
    }
    val coroutineScope = rememberCoroutineScope()
    val showEnvironmentTitleEdit = remember{ mutableStateOf(false)}

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 23.dp, end = 23.dp)
            .background(color = Color(0xFF00C9E3), shape = RoundedCornerShape(size = 10.dp)),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(11.dp),
        content = {
            item{ //title
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp)
                        .height(46.dp),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(
                        text = environmentEditViewModel.uiState.collectAsState().value.title,
                        style = TextStyle(
                            color = Color.Black,
                            fontSize = 26.sp,
                            fontFamily = FontFamily(Font(R.font.nanumsquareneootf_eb)),
                            fontWeight = FontWeight.Bold
                        )
                    )
                    IconButton(
                        onClick = {
                            Log.d("Button", "Title Edit")
                            showEnvironmentTitleEdit.value = true
                        }
                    ){
                        Image(
                            painter = painterResource(id = R.drawable.edit),
                            contentDescription = null,
                            modifier = Modifier
                                .height(24.dp)
                                .width(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(
                        onClick = {
                            Log.d("Button", "Environment Delete")
                            //todo: Anchor delete
                            onNavigateUp()
                            coroutineScope.launch {
                                environmentEditViewModel.deleteNowEnvironment()

                            }
                        },
                        colors = ButtonDefaults.buttonColors(Color.Red)
                    ){
                        Text(text = "삭제",
                            style = TextStyle(fontFamily = FontFamily(Font(R.font.nanumsquareneootf_bd)))
                        )
                    }
                }
            }
            item{
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(248.dp)
                        .background(color = Color.White, shape = RoundedCornerShape(10.dp))
                    ,contentAlignment = Alignment.Center
                ){
                    if((environmentEditViewModel.uiState.collectAsState().value.imageUri) != null){
                        Log.d("DetailImage","DetailImageUri: $environmentEditViewModel.uiState.collectAsState().value.imageUri")
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(environmentEditViewModel.uiState.collectAsState().value.imageUri)
                                .diskCachePolicy(CachePolicy.DISABLED) // 디스크 캐시 활성화
                                .memoryCachePolicy(CachePolicy.DISABLED) // 메모리 캐시 활성화
                                .build(),
                            contentDescription = null,
                            modifier = Modifier
                                .matchParentSize(),
                        )
                    }
                    FloatingActionButton(
                        onClick = {
                            //todo: edit image button
                            Log.d("button","edit image")
                            launcher.launch("image/*")
                            coroutineScope.launch{
                                environmentEditViewModel.updateEnvironment()
                            }
                                  },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(4.dp)
                            .shadow(
                                elevation = 3.dp,
                                spotColor = Color(0x4D000000),
                                ambientColor = Color(0x4D000000)
                            )
                            .shadow(
                                elevation = 8.dp,
                                spotColor = Color(0x26000000),
                                ambientColor = Color(0x26000000)
                            )
                        ,shape = RoundedCornerShape(12.dp),
                        backgroundColor = Color(0xFF4F378B)
                    ) {
                        Image(painter = painterResource(id = R.drawable.edit),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(Color(0xFFEADDFF))
                        )
                    }
                }
            }


            if(environmentEditViewModel.uiState.value.anchors.isNotEmpty()) {
                itemsIndexed(environmentEditViewModel.uiState.value.anchors) { index, anchor ->
                    AnchorCard(
                        index,
                        anchor,
                        toDeleteAnchor = {
                            Log.d("button","DeleteAnchor")
                            environmentEditViewModel.editDeleteAnchor(index)
                            coroutineScope.launch {
                                environmentEditViewModel.updateEnvironment()
                            }
                        },
                        toEditAnchor = {
                            Log.d("button", "resetAnchor")

                            coroutineScope.launch {
                                environmentEditViewModel.updateUiState(
                                    environmentEditViewModel.uiState.value.copy(
                                        anchors = environmentEditViewModel.uiState.value.anchors.toMutableList().apply{
                                            this[index] = it
                                        }.toList()
                                    )
                                )
                                environmentEditViewModel.updateEnvironment()
                            }
                        }
                    )
                }
            }
            item{
                AddAnchorButton(modifier = Modifier) {
                    Log.d("button", "add anchor")
                    environmentEditViewModel.editAddAnchor(it)
                    coroutineScope.launch{
                        environmentEditViewModel.updateEnvironment()
                    }
                }
            }
        }
    )
    if(showEnvironmentTitleEdit.value){
        TitleEditDialog(
            onConfirm = {
                environmentEditViewModel.updateUiState(it)
                coroutineScope.launch{environmentEditViewModel.updateEnvironment()}
                showEnvironmentTitleEdit.value = false
                        },
            onDismiss ={showEnvironmentTitleEdit.value = false}
        )
    }
}

        @Composable
        fun AnchorCard(index: Int, anchor: Anchor, toDeleteAnchor: ()->Unit, toEditAnchor: (Anchor)->Unit = {}){
            val showAnchorTitleEditDialog = remember{ mutableStateOf(false)}
            val showAnchorEditDialog = remember{ mutableStateOf(false)}
            if(anchor.name=="") anchor.name = "앵커 ${index+1}"
            Box(
                modifier = Modifier
                    .width(364.dp)
                    .height(123.dp)
                    .background(color = Color(0xFFFFFFFF), shape = RoundedCornerShape(size = 10.dp))
    ){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 9.dp)
        ){
            Row(
                modifier = Modifier

                    .padding(top = 8.dp)
                    .height(30.dp),
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = anchor.name,
                    style = TextStyle(
                        color = Color.Black,
                        fontSize = 26.sp,
                        fontFamily = FontFamily(Font(R.font.nanumsquareneootf_eb)),
                        fontWeight = FontWeight.Bold
                    )

                )
                IconButton(
                    onClick = {
                        Log.d("Button", "Anchor Edit")
                        showAnchorTitleEditDialog.value = true
                    }
                ){
                    Image(
                        painter = painterResource(id = R.drawable.edit),
                        contentDescription = null,
                        modifier = Modifier
                            .height(24.dp)
                            .width(24.dp)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                TextButton(
                    onClick = {
                        Log.d("Button", "Environment Delete")
                        //todo: Anchor delete
                        toDeleteAnchor()
                    },
                    colors = ButtonDefaults.buttonColors(Color.Red)
                ){
                    Text(text = "삭제",
                        style = TextStyle(fontFamily = FontFamily(Font(R.font.nanumsquareneootf_bd)))
                    )
                }
            }
            Row(
                modifier = Modifier
                    .padding(top = 16.dp, bottom = 9.dp)
                    .fillMaxWidth()
                    .fillMaxHeight(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ){
                Text(
                    text = "id: ${anchor.id} \n" +
                            "좌표: (${anchor.coordinateX}"+
                            ",${anchor.coordinateY})",
                    style = TextStyle(
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.nanumsquareneootf_bd)),
                        fontWeight = FontWeight.Normal
                    )
                )
                TextButton(
                    onClick = {
                        Log.d("Button", "Reset Anchor")
                        //toResetAnchor(Anchor(id= 354, name = "newew"))
                        showAnchorEditDialog.value = true
                              },
                    modifier = Modifier
                        .width(48.dp)
                        .height(32.dp)
                        .align(Alignment.Bottom),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFD91E1E)),
                    contentPadding = PaddingValues()
                ){
                    Text(
                        text = "재설정",
                        style = TextStyle(
                            color = Color.Black,
                            fontSize = 12.sp,
                            fontFamily = FontFamily(Font(R.font.nanumsquareneootf_bd)),
                            fontWeight = FontWeight.Normal
                        ),
                    )
                }
                if(showAnchorTitleEditDialog.value){
                    TitleEditDialog(
                        onConfirm = {toEditAnchor(anchor.copy(name = it)); showAnchorTitleEditDialog.value = false},
                        onDismiss = { showAnchorTitleEditDialog.value = false }
                    )
                }
                if(showAnchorEditDialog.value){
                    AnchorEditDialog(
                        onConfirm = { id,x,y -> toEditAnchor(
                            Anchor(id = id ?: anchor.id, coordinateX = x?:anchor.coordinateX, coordinateY = y?:anchor.coordinateY, name = anchor.name))
                            showAnchorEditDialog.value = false},
                        onDismiss = {showAnchorEditDialog.value = false}
                    )
                }
            }
        }
    }
}

@Composable
fun AddAnchorButton(
    modifier: Modifier,
    onClick: (Anchor) -> Unit
){
    val showAddAnchorDialog = remember{ mutableStateOf(false) }
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .width(364.dp)
            .height(123.dp)
            .background(color = Color(0xFFFFFFFF), shape = RoundedCornerShape(size = 10.dp))
            .clickable { showAddAnchorDialog.value = true }
    ) {
        Text(
            text = "새로운 앵커 추가",
            style = TextStyle(
                color = Color.Black,
                fontSize = 24.sp,
                fontFamily = FontFamily(Font(R.font.nanumsquareneootf_eb)),
                fontWeight = FontWeight.Normal
            )
        )
    }
    if(showAddAnchorDialog.value){
        NewAnchorDialog(
            onConfirm ={ name, id, x, y ->
                onClick(
                    Anchor(
                        name = name ?:"",
                        id = id ?: 0,
                        coordinateX = x ?: 0f,
                        coordinateY = y ?: 0f
                    )
                )
                showAddAnchorDialog.value = false
            },
            onDismiss ={ showAddAnchorDialog.value = false }
        )
    }
}

@Preview
@Composable
fun TitleEditDialogPreview(){
    ICONS_UWB_APPTheme {
        TitleEditDialog({}, {})
    }
}

@Composable
fun TitleEditDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val textState = remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "이름 변경")
        },
        text = {
            Column {
                Card(modifier = Modifier.height(16.dp)){}
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = textState.value,
                    onValueChange = { textState.value = it },
                    label = { Text(text = "Name") },
                    modifier = Modifier.padding(top = 10.dp)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(textState.value) }) {
                Text(text = "저장")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "취소")
            }
        },
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    )
}

@Composable
fun AnchorEditDialog(
    onConfirm: (Int?, Float?, Float?) -> Unit,
    onDismiss: () -> Unit
) {
    val isValid = remember{ mutableStateOf(true) }
    val idState = remember { mutableStateOf("") }
    val xState = remember { mutableStateOf("") }
    val yState = remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "앵커 정보 변경")
        },
        text = {
            Column {
                Card(modifier = Modifier.height(16.dp)){}
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = idState.value,
                    onValueChange = { idState.value = it },
                    label = { Text(text = "Anchor ID") },
                    modifier = Modifier.padding(top = 10.dp)
                )
                OutlinedTextField(
                    value = xState.value,
                    onValueChange = { xState.value = it },
                    label = { Text(text = "X") },
                    modifier = Modifier.padding(top = 10.dp)
                )
                OutlinedTextField(
                    value = yState.value,
                    onValueChange = { yState.value = it },
                    label = { Text(text = "Y") },
                    modifier = Modifier.padding(top = 10.dp)
                )
                if(!isValid.value){
                    Text("잘못된 입력입니다")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                try {
                    onConfirm(
                        if (idState.value == "") null else idState.value.toInt(),
                        if (xState.value == "") null else xState.value.toFloat(),
                        if (yState.value == "") null else yState.value.toFloat()
                    )
                }catch(e:NumberFormatException){
                    isValid.value = false
                }
            }) {
                Text(text = "저장")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "취소")
            }
        },
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    )
}
@Preview
@Composable
fun NewAnchorDialogPreview(){
    ICONS_UWB_APPTheme {
        NewAnchorDialog({_,_,_,_ -> }, {})
    }
}
@Composable
fun NewAnchorDialog(
    onConfirm: (String?, Int?, Float?, Float?) -> Unit,
    onDismiss: () -> Unit
) {
    val isValid = remember{ mutableStateOf(true)}
    val nameState = remember { mutableStateOf("")}
    val idState = remember { mutableStateOf("") }
    val xState = remember { mutableStateOf("") }
    val yState = remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "새로운 앵커 추가")
        },
        text = {
            Column {
                OutlinedTextField(
                    value = nameState.value,
                    onValueChange = { nameState.value = it },
                    label = { Text(text = "Anchor Name") },
                    modifier = Modifier.padding(top = 10.dp)
                )
                OutlinedTextField(
                    value = idState.value,
                    onValueChange = { idState.value = it },
                    label = { Text(text = "Anchor ID") },
                    modifier = Modifier.padding(top = 10.dp)
                )
                OutlinedTextField(
                    value = xState.value,
                    onValueChange = { xState.value = it },
                    label = { Text(text = "X") },
                    modifier = Modifier.padding(top = 10.dp)
                )
                OutlinedTextField(
                    value = yState.value,
                    onValueChange = { yState.value = it },
                    label = { Text(text = "Y") },
                    modifier = Modifier.padding(top = 10.dp)
                )
                if(!isValid.value){
                    Text("잘못된 입력입니다")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                try {
                    onConfirm(
                        if (nameState.value == "") null else nameState.value,
                        if (idState.value == "") null else idState.value.toInt(),
                        if (xState.value == "") null else xState.value.toFloat(),
                        if (yState.value == "") null else yState.value.toFloat()
                    )
                }catch(e:NumberFormatException){
                    isValid.value = false
                }
            }) {
                Text(text = "저장")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "취소")
            }
        },
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewAnchorEditDialog() {
    AnchorEditDialog(
        onConfirm = { id,x,y -> println("User input: $id,($x,$y)") },
        onDismiss = { println("Dialog dismissed") }
    )
}


@Preview
@Composable
fun AnchorCardPreview(){
    ICONS_UWB_APPTheme {
        AnchorCard(index = 1, anchor = Anchor(),{})
    }
}
@Preview
@Composable
fun AddAnchorButtonPreview(){
    ICONS_UWB_APPTheme {
        AddAnchorButton(modifier = Modifier) {
            
        }
    }
}