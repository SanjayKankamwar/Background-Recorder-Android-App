package com.example.audiorecorder

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.MediaRecorder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import java.io.File
import java.io.IOException

class AudioRecordingService : Service() {

    private var mediaRecorder: MediaRecorder? = null
    private var isRecording = false

    companion object {
        private const val CHANNEL_ID = "AudioRecordingServiceChannel"
        private const val NOTIFICATION_ID = 1
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, createNotification())
        startRecording()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopRecording()
    }

    private fun startRecording() {
        val file = File(getExternalFilesDir(null), "recording_${System.currentTimeMillis()}.m4a")

        mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(this)
        } else {
            MediaRecorder()
        }

        mediaRecorder?.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(file.absolutePath)
            try {
                prepare()
                start()
                isRecording = true
            } catch (e: IOException) {
                // Handle exception
            }
        }
    }

    private fun stopRecording() {
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null
        isRecording = false
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Audio Recording Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Audio Recorder")
            .setContentText("Recording in background...")
            .setSmallIcon(R.drawable.ic_mic)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
