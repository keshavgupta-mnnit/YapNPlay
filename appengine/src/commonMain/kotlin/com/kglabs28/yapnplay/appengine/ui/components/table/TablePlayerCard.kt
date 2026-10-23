package com.kglabs28.yapnplay.appengine.ui.components.table

import com.kglabs28.yapnplay.appengine.core.ui.LudoStrings
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.coerceAtLeast
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/**
 * A player's card at the Royal Ludo and City Royale tables, in the arena
 * design (the MONO seat): a round avatar in their colour ([tint]) with the
 * level under it, joined to a glass pill with the name, [details] (e.g. a
 * balance) and, on others' cards, the mute button. The turn timer runs
 * round the whole outline and a blue-pink glow shows while they talk;
 * [badge] ("1st", "Jail", "Away"…) sits under the pill. A tap
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
    details: (@Composable ColumnScope.() -> Unit)? = null,
    controls: (@Composable ColumnScope.() -> Unit)? = null,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    val avatar = avatarSize.coerceAtLeast(dimens.monoSeatAvatarCompact)
    val gap = if (density == TableCardDensity.ROOMY) Spacing.Sm else Spacing.Xs
    val press = when {
        !card.isMe && card.canSpeak -> Modifier.combinedClickable(onClick = onClick ?: {}, onLongClick = onLongPress)
        onClick != null -> Modifier.clickable(onClick = onClick)
        else -> Modifier
    }
    Column(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer { alpha = if (faded) FADED_ALPHA else 1f }
                .then(press),
        ) {
            ArenaSeatHeader(
                name = card.name,
                level = card.level,
                avatarId = card.avatarId,
                tint = tint,
                isTurn = card.isTurn,
                timerFraction = card.timerFraction,
                talking = card.talking,
                avatar = avatar,
                details = details,
                trailing = if (!card.isMe && card.canSpeak) {
                    {
                        TableMuteButton(
                            muted = card.mutedForMe,
                            description = LudoStrings.ludoMuteFor(card.name),
                            onToggle = onToggleHear,
                            modifier = Modifier.size(avatar * MUTE_SHARE),
                        )
                    }
                } else {
                    null
                },
            )
            // The user's switches under the card, centred on it, sized to it and clear of the avatar's badge.
            if (controls != null) {
                CompositionLocalProvider(LocalSeatSwitchSize provides seatSwitchSize(avatar)) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(top = Spacing.Xxs),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(gap),
                        content = controls,
                    )
                }
            }
        }
        badge?.let { label ->
            BasicText(
                text = label,
                style = typography.micro.copy(color = colors.TextPrimary),
                maxLines = 1,
                // Tucked right under the pill, just past the avatar: no gap, no drift to the middle.
                modifier = Modifier
                    .padding(start = avatar * BADGE_START)
                    .offset(y = -Spacing.Xxs)
                    .clip(YapNPlayTheme.shapes.pill)
                    .background(lerp(badgeTint, colors.Background, BADGE_DARKEN))
                    .border(dimens.hairline, badgeTint, YapNPlayTheme.shapes.pill)
                    .padding(horizontal = Spacing.Xs, vertical = Spacing.Xxxs),
            )
        }
    }
}

private const val FADED_ALPHA = 0.4f
private const val BADGE_DARKEN = 0.45f
/** Where the status badge starts (× the avatar): just inside its right edge. */
private const val BADGE_START = 0.8f
private const val MUTE_SHARE = 0.55f
