package com.kglabs28.yapnplay.appengine.appmanager

/**
 * No remote backend on plain JVM: local values always win.
 */
suspend fun fetchRemoteFlags(): Map<String, String> = emptyMap()
