package com.kglabs28.yapnplay.appengine.core.audio

import kotlinx.coroutines.flow.StateFlow

/**
 * Game audio: one-shot [GameSound]s and a quiet background music loop,
 * each switchable in the match settings (remembered on the device).
 * Implemented per platform; desktop/tests use [SilentGameAudio].
 */
interface GameAudio {
    val settings: StateFlow<AudioSettings>
    fun play(sound: GameSound)
    fun startMusic()
    fun stopMusic()
    fun setEffectsOn(on: Boolean)
    fun setMusicOn(on: Boolean)
}
