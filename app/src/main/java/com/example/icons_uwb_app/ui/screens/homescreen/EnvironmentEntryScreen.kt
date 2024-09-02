package com.example.icons_uwb_app.ui.screens.homescreen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.icons_uwb_app.R
import com.example.icons_uwb_app.data.environments.Anchor
import com.example.icons_uwb_app.data.environments.UWBEnvironment
import com.example.icons_uwb_app.ui.screens.navigation.NavigationDestination
import kotlinx.coroutines.launch

@Composable
fun EnvironmentEntryScreen(
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    environmentEditViewModel: EnvironmentEditViewModel
) {
    environmentEditViewModel.resetEnvironment()
    val coroutineScope = rememberCoroutineScope()
    EnvironmentEntryBody(
        environmentEditViewModel = environmentEditViewModel,
        onValueChange = environmentEditViewModel::updateUiState,
        onSaveClick = {
            coroutineScope.launch{
                environmentEditViewModel.saveEnvironment()
                onNavigateUp()
            }
        }
    )
}

@Composable
fun EnvironmentEntryBody(
    environmentEditViewModel: EnvironmentEditViewModel,
    onValueChange: (UWBEnvironment) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val editingEnvironment = environmentEditViewModel.uiState.collectAsState().value
    val showTitleEditDialog = remember{mutableStateOf(false)}
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 23.dp, end = 23.dp)
            .background(color = Color(0xFFF4FEFF), shape = RoundedCornerShape(size = 10.dp))
            .border(
                width = 1.dp,
                color = Color(0xFF1554F6),
                shape = RoundedCornerShape(size = 10.dp)
            ),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(11.dp),
    ){

        item { //title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp)
                    .height(46.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                androidx.compose.material.Text(
                    text = editingEnvironment.title,
                    style = TextStyle(
                        color = Color.Black,
                        fontSize = 26.sp,
                        fontFamily = FontFamily(Font(R.font.nanumsquareneootf_eb)),
                        fontWeight = FontWeight.Bold
                    )

                )
                androidx.compose.material.IconButton(
                    onClick = {
                        Log.d("Button", "title Edit")
                        showTitleEditDialog.value = true
                        //toDo: new title popup 만들기

                    }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.edit),
                        contentDescription = null,
                        modifier = Modifier
                            .height(24.dp)
                            .width(24.dp)
                    )
                }
            }
        }/*
        item {
            NewImageButton(environmentEditViewModel, onValueChange)
        }*/
        itemsIndexed(editingEnvironment.anchors) { index, anchor ->
            AnchorCard(
                index, anchor,
                toDeleteAnchor = {
                    Log.d("button","DeleteAnchor")
                    environmentEditViewModel.editDeleteAnchor(index)
                },
                toEditAnchor = {
                    Log.d("button", "resetAnchor")
                    environmentEditViewModel.updateUiState(
                        environmentEditViewModel.uiState.value.copy(
                            anchors = environmentEditViewModel.uiState.value.anchors.toMutableList().apply{
                                this[index] = it
                            }.toList()
                        )
                    )
                }
            )
        }
        item {
            AddAnchorButton(
                modifier = Modifier.border(
                    width = 1.dp,
                    color = Color.Black,
                    shape = RoundedCornerShape(10.dp)
                )
            ) {
                Log.d("button", "add anchor")
                environmentEditViewModel.editAddAnchor(it)
            }
        }
        item { //saveButton
            Box(
                contentAlignment = Alignment.Center,
                modifier = modifier
                    .width(364.dp)
                    .height(50.dp)
                    .background(
                        color = Color(0xFF4829B8),
                        shape = RoundedCornerShape(size = 10.dp)
                    )
                    .clickable { onSaveClick() }
            ) {
                androidx.compose.material.Text(
                    text = "저장",
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 24.sp,
                        fontFamily = FontFamily(Font(R.font.nanumsquareneootf_eb)),
                        fontWeight = FontWeight.Normal
                    )
                )
            }
        }
    }
    if(showTitleEditDialog.value) {
        TitleEditDialog(
            onConfirm = {
                environmentEditViewModel.updateUiState(it)
                showTitleEditDialog.value = false
            },
            onDismiss = { showTitleEditDialog.value = false }

        )
    }

}

