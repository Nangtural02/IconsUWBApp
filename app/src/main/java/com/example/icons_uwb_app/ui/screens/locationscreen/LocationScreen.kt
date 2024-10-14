package com.example.icons_uwb_app.ui.screens.locationscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.icons_uwb_app.MainSerialViewModel
import com.example.icons_uwb_app.R
import com.example.icons_uwb_app.data.environments.getPoint
import com.example.icons_uwb_app.data.rainging.toPoint
import com.example.icons_uwb_app.serial.CoordinatePlane
import com.example.icons_uwb_app.serial.SerialViewModel

@Composable
fun LocationScreen(mainViewModel: MainSerialViewModel, serialViewModel: SerialViewModel) {
    val data = serialViewModel.nowRangingData
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
        .fillMaxWidth()
        .padding(end = 5.dp)) {
        ZoomableBox {
                AsyncImage(
                    model = mainViewModel.uiState.collectAsState().value.connectedEnvironmentImage,
                    contentDescription = "backGroundImage"
                )

                CoordinatePlane(
                    anchorList = mainViewModel.uiState.collectAsState().value.connectedEnvironmentAnchors.map { it.getPoint() },
                    pointsList = listOf(data.value.toPoint()),
                    distanceList = data.value.distanceList.map{it.distance},
                    displayDistanceCircle = mainViewModel.uiState.collectAsState().value.toggleDistanceCircle,
                    toggleGrid = mainViewModel.uiState.collectAsState().value.toggleGrid,
                    toggleAxis = mainViewModel.uiState.collectAsState().value.toggleAxis,
                    scale = scale,
                    offsetX = offsetX,
                    offsetY = offsetY
                )
        }

        if(data.value.toPoint().z!=0f){
            Text(text = "(${"%.2f".format(data.value.toPoint().x)},${"%.2f".format(data.value.toPoint().y)},±${"%.2f".format(data.value.toPoint().z)})",
                style = TextStyle(
                    fontSize = 30.sp,
                    lineHeight = 42.sp,
                    fontFamily = FontFamily(Font(R.font.nanumgothicbold)),
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF000000)
                )
            )
        }else{
            Text(text = "(${"%.2f".format(data.value.toPoint().x)},${"%.2f".format(data.value.toPoint().y)})",
                style = TextStyle(
                    fontSize = 30.sp,
                    lineHeight = 42.sp,
                    fontFamily = FontFamily(Font(R.font.nanumgothicbold)),
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF000000)
                )
            )
        }
    }
    /*
    val environmentImage = mainViewModel.uiState.collectAsState().value.connectedEnvironmentImage
    ZoomableBox {
        if(environmentImage == -1){
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ){
                Text("연결된 시스템 없음")
            }
        }
        else if(environmentImage == 0){
            Box(
                modifier = Modifier.fillMaxSize()
                    .background(Color.White)
            )
        }
        else {
            Box {
                Image(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offsetX,
                            translationY = offsetY
                        ),
                    painter = painterResource(id = environmentImage),
                    contentDescription = null
                )
            }
        }
    }*/

    //ZoomableImage()
}

@Composable
fun ZoomableImage() {
    val scale = remember { mutableStateOf(1f) }
    val rotationState = remember { mutableStateOf(1f) }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .clip(RectangleShape) // Clip the box content
            .fillMaxSize() // Give the size you want...
            .background(Color.Gray)
            .pointerInput(Unit) {
                detectTransformGestures { centroid, pan, zoom, rotation ->
                    scale.value *= zoom
                    rotationState.value += rotation
                }
            }
    ) {
        Image(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center) // keep the image centralized into the Box
                .graphicsLayer(
                    // adding some zoom limits (min 50%, max 200%)
                    scaleX = maxOf(.5f, minOf(3f, scale.value)),
                    scaleY = maxOf(.5f, minOf(3f, scale.value)),
                    rotationZ = rotationState.value
                ),
            contentDescription = null,
            painter = painterResource(R.drawable.ex_paldal1f)
        )
    }
}

@Composable
fun ZoomableBox(
    modifier: Modifier = Modifier.background(Color.Transparent),
    minScale: Float = 1f,
    maxScale: Float = 5f,
    content: @Composable ZoomableBoxScope.() -> Unit
) {
    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var size by remember { mutableStateOf(IntSize.Zero) }

    Box(
        modifier = modifier
            .clip(RectangleShape)
            .onSizeChanged { size = it }
            .pointerInput(Unit) {
                detectTransformGestures { centroid, pan, zoom, _ ->
                    val newScale = maxOf(minScale, minOf(scale * zoom, maxScale))

                    // 줌 중심점 계산 (중앙을 기준으로 보정)
                    val scaleFactor = newScale / scale
                    offsetX = (offsetX + (1 - scaleFactor) * (centroid.x - offsetX))
                    offsetY = (offsetY + (1 - scaleFactor) * (centroid.y - offsetY))

                    scale = newScale

                    // 팬 동작 처리
                    val maxX = (size.width * (scale - 1)) / 2
                    val minX = -maxX
                    offsetX = maxOf(minX, minOf(maxX, offsetX + pan.x))

                    val maxY = (size.height * (scale - 1)) / 2
                    val minY = -maxY
                    offsetY = maxOf(minY, minOf(maxY, offsetY + pan.y))
                }
            }
    ) {
        val scope = ZoomableBoxScopeImpl(scale, offsetX, offsetY)
        scope.content()
    }
}



interface ZoomableBoxScope {
    val scale: Float
    val offsetX: Float
    val offsetY: Float
}

private data class ZoomableBoxScopeImpl(
    override val scale: Float,
    override val offsetX: Float,
    override val offsetY: Float
) : ZoomableBoxScope
