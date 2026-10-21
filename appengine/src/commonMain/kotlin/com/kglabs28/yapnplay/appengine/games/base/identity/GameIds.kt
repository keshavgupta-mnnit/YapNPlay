package com.kglabs28.yapnplay.appengine.games.base.identity

/**
 * Every game's id, in one place. The same id is used by the backend
 * (GameSetup.gameId, match snapshots), the app's GameRegistry, and the
 * Remote Config kill switch `game_enabled_<id with _>` (see AppManagerKeys).
 *
 * | Game         | id            | Remote Config switch       |
 * |--------------|---------------|----------------------------|
 * | Royal Ludo   | royal-ludo    | game_enabled_royal_ludo    |
 * | City Bazaar  | city-bazaar   | game_enabled_city_bazaar   |
 */
object GameIds {
    const val ROYAL_LUDO = "royal-ludo"
    const val CITY_BAZAAR = "city-bazaar"
}
