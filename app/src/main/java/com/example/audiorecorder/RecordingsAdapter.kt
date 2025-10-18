package com.example.audiorecorder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RecordingsAdapter(
    private val onPlayClick: (File) -> Unit,
    private val onDeleteClick: (File) -> Unit
) : ListAdapter<File, RecordingsAdapter.RecordingViewHolder>(RecordingDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recording, parent, false)
        return RecordingViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecordingViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class RecordingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val recordingName: TextView = itemView.findViewById(R.id.recordingName)
        private val recordingDate: TextView = itemView.findViewById(R.id.recordingDate)
        private val recordingDuration: TextView = itemView.findViewById(R.id.recordingDuration)
        private val playButton: ImageButton = itemView.findViewById(R.id.playButton)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)

        fun bind(file: File) {
            recordingName.text = file.nameWithoutExtension
            
            val dateFormat = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
            recordingDate.text = dateFormat.format(Date(file.lastModified()))
            
            val fileSizeKB = file.length() / 1024
            recordingDuration.text = "${fileSizeKB} KB"
            
            playButton.setOnClickListener { onPlayClick(file) }
            deleteButton.setOnClickListener { onDeleteClick(file) }
        }
    }

    class RecordingDiffCallback : DiffUtil.ItemCallback<File>() {
        override fun areItemsTheSame(oldItem: File, newItem: File): Boolean {
            return oldItem.absolutePath == newItem.absolutePath
        }

        override fun areContentsTheSame(oldItem: File, newItem: File): Boolean {
            return oldItem.lastModified() == newItem.lastModified()
        }
    }
}
