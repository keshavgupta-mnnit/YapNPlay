package com.kglabs28.yapnplay.appengine.ui.components.moderation

import com.kglabs28.yapnplay.appengine.core.audio.GameAudio

/** The app-wide sound switches behind the settings dialog of Home and Profile. */
class AppSettingsController(private val audio: GameAudio) {
    fun current(): AppSettingsUi = audio.settings.value.let { AppSettingsUi(effectsOn = it.effectsOn, musicOn = it.musicOn) }

    fun toggleEffects(): AppSettingsUi {
        audio.setEffectsOn(!audio.settings.value.effectsOn)
        return current()
    }

    fun toggleMusic(): AppSettingsUi {
        audio.setMusicOn(!audio.settings.value.musicOn)
        return current()
    }
}
