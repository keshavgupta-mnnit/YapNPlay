package com.kglabs28.yapnplay.appengine.appmanager

import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import com.kglabs28.yapnplay.appengine.core.common.CrashReporter

/**
 * Single source of truth for remotely controllable app values.
 *
 * Two phases, same as proven in production elsewhere:
 * 1. Construction loads local values instantly (offline-safe).
 * 2. [syncWithRemote] merges remote values over local and persists them.
 *
 * Every getter never throws: corrupt values fall back to defaults. Keys
 * live in [AppManagerKeys]; features read them through their module's
 * helper (AppManagerHelper, GamesAppManagerHelper…), not from here.
 */
class AppManager(private val store: AppConfigStore) {

    private val _revision = MutableStateFlow(0)

    /** Goes up each time remote values arrive, so screens can re-read them. */
    val revision: StateFlow<Int> = _revision.asStateFlow()

    private var configMap: Map<String, String> = try {
        store.getAll()
    } catch (e: Exception) {
        CrashReporter.record(e, "AppManager.init")
        emptyMap()
    }

    /**
     * Merge a remotely fetched map over local values and persist the
     * result. Safe to call with an empty map (keeps local values).
     */
    fun syncWithRemote(remote: Map<String, String>) {
        if (remote.isEmpty()) return
        configMap = configMap + remote
        _revision.value += 1
        try {
            store.saveAll(configMap)
        } catch (e: Exception) {
            CrashReporter.record(e, "AppManager.sync.persist")
        }
    }

    /** [key]'s value, or its default (never throws). */
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> get(key: ConfigKey<T>): T = when (val default = key.default) {
        is Boolean -> getBoolean(key.name, default)
        is Int -> getInt(key.name, default)
        is Long -> getLong(key.name, default)
        is Float -> getFloat(key.name, default)
        is Double -> getDouble(key.name, default)
        is String -> getString(key.name, default)
        else -> default
    } as T

    // ── Typed getters ────────────────────────────────────────────────────

    fun getString(key: String, default: String = ""): String = try {
        configMap[key] ?: default
    } catch (e: Exception) {
        CrashReporter.record(e, "AppManager.getString:$key")
        default
    }

    fun getBoolean(key: String, default: Boolean = false): Boolean = try {
        configMap[key]?.toBooleanStrictOrNull() ?: default
    } catch (e: Exception) {
        CrashReporter.record(e, "AppManager.getBoolean:$key")
        default
    }

    fun getInt(key: String, default: Int = 0): Int = try {
        configMap[key]?.toIntOrNull() ?: default
    } catch (e: Exception) {
        CrashReporter.record(e, "AppManager.getInt:$key")
        default
    }

    fun getLong(key: String, default: Long = 0L): Long = try {
        configMap[key]?.toLongOrNull() ?: default
    } catch (e: Exception) {
        CrashReporter.record(e, "AppManager.getLong:$key")
        default
    }

    fun getFloat(key: String, default: Float = 0f): Float = try {
        configMap[key]?.toFloatOrNull() ?: default
    } catch (e: Exception) {
        CrashReporter.record(e, "AppManager.getFloat:$key")
        default
    }

    fun getDouble(key: String, default: Double = 0.0): Double = try {
        configMap[key]?.toDoubleOrNull() ?: default
    } catch (e: Exception) {
        CrashReporter.record(e, "AppManager.getDouble:$key")
        default
    }

}
