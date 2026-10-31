package com.kglabs28.yapnplay.appengine.ui.components.table

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/**
 * The local player's own seat, at the bottom left: the same seat card as
 * everyone else's (their hand is the fan along the bottom, so none here),
 * with their two switches — the microphone and autoplay — as round icons
 * under the pill.
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
    val switch = seatSwitchSize(dimens.monoSeatAvatar)
    Column(
        // Room for the seat, a tag and the switches, kept up front: the seat stays put; the switches sit
        // right under it and move down only while a tag shows, never into the CAUGHT button or a seat.
        modifier = modifier.width(dimens.monoLocalPodWidth).heightIn(min = seatBoxHeight(dimens.monoSeatAvatar, dimens.seatTagSlot, switch)),
    ) {
        // No speaker on the user's own pill.
        MonoPlayerPod(card = card, showFan = false, trailing = {})
        ArenaSeatSwitches(
            micOn = micOn,
            autoplay = autoplay,
            onToggleMic = onToggleMic,
            onToggleAutoplay = onToggleAutoplay,
            // Centred under the whole seat, sized to it and clear of the avatar's badge.
            size = switch,
            // Knocked out or finished: no turns left, so no Autoplay.
            showAutoplay = !card.eliminated && card.placeLabel == null,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
