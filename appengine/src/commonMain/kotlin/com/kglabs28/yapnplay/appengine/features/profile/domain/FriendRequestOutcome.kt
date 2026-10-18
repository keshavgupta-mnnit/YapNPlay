package com.kglabs28.yapnplay.appengine.features.profile.domain

/**
 * What became of a friend request the user sent: sent; nobody has that
 * ID; it's their own ID; already friends; already asked; they've blocked
 * that person; or that person had already asked them (now friends).
 */
enum class FriendRequestOutcome { SENT, NOT_FOUND, SELF, ALREADY_FRIENDS, ALREADY_REQUESTED, BLOCKED, ACCEPTED }
