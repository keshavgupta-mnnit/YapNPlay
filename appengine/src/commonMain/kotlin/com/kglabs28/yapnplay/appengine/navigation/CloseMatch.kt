package com.kglabs28.yapnplay.appengine.navigation

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.toRoute

/**
 * Closes a match table. A group match always goes back to its group,
 * even when it was rejoined from Home (Home → match): the group opens
 * over Home rather than leaving the user on the dashboard.
 */
fun NavHostController.closeMatch(entry: NavBackStackEntry, roomId: String?) {
    if (currentBackStackEntry?.id != entry.id) return
    popBackStack()
    if (roomId == null) return
    val back = currentBackStackEntry
    val inGroup = back?.destination?.hasRoute(RoomLobby::class) == true && runCatching { back.toRoute<RoomLobby>().roomId }.getOrNull() == roomId
    if (!inGroup) navigate(RoomLobby(roomId)) { launchSingleTop = true }
}
