package com.kglabs28.yapnplay.appengine.features.about.domain

/**
 * About-screen data: what the app is and which contract it speaks.
 */
data class AppInfo(
    val appName: String,
    val version: String,
    val contractVersion: String,
)
