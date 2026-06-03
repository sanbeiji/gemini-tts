package com.example.geminittsdialects.api

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DialectPcmPlayer {

    private val sampleRate = 24000 // Gemini default spec
    private var activeAudioTrack: AudioTrack? = null
    
    suspend fun playBase64Pcm(base64Data: String) {
        val audioBytes = Base64.decode(base64Data, Base64.DEFAULT)
        playRawPcm(audioBytes)
    }

    fun stop() {
        try {
            activeAudioTrack?.let {
                if (it.playState == AudioTrack.PLAYSTATE_PLAYING) {
                    it.stop()
                }
                it.release()
            }
            activeAudioTrack = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Writes raw ByteArray PCM frames to an AudioTrack channel
     */
    suspend fun playRawPcm(audioBytes: ByteArray) = withContext(Dispatchers.IO) {
        try {
            stop() // Stop any previous playback

            // Calculate optimal buffer sizing
            val minBufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            
            // Build native Android linear PCM hardware track
            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(minBufferSize.coerceAtLeast(audioBytes.size))
                .setTransferMode(AudioTrack.MODE_STATIC) // Static supports complete buffer load
                .build()
            
            activeAudioTrack = audioTrack

            // Static playback: load entire buffer array, play, and clean resources on completion
            audioTrack.write(audioBytes, 0, audioBytes.size)
            audioTrack.play()
            
            // Block thread waiting for completion to safely release stream resources
            val durationMs = ((audioBytes.size / 2.0) / sampleRate) * 1000
            Thread.sleep(durationMs.toLong() + 200)
            
            audioTrack.stop()
            audioTrack.release()
            if (activeAudioTrack == audioTrack) {
                activeAudioTrack = null
            }
            
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
