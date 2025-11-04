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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.headtrackertest.ui.theme.HeadtrackerTestTheme
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.sin

/**
 * Main activity for the Spatial Audio Head Tracker Test application.
 * 
 * This activity demonstrates the use of Android's Spatializer API for spatial audio
 * and head tracking functionality. It provides a UI to check device capabilities
 * and play test audio with spatial effects.
 */
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

/**
 * Composable function that displays the spatial audio demo UI.
 * 
 * @param spatializer The Spatializer instance from AudioManager
 * @param modifier Modifier for the composable
 */
@Composable
fun SpatialAudioDemo(spatializer: Spatializer, modifier: Modifier = Modifier) {
    var isPlaying by remember { mutableStateOf(false) }
    var statusText by remember { mutableStateOf("") }
    var headTrackerData by remember { mutableStateOf("") }
    var playbackStatus by remember { mutableStateOf("") }
    var audioPlayer by remember { mutableStateOf<SpatialAudioPlayer?>(null) }

    // Initialize spatial audio status and player
    LaunchedEffect(spatializer) {
        statusText = buildStatusText(spatializer)
        
        if (spatializer.isAvailable) {
            audioPlayer = SpatialAudioPlayer(spatializer)
        }
    }

    // Update head tracker data while playing
    LaunchedEffect(isPlaying) {
        while (isPlaying && spatializer.isHeadTrackerAvailable) {
            headTrackerData = "Head tracker active - Audio responding to head movement"
            delay(100)
        }
    }

    // Clean up audio player when composable is disposed
    DisposableEffect(Unit) {
        onDispose {
            audioPlayer?.stopPlaying()
        }
    }

    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Spatial Audio Status Card
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.spatial_audio_status),
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = statusText, style = MaterialTheme.typography.bodyMedium)
            }
        }

        // Playback Status Card (only shown when there's status to display)
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
                        text = stringResource(R.string.playback_status),
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = playbackStatus, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        // Head Tracker Data Card (only shown if head tracker is available)
        if (spatializer.isHeadTrackerAvailable) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.head_tracker_data),
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = headTrackerData, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        // Control Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    if (!isPlaying) {
                        val result = audioPlayer?.startPlayingTone() 
                            ?: "AudioPlayer not initialized"
                        playbackStatus = result
                        if (result.contains("Started playing")) {
                            isPlaying = true
                        }
                    }
                },
                enabled = spatializer.isAvailable && !isPlaying
            ) {
                Text(stringResource(R.string.play_test_audio))
            }

            Button(
                onClick = {
                    audioPlayer?.stopPlaying()
                    isPlaying = false
                    playbackStatus = "Stopped playing"
                },
                enabled = isPlaying
            ) {
                Text(stringResource(R.string.stop))
            }
        }
    }
}

/**
 * Player class for generating and playing spatial audio with 5.1 surround sound.
 * 
 * This class creates an AudioTrack configured for spatial audio and generates
 * test tones for each channel in a 5.1 surround setup.
 * 
 * @param spatializer The Spatializer instance to check spatialization capabilities
 */
class SpatialAudioPlayer(private val spatializer: Spatializer) {
    private var audioTrack: AudioTrack? = null
    private var isPlaying = false
    private var audioThread: Thread? = null
    private var canSpatialized = false

    companion object {
        private const val SAMPLE_RATE = 48000
        private const val BUFFER_SIZE = 1024
        private const val CHANNELS_5_1 = 6
        private const val BASE_FREQUENCY = 440.0 // A4 note
        private const val AMPLITUDE_SCALE = 0.2
    }

