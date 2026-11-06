package com.kglabs28.yapnplay.appengine.appmanager

import com.kglabs28.yapnplay.appengine.core.common.BrowserStorage
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/** AppManager's config in the browser's storage (one JSON map), kept across visits. */
class WebConfigStore : AppConfigStore {
    override fun getAll(): Map<String, String> =
        BrowserStorage.local.get(KEY)?.let { runCatching { Json.decodeFromString<Map<String, String>>(it) }.getOrNull() }.orEmpty()

    override fun saveAll(map: Map<String, String>) {
        BrowserStorage.local.set(KEY, Json.encodeToString(getAll() + map))
    }

    private companion object {
        const val KEY = "yapnplay.config"
    }
}
