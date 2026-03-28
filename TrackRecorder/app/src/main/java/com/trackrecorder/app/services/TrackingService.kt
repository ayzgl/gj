package com.trackrecorder.app.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.trackrecorder.app.R
import com.trackrecorder.app.models.LocationPoint
import com.trackrecorder.app.models.Track
import com.trackrecorder.app.repository.TrackRepository
import kotlinx.coroutines.*
import kotlin.math.*

class TrackingService : Service(), LocationListener {
    
    private lateinit var locationManager: LocationManager
    private lateinit var repository: TrackRepository
    private var currentTrack: Track? = null
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    private var lastLocation: Location? = null
    private var totalDistance = 0f
    
    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "tracking_channel"
        private const val CHANNEL_NAME = "轨迹记录"
        
        const val ACTION_START_TRACKING = "START_TRACKING"
        const val ACTION_STOP_TRACKING = "STOP_TRACKING"
        const val EXTRA_TRACK_NAME = "TRACK_NAME"
        
        var isTracking = false
            private set
    }
    
    override fun onCreate() {
        super.onCreate()
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        repository = TrackRepository(application)
        createNotificationChannel()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_TRACKING -> {
                val trackName = intent.getStringExtra(EXTRA_TRACK_NAME) ?: "Track ${System.currentTimeMillis()}"
                startTracking(trackName)
            }
            ACTION_STOP_TRACKING -> {
                stopTracking()
            }
        }
        return START_STICKY
    }
    
    private fun startTracking(trackName: String) {
        if (isTracking) return
        
        isTracking = true
        totalDistance = 0f
        lastLocation = null
        
        serviceScope.launch {
            currentTrack = Track.createNew(trackName)
            val trackId = repository.insertTrack(currentTrack!!)
            currentTrack = currentTrack!!.copy(id = trackId)
            
            startForeground(NOTIFICATION_ID, createNotification())
            
            try {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    3000, // 3秒
                    10f,  // 10米
                    this@TrackingService
                )
            } catch (e: SecurityException) {
                stopTracking()
            }
        }
    }
    
    private fun stopTracking() {
        if (!isTracking) return
        
        isTracking = false
        locationManager.removeUpdates(this)
        
        serviceScope.launch {
            currentTrack?.let { track ->
                val updatedTrack = track.copy(
                    endTime = System.currentTimeMillis(),
                    totalDistance = totalDistance,
                    duration = System.currentTimeMillis() - track.startTime
                )
                repository.updateTrack(updatedTrack)
            }
            
            currentTrack = null
            lastLocation = null
            stopForeground(true)
            stopSelf()
        }
    }
    
    override fun onLocationChanged(location: Location) {
        serviceScope.launch {
            currentTrack?.let { track ->
                val locationPoint = LocationPoint(
                    trackId = track.id,
                    latitude = location.latitude,
                    longitude = location.longitude,
                    altitude = location.altitude,
                    speed = if (location.hasSpeed()) location.speed else 0f,
                    accuracy = location.accuracy
                )
                
                repository.insertLocationPoint(locationPoint)
                
                // 计算距离
                lastLocation?.let { last ->
                    val distance = last.distanceTo(location)
                    totalDistance += distance
                }
                lastLocation = location
                
                // 更新通知
                updateNotification()
            }
        }
    }
    
    @Deprecated("Deprecated in Java")
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
    
    override fun onProviderEnabled(provider: String) {}
    
    override fun onProviderDisabled(provider: String) {}
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "轨迹记录通知"
            }
            
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun createNotification(): Notification {
        val stopIntent = Intent(this, TrackingService::class.java).apply {
            action = ACTION_STOP_TRACKING
        }
        val stopPendingIntent = PendingIntent.getService(
            this,
            0,
            stopIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.tracking_notification_title))
            .setContentText(getString(R.string.tracking_notification_content))
            .setSmallIcon(R.drawable.ic_notification)
            .setOngoing(true)
            .addAction(0, getString(R.string.stop_tracking), stopPendingIntent)
            .build()
    }
    
    private fun updateNotification() {
        val notification = createNotification()
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}