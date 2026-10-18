package com.kglabs28.yapnplay.appengine.ui.components.profile

/**
 * The app's avatars a user picks from: "a01" … "a18" (art in
 * androidMain/res/drawable/ic_avatar_NN.webp; a09 and a18 are the bot
 * characters). The id is what the server stores.
 */
object AvatarPresets {
    val ids: List<String> = (1..COUNT).map { ID_PREFIX + it.toString().padStart(ID_DIGITS, '0') }

    /** Position of [avatarId] among the presets, or -1 for none (the default person icon). */
    fun index(avatarId: String): Int = ids.indexOf(avatarId)

    /** The robot avatar a seat shows while the CPU plays it for a player who left. */
    const val CPU = "a09"

    private const val COUNT = 18
    private const val ID_PREFIX = "a"
    private const val ID_DIGITS = 2
}