@Composable
fun CustomTextFieldDialog(
    initialText: String?,
    instructionText: String?,
    onClickCancel: () -> Unit,
    onClickConfirm: (text: String) -> Unit
) {
    val text = remember { mutableStateOf(initialText ?: "") }
    Dialog(
        onDismissRequest = { onClickCancel() },
    ) {
        Card(
            shape = RoundedCornerShape(8.dp), // Card의 모든 꼭지점에 8.dp의 둥근 모서리 적용
        ) {
            Column(
                modifier = Modifier
                    .width(300.dp)
                    .wrapContentHeight()
                    .background(
                        color = Color.White,
                    )
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                androidx.compose.material3.Text(text = instructionText ?: "")

                Spacer(modifier = Modifier.height(15.dp))

                // TextField
                BasicTextField(
                    value = text.value,
                    onValueChange = { text.value = it },
                    singleLine = true,
                    textStyle = TextStyle(
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                    ),
                    decorationBox = { innerTextField ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = Color(0xFFBAE5F5),
                                    shape = RoundedCornerShape(size = 10.dp)
                                )
                                .padding(all = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Create,
                                contentDescription = "",
                                tint = Color.DarkGray,
                            )
                            Spacer(modifier = Modifier.width(width = 8.dp))
                            innerTextField()
                        }
                    },
                )

                Spacer(modifier = Modifier.height(15.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    Button(onClick = {
                        onClickCancel()
                    },
                        shape = RoundedCornerShape(10.dp)) {
                        androidx.compose.material3.Text(text = "취소")
                    }

                    Spacer(modifier = Modifier.width(5.dp))

                    Button(onClick = {
                        onClickConfirm(text.value)
                    },
                        shape = RoundedCornerShape(10.dp)) {
                        androidx.compose.material3.Text(text = "확인")
                    }
                }
            }
        }
    }
}
@Composable
fun NewImageButton(viewModel: EnvironmentEditViewModel, onValueChange: (UWBEnvironment) -> Unit){
    val EditingImage: Int = viewModel.uiState.collectAsState().value.imagePainterID
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(248.dp)
            .background(color = Color.White, shape = RoundedCornerShape(10.dp))
        ,
        contentAlignment = Alignment.Center

    ){
        if((EditingImage) != 0) {
            Image(
                painterResource(id = EditingImage),
                contentDescription = null,
                modifier = Modifier
                    .matchParentSize(),
            )

            FloatingActionButton(
                onClick = { Log.d("button", "edit image")
                    //Change image
                    //onValueChange(tempEnvironment.copy(imagePainterID = R.drawable.settings))
                    viewModel.editChangeImage(newImage = R.drawable.settings)
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
                    ),
                shape = RoundedCornerShape(12.dp),
                backgroundColor = Color(0xFF4F378B)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.edit),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(Color(0xFFEADDFF))
                )
            }
        }
        else{
            Button(
                onClick = {
                    Log.d("Button","Insert Image")
                    //toDo: Insert Image popup
                    viewModel.editChangeImage(newImage = R.drawable.ex_paldal1f)
                    //onValueChange(tempEnvironment.copy(imagePainterID = tempEnvironment.imagePainterID))
                },
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color(0xFFD9D9D9)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .border(
                        width = 1.dp,
                        color = Color(0xFF4F378B),
                        shape = RoundedCornerShape(10.dp)
                    )){
                androidx.compose.material3.Text(
                    text = "배경 이미지 추가",
                    // text-xl/SemiBold
                    style = TextStyle(
                        fontSize = 24.sp,
                        lineHeight = 30.sp,
                        fontFamily = FontFamily(Font(R.font.nanumgothicbold)),
                        fontWeight = FontWeight(600),
                        color = Color(0xFF001752),
                        textAlign = TextAlign.Center,)
                )
            }
        }
    }
}