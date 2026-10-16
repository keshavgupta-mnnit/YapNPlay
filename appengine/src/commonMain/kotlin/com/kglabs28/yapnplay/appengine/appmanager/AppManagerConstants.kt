package com.kglabs28.yapnplay.appengine.appmanager

/**
 * Remote flag keys (snake_case, matching Remote Config) and the safe
 * defaults used until remote values arrive. Few flags on purpose:
 * this is for major switches only, not everyday settings.
 */
object AppManagerConstants {

    object Key {
        const val VOICE_ENABLED = "voice_enabled"
        const val ROYAL_LUDO_ENABLED = "royal_ludo_enabled"
        const val PARTY_CHAT_ENABLED = "party_chat_enabled"
        const val UPDATE_CONFIG = "update_config"
    }

    object Default {
        const val VOICE_ENABLED = true
        const val ROYAL_LUDO_ENABLED = true
        const val PARTY_CHAT_ENABLED = true
        const val UPDATE_CONFIG = ""
    }
}
