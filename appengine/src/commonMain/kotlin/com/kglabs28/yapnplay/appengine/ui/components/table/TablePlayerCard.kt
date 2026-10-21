package com.kglabs28.yapnplay.appengine.ui.components.table

import com.kglabs28.yapnplay.appengine.core.ui.LudoStrings
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.coerceAtMost
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/**
 * A player's card at any game table: avatar with level, name in their
 * colour ([tint]), [details] under it (e.g. a balance), the voice wave,
 * and on others' cards the mute button. The turn timer runs round the
 * border; [badge] ("1st", "Jail", "Away"…) sits on the top edge. A tap
 * ([onClick]) and, on another person's card, a long press ([onLongPress],
 * add / remove friend). The user's own card adds [controls] under it.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TablePlayerCard(
    card: TablePlayerCardUi,
    tint: Color,
    modifier: Modifier = Modifier,
    /** Out of the game (left, bankrupt): drawn faded. */
    faded: Boolean = false,
    badge: String? = null,
    badgeTint: Color = tint,
    onToggleHear: () -> Unit = {},
    onClick: (() -> Unit)? = null,
    onLongPress: () -> Unit = {},
    avatarSize: Dp = YapNPlayTheme.dimens.ludoCardAvatar,
    density: TableCardDensity = TableCardDensity.ROOMY,
    details: @Composable ColumnScope.() -> Unit = {},
    controls: @Composable ColumnScope.() -> Unit = {},
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    val shape = RoundedCornerShape(dimens.ludoCardRadius)
    val compact = density == TableCardDensity.COMPACT
    val gap = if (density == TableCardDensity.ROOMY) Spacing.Sm else Spacing.Xs
    val padding = when (density) {
        TableCardDensity.ROOMY -> Spacing.Sm
        TableCardDensity.DENSE -> Spacing.Xs
        TableCardDensity.COMPACT -> Spacing.Xxs
    }
    val press = when {
        !card.isMe && card.canSpeak -> Modifier.combinedClickable(onClick = onClick ?: {}, onLongClick = onLongPress)
        onClick != null -> Modifier.clickable(onClick = onClick)
        else -> Modifier
    }
    Box(modifier = modifier, contentAlignment = Alignment.TopCenter) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dimens.ludoPlaceBadgeHeight / 2)
                .graphicsLayer { alpha = if (faded) FADED_ALPHA else 1f }
                .tableFrostedGlass(tint = colors.AvatarRingSilver, shape = shape, radius = dimens.ludoCardRadius, glow = dimens.zero)
                .then(press)
                .turnTimerBorder(
                    active = card.isTurn,
                    fraction = card.timerFraction,
                    tint = lerp(tint, colors.Background, TIMER_DARKEN),
                    radius = dimens.ludoCardRadius,
                    width = dimens.ludoTimerStroke,
                )
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(gap),
        ) {
            Row(
                // Dense cards leave room under the avatar for its level badge, clear of the turn border.
                modifier = if (density == TableCardDensity.ROOMY) Modifier else Modifier.padding(bottom = dimens.ludoLevelBadgeHeight / 2),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(gap),
            ) {
                TableCardAvatar(tint = tint, level = card.level, size = avatarSize, avatarId = card.avatarId)
                // Name, the game's details, and the voice wave.
                Column(modifier = Modifier.weight(1f)) {
                    val nameStyle = if (compact) typography.micro.copy(fontWeight = FontWeight.Bold) else typography.cardTitle
                    BasicText(text = card.name, style = nameStyle.copy(color = tint), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    details()
                    if (card.canSpeak) TableTalkWave(talking = card.talking)
                }
                if (!card.isMe && card.canSpeak) {
                    TableMuteButton(
                        muted = card.mutedForMe,
                        description = LudoStrings.ludoMuteFor(card.name),
                        onToggle = onToggleHear,
                        modifier = if (compact) Modifier.size(avatarSize.coerceAtMost(dimens.ludoMuteButton)) else Modifier,
                    )
                }
            }
            controls()
        }
        badge?.let { label ->
            BasicText(
                text = label,
                style = typography.label.copy(color = colors.TextPrimary),
                modifier = Modifier
                    .clip(YapNPlayTheme.shapes.pill)
                    .background(lerp(badgeTint, colors.Background, BADGE_DARKEN))
                    .border(dimens.hairline, badgeTint, YapNPlayTheme.shapes.pill)
                    .padding(horizontal = Spacing.Sm, vertical = Spacing.Xxxs),
            )
        }
    }
}

private const val FADED_ALPHA = 0.4f
private const val TIMER_DARKEN = 0.3f
private const val BADGE_DARKEN = 0.45f
