package com.kglabs28.yapnplay.appengine.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kglabs28.yapnplay.appengine.features.about.ui.AboutScreen
import com.kglabs28.yapnplay.appengine.ui.screens.games.GamesViewModel
import com.kglabs28.yapnplay.appengine.ui.screens.main.MainScreen
import com.kglabs28.yapnplay.appengine.ui.screens.main.MainViewModel
import com.kglabs28.yapnplay.appengine.ui.screens.rooms.RoomsViewModel
import org.koin.compose.koinInject

/**
 * App navigation. Routes are serializable destinations, so the compiler
 * checks every navigation call. Main is the dashboard scaffold;
 * everything else sits outside it.
 */
@Composable
fun YapNPlayNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = Main,
        modifier = modifier,
    ) {
        composable<Main> {
            val mainViewModel: MainViewModel = koinInject()
            val gamesViewModel: GamesViewModel = koinInject()
            val roomsViewModel: RoomsViewModel = koinInject()
            MainScreen(
                onOpenMatch = { navController.navigate(MatchPlaceholder) },
                onOpenRoom = { navController.navigate(RoomPlaceholder) },
                viewModel = mainViewModel,
                gamesViewModel = gamesViewModel,
                roomsViewModel = roomsViewModel,
            )
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
