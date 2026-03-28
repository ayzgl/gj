package com.trackrecorder.app.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "location_points")
data class LocationPoint(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val trackId: Long,
    val latitude: Double,
    val longitude: Double,
    val altitude: Double,
    val speed: Float,
    val accuracy: Float,
    val timestamp: Long
) {
    constructor(trackId: Long, latitude: Double, longitude: Double, altitude: Double, 
                speed: Float, accuracy: Float) : this(
        trackId = trackId,
        latitude = latitude,
        longitude = longitude,
        altitude = altitude,
        speed = speed,
        accuracy = accuracy,
        timestamp = System.currentTimeMillis()
    )
}