    /**
     * Starts playing a test tone with 5.1 surround sound.
     * 
     * @return Status message indicating success or failure
     */
    fun startPlayingTone(): String {
        if (isPlaying) return "Already playing"

        try {
            // Configure audio attributes for spatial audio
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setSpatializationBehavior(AudioAttributes.SPATIALIZATION_BEHAVIOR_AUTO)
                .build()

            val audioFormat = AudioFormat.Builder()
                .setSampleRate(SAMPLE_RATE)
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setChannelMask(AudioFormat.CHANNEL_OUT_5POINT1)
                .build()

            // Check if the audio can be spatialized
            canSpatialized = spatializer.canBeSpatialized(audioAttributes, audioFormat)

            val minBufferSize = AudioTrack.getMinBufferSize(
                SAMPLE_RATE,
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

            // Start audio generation in a separate thread
            audioThread = Thread {
                generateAudio()
            }.apply { start() }

            return "Started playing 5.1 surround tone - Spatialized: $canSpatialized"

        } catch (e: Exception) {
            e.printStackTrace()
            return "Error: ${e.message}"
        }
    }

    /**
     * Stops playback and releases audio resources.
     */
    fun stopPlaying() {
        isPlaying = false
        
        // Wait for audio thread to finish
        audioThread?.join(1000)
        audioThread = null
        
        // Release audio track resources
        audioTrack?.stop()
        audioTrack?.release()
        audioTrack = null
    }

    /**
     * Generates multi-channel audio data for 5.1 surround sound.
     * Each channel plays a different frequency to demonstrate spatial positioning:
     * - Front Left: 440Hz (A4)
     * - Front Right: 554Hz (C#5 - higher fourth)
     * - Center: 330Hz (E4 - lower fourth)
     * - LFE: 80Hz (low frequency)
     * - Rear Left: 660Hz (E5)
     * - Rear Right: 880Hz (A5 - higher octave)
     */
    private fun generateAudio() {
        val buffer = ShortArray(BUFFER_SIZE * CHANNELS_5_1)
        var phase = 0.0
        val phaseIncrement = 2 * PI * BASE_FREQUENCY / SAMPLE_RATE

        while (isPlaying) {
            // Generate audio samples for each channel
            for (i in 0 until BUFFER_SIZE) {
                val sampleIndex = i * CHANNELS_5_1
                val amplitude = (Short.MAX_VALUE * AMPLITUDE_SCALE).toInt().toShort()

                // Front left channel (440Hz)
                buffer[sampleIndex] = (sin(phase) * amplitude).toInt().toShort()
                // Front right channel (554Hz - higher fourth)
                buffer[sampleIndex + 1] = (sin(phase * 1.26) * amplitude).toInt().toShort()
                // Center channel (330Hz - lower fourth)
                buffer[sampleIndex + 2] = (sin(phase * 0.75) * amplitude).toInt().toShort()
                // LFE channel (80Hz - low frequency)
                buffer[sampleIndex + 3] = (sin(phase * 0.18) * amplitude).toInt().toShort()
                // Rear left channel (660Hz)
                buffer[sampleIndex + 4] = (sin(phase * 1.5) * amplitude).toInt().toShort()
                // Rear right channel (880Hz - higher octave)
                buffer[sampleIndex + 5] = (sin(phase * 2.0) * amplitude).toInt().toShort()

                phase += phaseIncrement
                if (phase >= 2 * PI) phase -= 2 * PI
            }

            audioTrack?.write(buffer, 0, buffer.size)
        }
    }
}

/**
 * Builds a formatted status text describing the spatial audio capabilities of the device.
 * 
 * @param spatializer The Spatializer instance to query for capabilities
 * @return Formatted string with device status information
 */
fun buildStatusText(spatializer: Spatializer): String {
    return buildString {
        val immersiveLevel = spatializer.immersiveAudioLevel
        val supportsSpatialization = immersiveLevel != Spatializer.SPATIALIZER_IMMERSIVE_LEVEL_NONE
        
        val yesCheck = "✅ Yes"
        val noCross = "❌ No"

        appendLine("📱 Device Spatialization Support: ${if (supportsSpatialization) yesCheck else noCross}")
        appendLine("   Immersive Level: $immersiveLevel")
        appendLine("🔌 Current Route Available: ${if (spatializer.isAvailable) yesCheck else noCross}")
        appendLine("⚙️ Spatializer Enabled: ${if (spatializer.isEnabled) yesCheck else noCross}")
        appendLine("🎯 Head Tracker Available: ${if (spatializer.isHeadTrackerAvailable) yesCheck else noCross}")
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