package com.example.icons_uwb_app.data.environments

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [UWBEnvironment::class], version = 1, exportSchema = false)
@TypeConverters(EnvironmentConverters :: class)
abstract class EnvironmentDatabase: RoomDatabase() {
    abstract fun environmentDao(): UWBEnvironmentDao

    companion object {
        @Volatile
        private var Instance: EnvironmentDatabase? = null
        fun getDatabase(context: Context) : EnvironmentDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, EnvironmentDatabase::class.java, "environment_database")
                    .build()
                    .also{ Instance = it}
            }
        }
    }
}