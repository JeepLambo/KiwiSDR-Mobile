package com.kiwisdr.mobile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kiwisdr.mobile.protocol.KiwiSDRClient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KiwiMainScreen() {
    var serverUrl by remember { mutableStateOf("rx.kiwisdr.com:8073") }
    var isConnected by remember { mutableStateOf(false) }
    var statusText by remember { mutableStateOf("Disconnected") }

    var freqKhz by remember { mutableStateOf(14200.0) }
    var mode by remember { mutableStateOf("usb") }
    var spectrumData by remember { mutableStateOf<ByteArray?>(null) }

    val client = remember {
        KiwiSDRClient(object : KiwiSDRClient.KiwiListener {
            override fun onConnected() {
                isConnected = true
                statusText = "Connected to $serverUrl"
            }

            override fun onDisconnected(reason: String) {
                isConnected = false
                statusText = "Disconnected: $reason"
            }

            override fun onWaterfallFrame(spectrum: ByteArray) {
                spectrumData = spectrum
            }

            override fun onError(error: String) {
                isConnected = false
                statusText = "Error: $error"
            }
        })
    }

    DisposableEffect(Unit) {
        onDispose {
            client.disconnect()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF111827))
    ) {
        // Top Server Address Bar
        Surface(
            color = Color(0xFF1F2937),
            tonalElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = serverUrl,
                    onValueChange = { serverUrl = it },
                    label = { Text("KiwiSDR Server / IP", color = Color.Gray) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF10B981)
                    ),
                    modifier = Modifier.weight(1f)
                )

                Button(
                    onClick = {
                        if (isConnected) {
                            client.disconnect()
                            isConnected = false
                        } else {
                            statusText = "Connecting..."
                            client.connect(serverUrl)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isConnected) Color(0xFFEF4444) else Color(0xFF10B981)
                    )
                ) {
                    Icon(
                        imageVector = if (isConnected) Icons.Default.Stop else Icons.Default.PlayArrow,
                        contentDescription = if (isConnected) "Disconnect" else "Connect"
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(if (isConnected) "Disconnect" else "Connect")
                }
            }
        }

        // Status bar
        Text(
            text = statusText,
            color = Color.Gray,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )

        // Main Frequency & Tuning Controls
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = String.format("%.2f kHz", freqKhz),
                color = Color(0xFF10B981),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            // Step Tuning Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf(-10.0, -1.0, +1.0, +10.0).forEach { step ->
                    OutlinedButton(
                        onClick = {
                            freqKhz = (freqKhz + step).coerceAtLeast(10.0)
                            if (isConnected) client.setFrequencyAndMode(freqKhz, mode)
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                    ) {
                        Text(if (step > 0) "+${step.toInt()}k" else "${step.toInt()}k")
                    }
                }
            }

            // Mode Selector Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("am", "lsb", "usb", "cw", "nbfm").forEach { m ->
                    FilterChip(
                        selected = mode == m,
                        onClick = {
                            mode = m
                            if (isConnected) client.setFrequencyAndMode(freqKhz, mode)
                        },
                        label = { Text(m.uppercase()) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF10B981),
                            selectedLabelColor = Color.Black
                        )
                    )
                }
            }

            // Band Shortcuts
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val bands = listOf(
                    "80m" to 3700.0,
                    "40m" to 7100.0,
                    "20m" to 14200.0,
                    "15m" to 21200.0,
                    "10m" to 28400.0,
                    "AM Broadcast" to 1000.0
                )
                bands.forEach { (name, targetFreq) ->
                    SuggestionChip(
                        onClick = {
                            freqKhz = targetFreq
                            if (isConnected) client.setFrequencyAndMode(freqKhz, mode)
                        },
                        label = { Text(name, color = Color.White) }
                    )
                }
            }
        }

        // Waterfall Display
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .padding(4.dp)
        ) {
            WaterfallView(
                spectrumFrame = spectrumData,
                centerFreqKhz = freqKhz,
                currentMode = mode,
                onTuneFrequency = { newKhz ->
                    freqKhz = newKhz
                    if (isConnected) client.setFrequencyAndMode(freqKhz, mode)
                }
            )
        }
    }
}
