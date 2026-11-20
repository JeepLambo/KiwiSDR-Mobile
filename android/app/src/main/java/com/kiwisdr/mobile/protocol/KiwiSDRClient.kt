package com.kiwisdr.mobile.protocol

import android.util.Log
import com.kiwisdr.mobile.audio.AudioPlayer
import com.kiwisdr.mobile.audio.ImaAdpcmDecoder
import okhttp3.*
import okio.ByteString
import java.util.concurrent.TimeUnit

class KiwiSDRClient(
    private val listener: KiwiListener
) {
    interface KiwiListener {
        fun onConnected()
        fun onDisconnected(reason: String)
        fun onWaterfallFrame(spectrum: ByteArray)
        fun onError(error: String)
    }

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .build()

    private var sndWebSocket: WebSocket? = null
    private var wfWebSocket: WebSocket? = null

    private val adpcmDecoder = ImaAdpcmDecoder()
    private var audioPlayer: AudioPlayer? = null

    @Volatile var isConnected = false
        private set

    var currentFreqKhz: Double = 14200.0
    var currentMode: String = "usb"
    var zoomLevel: Int = 0

    fun connect(serverAddress: String) {
        disconnect()

        val cleanHost = serverAddress.replace("http://", "").replace("https://", "").trimEnd('/')
        val timestamp = System.currentTimeMillis() / 1000

        val sndUrl = "ws://$cleanHost/$timestamp/SND"
        val wfUrl = "ws://$cleanHost/$timestamp/W/F"

        val requestSnd = Request.Builder().url(sndUrl).build()
        val requestWf = Request.Builder().url(wfUrl).build()

        audioPlayer = AudioPlayer(12000)
        audioPlayer?.start()
        adpcmDecoder.reset()

        sndWebSocket = okHttpClient.newWebSocket(requestSnd, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d("KiwiClient", "SND WebSocket Connected")
                isConnected = true
                
                // Initialize audio stream & set parameters
                webSocket.send("SET auth t=kiwi p=")
                webSocket.send("SET mod=$currentMode low_cut=-300 high_cut=3000 freq=$currentFreqKhz")
                webSocket.send("SET compression=1")
                webSocket.send("SET gen=0")
                
                listener.onConnected()
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                // Parse text control messages
                if (text.startsWith("audio_rate=")) {
                    val rate = text.substringAfter("audio_rate=").toIntOrNull() ?: 12000
                    Log.d("KiwiClient", "Server audio rate: $rate")
                }
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                val data = bytes.toByteArray()
                if (data.size > 4 && data[0] == 'A'.code.toByte() && data[1] == 'U'.code.toByte() && data[2] == 'D'.code.toByte()) {
                    // AUD packet: header is 4 bytes 'AUD' + flags
                    val pcm = adpcmDecoder.decodeFrame(data, 4, data.size - 4)
                    audioPlayer?.write(pcm)
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e("KiwiClient", "SND Failure", t)
                isConnected = false
                listener.onError(t.message ?: "Connection failed")
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                isConnected = false
                listener.onDisconnected(reason)
            }
        })

        wfWebSocket = okHttpClient.newWebSocket(requestWf, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d("KiwiClient", "WF WebSocket Connected")
                webSocket.send("SET auth t=kiwi p=")
                webSocket.send("SET zoom=$zoomLevel start=0")
                webSocket.send("SET max_db=-30 min_db=-110")
                webSocket.send("SET wf_speed=1")
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                val data = bytes.toByteArray()
                if (data.size > 4 && data[0] == 'W'.code.toByte() && data[1] == 'F'.code.toByte() && data[2] == 'R'.code.toByte()) {
                    // WFR packet contains spectrum FFT bins
                    val spectrum = data.copyOfRange(4, data.size)
                    listener.onWaterfallFrame(spectrum)
                }
            }
        })
    }

    fun setFrequencyAndMode(freqKhz: Double, mode: String) {
        this.currentFreqKhz = freqKhz
        this.currentMode = mode
        sndWebSocket?.send("SET mod=$mode low_cut=-300 high_cut=3000 freq=$freqKhz")
    }

    fun setZoom(zoom: Int) {
        this.zoomLevel = zoom
        wfWebSocket?.send("SET zoom=$zoom start=0")
    }

    fun disconnect() {
        isConnected = false
        sndWebSocket?.close(1000, "User disconnected")
        wfWebSocket?.close(1000, "User disconnected")
        sndWebSocket = null
        wfWebSocket = null
        audioPlayer?.stop()
        audioPlayer = null
    }
}
