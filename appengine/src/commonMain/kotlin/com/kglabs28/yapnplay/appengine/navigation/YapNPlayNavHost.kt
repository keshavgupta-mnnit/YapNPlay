package com.kglabs28.yapnplay.appengine.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.kglabs28.yapnplay.appengine.ui.screens.playroomlobby.PlayRoomLobbyScreen
import com.kglabs28.yapnplay.appengine.features.about.ui.AboutScreen
import com.kglabs28.yapnplay.appengine.ui.screens.gamezone.GameZoneViewModel
import com.kglabs28.yapnplay.appengine.ui.screens.main.MainScreen
import com.kglabs28.yapnplay.appengine.ui.screens.main.MainViewModel
import com.kglabs28.yapnplay.appengine.ui.screens.playrooms.PlayRoomsViewModel

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
        composable<Main> { entry ->
            // Scoped to the back-stack entry: the selected tab and tab
            // data survive a trip to another screen and back.
            val mainViewModel: MainViewModel = entry.rememberScreenViewModel()
            val gameZoneViewModel: GameZoneViewModel = entry.rememberScreenViewModel()
            val playRoomsViewModel: PlayRoomsViewModel = entry.rememberScreenViewModel()
            MainScreen(
                onOpenMatch = { navController.navigate(MatchPlaceholder) },
                onOpenRoom = { roomId -> navController.navigate(RoomLobby(roomId)) },
                onCreateRoom = { navController.navigate(RoomPlaceholder) },
                viewModel = mainViewModel,
                gameZoneViewModel = gameZoneViewModel,
                playRoomsViewModel = playRoomsViewModel,
            )
        }
        composable<About> { entry ->
            AboutScreen(
                onBack = { navController.popBackStack() },
                viewModel = entry.rememberScreenViewModel(),
            )
        }
        composable<RoomLobby> { entry ->
            PlayRoomLobbyScreen(
                roomId = entry.toRoute<RoomLobby>().roomId,
                onClose = { navController.popBackStack() },
                onOpenMatch = { navController.navigate(MatchPlaceholder) },
                viewModel = entry.rememberScreenViewModel(),
            )
        }
        composable<RoomPlaceholder> {
            PlaceholderScreen()
        }
        composable<MatchPlaceholder> {
            PlaceholderScreen()
        }
    }
}
