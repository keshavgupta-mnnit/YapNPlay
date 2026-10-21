package com.kglabs28.yapnplay.appengine.appmanager

/** Desktop / tests: no remote values (local ones stay). */
class NoRemoteConfigSource : RemoteConfigSource {
    override suspend fun fetch(defaults: Map<String, Any>): Map<String, String> = emptyMap()
}
