package com.kglabs28.yapnplay.appengine.ui.screens.main

import app.cash.turbine.test
import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.core.testing.TestDispatcherProvider
import com.kglabs28.yapnplay.appengine.features.profile.domain.Friend
import com.kglabs28.yapnplay.appengine.features.profile.domain.UserProfile
import com.kglabs28.yapnplay.appengine.features.profile.repository.ProfileRepository
import com.kglabs28.yapnplay.appengine.features.profile.usecase.GetHomeScreenData
import com.kglabs28.yapnplay.appengine.navigation.AppNavTab
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

private class StubProfileRepository : ProfileRepository {
    override suspend fun fetchProfileData(): AppResult<UserProfile> = AppResult.Success(
        UserProfile(
            name = "Keshav",
            level = 12,
            xp = 2450,
            xpMax = 3000,
            online = true,
            coins = 4250,
            gems = 120,
        ),
    )

    override suspend fun getFriendList(): AppResult<List<Friend>> = AppResult.Success(
        listOf(Friend(userId = "user-neo-kid", name = "NEO_KID", online = true)),
    )
}

/**
 * Dashboard loads its top bar from the use case — never from inline
 * values — and switches tabs in state.
 */
class MainViewModelTest {

    private fun viewModel() = MainViewModel(
        getHomeScreenData = GetHomeScreenData(StubProfileRepository()),
        dispatchers = TestDispatcherProvider(),
    )

    @Test
    fun `load fills top bar from use case`() = runTest {
        val vm = viewModel()
        vm.state.test {
            assertEquals("", awaitItem().userName)
            vm.onIntent(MainIntent.LoadProfile)
            val loaded = awaitItem()
            assertEquals("Keshav", loaded.userName)
            assertEquals("12", loaded.level)
            assertEquals("4,250 +", loaded.coinBalance)
            assertEquals("120 +", loaded.diamondBalance)
            cancelAndIgnoreRemainingEvents()
        }
        vm.clear()
    }

    @Test
    fun `select tab updates state`() = runTest {
        val vm = viewModel()
        vm.state.test {
            assertEquals(AppNavTab.HOME, awaitItem().selectedTab)
            vm.onIntent(MainIntent.SelectTab(AppNavTab.GAMES))
            assertEquals(AppNavTab.GAMES, awaitItem().selectedTab)
            vm.onIntent(MainIntent.SelectTab(AppNavTab.ROOMS))
            assertEquals(AppNavTab.ROOMS, awaitItem().selectedTab)
            cancelAndIgnoreRemainingEvents()
        }
        vm.clear()
    }

    @Test
    fun `settings and notifications emit effects once`() = runTest {
        val vm = viewModel()
        vm.effects.test {
            vm.onIntent(MainIntent.OpenSettings)
            assertEquals(MainEffect.OpenSettingsDialog, awaitItem())
            vm.onIntent(MainIntent.OpenNotifications)
            assertEquals(MainEffect.OpenNotificationsDialog, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        vm.clear()
    }
}
