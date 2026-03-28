package com.trackrecorder.app.utils

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.Room
import android.content.Context
import com.trackrecorder.app.models.LocationPoint
import com.trackrecorder.app.models.Track
import com.trackrecorder.app.dao.TrackDao
import com.trackrecorder.app.dao.LocationPointDao

@Database(entities = [Track::class, LocationPoint::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao
    abstract fun locationPointDao(): LocationPointDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "track_recorder_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}