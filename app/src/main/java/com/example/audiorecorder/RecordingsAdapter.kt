package com.example.audiorecorder

import android.media.MediaMetadataRetriever
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class RecordingsAdapter(
    private val onPlayClick: (File) -> Unit,
    private val onDeleteClick: (File) -> Unit
) : ListAdapter<File, RecordingsAdapter.RecordingViewHolder>(FileDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recording, parent, false)
        return RecordingViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecordingViewHolder, position: Int) {
        val file = getItem(position)
        holder.bind(file, onPlayClick, onDeleteClick)
    }

    class RecordingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.recordingName)
        private val dateTextView: TextView = itemView.findViewById(R.id.recordingDate)
        private val durationTextView: TextView = itemView.findViewById(R.id.recordingDuration)
        private val playButton: MaterialButton = itemView.findViewById(R.id.playButton)
        private val deleteButton: MaterialButton = itemView.findViewById(R.id.deleteButton)

        fun bind(file: File, onPlayClick: (File) -> Unit, onDeleteClick: (File) -> Unit) {
            nameTextView.text = file.name
            dateTextView.text = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(file.lastModified()))
            durationTextView.text = getAudioFileDuration(file.absolutePath)

            playButton.setOnClickListener { onPlayClick(file) }
            deleteButton.setOnClickListener { onDeleteClick(file) }
        }

        private fun getAudioFileDuration(filePath: String): String {
            return try {
                val retriever = MediaMetadataRetriever()
                retriever.setDataSource(filePath)
                val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                val timeInMillis = time?.toLongOrNull() ?: 0
                retriever.release()

                val minutes = TimeUnit.MILLISECONDS.toMinutes(timeInMillis)
                val seconds = TimeUnit.MILLISECONDS.toSeconds(timeInMillis) % 60
                String.format("%02d:%02d", minutes, seconds)
            } catch (e: Exception) {
                "--:--" // Return a placeholder on error
            }
        }
    }

    class FileDiffCallback : DiffUtil.ItemCallback<File>() {
        override fun areItemsTheSame(oldItem: File, newItem: File): Boolean {
            return oldItem.absolutePath == newItem.absolutePath
        }

        override fun areContentsTheSame(oldItem: File, newItem: File): Boolean {
            // This might be too simple; for more robust checking, you might compare file size or last modified time
            return oldItem.length() == newItem.length() && oldItem.lastModified() == newItem.lastModified()
        }
    }
}
