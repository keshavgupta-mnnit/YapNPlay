package com.kglabs28.yapnplay.appengine.navigation

import androidx.navigation.NavController
import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.CITY_BAZAAR_ID
import com.kglabs28.yapnplay.appengine.games.mono.identity.MonoConstants
import com.kglabs28.yapnplay.appengine.games.monochaos.identity.MonoChaosConstants

/** Opens the table for [gameId]'s match (Royal Ludo unless another game claims it). */
fun NavController.openMatch(gameId: String, matchId: String) {
    when (gameId) {
        CITY_BAZAAR_ID -> navigate(CityBazaarMatch(matchId))
        MonoConstants.ID -> navigate(MonoMatchRoute(matchId))
        MonoChaosConstants.ID -> navigate(MonoChaosMatchRoute(matchId))
        else -> navigate(RoyalLudoMatch(matchId))
    }
}
