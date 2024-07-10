package com.example.icons_uwb_app.ui.screens.homescreen

import android.util.Log
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
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.icons_uwb_app.R
import com.example.icons_uwb_app.data.environments.Anchor
import com.example.icons_uwb_app.data.environments.UWBEnvironment
import com.example.icons_uwb_app.ui.theme.ICONS_UWB_APPTheme
import kotlinx.coroutines.launch

@Composable
fun EnvironmentDetailScreen(
    onNavigateUp : () -> Unit,
    //homeScreenViewModel : HomeScreenViewModel,
    selectedEnvironment : UWBEnvironment,
    environmentEditViewModel: EnvironmentEditViewModel
){
    //val selectedEnvironmentState by homeScreenViewModel.getSelectedEnvironment().collectAsState(initial = UWBEnvironment(title = "Sex"))
    //val selectedEnvironment = selectedEnvironmentState ?: UWBEnvironment(title = "Sibal?")
    environmentEditViewModel.updateUiState(selectedEnvironment)

    val coroutineScope = rememberCoroutineScope()
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
                        onClick = { Log.d("Button", "Title Edit")}
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
                    ,
                    contentAlignment = Alignment.Center

                ){
                    if((environmentEditViewModel.uiState.collectAsState().value.imagePainterID) != 0){
                        Image(
                            painterResource(id = environmentEditViewModel.uiState.collectAsState().value.imagePainterID),
                            contentDescription = null,
                            modifier = Modifier
                                .matchParentSize(),
                        )
                    }
                    FloatingActionButton(
                        onClick = { Log.d("button","edit image")},
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


                        ,
                        shape = RoundedCornerShape(12.dp),
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
                    AnchorCard(index, anchor,
                        toDeleteAnchor = {
                            Log.d("button","DeleteAnchor")
                            environmentEditViewModel.editDeleteAnchor(index)
                            /*
                            environmentEditViewModel.updateUiState(
                                environmentEditViewModel.uiState.value.copy(
                                    anchors = environmentEditViewModel.uiState.value.anchors.minus(anchor)
                                )
                            )*/
                            coroutineScope.launch {
                                environmentEditViewModel.updateEnvironment()
                            }
                        },
                        toResetAnchor = {
                            Log.d("button", "resetAnchor")

                            //environmentEditViewModel.uiState.value.anchors[selectedEnvironment.anchors.indexOf(anchor)] = Anchor(id= 354, name = "newew")
                            coroutineScope.launch {

                                environmentEditViewModel.updateUiState(
                                    environmentEditViewModel.uiState.value.copy(
                                        anchors = environmentEditViewModel.uiState.value.anchors.toMutableList().apply{
                                        //    this[environmentEditViewModel.uiState.value.anchors.indexOf(anchor)] = it
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
                    environmentEditViewModel.editAddAnchor(Anchor(id = 345, name = "fdsa"))
                    coroutineScope.launch{
                        environmentEditViewModel.updateEnvironment()
                    }
                }
            }
        }
    )
}

@Composable
fun AnchorCard(index: Int, anchor: Anchor, toDeleteAnchor: ()->Unit, toResetAnchor: (Anchor)->Unit = {}){
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
                    text = if(anchor.name=="")"앵커 ${index+1}"
                    else anchor.name,
                    style = TextStyle(
                        color = Color.Black,
                        fontSize = 26.sp,
                        fontFamily = FontFamily(Font(R.font.nanumsquareneootf_eb)),
                        fontWeight = FontWeight.Bold
                    )

                )
                IconButton(
                    onClick = { Log.d("Button", "Anchor Edit")}
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
                        toResetAnchor(Anchor(id= 354, name = "newew"))
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

            }
        }
    }
}

@Composable
fun AddAnchorButton(
    modifier: Modifier,
    onClick: () -> Unit
){
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .width(364.dp)
            .height(123.dp)
            .background(color = Color(0xFFFFFFFF), shape = RoundedCornerShape(size = 10.dp))
            .clickable { onClick() }
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