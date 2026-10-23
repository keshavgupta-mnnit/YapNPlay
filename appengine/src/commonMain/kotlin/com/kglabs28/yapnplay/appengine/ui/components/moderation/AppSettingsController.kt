package com.kglabs28.yapnplay.appengine.ui.components.moderation

import com.kglabs28.yapnplay.appengine.core.common.KeyboardPreference
import com.kglabs28.yapnplay.appengine.core.audio.GameAudio

/** The app-wide switches behind the settings dialog of Home and Profile: sound, music and the keyboard. */
class AppSettingsController(private val audio: GameAudio, private val keyboard: KeyboardPreference) {
    fun current(): AppSettingsUi = audio.settings.value.let {
        AppSettingsUi(effectsOn = it.effectsOn, musicOn = it.musicOn, systemKeyboard = keyboard.useSystem.value)
    }

    fun toggleKeyboard(): AppSettingsUi {
        keyboard.setUseSystem(!keyboard.useSystem.value)
        return current()
    }

    fun toggleEffects(): AppSettingsUi {
        audio.setEffectsOn(!audio.settings.value.effectsOn)
        return current()
    }

    fun toggleMusic(): AppSettingsUi {
        audio.setMusicOn(!audio.settings.value.musicOn)
        return current()
    }
}
