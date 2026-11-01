package com.kglabs28.yapnplay.appengine.ui.components.table

import androidx.compose.ui.unit.Dp
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing

/** The round buttons just outside a seat's name pill (mute, emoji): this big beside an [avatar]-sized avatar. */
fun seatButtonSize(avatar: Dp): Dp = avatar * SEAT_BUTTON_SHARE

/**
 * How much wider a seat is drawn for the buttons outside its pill, so the
 * name keeps its room: the emoji button on anyone but the user, and the mute
 * button too when [canMute] (a person, not a CPU).
 */
fun seatButtonsRoom(avatar: Dp, isMe: Boolean, canMute: Boolean): Dp {
    val buttons = when {
        isMe -> 0
        canMute -> 2
        else -> 1
    }
    return (seatButtonSize(avatar) + Spacing.Xxs) * buttons
}

private const val SEAT_BUTTON_SHARE = 0.55f
