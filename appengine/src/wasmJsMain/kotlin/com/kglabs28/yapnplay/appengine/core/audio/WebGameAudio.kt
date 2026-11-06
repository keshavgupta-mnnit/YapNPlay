package com.kglabs28.yapnplay.appengine.core.audio

import com.kglabs28.yapnplay.appengine.core.common.BrowserStorage
import com.kglabs28.yapnplay.appengine.core.common.CrashReporter
import com.kglabs28.yapnplay.appengine.resources.Res
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.await
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi

/**
 * Web game audio over Web Audio ([webAudioLoad] and friends): the same
 * shared sound files as the app (GameSoundFiles, served with the page),
 * the same volumes, the on/off switches kept in the browser's storage.
 */
@OptIn(ExperimentalResourceApi::class)
class WebGameAudio : GameAudio {

    private val storage = BrowserStorage.local
    private val state = MutableStateFlow(AudioSettings(storage.get(KEY_EFFECTS) != OFF, storage.get(KEY_MUSIC) != OFF))
    override val settings: StateFlow<AudioSettings> = state.asStateFlow()

    private var musicWanted = false

    init {
        // Every sound decoded once in the background; plays before then are skipped.
        CoroutineScope(SupervisorJob() + Dispatchers.Default).launch {
            (GameSound.entries.map { GameSoundFiles.path(it) } + GameSoundFiles.MUSIC).forEach { path ->
                runCatching { webAudioLoad(path, Res.getUri(path)).await<JsAny?>() }
                    .onFailure { CrashReporter.record(it, "WebGameAudio.load:$path") }
            }
            if (musicWanted) startMusic()
        }
    }

    override fun play(sound: GameSound) {
        if (state.value.effectsOn) webAudioPlay(GameSoundFiles.path(sound), EFFECT_VOLUME)
    }

    override fun startMusic() {
        musicWanted = true
        if (state.value.musicOn) webAudioStartLoop(GameSoundFiles.MUSIC, MUSIC_VOLUME)
    }

    override fun stopMusic() {
        musicWanted = false
        webAudioStopLoop()
    }

    override fun setEffectsOn(on: Boolean) {
        storage.set(KEY_EFFECTS, if (on) null else OFF)
        state.update { it.copy(effectsOn = on) }
    }

    override fun setMusicOn(on: Boolean) {
        storage.set(KEY_MUSIC, if (on) null else OFF)
        state.update { it.copy(musicOn = on) }
        if (!on) webAudioStopLoop() else if (musicWanted) startMusic()
    }

    private companion object {
        const val KEY_EFFECTS = "yapnplay.effectsOn"
        const val KEY_MUSIC = "yapnplay.musicOn"
        const val OFF = "0"
        /** The same volumes as the app. */
        const val EFFECT_VOLUME = 0.9
        const val MUSIC_VOLUME = 0.35
    }
}
