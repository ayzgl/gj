package com.trackrecorder.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.trackrecorder.app.R
import com.trackrecorder.app.models.Track
import java.text.SimpleDateFormat
import java.util.*

class TrackAdapter(private val listener: OnTrackActionListener) : 
    ListAdapter<Track, TrackAdapter.TrackViewHolder>(TrackDiffCallback()) {
    
    interface OnTrackActionListener {
        fun onTrackViewMap(trackId: Long)
        fun onTrackExport(trackId: Long)
        fun onTrackDelete(trackId: Long)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_track, parent, false)
        return TrackViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameText: TextView = itemView.findViewById(R.id.track_name)
        private val dateText: TextView = itemView.findViewById(R.id.track_date)
        private val distanceText: TextView = itemView.findViewById(R.id.track_distance)
        private val durationText: TextView = itemView.findViewById(R.id.track_duration)
        private val viewMapButton: Button = itemView.findViewById(R.id.view_map_button)
        private val exportButton: Button = itemView.findViewById(R.id.export_button)
        private val deleteButton: Button = itemView.findViewById(R.id.delete_button)
        
        fun bind(track: Track) {
            nameText.text = track.name
            
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            dateText.text = dateFormat.format(Date(track.startTime))
            
            distanceText.text = itemView.context.getString(
                R.string.track_distance, 
                track.totalDistance / 1000
            )
            
            durationText.text = itemView.context.getString(
                R.string.track_duration,
                track.getDurationText()
            )
            
            viewMapButton.setOnClickListener {
                listener.onTrackViewMap(track.id)
            }
            
            exportButton.setOnClickListener {
                listener.onTrackExport(track.id)
            }
            
            deleteButton.setOnClickListener {
                listener.onTrackDelete(track.id)
            }
        }
    }
    
    class TrackDiffCallback : DiffUtil.ItemCallback<Track>() {
        override fun areItemsTheSame(oldItem: Track, newItem: Track): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: Track, newItem: Track): Boolean {
            return oldItem == newItem
        }
    }
}