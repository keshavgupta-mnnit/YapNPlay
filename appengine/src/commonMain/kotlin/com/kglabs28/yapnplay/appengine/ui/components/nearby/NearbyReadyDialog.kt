package com.kglabs28.yapnplay.appengine.ui.components.nearby

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.WifiTethering
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.glassPanel
import com.kglabs28.yapnplay.appengine.core.ui.NearbyStrings
import com.kglabs28.yapnplay.appengine.core.ui.RoomStrings
import com.kglabs28.yapnplay.appengine.features.nearby.presentation.NearbyCheck
import com.kglabs28.yapnplay.appengine.features.nearby.presentation.NearbyReadyUi
import com.kglabs28.yapnplay.appengine.ui.components.common.GlassIconButton
import com.kglabs28.yapnplay.appengine.ui.components.common.IconCircle
import com.kglabs28.yapnplay.appengine.ui.components.common.PillButton
import com.kglabs28.yapnplay.appengine.ui.components.common.ShrinkText

/**
 * "Get ready for Nearby", in the Nearby card's own look: its orange glow,
 * the two-tone title, how many things are ready, then one slim row per
 * thing Nearby play needs (tick when ready; cross and ">" when not, and a
 * tap opens that setting). The orange Enter Lobby lights up only once every
 * row is ticked.
 */
@Composable
fun NearbyReadyDialog(
    ready: NearbyReadyUi,
    onFix: (NearbyCheck) -> Unit,
    onContinue: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    Column(
        modifier = modifier
            .width(dimens.modalWidth)
            .glassPanel(
                glowColor = colors.PlayNearbyAccent,
                surfaceBrush = Brush.verticalGradient(listOf(colors.HomeCardFrostTop, colors.HomeCardFrostBottom)),
            )
            .padding(Spacing.Md),
        verticalArrangement = Arrangement.spacedBy(Spacing.Sm),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.Xs),
        ) {
            IconCircle(
                imageVector = Icons.Rounded.WifiTethering,
                fill = colors.HomeIconFrost,
                size = dimens.playHubHeaderIcon,
                iconSize = dimens.homeBarIcon,
                rimColor = colors.PlayNearbyAccent,
            )
            Column(modifier = Modifier.weight(1f)) {
                ShrinkText(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = colors.TextPrimary)) { append(NearbyStrings.readyTitle) }
                        withStyle(SpanStyle(color = colors.PlayNearbyAccent)) { append(NearbyStrings.readyTitleAccent) }
                    },
                    style = typography.zoneTitle,
                )
                ShrinkText(
                    text = NearbyStrings.readyProgress(ready.readyCount, ready.items.size),
                    style = typography.caption.copy(color = if (ready.allReady) colors.NearbyReadyGreen else colors.HomeTextSoft),
                )
            }
            GlassIconButton(
                imageVector = Icons.Rounded.Close,
                contentDescription = RoomStrings.lobbyClose,
                onClick = onClose,
                size = dimens.modalCloseButton,
                iconSize = dimens.modalCloseIcon,
                tint = colors.TextPrimary,
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.Xs)) {
            ready.items.forEach { item -> NearbyReadyRow(item = item, onFix = { onFix(item.check) }) }
        }
        PillButton(
            text = NearbyStrings.readyEnterLobby,
            onClick = onContinue,
            enabled = ready.allReady,
            gradient = listOf(colors.PlayNearbyCta),
            contentColor = colors.TextPrimary,
            rimColor = colors.PlayNearbyCtaRim,
            glassFill = colors.PlayNearbyCta,
            leadingIcon = Icons.Rounded.Bolt,
            height = dimens.playHubCtaHeight,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(AppConstants.PLAY_HUB_CTA_WIDTH_FRACTION),
        )
    }
}
