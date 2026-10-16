package com.kglabs28.yapnplay.appengine.appmanager

/**
 * In-memory config store for JVM (unit tests, desktop previews).
 * Nothing persists; remote is always empty so local defaults rule.
 */
class JvmConfigStore(initial: Map<String, String> = emptyMap()) : AppConfigStore {

    private val map = initial.toMutableMap()

    override fun getAll(): Map<String, String> = map.toMap()

    override fun saveAll(map: Map<String, String>) {
        this.map.putAll(map)
    }
}
