package com.example.icons_uwb_app.data.rainging

import android.icu.text.SimpleDateFormat
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.example.icons_uwb_app.data.environments.Anchor
import com.example.icons_uwb_app.serial.Point
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date
import kotlin.time.TimeSource

@Entity(tableName = "RangingData")
data class RangingData(
    @PrimaryKey(autoGenerate = false)
    val blockNum: Int = 0,
    val distanceList: List<RangingDistance> = emptyList(),
    var coordinates: Point = Point(),
    val time: String = SimpleDateFormat("dd HH:mm:ss").format(Date())
)
fun RangingData.toPoint():Point{
    return Point(this.coordinates.x,this.coordinates.y,this.coordinates.z)
}
data class RangingDistance(
    val id: Int,
    val distance : Float,
    val PDOA : Float? = null,
    val AOA : Float? = null
)

data class AnchorData(
    val time : TimeSource,
    val anchorID : Int = 0,
    val range : Float = 0f
)

class AnchorDataConverters{
    @TypeConverter
    fun fromList(dataSet: List<AnchorData>): String{
        val gson= Gson()
        return gson.toJson(dataSet)
    }

    @TypeConverter
    fun toList(anchorDataString: String): List<Anchor> {
        val gson = Gson()
        val type = object : TypeToken<List<Anchor>>() {}.type
        return gson.fromJson(anchorDataString,type)
    }
}