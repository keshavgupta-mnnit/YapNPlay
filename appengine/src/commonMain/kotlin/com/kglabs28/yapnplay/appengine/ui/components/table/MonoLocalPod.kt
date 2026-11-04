package com.kglabs28.yapnplay.appengine.ui.components.table

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/**
 * The local player's own seat, at the bottom left: the same seat card as
 * everyone else's (their hand is the fan along the bottom, so none here),
 * with their two switches — the microphone and autoplay — as round icons
 * beside it, as in every game. The seat is widened by the switches' room,
 * so the name keeps the same space.
 */
@Composable
fun MonoLocalPod(
    card: MonoPlayerPodUi,
    micOn: Boolean,
    autoplay: Boolean,
    onToggleMic: () -> Unit,
    onToggleAutoplay: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dimens = YapNPlayTheme.dimens
    // Knocked out or finished: no turns left, so no Autoplay.
    val showAutoplay = !card.eliminated && card.placeLabel == null
    // No speaker on the user's own pill. Room for the seat and a tag kept up front: the seat stays put.
    MonoPlayerPod(
        card = card,
        showFan = false,
        trailing = {},
        sideControls = {
            ArenaSeatSwitches(
                micOn = micOn,
                autoplay = autoplay,
                onToggleMic = onToggleMic,
                onToggleAutoplay = onToggleAutoplay,
                size = seatSwitchSize(dimens.monoSeatAvatar),
                showAutoplay = showAutoplay,
            )
        },
        modifier = modifier
            .width(dimens.monoLocalPodWidth + seatSwitchesRoom(dimens.monoSeatAvatar))
            .heightIn(min = seatBoxHeight(dimens.monoSeatAvatar, dimens.seatTagSlot, dimens.zero)),
    )
}
