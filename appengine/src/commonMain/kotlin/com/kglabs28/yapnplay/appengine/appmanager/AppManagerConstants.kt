package com.kglabs28.yapnplay.appengine.appmanager

/**
 * Remote flag keys (snake_case, matching Remote Config) and the safe
 * defaults used until remote values arrive. Few flags on purpose:
 * this is for major switches only, not everyday settings.
 */
object AppManagerConstants {

    /** Registered id of the game behind the legacy royal_ludo_enabled flag. */
    const val ROYAL_LUDO_GAME_ID = "royal-ludo"

    object Key {
        const val VOICE_ENABLED = "voice_enabled"
        const val ROYAL_LUDO_ENABLED = "royal_ludo_enabled"
        const val PARTY_CHAT_ENABLED = "party_chat_enabled"
        const val UPDATE_CONFIG = "update_config"
        const val MAX_ROOMS_PER_USER = "max_rooms_per_user"
        const val CITY_BAAZAR_STARTING_CASH = "city_baazar_starting_cash"
        const val CITY_BAAZAR_ROUND_LIMIT = "city_baazar_round_limit"
        const val CITY_BAAZAR_TIME_LIMIT_MINUTES = "city_baazar_time_limit_minutes"

        /** Per-game kill switch key, e.g. game_enabled_card_clash. */
        fun gameEnabled(gameId: String) = "game_enabled_" + gameId.replace('-', '_')
    }

    object Default {
        const val VOICE_ENABLED = true
        const val ROYAL_LUDO_ENABLED = true
        const val PARTY_CHAT_ENABLED = true
        const val UPDATE_CONFIG = ""
        const val MAX_ROOMS_PER_USER = 5
        const val CITY_BAAZAR_STARTING_CASH = 20_000
        const val CITY_BAAZAR_ROUND_LIMIT = 30
        const val CITY_BAAZAR_TIME_LIMIT_MINUTES = 30
    }
}
