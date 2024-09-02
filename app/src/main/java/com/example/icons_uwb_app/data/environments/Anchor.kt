package com.example.icons_uwb_app.data.environments

import com.example.icons_uwb_app.serial.Point


data class Anchor(
    var id : Int = 0,
    var coordinateX: Float = 0f,
    var coordinateY: Float = 0f,
    var name : String = ""
)
fun Anchor.getPoint(): Point {
    return Point(this.coordinateX,this.coordinateY)
}