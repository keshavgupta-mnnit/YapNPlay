package com.kglabs28.yapnplay.appengine.ui.components.table

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.MicOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.MonoStrings
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon

/**
 * The local player's own pod, at the far left of the bottom row: their
 * avatar and name, the number of cards they hold, and the two switches
 * they own — the microphone and autoplay.
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
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val shape = RoundedCornerShape(dimens.ludoCardRadius)

    Row(
        modifier = modifier
            .width(dimens.monoLocalPodWidth)
            .tableFrostedGlass(
                tint = card.tint,
                shape = shape,
                radius = dimens.ludoCardRadius,
                glow = dimens.zero,
            )
            .padding(horizontal = Spacing.Sm, vertical = Spacing.Xxs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Xs),
    ) {
        TableCardAvatar(
            tint = card.tint,
            level = card.level,
            avatarId = card.avatarId,
            size = dimens.monoPodAvatar,
        )
        Column(modifier = Modifier.weight(1f)) {
            BasicText(
                text = card.name,
                style = YapNPlayTheme.typography.cardTitle.copy(color = card.tint),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            BasicText(
                text = MonoStrings.monoCards(card.cards),
                style = YapNPlayTheme.typography.micro.copy(color = colors.TextSecondary),
                maxLines = 1,
            )
        }
        MonoToggleButton(
            icon = if (micOn) Icons.Rounded.Mic else Icons.Rounded.MicOff,
            description = MonoStrings.monoMic,
            on = micOn,
            onClick = onToggleMic,
        )
        MonoToggleButton(
            icon = Icons.Rounded.Bolt,
            description = MonoStrings.monoAutoplay,
            on = autoplay,
            onClick = onToggleAutoplay,
        )
    }
}

/** A small round switch: lit when the setting is on. */
@Composable
private fun MonoToggleButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    description: String,
    on: Boolean,
    onClick: () -> Unit,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val tint = if (on) colors.CtaGreenStart else colors.IconMuted
    Box(
        modifier = Modifier
            .size(dimens.monoToggleSize)
            .clip(CircleShape)
            .background(colors.Panel)
            .border(dimens.hairline, tint.copy(alpha = TOGGLE_RIM_ALPHA), CircleShape)
            .clickable(role = Role.Button, onClick = onClick)
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center,
    ) {
        AppIcon(imageVector = icon, tint = tint, size = dimens.iconSmall)
    }
}

private const val TOGGLE_RIM_ALPHA = 0.7f
