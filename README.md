# SpatialAudio HeadTracker Test

A demonstration Android application showcasing spatial audio capabilities and head tracking using Android's Spatializer API. This app helps developers understand and test spatial audio features on compatible Android devices.

## Features

- **Spatial Audio Detection**: Automatically detects device capabilities for spatial audio
- **Head Tracking**: Demonstrates head tracking integration with spatial audio
- **5.1 Surround Sound Test**: Plays multi-channel test tones with different frequencies per channel:
  - Front Left: 440Hz (A4)
  - Front Right: 554Hz (C#5)
  - Center: 330Hz (E4)
  - LFE: 80Hz (Low Frequency Effects)
  - Rear Left: 660Hz (E5)
  - Rear Right: 880Hz (A5)
- **Real-time Status Display**: Shows spatializer status, head tracker availability, and playback information
- **Material Design 3**: Modern UI with dynamic color support

## Requirements

### Minimum Requirements
- **Android Version**: Android 13 (API level 33) or higher
- **Kotlin**: 1.9.22 or higher
- **Gradle**: 8.3.0 or higher
- **Android Gradle Plugin**: 8.3.0 or higher

### Device Requirements
For full functionality, your device should support:
- Spatial audio (Spatializer API)
- Head tracking sensors
- Multi-channel audio output (headphones/speakers)

### Recommended Test Devices
- Devices with built-in head tracking (e.g., newer Pixel devices)
- Headphones with head tracking support
- Bluetooth headphones with spatial audio capabilities

## Getting Started

### Prerequisites
1. Install [Android Studio](https://developer.android.com/studio) (latest stable version recommended)
2. Ensure you have JDK 11 or higher installed
3. An Android device or emulator running Android 13+

### Installation

1. Clone the repository:
```bash
git clone https://github.com/3130585774/SpatialAudio-HeadTracker-Test.git
cd SpatialAudio-HeadTracker-Test
```

2. Open the project in Android Studio:
   - Launch Android Studio
   - Select "Open an Existing Project"
   - Navigate to the cloned directory and select it

3. Sync Gradle:
   - Android Studio should automatically sync Gradle files
   - If not, click "Sync Project with Gradle Files" in the toolbar

4. Build the project:
   - Select "Build" → "Make Project" from the menu
   - Wait for the build to complete

### Running the App

1. **On a Physical Device** (Recommended for testing spatial audio):
   - Enable Developer Options and USB debugging on your device
   - Connect your device via USB
   - Click the "Run" button in Android Studio
   - Select your device from the list

2. **On an Emulator**:
   - Create an AVD (Android Virtual Device) with API 33+
   - Launch the emulator
   - Click the "Run" button and select the emulator
   - **Note**: Emulators have limited spatial audio support

## How to Use

1. **Launch the App**: Open the Headtracker Test app on your device

2. **Check Device Status**: 
   - The app displays your device's spatial audio capabilities
   - Green checkmarks (✅) indicate supported features
   - Red crosses (❌) or warnings (⚠️) indicate limitations

3. **Play Test Audio**:
   - Tap the "Play Test Audio" button to start playback
   - If head tracking is available, move your head to experience spatial effects
   - Each speaker in the 5.1 setup plays a distinct frequency for easy identification

4. **Monitor Playback**:
   - The "Playback Status" card shows whether audio is spatialized
   - "Head Tracker Data" updates during playback if available

5. **Stop Playback**:
   - Tap the "Stop" button to end playback

## Architecture

### Project Structure
```
app/
├── src/main/
│   ├── java/com/example/headtrackertest/
│   │   ├── MainActivity.kt          # Main activity and UI
│   │   └── ui/theme/               # Material3 theme configuration
│   ├── res/
│   │   ├── values/
│   │   │   ├── strings.xml         # String resources
│   │   │   ├── colors.xml          # Color definitions
│   │   │   └── themes.xml          # Theme definitions
│   │   └── mipmap/                 # App icons
│   └── AndroidManifest.xml         # App configuration
└── build.gradle.kts                # App-level build configuration
```

### Key Components

#### MainActivity
The main entry point that initializes the Spatializer and sets up the Compose UI.

#### SpatialAudioDemo (Composable)
The main UI component that displays:
- Device spatial audio capabilities
- Head tracker status
- Playback controls
- Real-time feedback

#### SpatialAudioPlayer
Handles audio generation and playback:
- Configures AudioTrack for spatial audio
- Generates multi-channel test tones
- Manages audio lifecycle and cleanup

## Technical Details

### Spatial Audio Configuration
The app uses the following audio configuration:
- **Sample Rate**: 48kHz
- **Encoding**: PCM 16-bit
- **Channel Mask**: 5.1 surround (6 channels)
- **Usage**: Media
- **Content Type**: Music
- **Spatialization Behavior**: Auto

### Head Tracking
Head tracking data is monitored through the Spatializer API. When available and enabled:
- The app indicates active head tracking
- Audio spatially responds to head movements
- Status updates every 100ms during playback

## Troubleshooting

### "Device does not support spatialization"
- **Cause**: Your device's hardware doesn't support spatial audio
- **Solution**: Try on a device with spatial audio support

### "Spatialization not available with current audio output"
- **Cause**: Current audio route (speaker/headphones) doesn't support spatialization
- **Solution**: Try different headphones or audio output devices

### "Spatialization is disabled"
- **Cause**: Spatial audio is disabled in system settings
- **Solution**: Enable spatial audio in Settings → Sound → Spatial audio

### "Head tracking not available"
- **Cause**: Device or headphones don't support head tracking
- **Solution**: Use compatible hardware with head tracking sensors

### Build Errors
- **Gradle sync failed**: Ensure you have a stable internet connection for dependency download
- **SDK not found**: Install required Android SDK platforms through SDK Manager
- **Kotlin version mismatch**: Sync Gradle files to download the correct Kotlin version

## References

- [Android Spatial Audio Developer Guide](https://developer.android.com/media/grow/spatial-audio#kotlin)
- [Spatializer API Documentation](https://developer.android.com/reference/android/media/Spatializer)
- [AudioTrack Documentation](https://developer.android.com/reference/android/media/AudioTrack)
- [Material Design 3](https://m3.material.io/)

## License

This project is licensed under the terms specified in the [LICENSE](LICENSE) file.

## Contributing

Contributions are welcome! Please feel free to submit issues or pull requests.

### Development Guidelines
1. Follow Kotlin coding conventions
2. Add KDoc comments for public APIs
3. Test on devices with and without spatial audio support
4. Maintain Material Design 3 design principles

## Changelog

### Version 1.0
- Initial release
- Spatial audio detection and testing
- Head tracking integration
- 5.1 surround sound test tone generation
- Material Design 3 UI

## Support

If you encounter any issues or have questions:
1. Check the [Troubleshooting](#troubleshooting) section
2. Review [Android Spatial Audio documentation](https://developer.android.com/media/grow/spatial-audio)
3. Open an issue on GitHub with:
   - Device model and Android version
   - Steps to reproduce the issue
   - Screenshots if applicable

## Acknowledgments

This project demonstrates the use of Android's modern audio APIs and serves as a learning resource for developers interested in spatial audio and head tracking technologies.
