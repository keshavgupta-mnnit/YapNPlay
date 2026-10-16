package com.kglabs28.yapnplay.appengine.ui.screens.home

import app.cash.turbine.test
import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.core.testing.TestDispatcherProvider
import com.kglabs28.yapnplay.appengine.features.profile.domain.Friend
import com.kglabs28.yapnplay.appengine.features.profile.domain.UserProfile
import com.kglabs28.yapnplay.appengine.features.profile.repository.ProfileRepository
import com.kglabs28.yapnplay.appengine.features.profile.usecase.GetHomeScreenData
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

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
 * Home loads its content from the use case — never from inline values.
 */
class HomeViewModelTest {

    private fun viewModel() = HomeViewModel(
        getHomeScreenData = GetHomeScreenData(StubProfileRepository()),
        dispatchers = TestDispatcherProvider(),
    )

    @Test
    fun `load fills profile and friends from use case`() = runTest {
        val vm = viewModel()
        vm.state.test {
            assertEquals("", awaitItem().userName)
            vm.onIntent(HomeIntent.LoadProfile)
            val loaded = awaitItem()
            assertEquals("Keshav", loaded.userName)
            assertEquals("12", loaded.level)
            assertEquals("2450 / 3000", loaded.levelProgressText)
            assertTrue(loaded.friends.isNotEmpty())
            cancelAndIgnoreRemainingEvents()
        }
        vm.clear()
    }

    @Test
    fun `navigation intents emit effects once`() = runTest {
        val vm = viewModel()
        vm.effects.test {
            vm.onIntent(HomeIntent.EnterGameZone)
            assertEquals(HomeEffect.NavigateToGameZone, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        vm.clear()
    }
}
