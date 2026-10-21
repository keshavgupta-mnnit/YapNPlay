package com.kglabs28.yapnplay.appengine.appmanager

import com.kglabs28.yapnplay.appengine.games.base.identity.GameIds

/**
 * EVERY Remote Config key the app reads, with its default and what it
 * controls — the one place to look before changing anything remotely.
 * Read them through the module's helper (AppManagerHelper,
 * GamesAppManagerHelper, CityBazaarAppManagerHelper,
 * AdsAppManagerHelper, BackgroundsAppManagerHelper), never by name in feature code.
 */
object AppManagerKeys {

    /** App-wide switches and addresses. */
    object App {
        val SERVER_BASE_URL = ConfigKey("server_base_url", "", "Backend address (https://…, no trailing slash); empty = built-in.")
        val UPDATE_CONFIG = ConfigKey("update_config", "", "Force/optional update rule as JSON (see UpdateRule); empty = none.")
        val VOICE_ENABLED = ConfigKey("voice_enabled", true, "Live voice in rooms and tables.")
        val PARTY_CHAT_ENABLED = ConfigKey("party_chat_enabled", true, "Party chat in rooms.")
        val MAX_ROOMS_PER_USER = ConfigKey("max_rooms_per_user", 5, "Groups one user may belong to (the server enforces the same).")
        val PLAY_STORE_URL = ConfigKey("play_store_url", "https://play.google.com/store/apps/details?id=com.kglabs28.yapnplay", "App link sent with shared results.")
    }

    /** Games: kill switches and table-wide timings. */
    object Games {
        val ROYAL_LUDO_ENABLED = ConfigKey(enabledKey(GameIds.ROYAL_LUDO), true, "Royal Ludo shown and playable.")
        val CITY_BAZAAR_ENABLED = ConfigKey(enabledKey(GameIds.CITY_BAZAAR), true, "City Bazaar shown and playable.")
        val TURN_HURRY_SECONDS = ConfigKey("turn_hurry_seconds", 5L, "Last seconds of the user's turn with the faster 'hurry' tick.")

        /** game_enabled_<id with _>, e.g. game_enabled_city_bazaar. */
        fun enabledKey(gameId: String) = "game_enabled_" + gameId.replace('-', '_')
    }

    /** City Bazaar table pacing and messages. */
    object CityBazaar {
        val MONEY_POPUPS_DEFAULT = ConfigKey("city_bazaar_money_popups_default", true, "Money popups on for players who never changed the setting.")
        val EVENT_BREATH_MS = ConfigKey("city_bazaar_event_breath_ms", 2_500L, "Pause after each event that shows a message (match the server's).")
        val EVENT_BREATH_MAX_MS = ConfigKey("city_bazaar_event_breath_max_ms", 8_000L, "Most pause in one update (match the server's cap).")
        val POPUP_MS = ConfigKey("city_bazaar_popup_ms", 3_000L, "How long the user's money / event popup stays.")
        val SNACKBAR_MS = ConfigKey("city_bazaar_snackbar_ms", 2_500L, "How long the bottom snackbar stays.")
    }

    /** Background and art images: a URL replaces the built-in image; empty = built-in. */
    object Backgrounds {
        val HOME = ConfigKey("bg_home_url", "", "Home / tabs background.")
        val LEADERBOARD = ConfigKey("bg_leaderboard_url", "", "End-of-match leaderboard background.")
        val GAME_ZONE_CARD = ConfigKey("bg_game_zone_card_url", "", "Home: Game Zone card art.")
        val FRIEND_ZONE_CARD = ConfigKey("bg_friend_zone_card_url", "", "Home: Friend Zone card art.")
        val ROYAL_LUDO_ART = ConfigKey("art_royal_ludo_url", "", "Royal Ludo game art (pickers, leaderboard).")
        val CITY_BAZAAR_ART = ConfigKey("art_city_bazaar_url", "", "City Bazaar game art (pickers, leaderboard).")
    }

    /** Rewarded ads (AdMob). Defaults are Google's TEST unit: swap in the real one from Remote Config. */
    object Ads {
        val ENABLED = ConfigKey("ads_enabled", true, "Rewarded ads (coins, lifting a play block) offered at all.")
        val REWARDED_UNIT_ID = ConfigKey("ad_rewarded_unit_id", "ca-app-pub-3940256099942544/5224354917", "AdMob rewarded ad unit id (default: Google's test unit).")
    }

    /** Every key with its default, for Remote Config's in-app defaults. */
    val all: List<ConfigKey<*>> = listOf(
        App.SERVER_BASE_URL, App.UPDATE_CONFIG, App.VOICE_ENABLED, App.PARTY_CHAT_ENABLED, App.MAX_ROOMS_PER_USER, App.PLAY_STORE_URL,
        Games.ROYAL_LUDO_ENABLED, Games.CITY_BAZAAR_ENABLED, Games.TURN_HURRY_SECONDS,
        CityBazaar.MONEY_POPUPS_DEFAULT, CityBazaar.EVENT_BREATH_MS, CityBazaar.EVENT_BREATH_MAX_MS, CityBazaar.POPUP_MS, CityBazaar.SNACKBAR_MS,
        Ads.ENABLED, Ads.REWARDED_UNIT_ID,
        Backgrounds.HOME, Backgrounds.LEADERBOARD, Backgrounds.GAME_ZONE_CARD, Backgrounds.FRIEND_ZONE_CARD, Backgrounds.ROYAL_LUDO_ART, Backgrounds.CITY_BAZAAR_ART,
    )

    val defaults: Map<String, Any> get() = all.associate { it.name to it.default }
}
