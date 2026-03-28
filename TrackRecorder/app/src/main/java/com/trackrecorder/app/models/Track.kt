package com.trackrecorder.app.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.trackrecorder.app.utils.Converters
import java.util.Date

@Entity(tableName = "tracks")
@TypeConverters(Converters::class)
data class Track(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val startTime: Long,
    val endTime: Long? = null,
    val totalDistance: Float = 0f,
    val duration: Long = 0,
    val createdAt: Date = Date()
) {
    fun isActive(): Boolean = endTime == null
    
    fun getDurationText(): String {
        val durationMs = if (endTime != null) {
            endTime!! - startTime
        } else {
            System.currentTimeMillis() - startTime
        }
        
        val seconds = (durationMs / 1000).toInt()
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        
        return String.format("%02d:%02d:%02d", hours, minutes, secs)
    }
    
    companion object {
        fun createNew(name: String = "Track ${System.currentTimeMillis()}"): Track {
            return Track(
                name = name,
                startTime = System.currentTimeMillis()
            )
        }
    }
}
