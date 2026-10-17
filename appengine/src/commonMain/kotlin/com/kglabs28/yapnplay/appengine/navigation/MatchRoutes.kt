package com.kglabs28.yapnplay.appengine.navigation

import androidx.navigation.NavController
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.CITY_BAAZAR_ID

/** Opens the table for [gameId]'s match (Royal Ludo unless another game claims it). */
fun NavController.openMatch(gameId: String, matchId: String) {
    when (gameId) {
        CITY_BAAZAR_ID -> navigate(CityBaazarMatch(matchId))
        else -> navigate(RoyalLudoMatch(matchId))
    }
}
