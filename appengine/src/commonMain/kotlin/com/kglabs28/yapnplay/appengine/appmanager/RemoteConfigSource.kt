package com.kglabs28.yapnplay.appengine.appmanager

/**
 * Where remote values come from (Firebase Remote Config on Android; none on
 * other platforms yet). Never throws: a failure returns an empty map, which
 * keeps the local values.
 */
interface RemoteConfigSource {
    suspend fun fetch(defaults: Map<String, Any>): Map<String, String>
}
