package com.kglabs28.yapnplay.appengine.navigation

import com.kglabs28.yapnplay.appengine.ui.screens.history.MatchHistoryScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CityBazaarMatchScreen
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.screens.match.RoyalLudoMatchScreen
import com.kglabs28.yapnplay.appengine.core.common.DeepLink
import com.kglabs28.yapnplay.appengine.ui.screens.playroomlobby.PlayRoomLobbyScreen
import com.kglabs28.yapnplay.appengine.ui.screens.gamezone.GameZoneViewModel
import com.kglabs28.yapnplay.appengine.ui.screens.main.MainScreen
import com.kglabs28.yapnplay.appengine.ui.screens.mono.MatchMonoChaosScreen
import com.kglabs28.yapnplay.appengine.ui.screens.mono.MatchMonoScreen
import com.kglabs28.yapnplay.appengine.ui.screens.profile.ProfileScreen
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
    deepLink: DeepLink? = null,
    onDeepLinkHandled: () -> Unit = {},
    navController: NavHostController = rememberNavController(),
) {
    // Invite code from a link, waiting for the Rooms tab to ask "Join?".
    var pendingInviteCode by remember { mutableStateOf<String?>(null) }
    // A friend picked on Profile for Create New Group on the Rooms tab.
    var pendingCreateGroupWith by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(deepLink) {
        when (deepLink) {
            // Notification tap: straight to that room lobby.
            is DeepLink.OpenRoom -> navController.navigate(RoomLobby(deepLink.roomId)) {
                popUpTo<Main>()
                launchSingleTop = true
            }
            // Invite link: back to the dashboard; Rooms asks to join.
            is DeepLink.JoinRoom -> {
                navController.popBackStack<Main>(inclusive = false)
                pendingInviteCode = deepLink.code
            }
            null -> return@LaunchedEffect
        }
        onDeepLinkHandled()
    }

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
                onOpenMatch = { gameId, matchId -> navController.openMatch(gameId, matchId) },
                onOpenRoom = { roomId -> navController.navigate(RoomLobby(roomId)) },
                onCreateRoom = { navController.navigate(RoomPlaceholder) },
                onOpenProfile = { navController.navigate(Profile) },
                onOpenHistory = { navController.navigate(MatchHistory) },
                pendingInviteCode = pendingInviteCode,
                onInviteConsumed = { pendingInviteCode = null },
                pendingCreateGroupWith = pendingCreateGroupWith,
                onCreateGroupConsumed = { pendingCreateGroupWith = null },
                viewModel = mainViewModel,
                gameZoneViewModel = gameZoneViewModel,
                playRoomsViewModel = playRoomsViewModel,
            )
        }
        composable<Profile> { entry ->
            ProfileScreen(
                onBack = { navController.popIfOnTop(entry) },
                onOpenHistory = { navController.navigate(MatchHistory) },
                onCreateGroupWith = { userId ->
                    pendingCreateGroupWith = userId
                    navController.popBackStack(Main, inclusive = false)
                },
                viewModel = entry.rememberScreenViewModel(),
            )
        }
        composable<MatchHistory> { entry ->
            MatchHistoryScreen(
                onBack = { navController.popIfOnTop(entry) },
                viewModel = entry.rememberScreenViewModel(),
            )
        }
        composable<RoomLobby> { entry ->
            PlayRoomLobbyScreen(
                roomId = entry.toRoute<RoomLobby>().roomId,
                onClose = { navController.popIfOnTop(entry) },
                onOpenMatch = { gameId, matchId -> navController.openMatch(gameId, matchId) },
                viewModel = entry.rememberScreenViewModel(),
            )
        }
        composable<RoomPlaceholder> {
            PlaceholderScreen()
        }
        composable<CityBazaarMatch> { entry ->
            CityBazaarMatchScreen(
                matchId = entry.toRoute<CityBazaarMatch>().matchId,
                onClose = { navController.popIfOnTop(entry) },
                viewModel = entry.rememberScreenViewModel(),
            )
        }
        composable<RoyalLudoMatch> { entry ->
            RoyalLudoMatchScreen(
                matchId = entry.toRoute<RoyalLudoMatch>().matchId,
                onClose = { navController.popIfOnTop(entry) },
                viewModel = entry.rememberScreenViewModel(),
            )
        }
        composable<MonoMatchRoute> { entry ->
            MatchMonoScreen(
                matchId = entry.toRoute<MonoMatchRoute>().matchId,
                onClose = { navController.popIfOnTop(entry) },
                viewModel = entry.rememberScreenViewModel(),
            )
        }
        composable<MonoChaosMatchRoute> { entry ->
            MatchMonoChaosScreen(
                matchId = entry.toRoute<MonoChaosMatchRoute>().matchId,
                onClose = { navController.popIfOnTop(entry) },
                viewModel = entry.rememberScreenViewModel(),
            )
        }
    }
}
