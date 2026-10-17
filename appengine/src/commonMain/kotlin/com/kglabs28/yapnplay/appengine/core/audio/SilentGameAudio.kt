package com.kglabs28.yapnplay.appengine.core.audio

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

/** Plays nothing but keeps the switches (desktop, previews, tests). */
class SilentGameAudio : GameAudio {
    private val state = MutableStateFlow(AudioSettings())
    override val settings: StateFlow<AudioSettings> = state
    override fun play(sound: GameSound) = Unit
    override fun startMusic() = Unit
    override fun stopMusic() = Unit
    override fun setEffectsOn(on: Boolean) = state.update { it.copy(effectsOn = on) }
    override fun setMusicOn(on: Boolean) = state.update { it.copy(musicOn = on) }
}
