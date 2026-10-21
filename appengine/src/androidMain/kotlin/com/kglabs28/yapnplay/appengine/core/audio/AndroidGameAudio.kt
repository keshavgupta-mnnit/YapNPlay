package com.kglabs28.yapnplay.appengine.core.audio

import com.kglabs28.yapnplay.appengine.resources.Res
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CoroutineScope
import java.util.concurrent.ConcurrentHashMap
import java.io.File
import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import com.kglabs28.yapnplay.appengine.core.common.CrashReporter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Android game audio: effects through a [SoundPool] (loaded once, low
 * latency), music through a looping [MediaPlayer] at a quiet volume.
 * The on/off switches are kept in SharedPreferences. The sound files are
 * common resources (GameSoundFiles), copied to the cache once.
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
    /** Sound → SoundPool id, filled once the shared files are copied out (play() skips sounds not loaded yet). */
    private val ids = ConcurrentHashMap<GameSound, Int>()
    private var musicFile: File? = null

    init {
        // The sound files are common resources: copy each to the cache once, then load it.
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            GameSound.entries.forEach { sound ->
                runCatching { cached(GameSoundFiles.path(sound)) }
                    .onSuccess { ids[sound] = pool.load(it.path, 1) }
                    .onFailure { CrashReporter.record(it, "AndroidGameAudio.load:$sound") }
            }
            musicFile = runCatching { cached(GameSoundFiles.MUSIC) }.onFailure { CrashReporter.record(it, "AndroidGameAudio.music") }.getOrNull()
            if (musicWanted) withContext(Dispatchers.Main) { startMusic() }
        }
    }

    private suspend fun cached(path: String): File {
        val file = File(File(context.cacheDir, SOUND_DIR).apply { mkdirs() }, path.substringAfterLast('/'))
        if (!file.exists()) file.writeBytes(Res.readBytes(path))
        return file
    }

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
            val file = musicFile ?: return
            music = MediaPlayer().apply {
                setDataSource(file.path)
                setAudioAttributes(attributes)
                isLooping = true
                setVolume(MUSIC_VOLUME, MUSIC_VOLUME)
                prepare()
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
        const val SOUND_DIR = "sounds"
        const val PREFS = "yapnplay_audio"
        const val KEY_EFFECTS = "effects_on"
        const val KEY_MUSIC = "music_on"
        const val MAX_STREAMS = 6
        const val EFFECT_VOLUME = 0.9f
        const val MUSIC_VOLUME = 0.35f
    }
}
