package com.kglabs28.yapnplay.appengine.features.profile.data.local

import com.kglabs28.yapnplay.appengine.features.profile.domain.Friend
import com.kglabs28.yapnplay.appengine.features.profile.domain.UserProfile

/**
 * Local copy of profile data: instant reads, offline survival.
 * In-memory today; persist in AppDatabase (Room) instead, keeping
 * this same API so callers never change.
 */
class ProfileLocalManager {

    private var cachedProfile: UserProfile? = null
    private var cachedFriends: List<Friend> = emptyList()

    fun profile(): UserProfile? = cachedProfile

    fun friends(): List<Friend> = cachedFriends

    fun saveProfile(profile: UserProfile) {
        cachedProfile = profile
    }

    fun saveFriends(friends: List<Friend>) {
        cachedFriends = friends
    }

    fun clear() {
        cachedProfile = null
        cachedFriends = emptyList()
    }
}
