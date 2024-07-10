package com.example.icons_uwb_app.data.environments

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "Environment")
data class UWBEnvironment(
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0,
    val title: String = "",
    val anchors : List<Anchor> = emptyList<Anchor>(),
    val imagePainterID : Int = 0,
    val lastConnectedDate: String = "2000-01-01"
)


class EnvironmentConverters{
    @TypeConverter
    fun fromList(anchors: List<Anchor>): String{
        val gson= Gson()
        return gson.toJson(anchors)
    }

    @TypeConverter
    fun toList(anchorString: String): List<Anchor> {
        val gson = Gson()
        val type = object : TypeToken<List<Anchor>>() {}.type
        return gson.fromJson(anchorString,type)
    }
}