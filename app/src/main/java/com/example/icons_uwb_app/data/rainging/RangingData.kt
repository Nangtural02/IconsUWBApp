package com.example.icons_uwb_app.data.rainging

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.example.icons_uwb_app.data.environments.Anchor
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.time.TimeSource

@Entity(tableName = "RangingData")
data class RangingData(
    @PrimaryKey(autoGenerate = false)
    val createdTime : TimeSource,
    val environmentID : Int = 0,
    val dataSet: List<AnchorData> = emptyList()
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