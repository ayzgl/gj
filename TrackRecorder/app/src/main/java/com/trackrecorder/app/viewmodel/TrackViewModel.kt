package com.trackrecorder.app.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.trackrecorder.app.models.Track
import com.trackrecorder.app.repository.TrackRepository
import kotlinx.coroutines.launch

class TrackViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = TrackRepository(application)
    
    val allTracks: LiveData<List<Track>> = repository.allTracks
    
    private val _activeTrack = MutableLiveData<Track?>()
    val activeTrack: LiveData<Track?> = _activeTrack
    
    init {
        checkActiveTrack()
    }
    
    private fun checkActiveTrack() {
        viewModelScope.launch {
            _activeTrack.value = repository.getActiveTrack()
        }
    }
    
    fun insertTrack(track: Track) {
        viewModelScope.launch {
            repository.insertTrack(track)
        }
    }
    
    fun updateTrack(track: Track) {
        viewModelScope.launch {
            repository.updateTrack(track)
            if (track.id == _activeTrack.value?.id) {
                _activeTrack.value = null
            }
        }
    }
    
    fun deleteTrack(track: Track) {
        viewModelScope.launch {
            repository.deleteTrack(track)
        }
    }
    
    fun refreshActiveTrack() {
        checkActiveTrack()
    }
}