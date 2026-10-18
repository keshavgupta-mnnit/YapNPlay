package com.kglabs28.yapnplay.appengine.features.profile.data.local

import com.kglabs28.yapnplay.appengine.features.profile.domain.Friend
import com.kglabs28.yapnplay.appengine.features.profile.domain.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Local copy of profile data: instant reads, offline survival, and a live
 * [profileFlow] so every screen shows the same name and avatar after an
 * edit. In-memory today; persist in AppDatabase (Room) instead, keeping
 * this same API so callers never change.
 */
class ProfileLocalManager {

    private val cachedProfile = MutableStateFlow<UserProfile?>(null)
    private var cachedFriends: List<Friend> = emptyList()

    val profileFlow: StateFlow<UserProfile?> = cachedProfile.asStateFlow()

    fun profile(): UserProfile? = cachedProfile.value

    fun friends(): List<Friend> = cachedFriends

    fun saveProfile(profile: UserProfile) {
        cachedProfile.value = profile
    }

    fun saveFriends(friends: List<Friend>) {
        cachedFriends = friends
    }

    fun clear() {
        cachedProfile.value = null
        cachedFriends = emptyList()
    }
}
