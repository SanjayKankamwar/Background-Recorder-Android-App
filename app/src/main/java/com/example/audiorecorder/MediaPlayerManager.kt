package com.example.audiorecorder

import android.content.Context
import android.media.MediaPlayer
import java.io.File

class MediaPlayerManager(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private var currentFile: File? = null

    fun play(file: File, onCompletion: () -> Unit) {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.stop()
        }
        mediaPlayer?.release()

        currentFile = file
        mediaPlayer = MediaPlayer().apply {
            setDataSource(file.absolutePath)
            prepare()
            start()
            setOnCompletionListener { 
                onCompletion()
                this@MediaPlayerManager.currentFile = null
            }
        }
    }

    fun stop() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        currentFile = null
    }

    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying ?: false
    }

    fun getCurrentFile(): File? {
        return currentFile
    }

    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
