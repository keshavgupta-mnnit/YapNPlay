package com.kglabs28.yapnplay.appengine.features.profile

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.features.profile.data.remote.ProfileRemoteManager
import com.kglabs28.yapnplay.appengine.features.profile.data.ProfileRepositoryImpl
import com.kglabs28.yapnplay.appengine.features.profile.data.local.ProfileLocalManager
import com.kglabs28.yapnplay.appengine.features.profile.data.remote.ProfileApi
import com.kglabs28.yapnplay.appengine.features.profile.data.remote.dto.FriendDto
import com.kglabs28.yapnplay.appengine.features.profile.data.remote.dto.ProfileDto
import com.kglabs28.yapnplay.appengine.features.profile.domain.Friend
import com.kglabs28.yapnplay.appengine.features.profile.domain.UserProfile
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

private val mockProfileDto = ProfileDto(
    userId = "user-riya",
    displayName = "Riya",
    level = 12,
    xp = 2450,
    xpMax = 3000,
    online = true,
    coins = 4250,
    gems = 120,
)

private val mockFriendDtos = listOf(
    FriendDto(userId = "user-neo-kid", displayName = "NEO_KID", online = true),
    FriendDto(userId = "user-neo-min", displayName = "NEO_MIN", online = false),
)

private class StubApi(
    private val fail: Boolean = false,
) : ProfileApi {
    override suspend fun getProfile(): ProfileDto {
        if (fail) throw IllegalStateException("offline")
        return mockProfileDto
    }

    override suspend fun getFriends(): List<FriendDto> {
        if (fail) throw IllegalStateException("offline")
        return mockFriendDtos
    }
}

private fun repository(fail: Boolean = false) = ProfileRepositoryImpl(
    remote = ProfileRemoteManager(StubApi(fail)),
    local = ProfileLocalManager(),
)

/**
 * Repository contract: network first with cache fallback, DTOs mapped
 * to domain, failures typed. The mock API stands in for the backend.
 */
class ProfileRepositoryTest {

    @Test
    fun `profile maps dto to domain`() = runTest {
        val result = repository().fetchProfileData()
        assertIs<AppResult.Success<UserProfile>>(result)
        assertEquals("Riya", result.data.name)
        assertEquals(12, result.data.level)
        assertEquals(2450, result.data.xp)
        assertEquals(4250, result.data.coins)
        assertTrue(result.data.online)
    }

    @Test
    fun `friends map dto to domain`() = runTest {
        val result = repository().getFriendList()
        assertIs<AppResult.Success<List<Friend>>>(result)
        assertEquals(2, result.data.size)
        assertEquals("user-neo-kid", result.data.first().userId)
    }

    @Test
    fun `offline falls back to cache`() = runTest {
        val local = ProfileLocalManager()
        val online = ProfileRepositoryImpl(ProfileRemoteManager(StubApi()), local)
        assertIs<AppResult.Success<UserProfile>>(online.fetchProfileData())
        assertIs<AppResult.Success<List<Friend>>>(online.getFriendList())

        val offline = ProfileRepositoryImpl(ProfileRemoteManager(StubApi(fail = true)), local)
        val profile = offline.fetchProfileData()
        assertIs<AppResult.Success<UserProfile>>(profile)
        assertEquals("Riya", profile.data.name)
        val friends = offline.getFriendList()
        assertIs<AppResult.Success<List<Friend>>>(friends)
        assertEquals(2, friends.data.size)
    }

    @Test
    fun `offline with empty cache returns typed failure`() = runTest {
        val offline = repository(fail = true)
        assertIs<AppResult.Failure>(offline.fetchProfileData())
        assertIs<AppResult.Failure>(offline.getFriendList())
    }
}
