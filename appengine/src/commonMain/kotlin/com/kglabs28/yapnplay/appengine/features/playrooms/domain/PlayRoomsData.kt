package com.kglabs28.yapnplay.appengine.features.playrooms.domain

import com.kglabs28.yapnplay.appengine.features.profile.domain.Friend

/**
 * Everything the Rooms tab needs in one load: the user's rooms plus the
 * friends who are online right now. Assembled by GetPlayRoomsScreenDataUseCase.
 */
data class PlayRoomsData(
    val rooms: List<PlayRoom>,
    val onlineFriends: List<Friend>,
)
