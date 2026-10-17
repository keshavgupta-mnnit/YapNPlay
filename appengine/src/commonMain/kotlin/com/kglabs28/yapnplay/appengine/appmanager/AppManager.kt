package com.kglabs28.yapnplay.appengine.appmanager

import com.kglabs28.yapnplay.appengine.core.common.CrashReporter

/**
 * Single source of truth for remotely controllable app values.
 *
 * Two phases, same as proven in production elsewhere:
 * 1. Construction loads local values instantly (offline-safe).
 * 2. [syncWithRemote] merges remote values over local and persists them.
 *
 * Every getter never throws: corrupt values fall back to defaults.
 * Typed feature helpers (e.g. [isVoiceEnabled]) keep call sites readable;
 * the generic getters cover anything added later without code changes.
 */
class AppManager(private val store: AppConfigStore) {

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
        try {
            store.saveAll(configMap)
        } catch (e: Exception) {
            CrashReporter.record(e, "AppManager.sync.persist")
        }
    }

    // ── Feature flags ────────────────────────────────────────────────────

    fun isVoiceEnabled(): Boolean =
        getBoolean(AppManagerConstants.Key.VOICE_ENABLED, AppManagerConstants.Default.VOICE_ENABLED)

    fun isRoyalLudoEnabled(): Boolean =
        getBoolean(AppManagerConstants.Key.ROYAL_LUDO_ENABLED, AppManagerConstants.Default.ROYAL_LUDO_ENABLED)

    fun isPartyChatEnabled(): Boolean =
        getBoolean(AppManagerConstants.Key.PARTY_CHAT_ENABLED, AppManagerConstants.Default.PARTY_CHAT_ENABLED)

    fun updateConfig(): String =
        getString(AppManagerConstants.Key.UPDATE_CONFIG, AppManagerConstants.Default.UPDATE_CONFIG)

    /**
     * Whether a registered game may be shown and queued. Royal Ludo keeps
     * its original key; any other game reads `game_enabled_<id>`.
     */
    fun isGameEnabled(gameId: String): Boolean = when (gameId) {
        AppManagerConstants.ROYAL_LUDO_GAME_ID -> isRoyalLudoEnabled()
        else -> getBoolean(AppManagerConstants.Key.gameEnabled(gameId), true)
    }

    /** How many rooms one user may belong to (server enforces the same). */
    fun maxRoomsPerUser(): Int =
        getInt(AppManagerConstants.Key.MAX_ROOMS_PER_USER, AppManagerConstants.Default.MAX_ROOMS_PER_USER)

    /** City Bazaar: each player's starting balance (₹). */
    fun cityBazaarStartingCash(): Int =
        getInt(AppManagerConstants.Key.CITY_BAZAAR_STARTING_CASH, AppManagerConstants.Default.CITY_BAZAAR_STARTING_CASH)

    /** City Bazaar: rounds before the match ends on net worth (when the room's toggle is on). */
    fun cityBazaarRoundLimit(): Int =
        getInt(AppManagerConstants.Key.CITY_BAZAAR_ROUND_LIMIT, AppManagerConstants.Default.CITY_BAZAAR_ROUND_LIMIT)

    /** City Bazaar: minutes before the match ends on net worth (when the room's toggle is on). */
    fun cityBazaarTimeLimitMinutes(): Int =
        getInt(AppManagerConstants.Key.CITY_BAZAAR_TIME_LIMIT_MINUTES, AppManagerConstants.Default.CITY_BAZAAR_TIME_LIMIT_MINUTES)

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
