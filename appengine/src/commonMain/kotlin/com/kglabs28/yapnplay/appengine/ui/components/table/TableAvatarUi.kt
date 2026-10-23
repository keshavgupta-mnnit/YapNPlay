package com.kglabs28.yapnplay.appengine.ui.components.table

/** Someone in a table's top bar strip (a spectator), with their voice ring and whether the user muted them (for the user only). */
data class TableAvatarUi(val userId: String, val name: String, val ring: AvatarRing, val mutedForMe: Boolean = false, val avatarId: String = "")
