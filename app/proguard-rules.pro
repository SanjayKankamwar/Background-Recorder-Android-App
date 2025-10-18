# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep MediaRecorder and MediaPlayer classes
-keep class android.media.MediaRecorder { *; }
-keep class android.media.MediaPlayer { *; }

# Keep service classes
-keep class com.example.audiorecorder.AudioRecordingService { *; }

# Keep RecyclerView adapter
-keep class com.example.audiorecorder.RecordingsAdapter { *; }
-keep class com.example.audiorecorder.RecordingsAdapter$* { *; }
