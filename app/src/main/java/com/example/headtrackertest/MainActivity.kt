package com.example.headtrackertest

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.Spatializer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.headtrackertest.ui.theme.HeadtrackerTestTheme
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.sin

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val audioManager = getSystemService(AudioManager::class.java)
        val spatializer = audioManager.spatializer

        setContent {
            HeadtrackerTestTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SpatialAudioDemo(
                        spatializer = spatializer,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun SpatialAudioDemo(spatializer: Spatializer, modifier: Modifier = Modifier) {
    var isPlaying by remember { mutableStateOf(false) }
    var statusText by remember { mutableStateOf("Loading spatial audio status...") }
    var headTrackerData by remember { mutableStateOf("Head tracker data: Not available") }
    var playbackStatus by remember { mutableStateOf("") }


    var audioPlayer by remember { mutableStateOf<SpatialAudioPlayer?>(null) }


    LaunchedEffect(spatializer) {
        statusText = buildStatusText(spatializer)


        if (spatializer.isAvailable) {
            audioPlayer = SpatialAudioPlayer(spatializer)
        }
    }


    LaunchedEffect(isPlaying) {
        while (isPlaying && spatializer.isHeadTrackerAvailable) {

            headTrackerData = "Head tracker active - Audio responding to head movement"
            delay(100)
        }
    }

    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Spatial Audio Status",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = statusText, style = MaterialTheme.typography.bodyMedium)
            }
        }


        if (playbackStatus.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (playbackStatus.contains("Error"))
                        MaterialTheme.colorScheme.errorContainer
                    else MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Playback Status",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = playbackStatus, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }


        if (spatializer.isHeadTrackerAvailable) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Head Tracker Data",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = headTrackerData, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    if (!isPlaying) {
                        val result =
                            audioPlayer?.startPlayingTone() ?: "AudioPlayer not initialized"
                        playbackStatus = result
                        if (result.contains("Started playing")) {
                            isPlaying = true
                        }
                    }
                },
                enabled = spatializer.isAvailable && !isPlaying
            ) {
                Text("Play Test Audio")
            }

            Button(
                onClick = {
                    audioPlayer?.stopPlaying()
                    isPlaying = false
                    playbackStatus = "Stopped playing"
                },
                enabled = isPlaying
            ) {
                Text("Stop")
            }
        }
    }
}

class SpatialAudioPlayer(private val spatializer: Spatializer) {
    private var audioTrack: AudioTrack? = null
    private var isPlaying = false
    private var canSpatialized = false

    fun startPlayingTone(): String {
        if (isPlaying) return "Already playing"

        try {

            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setSpatializationBehavior(AudioAttributes.SPATIALIZATION_BEHAVIOR_AUTO)
                .build()

            val audioFormat = AudioFormat.Builder()
                .setSampleRate(48000)
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setChannelMask(AudioFormat.CHANNEL_OUT_5POINT1)
                .build()


            canSpatialized = spatializer.canBeSpatialized(audioAttributes, audioFormat)

            val minBufferSize = AudioTrack.getMinBufferSize(
                48000,
                AudioFormat.CHANNEL_OUT_5POINT1,
                AudioFormat.ENCODING_PCM_16BIT
            )

            audioTrack = AudioTrack.Builder()
                .setAudioAttributes(audioAttributes)
                .setAudioFormat(audioFormat)
                .setBufferSizeInBytes(minBufferSize * 4)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            audioTrack?.play()
            isPlaying = true

            Thread {
                generateAudio()
            }.start()

            return "Started playing 5.1 surround tone - Spatialized: $canSpatialized"

        } catch (e: Exception) {
            e.printStackTrace()
            return "Error: ${e.message}"
        }
    }

    fun stopPlaying() {
        isPlaying = false
        audioTrack?.stop()
        audioTrack?.release()
        audioTrack = null
    }

    private fun generateAudio() {
        val sampleRate = 48000
        val bufferSize = 1024
        val channels = 6 // 5.1
        val buffer = ShortArray(bufferSize * channels)

        var phase = 0.0
        val baseFreq = 440.0 // A4音符
        val phaseIncrement = 2 * PI * baseFreq / sampleRate

        while (isPlaying) {
            // 为每个声道生成不同频率的音调
            for (i in 0 until bufferSize) {
                val sampleIndex = i * channels
                val amplitude = (Short.MAX_VALUE * 0.2).toInt().toShort()

                // 前左声道 (440Hz)
                buffer[sampleIndex] = (sin(phase) * amplitude).toInt().toShort()
                // 前右声道 (554Hz - 高四度)
                buffer[sampleIndex + 1] = (sin(phase * 1.26) * amplitude).toInt().toShort()
                // 中央声道 (330Hz - 低四度)
                buffer[sampleIndex + 2] = (sin(phase * 0.75) * amplitude).toInt().toShort()
                // LFE声道 (80Hz - 低频)
                buffer[sampleIndex + 3] = (sin(phase * 0.18) * amplitude).toInt().toShort()
                // 后左声道 (660Hz)
                buffer[sampleIndex + 4] = (sin(phase * 1.5) * amplitude).toInt().toShort()
                // 后右声道 (880Hz - 高八度)
                buffer[sampleIndex + 5] = (sin(phase * 2.0) * amplitude).toInt().toShort()

                phase += phaseIncrement
                if (phase >= 2 * PI) phase -= 2 * PI
            }

            audioTrack?.write(buffer, 0, buffer.size)
        }
    }


}


fun buildStatusText(spatializer: Spatializer): String {
    return buildString {

        val immersiveLevel = spatializer.immersiveAudioLevel
        val supportsSpatialization = immersiveLevel != Spatializer.SPATIALIZER_IMMERSIVE_LEVEL_NONE

        appendLine("📱 Device Spatialization Support: ${if (supportsSpatialization) "✅ Yes" else "❌ No"}")
        appendLine("   Immersive Level: $immersiveLevel")
        appendLine("🔌 Current Route Available: ${if (spatializer.isAvailable) "✅ Yes" else "❌ No"}")
        appendLine("⚙️ Spatializer Enabled: ${if (spatializer.isEnabled) "✅ Yes" else "❌ No"}")
        appendLine("🎯 Head Tracker Available: ${if (spatializer.isHeadTrackerAvailable) "✅ Yes" else "❌ No"}")
        appendLine("")
        when {
            !supportsSpatialization -> appendLine("❌ Device does not support spatialization")
            !spatializer.isAvailable -> appendLine("⚠️ Spatialization not available with current audio output")
            !spatializer.isEnabled -> appendLine("⚠️ Spatialization is disabled")
            !spatializer.isHeadTrackerAvailable -> appendLine("⚠️ Head tracking not available")
            else -> appendLine("🎉 Ready for spatial audio with head tracking!")
        }
    }
}