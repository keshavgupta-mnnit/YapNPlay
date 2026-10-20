package com.kglabs28.yapnplay.appengine.core.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import com.kglabs28.yapnplay.appengine.R
import com.kglabs28.yapnplay.appengine.core.common.CrashReporter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Android game audio: effects through a [SoundPool] (loaded once, low
 * latency), music through a looping [MediaPlayer] at a quiet volume.
 * The on/off switches are kept in SharedPreferences. Sound files live in
 * res/raw (placeholders; see docs/placeholder-assets.md).
 */
class AndroidGameAudio(private val context: Context) : GameAudio {

    private val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    private val state = MutableStateFlow(
        AudioSettings(prefs.getBoolean(KEY_EFFECTS, true), prefs.getBoolean(KEY_MUSIC, true)),
    )
    override val settings: StateFlow<AudioSettings> = state.asStateFlow()

    private val attributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_GAME)
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .build()
    private val pool = SoundPool.Builder().setMaxStreams(MAX_STREAMS).setAudioAttributes(attributes).build()
    private val ids = mapOf(
        GameSound.DICE_ROLL to pool.load(context, R.raw.dice_roll, 1),
        GameSound.HOP to pool.load(context, R.raw.piece_hop, 1),
        GameSound.CAPTURE to pool.load(context, R.raw.piece_capture, 1),
        GameSound.WIN to pool.load(context, R.raw.winner, 1),
        GameSound.YOUR_TURN to pool.load(context, R.raw.your_turn, 1),
        GameSound.TICK to pool.load(context, R.raw.clock_tick, 1),
        GameSound.HURRY_TICK to pool.load(context, R.raw.hurry_tick, 1),
    )
    private var music: MediaPlayer? = null
    private var musicWanted = false

    override fun play(sound: GameSound) {
        if (!state.value.effectsOn) return
        ids[sound]?.let { pool.play(it, EFFECT_VOLUME, EFFECT_VOLUME, 1, 0, 1f) }
    }

    override fun startMusic() {
        musicWanted = true
        if (!state.value.musicOn || music != null) return
        try {
            music = MediaPlayer.create(context, R.raw.music_loop)?.apply {
                isLooping = true
                setVolume(MUSIC_VOLUME, MUSIC_VOLUME)
                start()
            }
        } catch (e: Exception) {
            CrashReporter.record(e, "AndroidGameAudio.startMusic")
        }
    }

    override fun stopMusic() {
        musicWanted = false
        releaseMusic()
    }

    override fun setEffectsOn(on: Boolean) {
        prefs.edit().putBoolean(KEY_EFFECTS, on).apply()
        state.update { it.copy(effectsOn = on) }
    }

    override fun setMusicOn(on: Boolean) {
        prefs.edit().putBoolean(KEY_MUSIC, on).apply()
        state.update { it.copy(musicOn = on) }
        if (!on) releaseMusic() else if (musicWanted) startMusic()
    }

    private fun releaseMusic() {
        music?.run {
            stop()
            release()
        }
        music = null
    }

    private companion object {
        const val PREFS = "yapnplay_audio"
        const val KEY_EFFECTS = "effects_on"
        const val KEY_MUSIC = "music_on"
        const val MAX_STREAMS = 6
        const val EFFECT_VOLUME = 0.9f
        const val MUSIC_VOLUME = 0.35f
    }
}
