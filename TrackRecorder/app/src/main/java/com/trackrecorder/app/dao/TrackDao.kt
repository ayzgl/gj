package com.trackrecorder.app.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.trackrecorder.app.models.Track

@Dao
interface TrackDao {
    @Query("SELECT * FROM tracks ORDER BY startTime DESC")
    fun getAllTracks(): LiveData<List<Track>>
    
    @Query("SELECT * FROM tracks WHERE id = :trackId")
    suspend fun getTrackById(trackId: Long): Track?
    
    @Query("SELECT * FROM tracks WHERE endTime IS NULL")
    suspend fun getActiveTrack(): Track?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(track: Track): Long
    
    @Update
    suspend fun update(track: Track)
    
    @Delete
    suspend fun delete(track: Track)
}