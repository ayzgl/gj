package com.trackrecorder.app.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.trackrecorder.app.R
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.trackrecorder.app.models.LocationPoint
import com.trackrecorder.app.repository.TrackRepository
import kotlinx.coroutines.*

class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    
    private var map: GoogleMap? = null
    private lateinit var repository: TrackRepository
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        
        val trackId = intent.getLongExtra("TRACK_ID", -1)
        if (trackId == -1L) {
            finish()
            return
        }
        
        repository = TrackRepository(application)
        
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = getString(R.string.title_activity_map)
        
        // 加载轨迹数据
        serviceScope.launch {
            val (track, points) = repository.getTrackWithPoints(trackId)
            if (track != null && points.isNotEmpty()) {
                withContext(Dispatchers.Main) {
                    displayTrackOnMap(points)
                }
            }
        }
    }
    
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map?.uiSettings?.isZoomControlsEnabled = true
        map?.uiSettings?.isCompassEnabled = true
    }
    
    private fun displayTrackOnMap(points: List<LocationPoint>) {
        if (points.isEmpty() || map == null) return
        
        val polylineOptions = PolylineOptions()
            .color(R.color.blue)
            .width(8f)
            .geodesic(true)
        
        val boundsBuilder = LatLngBounds.Builder()
        
        points.forEach { point ->
            val latLng = LatLng(point.latitude, point.longitude)
            polylineOptions.add(latLng)
            boundsBuilder.include(latLng)
        }
        
        map?.addPolyline(polylineOptions)
        
        // 添加起点和终点标记
        val startPoint = points.first()
        val endPoint = points.last()
        
        map?.addMarker(
            MarkerOptions()
                .position(LatLng(startPoint.latitude, startPoint.longitude))
                .title(getString(R.string.start_point))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        )
        
        if (points.size > 1) {
            map?.addMarker(
                MarkerOptions()
                    .position(LatLng(endPoint.latitude, endPoint.longitude))
                    .title(getString(R.string.end_point))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            )
        }
        
        // 调整地图视野
        try {
            val bounds = boundsBuilder.build()
            val padding = 100
            val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding)
            map?.animateCamera(cameraUpdate)
        } catch (e: Exception) {
            // 如果只有一个点，直接定位到该点
            val firstPoint = points.first()
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                LatLng(firstPoint.latitude, firstPoint.longitude),
                15f
            )
            map?.animateCamera(cameraUpdate)
        }
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
    
    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}