package com.kglabs28.yapnplay.appengine.core.common

import com.kglabs28.yapnplay.appengine.features.profile.data.remote.dto.FriendDto
import com.kglabs28.yapnplay.appengine.features.profile.data.remote.dto.ProfileDto
import com.kglabs28.yapnplay.appengine.features.profile.data.toDomain
import com.kglabs28.yapnplay.appengine.features.profile.domain.Friend
import com.kglabs28.yapnplay.appengine.features.profile.domain.UserProfile
import com.kglabs28.yapnplay.appengine.features.playrooms.data.remote.dto.PlayRoomDto

/**
 * The single source of mock data for the whole app until the backend
 * lands. One file on purpose: every fake object lives here, sectioned
 * per feature, so nothing hides fakes in its own corner.
 */
object FakeDataProvider {

    /** How long every fake API call waits, so the UI loads like production. */
    const val NETWORK_DELAY_MS = 600L

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
            FriendDto(userId = "user-neha", displayName = "Neha", online = true),
            FriendDto(userId = "user-arjun", displayName = "Arjun", online = true),
            FriendDto(userId = "user-riya", displayName = "Riya", online = true),
            FriendDto(userId = "user-karan", displayName = "Karan", online = true),
            FriendDto(userId = "user-sneha", displayName = "Sneha", online = true),
            FriendDto(userId = "user-vikram", displayName = "Vikram", online = true),
            FriendDto(userId = "user-rohan", displayName = "Rohan", online = true),
            FriendDto(userId = "user-meera", displayName = "Meera", online = false),
            FriendDto(userId = "user-dev", displayName = "Dev", online = false),
        )

        val domain: UserProfile = dto.toDomain()
        val domainFriends: List<Friend> = friendDtos.map { it.toDomain() }
    }

    object PlayRooms {
        val myRooms = listOf(
            PlayRoomDto(
                roomId = "room-college-crew",
                name = "College Crew",
                code = "CC4821",
                memberCount = 10,
                liveCount = 6,
                isOwner = true,
                icon = "group",
            ),
            PlayRoomDto(
                roomId = "room-game-night",
                name = "Game Night Squad",
                code = "GN1907",
                memberCount = 8,
                liveCount = 4,
                isOwner = false,
                icon = "dice",
            ),
            PlayRoomDto(
                roomId = "room-family-fun",
                name = "Family Fun",
                code = "FF2210",
                memberCount = 9,
                liveCount = 2,
                isOwner = false,
                icon = "family",
            ),
            PlayRoomDto(
                roomId = "room-weekend-warriors",
                name = "Weekend Warriors",
                code = "WW0614",
                memberCount = 5,
                liveCount = 1,
                isOwner = false,
                icon = "gamepad",
            ),
        )

        /** Pretend the server accepted [code] and returned a fresh room. */
        fun joinedRoom(code: String) = PlayRoomDto(
            roomId = "room-$code",
            name = "Room $code",
            code = code,
            memberCount = 3,
            liveCount = 2,
            isOwner = false,
            icon = "group",
        )
    }
}
