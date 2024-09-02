package com.example.icons_uwb_app.ui.screens.navigation

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.icons_uwb_app.AppViewModelProvider
import com.example.icons_uwb_app.MainSerialViewModel
import com.example.icons_uwb_app.MainUiState
import com.example.icons_uwb_app.R
import com.example.icons_uwb_app.ui.theme.ICONS_UWB_APPTheme

@Composable
fun TopBar(navController: NavHostController,
           mainViewModel: MainSerialViewModel,
           modifier: Modifier = Modifier
){
    val mainUiState by mainViewModel.uiState.collectAsState()
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.Bottom),
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .border(
                width = 1.dp,
                color = Color(0xFFC7C7C7),
                shape = RoundedCornerShape(size = 20.dp)
            )
            .width(430.dp)
            .height(98.dp)
            .padding(bottom = 20.dp)){

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .width(430.dp)
                .height(48.dp)
                .padding(start = 20.dp, end = 20.dp)
        ){
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.Start),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .width(216.dp)
                    .height(48.dp)
            ) {
                Image(
                    painterResource(id = R.drawable.uwbicon),
                    contentDescription = null,
                    Modifier
                        .width(48.dp)
                        .height(48.dp)
                        .background(Color.Transparent)
                )

                Text(
                    text = mainUiState.connectedEnvironmentName,
                    // text-lg/SemiBold
                    style = TextStyle(
                        fontSize = 19.sp,
                        lineHeight = 28.sp,
                        fontFamily = FontFamily(Font(R.font.nanumgothicbold)),
                        fontWeight = FontWeight(600),
                        color = Color(0xFF1F1F1F),),
                    modifier = Modifier
                        .width(160.dp)
                        .height(28.dp)
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.Start),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .width(94.dp)
                    .height(42.dp)
            ) {
                IconButton(
                    onClick = {
                        Log.d("Button", "toggle distance circle")
                        mainViewModel.toggleDistanceCircle()
                              },
                    modifier = Modifier
                        .width(42.dp)
                        .height(42.dp)
                        .border(
                            width = 1.dp,
                            color = Color(0xFFC7C7C7),
                            shape = RoundedCornerShape(size = 50.dp)
                        )
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.tagpng),
                        contentDescription = "image description",
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .width(27.73002.dp)
                            .height(27.99765.dp)
                    )
                }
                IconButton(
                    onClick = { Log.d("Button", "Menu") },
                    modifier = Modifier
                        .width(42.dp)
                        .height(42.dp)
                        .border(
                            width = 1.dp,
                            color = Color(0xFFC7C7C7),
                            shape = RoundedCornerShape(size = 50.dp)
                        )
                ) {

                    Image(
                        painter = painterResource(id = R.drawable.menu),
                        contentDescription = "image description",
                        contentScale = ContentScale.None,
                        modifier = Modifier
                            .width(430.dp)
                            .height(932.dp)
                    )
                }
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun TopBarPreview(){
    ICONS_UWB_APPTheme {
        TopBar(navController = rememberNavController(), mainViewModel = viewModel(factory = AppViewModelProvider.Factory))
    }
}