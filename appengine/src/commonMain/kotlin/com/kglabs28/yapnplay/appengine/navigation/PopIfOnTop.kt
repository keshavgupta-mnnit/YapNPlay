package com.kglabs28.yapnplay.appengine.navigation

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController

/**
 * Closes [entry]'s screen, but only while it is the one on top. A second
 * close (a double tap on Back, or Back during the exit animation) would
 * otherwise pop the screen under it too, down to an empty, black app.
 */
fun NavHostController.popIfOnTop(entry: NavBackStackEntry) {
    if (currentBackStackEntry?.id == entry.id) popBackStack()
}
