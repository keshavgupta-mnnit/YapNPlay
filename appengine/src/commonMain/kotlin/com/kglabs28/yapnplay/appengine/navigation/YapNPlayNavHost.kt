package com.kglabs28.yapnplay.appengine.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kglabs28.yapnplay.appengine.features.about.ui.AboutScreen
import com.kglabs28.yapnplay.appengine.ui.screens.gallery.GalleryScreen
import com.kglabs28.yapnplay.appengine.ui.screens.home.HomeScreen

/**
 * App navigation. Routes are serializable destinations, so the compiler
 * checks every navigation call.
 */
@Composable
fun YapNPlayNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = Home,
        modifier = modifier,
    ) {
        composable<Home> {
            HomeScreen(
                onOpenGallery = { navController.navigate(Gallery) },
                onOpenAbout = { navController.navigate(About) },
            )
        }
        composable<Gallery> {
            GalleryScreen(onBack = { navController.popBackStack() })
        }
        composable<About> {
            AboutScreen(onBack = { navController.popBackStack() })
        }
        composable<RoomPlaceholder> {
            PlaceholderScreen()
        }
        composable<MatchPlaceholder> {
            PlaceholderScreen()
        }
    }
}
