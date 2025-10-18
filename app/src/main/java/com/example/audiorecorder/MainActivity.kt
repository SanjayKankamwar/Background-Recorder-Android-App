package com.example.audiorecorder

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var startButton: Button
    private lateinit var stopButton: Button
    private lateinit var statusText: TextView
    private lateinit var timerText: TextView
    private lateinit var recordingIndicator: View
    private lateinit var recordingsList: RecyclerView
    private lateinit var emptyState: View
    private lateinit var recordingCount: TextView
    
    private var isRecording = false
    private var recordingStartTime = 0L
    private val timerHandler = Handler(Looper.getMainLooper())
    private lateinit var recordingsAdapter: RecordingsAdapter
    private lateinit var mediaPlayerManager: MediaPlayerManager

    private val PERMISSION_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startButton = findViewById(R.id.startButton)
        stopButton = findViewById(R.id.stopButton)
        statusText = findViewById(R.id.statusText)
        timerText = findViewById(R.id.timerText)
        recordingIndicator = findViewById(R.id.recordingIndicator)
        recordingsList = findViewById(R.id.recordingsList)
        emptyState = findViewById(R.id.emptyState)
        recordingCount = findViewById(R.id.recordingCount)

        mediaPlayerManager = MediaPlayerManager(this)

        setupRecyclerView()
        loadRecordings()

        // Check and request permissions
        if (!hasPermissions()) {
            requestPermissions()
        }

        startButton.setOnClickListener {
            if (hasPermissions()) {
                startRecordingService()
            } else {
                Toast.makeText(this, "Permissions not granted", Toast.LENGTH_SHORT).show()
                requestPermissions()
            }
        }

        stopButton.setOnClickListener {
            stopRecordingService()
        }
    }

    private fun setupRecyclerView() {
        recordingsAdapter = RecordingsAdapter(
            onPlayClick = { file -> playRecording(file) },
            onDeleteClick = { file -> deleteRecording(file) }
        )
        recordingsList.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = recordingsAdapter
        }
    }

    private fun loadRecordings() {
        val recordingsDir = getExternalFilesDir(null)
        val files = recordingsDir?.listFiles { file -> 
            file.extension == "m4a" 
        }?.sortedByDescending { it.lastModified() } ?: emptyList()
        
        recordingsAdapter.submitList(files)
        recordingCount.text = files.size.toString()
        
        if (files.isEmpty()) {
            emptyState.visibility = View.VISIBLE
            recordingsList.visibility = View.GONE
        } else {
            emptyState.visibility = View.GONE
            recordingsList.visibility = View.VISIBLE
        }
    }

    private fun playRecording(file: File) {
        if (mediaPlayerManager.isPlaying() && mediaPlayerManager.getCurrentFile() == file) {
            mediaPlayerManager.stop()
            Toast.makeText(this, "Playback stopped", Toast.LENGTH_SHORT).show()
        } else {
            mediaPlayerManager.play(file) {
                Toast.makeText(this, "Playback completed", Toast.LENGTH_SHORT).show()
            }
            Toast.makeText(this, "Playing: ${file.name}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteRecording(file: File) {
        if (mediaPlayerManager.getCurrentFile() == file) {
            mediaPlayerManager.stop()
        }
        
        if (file.delete()) {
            Toast.makeText(this, "Recording deleted", Toast.LENGTH_SHORT).show()
            loadRecordings()
        } else {
            Toast.makeText(this, "Failed to delete recording", Toast.LENGTH_SHORT).show()
        }
    }

    private fun hasPermissions(): Boolean {
        val recordAudio = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

        return recordAudio && notification
    }

    private fun requestPermissions() {
        val permissions = mutableListOf(Manifest.permission.RECORD_AUDIO)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        ActivityCompat.requestPermissions(
            this,
            permissions.toTypedArray(),
            PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startRecordingService() {
        val intent = Intent(this, AudioRecordingService::class.java)
        ContextCompat.startForegroundService(this, intent)
        
        isRecording = true
        recordingStartTime = System.currentTimeMillis()
        
        startButton.isEnabled = false
        stopButton.isEnabled = true
        statusText.text = "Recording..."
        recordingIndicator.visibility = View.VISIBLE
        
        startTimer()
        
        Toast.makeText(this, "Recording started", Toast.LENGTH_SHORT).show()
    }

    private fun stopRecordingService() {
        val intent = Intent(this, AudioRecordingService::class.java)
        stopService(intent)
        
        isRecording = false
        
        startButton.isEnabled = true
        stopButton.isEnabled = false
        statusText.text = "Ready to record"
        recordingIndicator.visibility = View.GONE
        timerText.text = "00:00:00"
        
        stopTimer()
        loadRecordings()
        
        Toast.makeText(this, "Recording stopped", Toast.LENGTH_SHORT).show()
    }

    private fun startTimer() {
        timerHandler.post(object : Runnable {
            override fun run() {
                if (isRecording) {
                    val elapsed = System.currentTimeMillis() - recordingStartTime
                    val seconds = (elapsed / 1000) % 60
                    val minutes = (elapsed / (1000 * 60)) % 60
                    val hours = (elapsed / (1000 * 60 * 60))
                    
                    timerText.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)
                    timerHandler.postDelayed(this, 1000)
                }
            }
        })
    }

    private fun stopTimer() {
        timerHandler.removeCallbacksAndMessages(null)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTimer()
        mediaPlayerManager.release()
    }
}
