package com.kglabs28.yapnplay.appengine.navigation

import androidx.navigation.NavController
import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.CITY_BAZAAR_ID

/** Opens the table for [gameId]'s match (Royal Ludo unless another game claims it). */
fun NavController.openMatch(gameId: String, matchId: String) {
    when (gameId) {
        CITY_BAZAAR_ID -> navigate(CityBazaarMatch(matchId))
        else -> navigate(RoyalLudoMatch(matchId))
    }
}
