package com.kglabs28.yapnplay.appengine.ui.components.moderation

/** The app's settings dialog (Home and Profile): sound effects, music, and the phone's keyboard instead of the app's ([systemKeyboard], off by default). */
data class AppSettingsUi(val effectsOn: Boolean, val musicOn: Boolean, val systemKeyboard: Boolean = false)
