# Background Audio Recorder

A professional Android app that records audio reliably in the background using a ForegroundService. The app continues recording even when minimized or when the screen is locked.

## Features

✅ **Background Recording** - Uses ForegroundService to ensure uninterrupted recording
✅ **Persistent Notification** - Shows recording status in the notification bar
✅ **Live Timer** - Real-time recording duration display
✅ **Recording Management** - View, play, and delete recordings
✅ **Modern UI** - Clean Material Design interface
✅ **Audio Playback** - Built-in player to listen to recordings
✅ **File Storage** - Saves high-quality AAC audio files (.m4a format)

## Technical Details

### Recording Specifications
- **Format**: AAC (Advanced Audio Coding)
- **Container**: M4A
- **Bitrate**: 128 kbps
- **Sample Rate**: 44.1 kHz
- **Audio Source**: Microphone

### Architecture
- **ForegroundService**: Ensures recording continues in background
- **MediaRecorder**: Handles audio capture
- **MediaPlayer**: Handles audio playback
- **RecyclerView**: Displays list of recordings
- **Material Design**: Modern UI components

## Requirements

- **Minimum SDK**: Android 7.0 (API 24)
- **Target SDK**: Android 14 (API 34)
- **Permissions Required**:
  - `RECORD_AUDIO` - To capture audio from microphone
  - `FOREGROUND_SERVICE` - To run background service
  - `FOREGROUND_SERVICE_MICROPHONE` - To use microphone in foreground service
  - `POST_NOTIFICATIONS` - To show persistent notification (Android 13+)

## Installation

1. Open the project in Android Studio
2. Sync Gradle files
3. Connect your Android device or start an emulator
4. Click Run or press Shift + F10

## Usage

1. **Grant Permissions**: On first launch, grant microphone and notification permissions
2. **Start Recording**: Tap the "Start" button to begin recording
3. **Background Recording**: Minimize the app or lock the screen - recording continues
4. **Stop Recording**: Return to the app and tap "Stop" to finish recording
5. **View Recordings**: Scroll down to see all your recordings
6. **Play Recording**: Tap the play button on any recording to listen
7. **Delete Recording**: Tap the delete button to remove a recording

## File Storage

Recordings are saved to:
\`\`\`
Internal Storage/Android/data/com.example.audiorecorder/files/
\`\`\`

Files are named with timestamps:
\`\`\`
recording_20250118_143022.m4a
\`\`\`

## Project Structure

\`\`\`
app/src/main/
├── java/com/example/audiorecorder/
│   ├── MainActivity.kt              # Main UI and recording controls
│   ├── AudioRecordingService.kt     # Background recording service
│   ├── RecordingsAdapter.kt         # RecyclerView adapter for recordings list
│   └── MediaPlayerManager.kt        # Audio playback manager
├── res/
│   ├── layout/
│   │   ├── activity_main.xml        # Main screen layout
│   │   └── item_recording.xml       # Recording list item layout
│   ├── drawable/
│   │   └── recording_indicator.xml  # Red dot indicator
│   └── values/
│       ├── colors.xml               # App color palette
│       ├── strings.xml              # String resources
│       └── themes.xml               # App theme
└── AndroidManifest.xml              # App configuration and permissions
\`\`\`

## Key Components

### AudioRecordingService
- Runs as a ForegroundService
- Creates persistent notification
- Manages MediaRecorder lifecycle
- Handles audio file creation and storage

### MainActivity
- Manages UI state and user interactions
- Handles permission requests
- Controls recording service
- Displays recordings list
- Manages audio playback

### RecordingsAdapter
- Displays list of recordings in RecyclerView
- Shows recording name, date, and file size
- Provides play and delete actions

### MediaPlayerManager
- Manages MediaPlayer lifecycle
- Handles audio playback
- Prevents memory leaks

## Testing

To verify the app works correctly:

1. Start a recording
2. Press the home button (app goes to background)
3. Wait 30 seconds
4. Return to the app
5. Stop the recording
6. Verify the recording file exists and plays correctly
7. Lock the screen during recording to test screen-off recording

## Troubleshooting

**Recording stops when app is minimized:**
- Ensure the ForegroundService is running (check notification)
- Verify all permissions are granted
- Check battery optimization settings

**No audio in recording:**
- Verify microphone permission is granted
- Check if another app is using the microphone
- Test with a different audio source

**Playback issues:**
- Ensure the file exists and is not corrupted
- Check file permissions
- Verify MediaPlayer is properly initialized

## License

This is a proof-of-concept application for demonstration purposes.
