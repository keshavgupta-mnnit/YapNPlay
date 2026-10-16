package com.kglabs28.yapnplay.appengine.appmanager

import android.content.Context
import android.content.SharedPreferences

/**
 * SharedPreferences-backed config store. Simple, synchronous and fast —
 * the manager reads it once at construction, so no coroutines involved.
 */
class AndroidConfigStore(context: Context) : AppConfigStore {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("appmanager_config", Context.MODE_PRIVATE)

    override fun getAll(): Map<String, String> =
        prefs.all.mapValues { it.value?.toString() ?: "" }

    override fun saveAll(map: Map<String, String>) {
        prefs.edit().apply {
            map.forEach { (key, value) -> putString(key, value) }
            apply()
        }
    }
}
