package com.kglabs28.yapnplay.appengine.core.common

import com.kglabs28.yapnplay.appengine.features.profile.data.remote.dto.FriendDto
import com.kglabs28.yapnplay.appengine.features.profile.data.remote.dto.ProfileDto
import com.kglabs28.yapnplay.appengine.features.profile.data.toDomain
import com.kglabs28.yapnplay.appengine.features.profile.domain.Friend
import com.kglabs28.yapnplay.appengine.features.profile.domain.UserProfile

/**
 * The single source of mock data for the whole app until the backend
 * lands. One file on purpose: every fake object lives here, sectioned
 * per feature, so nothing hides fakes in its own corner.
 */
object FakeDataProvider {

    object Profile {
        val dto = ProfileDto(
            userId = "user-keshav",
            displayName = "Keshav",
            level = 12,
            xp = 2450,
            xpMax = 3000,
            online = true,
            coins = 4250,
            gems = 120,
        )

        val friendDtos = listOf(
            FriendDto(userId = "user-neo-kid", displayName = "NEO_KID", online = true),
            FriendDto(userId = "user-derry50", displayName = "DERRY50", online = true),
            FriendDto(userId = "user-cyber-vibe", displayName = "CYBER_VIBE", online = true),
            FriendDto(userId = "user-neo-min", displayName = "NEO_MIN", online = false),
            FriendDto(userId = "user-pixel-punk", displayName = "PIXEL_PUNK", online = false),
        )

        val domain: UserProfile = dto.toDomain()
        val domainFriends: List<Friend> = friendDtos.map { it.toDomain() }
    }
}
