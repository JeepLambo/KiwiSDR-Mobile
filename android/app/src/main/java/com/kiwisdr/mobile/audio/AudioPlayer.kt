package com.kiwisdr.mobile.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.util.Log

class AudioPlayer(private val sampleRate: Int = 12000) {

    private var audioTrack: AudioTrack? = null
    @Volatile private var isPlaying = false

    fun start() {
        if (isPlaying) return

        val minBufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        val bufferSize = minBufferSize.coerceAtLeast(sampleRate * 2)

        try {
            audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            audioTrack?.play()
            isPlaying = true
        } catch (e: Exception) {
            Log.e("AudioPlayer", "Error initializing AudioTrack", e)
        }
    }

    fun write(pcmSamples: ShortArray) {
        if (!isPlaying || pcmSamples.isEmpty()) return
        audioTrack?.write(pcmSamples, 0, pcmSamples.size)
    }

    fun stop() {
        isPlaying = false
        try {
            audioTrack?.stop()
            audioTrack?.release()
        } catch (e: Exception) {
            Log.e("AudioPlayer", "Error stopping AudioTrack", e)
        }
        audioTrack = null
    }
}
