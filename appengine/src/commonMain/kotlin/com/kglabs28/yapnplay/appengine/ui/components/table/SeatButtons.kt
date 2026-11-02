package com.kglabs28.yapnplay.appengine.ui.components.table

import androidx.compose.ui.unit.Dp
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing

/** The round button just outside a seat's name pill (mute): this big beside an [avatar]-sized avatar. */
fun seatButtonSize(avatar: Dp): Dp = avatar * SEAT_BUTTON_SHARE

/**
 * How much wider a seat is drawn for the button outside its pill, so the
 * name keeps its room: the mute button on another person (when [canMute]);
 * the emoji smiley sits on the avatar and needs none.
 */
fun seatButtonsRoom(avatar: Dp, isMe: Boolean, canMute: Boolean): Dp =
    (seatButtonSize(avatar) + Spacing.Xxs) * if (!isMe && canMute) 1 else 0

private const val SEAT_BUTTON_SHARE = 0.55f
