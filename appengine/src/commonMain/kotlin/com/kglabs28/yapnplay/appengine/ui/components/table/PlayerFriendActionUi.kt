package com.kglabs28.yapnplay.appengine.ui.components.table

/** Long-press on a player card: add them as a friend, or remove them ([isFriend]), after a confirm. */
data class PlayerFriendActionUi(
    val userId: String,
    val name: String,
    val avatarId: String,
    val isFriend: Boolean,
    val busy: Boolean = false,
    val error: String? = null,
)
