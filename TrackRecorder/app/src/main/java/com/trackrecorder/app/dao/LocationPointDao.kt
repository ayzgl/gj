package com.trackrecorder.app.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.trackrecorder.app.models.LocationPoint

@Dao
interface LocationPointDao {
    @Query("SELECT * FROM location_points WHERE trackId = :trackId ORDER BY timestamp ASC")
    fun getPointsForTrack(trackId: Long): LiveData<List<LocationPoint>>
    
    @Query("SELECT * FROM location_points WHERE trackId = :trackId ORDER BY timestamp ASC")
    suspend fun getPointsForTrackSync(trackId: Long): List<LocationPoint>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(point: LocationPoint): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(points: List<LocationPoint>)
    
    @Query("DELETE FROM location_points WHERE trackId = :trackId")
    suspend fun deletePointsForTrack(trackId: Long)
}