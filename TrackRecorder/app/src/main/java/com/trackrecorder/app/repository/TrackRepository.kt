package com.trackrecorder.app.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.trackrecorder.app.dao.LocationPointDao
import com.trackrecorder.app.dao.TrackDao
import com.trackrecorder.app.models.LocationPoint
import com.trackrecorder.app.models.Track
import com.trackrecorder.app.utils.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TrackRepository(application: Application) {
    private val trackDao: TrackDao
    private val locationPointDao: LocationPointDao
    
    val allTracks: LiveData<List<Track>>
    
    init {
        val database = AppDatabase.getDatabase(application)
        trackDao = database.trackDao()
        locationPointDao = database.locationPointDao()
        allTracks = trackDao.getAllTracks()
    }
    
    suspend fun getActiveTrack(): Track? = withContext(Dispatchers.IO) {
        trackDao.getActiveTrack()
    }
    
    suspend fun insertTrack(track: Track): Long = withContext(Dispatchers.IO) {
        trackDao.insert(track)
    }
    
    suspend fun updateTrack(track: Track) = withContext(Dispatchers.IO) {
        trackDao.update(track)
    }
    
    suspend fun deleteTrack(track: Track) = withContext(Dispatchers.IO) {
        trackDao.delete(track)
        locationPointDao.deletePointsForTrack(track.id)
    }
    
    suspend fun insertLocationPoint(point: LocationPoint): Long = withContext(Dispatchers.IO) {
        locationPointDao.insert(point)
    }
    
    suspend fun getPointsForTrack(trackId: Long): List<LocationPoint> = withContext(Dispatchers.IO) {
        locationPointDao.getPointsForTrackSync(trackId)
    }
    
    suspend fun getTrackWithPoints(trackId: Long): Pair<Track?, List<LocationPoint>> = withContext(Dispatchers.IO) {
        val track = trackDao.getTrackById(trackId)
        val points = if (track != null) {
            locationPointDao.getPointsForTrackSync(trackId)
        } else {
            emptyList()
        }
        Pair(track, points)
    }
}