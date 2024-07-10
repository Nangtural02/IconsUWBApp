package com.example.icons_uwb_app.ui.screens.homescreen

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.icons_uwb_app.MainSerialViewModel
import com.example.icons_uwb_app.R
import com.example.icons_uwb_app.data.environments.Anchor
import com.example.icons_uwb_app.data.environments.UWBEnvironment
import com.example.icons_uwb_app.ui.theme.ICONS_UWB_APPTheme

@Composable
fun HomeScreenTopBar(
    currentScreen: HomeScreens,
    canNavigateBack: Boolean,
    navigateBack: () -> Unit,
){
    Row(
        horizontalArrangement = Arrangement.Absolute.Left,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(76.dp)
    ){
        if (canNavigateBack) {
            IconButton(
                onClick = navigateBack
            ) {
                Image(
                    painterResource(id = R.drawable.backbutton),
                    contentDescription = "back_button"
                )
            }
        }else { Box(modifier = Modifier.width(26.dp)) }
        Text(
            text = stringResource(id = currentScreen.title),
            modifier = Modifier,
            style = TextStyle(
                fontSize = 40.sp,
                lineHeight = 42.sp,
                fontFamily = FontFamily(Font(R.font.nanumgothicextrabold)),
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF000000)
            )
        )
    }
}

@Composable
fun HomeScreen(
    navigateToEnvironmentDetail: (UWBEnvironment) -> Unit,
    navigateToEnvironmentEntry: () -> Unit,
    mainViewModel: MainSerialViewModel,
    homeScreenViewModel: HomeScreenViewModel,
    environmentEditViewModel: EnvironmentEditViewModel
) {
    val homeUiState by homeScreenViewModel.homeUiState.collectAsState()
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        content = {
            itemsIndexed(homeUiState.environments) { _, environment ->
                EnvironmentProfile(
                    title = environment.title,
                    index = environment.id,
                    anchors = environment.anchors,
                    imagePainterID = environment.imagePainterID
                    , toDetails = {
                        homeScreenViewModel.setSelectedID(environment.id)
                        navigateToEnvironmentDetail(UWBEnvironment(id=environment.id,title=environment.title,anchors =environment.anchors, imagePainterID = environment.imagePainterID))
                    },
                    mainViewModel = mainViewModel
                )
            }
            item{
                NewEnvironmentButton(onClick = navigateToEnvironmentEntry)
            }
        }
    )
}

@Composable
fun EnvironmentProfile(title : String, index: Int, anchors: List<Anchor>, imagePainterID: Int,
                       toDetails : () -> Unit,
                       mainViewModel: MainSerialViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(194.dp)
            .padding(start = 22.dp, end = 22.dp)
            .clickable { toDetails() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF00C9E3),
        ),

        ){
        Column(
            modifier = Modifier
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()

                    .height(46.dp)

            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 19.dp, start = 21.dp),
                    style = TextStyle(
                        color = Color.Black,
                        fontSize = 26.sp,
                        fontFamily = FontFamily(Font(R.font.nanumsquareneootf_eb)),
                        fontWeight = FontWeight.Bold
                    ),
                    text = title
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(top = 14.dp, bottom = 13.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.Start
                ) {

                    Spacer(modifier = Modifier.height(10.dp))
                    if (imagePainterID != 0) {
                        Image(
                            painterResource(id = imagePainterID),
                            "map", modifier =
                            Modifier
                                .width(160.dp)
                                .height(110.dp)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .background(Color.LightGray)
                                .width(160.dp)
                                .height(110.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No image",
                                style = TextStyle(
                                    fontSize = 27.sp,
                                    fontFamily = FontFamily(Font(R.font.nanumgothicbold)),
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        modifier = Modifier.padding(start = 2.dp),
                        text = "앵커 : ${anchors?.size}개"
                    )
                    Text(
                        modifier = Modifier
                            .padding(start = 2.dp),
                        text = "마지막 연결 시점 :\n" +
                                " 2000-00-00",
                        textAlign = TextAlign.Right
                    )
                    if(mainViewModel.uiState.collectAsState().value.connectedEnvironmentID == index) {
                        Button(
                            onClick = {
                                Log.d(
                                    "Button",
                                    "DisConnect to environment \"$title\" "
                                )
                                mainViewModel.disconnectEnvironment()
                            },
                            modifier = Modifier
                                .width(150.dp)
                                .padding(bottom = 8.dp),
                            shape = RoundedCornerShape(15.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Magenta
                            )
                        ) {
                            Text(text = "연결 해제")
                        }
                    }
                    else {
                        Button(
                            onClick = {
                                Log.d(
                                    "Button",
                                    "Connect to environment \"$title\" "
                                )/*Connection with environment*/
                                mainViewModel.connectEnvironment(
                                    UWBEnvironment(index, title, anchors, imagePainterID)
                                )
                            },
                            modifier = Modifier
                                .width(150.dp)
                                .padding(bottom = 8.dp),
                            shape = RoundedCornerShape(15.dp)
                        ) {
                            Text(text = "연결")
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun NewEnvironmentButton(
    onClick: () -> Unit
){

    Button(
        onClick = {
            Log.d("Button","Make new environment")
            onClick() //new Environment
        },
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF4FEFF)),
        modifier = Modifier
            .fillMaxWidth()
            .height(194.dp)
            .padding(start = 22.dp, end = 22.dp),
        border = BorderStroke(1.dp, Color(0xFF1554F6)),){
        Text(
            text = "새로운 시스템 추가",
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


@Preview(showBackground = true)
@Composable
fun EnvironmentProfilePreview(){
    ICONS_UWB_APPTheme {
        EnvironmentProfile(title ="asdf" , index =1 , anchors = mutableListOf() , imagePainterID =R.drawable.ic_launcher_background, toDetails = {}, mainViewModel = MainSerialViewModel() )
    }
}
@Preview(showBackground = true)
@Composable
fun NewEnvironmentButtonPreview(){
    ICONS_UWB_APPTheme {
        NewEnvironmentButton(onClick = {})
    }
}