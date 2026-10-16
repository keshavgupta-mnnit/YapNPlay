package com.kglabs28.yapnplay.appengine.features.profile.domain

/**
 * Everything the home screen needs in one load: the user plus friends.
 * Assembled by [GetHomeScreenData]; screens never fetch piecemeal.
 */
data class HomeData(
    val profile: UserProfile,
    val friends: List<Friend>,
)
