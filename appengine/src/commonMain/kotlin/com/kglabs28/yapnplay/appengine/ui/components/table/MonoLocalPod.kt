package com.kglabs28.yapnplay.appengine.ui.components.table

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
    Column(
        modifier = modifier.width(dimens.monoLocalPodWidth),
        // The switches sit right under the pill.
    ) {
        // No speaker on the user's own pill. Under it, the tag's fixed slot, then the switches:
        // a tag coming or going never moves the seat or pushes the switches into anything.
        MonoPlayerPod(card = card, showFan = false, trailing = {})
        ArenaSeatSwitches(
            micOn = micOn,
            autoplay = autoplay,
            onToggleMic = onToggleMic,
            onToggleAutoplay = onToggleAutoplay,
            // Centred under the whole seat, sized to it and clear of the avatar's badge.
            size = seatSwitchSize(dimens.monoSeatAvatar),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
