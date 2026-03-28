package com.trackrecorder.app.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.trackrecorder.app.R
import com.trackrecorder.app.adapters.TrackAdapter
import com.trackrecorder.app.viewmodel.TrackViewModel

class HistoryActivity : AppCompatActivity(), TrackAdapter.OnTrackActionListener {
    
    private val viewModel: TrackViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyText: TextView
    private lateinit var adapter: TrackAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        
        setupViews()
        setupObservers()
        setupRecyclerView()
        
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = getString(R.string.view_history)
    }
    
    private fun setupViews() {
        recyclerView = findViewById(R.id.tracks_recycler_view)
        emptyText = findViewById(R.id.empty_text)
    }
    
    private fun setupRecyclerView() {
        adapter = TrackAdapter(this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }
    
    private fun setupObservers() {
        viewModel.allTracks.observe(this, Observer { tracks ->
            if (tracks.isEmpty()) {
                recyclerView.visibility = View.GONE
                emptyText.visibility = View.VISIBLE
            } else {
                recyclerView.visibility = View.VISIBLE
                emptyText.visibility = View.GONE
                adapter.submitList(tracks)
            }
        })
    }
    
    override fun onTrackViewMap(trackId: Long) {
        val intent = Intent(this, MapActivity::class.java).apply {
            putExtra("TRACK_ID", trackId)
        }
        startActivity(intent)
    }
    
    override fun onTrackExport(trackId: Long) {
        // TODO: 实现导出GPX功能
        Toast.makeText(this, "导出功能开发中", Toast.LENGTH_SHORT).show()
    }
    
    override fun onTrackDelete(trackId: Long) {
        viewModel.allTracks.value?.find { it.id == trackId }?.let { track ->
            viewModel.deleteTrack(track)
            Toast.makeText(this, "已删除", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}