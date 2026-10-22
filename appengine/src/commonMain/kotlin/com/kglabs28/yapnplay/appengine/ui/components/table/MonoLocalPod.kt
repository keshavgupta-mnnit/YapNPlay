package com.kglabs28.yapnplay.appengine.ui.components.table

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
        verticalArrangement = Arrangement.spacedBy(Spacing.Sm),
    ) {
        // No speaker on the user's own pill.
        MonoPlayerPod(card = card, showFan = false, trailing = {})
        ArenaSeatSwitches(
            micOn = micOn,
            autoplay = autoplay,
            onToggleMic = onToggleMic,
            onToggleAutoplay = onToggleAutoplay,
            modifier = Modifier.padding(start = dimens.monoSeatAvatar),
        )
    }
}
