package com.kiwisdr.mobile.ui

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput

@Composable
fun WaterfallView(
    spectrumFrame: ByteArray?,
    centerFreqKhz: Double,
    currentMode: String,
    onTuneFrequency: (Double) -> Unit,
    modifier: Modifier = Modifier
) {
    val waterfallWidth = 1024
    val waterfallHeight = 400

    var bitmap by remember {
        mutableStateOf(Bitmap.createBitmap(waterfallWidth, waterfallHeight, Bitmap.Config.ARGB_8888))
    }

    // Color palette mapping 0..255 amplitude to ARGB color
    val palette = remember {
        IntArray(256) { i ->
            when {
                i < 64 -> Color.rgb(0, 0, (i * 4))
                i < 128 -> Color.rgb(0, ((i - 64) * 4), 255 - ((i - 64) * 4))
                i < 192 -> Color.rgb(((i - 128) * 4), 255, 0)
                else -> Color.rgb(255, 255 - ((i - 192) * 4), 0)
            }
        }
    }

    // Shift waterfall down 1 pixel and draw new top line when a frame arrives
    LaunchedEffect(spectrumFrame) {
        spectrumFrame?.let { frame ->
            if (frame.isEmpty()) return@let

            val newBitmap = Bitmap.createBitmap(waterfallWidth, waterfallHeight, Bitmap.Config.ARGB_8888)
            val canvas = android.graphics.Canvas(newBitmap)

            // Draw previous bitmap shifted down by 1 pixel
            canvas.drawBitmap(bitmap, 0f, 1f, null)

            // Render top row pixels
            val topPixels = IntArray(waterfallWidth) { col ->
                val sampleIdx = (col * frame.size) / waterfallWidth
                val amp = frame.getOrElse(sampleIdx) { 0 }.toInt() and 0xFF
                palette[amp]
            }

            newBitmap.setPixels(topPixels, 0, waterfallWidth, 0, 0, waterfallWidth, 1)
            bitmap = newBitmap
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(centerFreqKhz) {
                detectTapGestures { offset ->
                    // Tap on waterfall to tune frequency
                    val fraction = offset.x / size.width
                    val tunedKhz = (centerFreqKhz - 15.0) + (fraction * 30.0)
                    onTuneFrequency(tunedKhz)
                }
            }
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        // Draw waterfall bitmap stretched to canvas
        drawContext.canvas.nativeCanvas.drawBitmap(
            bitmap,
            null,
            android.graphics.RectF(0f, 0f, canvasWidth, canvasHeight),
            null
        )

        // Draw active tuning line in center
        val centerLineX = canvasWidth / 2f
        drawLine(
            color = androidx.compose.ui.graphics.Color.Red,
            start = Offset(centerLineX, 0f),
            end = Offset(centerLineX, canvasHeight),
            strokeWidth = 3f
        )
    }
}
