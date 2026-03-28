package com.trackrecorder.app.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.trackrecorder.app.R
import com.trackrecorder.app.services.TrackingService
import com.trackrecorder.app.viewmodel.TrackViewModel
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    
    private val viewModel: TrackViewModel by viewModels()
    
    private lateinit var statusText: TextView
    private lateinit var distanceText: TextView
    private lateinit var durationText: TextView
    private lateinit var startButton: Button
    private lateinit var stopButton: Button
    private lateinit var historyButton: Button
    
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                startTracking()
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                startTracking()
            }
            else -> {
                Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        setupViews()
        setupObservers()
        setupClickListeners()
    }
    
    private fun setupViews() {
        statusText = findViewById(R.id.status_text)
        distanceText = findViewById(R.id.distance_text)
        durationText = findViewById(R.id.duration_text)
        startButton = findViewById(R.id.start_button)
        stopButton = findViewById(R.id.stop_button)
        historyButton = findViewById(R.id.history_button)
        
        setSupportActionBar(findViewById(R.id.toolbar))
        title = getString(R.string.app_name)
    }
    
    private fun setupObservers() {
        viewModel.activeTrack.observe(this, Observer { track ->
            updateUI(track != null)
            if (track != null) {
                updateTrackInfo(track)
            }
        })
    }
    
    private fun setupClickListeners() {
        startButton.setOnClickListener {
            checkLocationPermission()
        }
        
        stopButton.setOnClickListener {
            stopTracking()
        }
        
        historyButton.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }
    }
    
    private fun checkLocationPermission() {
        val permissions = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }
        
        val allPermissionsGranted = permissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
        
        if (allPermissionsGranted) {
            startTracking()
        } else {
            locationPermissionRequest.launch(permissions.toTypedArray())
        }
    }
    
    private fun startTracking() {
        val intent = Intent(this, TrackingService::class.java).apply {
            action = TrackingService.ACTION_START_TRACKING
            putExtra(TrackingService.EXTRA_TRACK_NAME, "Track ${SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())}")
        }
        startService(intent)
        viewModel.refreshActiveTrack()
    }
    
    private fun stopTracking() {
        val intent = Intent(this, TrackingService::class.java).apply {
            action = TrackingService.ACTION_STOP_TRACKING
        }
        startService(intent)
        viewModel.refreshActiveTrack()
    }
    
    private fun updateUI(isTracking: Boolean) {
        if (isTracking) {
            statusText.text = getString(R.string.tracking_active)
            startButton.visibility = View.GONE
            stopButton.visibility = View.VISIBLE
            distanceText.visibility = View.VISIBLE
            durationText.visibility = View.VISIBLE
        } else {
            statusText.text = getString(R.string.tracking_inactive)
            startButton.visibility = View.VISIBLE
            stopButton.visibility = View.GONE
            distanceText.visibility = View.GONE
            durationText.visibility = View.GONE
        }
    }
    
    private fun updateTrackInfo(track: com.trackrecorder.app.models.Track) {
        distanceText.text = getString(R.string.distance_traveled, track.totalDistance / 1000)
        durationText.text = getString(R.string.duration, track.getDurationText())
    }
    
    override fun onResume() {
        super.onResume()
        viewModel.refreshActiveTrack()
    }
}