package com.kglabs28.yapnplay.appengine.core.audio

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.SystemClock
import android.util.Log
import com.kglabs28.yapnplay.appengine.core.common.CrashReporter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.sqrt

/**
 * Reads the microphone in short frames and measures how loud each is
 * (RMS). It also tracks the room's background noise (a floor that drops
 * at once and rises slowly), so a frame counts as speech only when it's
 * clearly above both [THRESHOLD] and [NOISE_MARGIN] × that floor — a fan
 * or the phone's gain creeping up doesn't light the wave. [speaking]
 * stays true for [HOLD_MS] after the last loud frame so it doesn't
 * flicker between words. Every [LOG_EVERY_MS] it logs the level under
 * the "MicLevel" tag, to check it's the real mic.
 */
class AndroidMicLevelMonitor(private val context: Context) : MicLevelMonitor {

    private val level = MutableStateFlow(false)
    override val speaking: StateFlow<Boolean> = level.asStateFlow()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var job: Job? = null

    override fun start() {
        if (job?.isActive == true) return
        if (context.checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) return
        job = scope.launch { listen() }
    }

    override suspend fun stop() {
        // Waits for the recorder to be released: the voice room must never find the mic still held.
        job?.cancelAndJoin()
        job = null
        level.value = false
    }

    @SuppressLint("MissingPermission") // checked in start()
    private suspend fun listen() {
        val minBuffer = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL, ENCODING)
        if (minBuffer <= 0) return
        val record = try {
            // The plain mic, not the "voice call" source: only the level is read, and the call source would switch
            // the phone into call mode (routing and volume) under the voice room, which can quieten what the user hears.
            AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, CHANNEL, ENCODING, maxOf(minBuffer, FRAME * BUFFER_FRAMES * BYTES_PER_SAMPLE))
        } catch (e: Exception) {
            CrashReporter.record(e, "AndroidMicLevelMonitor.create")
            return
        }
        if (record.state != AudioRecord.STATE_INITIALIZED) {
            record.release()
            return
        }
        val frame = ShortArray(FRAME)
        var lastLoud = 0L
        // Loud frames in a row: speech runs on, a knock, a cough or a passing noise does not.
        var streak = 0
        var floor = THRESHOLD
        var lastLog = 0L
        try {
            record.startRecording()
            while (currentCoroutineContext().isActive) {
                val read = record.read(frame, 0, frame.size)
                if (read <= 0) continue
                var sum = 0.0
                for (i in 0 until read) {
                    val v = frame[i] / FULL_SCALE
                    sum += v * v
                }
                val rms = sqrt(sum / read)
                val now = SystemClock.elapsedRealtime()
                floor = if (rms < floor) rms else floor + (rms - floor) * FLOOR_RISE
                streak = if (rms >= THRESHOLD && rms >= floor * NOISE_MARGIN) streak + 1 else 0
                if (streak >= MIN_LOUD_FRAMES) lastLoud = now
                level.value = lastLoud > 0 && now - lastLoud < HOLD_MS
                if (now - lastLog >= LOG_EVERY_MS) {
                    lastLog = now
                    Log.d(LOG_TAG, "rms=%.4f floor=%.4f speaking=%b".format(rms, floor, level.value))
                }
            }
        } catch (e: Exception) {
            CrashReporter.record(e, "AndroidMicLevelMonitor.listen")
        } finally {
            runCatching { record.stop() }
            record.release()
            level.value = false
        }
    }

    private companion object {
        const val SAMPLE_RATE = 16_000
        const val CHANNEL = AudioFormat.CHANNEL_IN_MONO
        const val ENCODING = AudioFormat.ENCODING_PCM_16BIT
        /** 20 ms frames. */
        const val FRAME = 320
        const val BUFFER_FRAMES = 4
        const val BYTES_PER_SAMPLE = 2
        const val FULL_SCALE = 32_768.0
        /** RMS level (0–1) that counts as speech: about −30 dBFS (the plain mic has no noise filter). */
        const val THRESHOLD = 0.03
        const val HOLD_MS = 400L
        /** Speech must be this many times louder than the background noise. */
        const val NOISE_MARGIN = 4.0
        /** Loud frames in a row (20 ms each) before it counts as talking: about a quarter of a second. */
        const val MIN_LOUD_FRAMES = 12
        /** How fast the noise floor follows a louder room (per 20 ms frame). */
        const val FLOOR_RISE = 0.002
        const val LOG_EVERY_MS = 500L
        const val LOG_TAG = "MicLevel"
    }
}
