package com.example.audiorecorder

import android.content.Context
import android.media.MediaPlayer
import java.io.File

class MediaPlayerManager(private val context: Context) {
    
    private var mediaPlayer: MediaPlayer? = null
    private var currentFile: File? = null
    
    fun play(file: File, onCompletion: () -> Unit = {}) {
        stop()
        
        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(file.absolutePath)
                prepare()
                start()
                setOnCompletionListener {
                    onCompletion()
                    release()
                    mediaPlayer = null
                }
            }
            currentFile = file
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    fun pause() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
            }
        }
    }
    
    fun resume() {
        mediaPlayer?.let {
            if (!it.isPlaying) {
                it.start()
            }
        }
    }
    
    fun stop() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
        }
        mediaPlayer = null
        currentFile = null
    }
    
    fun isPlaying(): Boolean = mediaPlayer?.isPlaying ?: false
    
    fun getCurrentFile(): File? = currentFile
    
    fun release() {
        stop()
    }
}
