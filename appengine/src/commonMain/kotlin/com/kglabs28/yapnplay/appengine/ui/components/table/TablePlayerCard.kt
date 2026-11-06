package com.kglabs28.yapnplay.appengine.ui.components.table

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import com.kglabs28.yapnplay.appengine.core.ui.LudoStrings
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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
 * add / remove friend). The user's own card adds [sideControls] (Mic,
 * Auto…) beside the whole seat, centred on it, in every game alike.
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
    /** At the name pill's right end, inside it (a status icon: drawing, guessed…). */
    pillEnd: (@Composable RowScope.() -> Unit)? = null,
    /** The user's own buttons beside the seat ([ArenaSeatSwitches] sizes itself to the card). */
    sideControls: (@Composable RowScope.() -> Unit)? = null,
    /** Under the name pill, where [badge] goes (Cyber Blast's hearts); shown instead of the badge. */
    underPill: (@Composable () -> Unit)? = null,
) {
    val dimens = YapNPlayTheme.dimens
    val avatar = tableCardAvatar(avatarSize)
    val gap = if (density == TableCardDensity.ROOMY) Spacing.Sm else Spacing.Xs
    val press = when {
        // Another person (friend), or a bot's seat (the leader may offer it to a spectator).
        !card.isMe -> Modifier.combinedClickable(onClick = onClick ?: {}, onLongClick = onLongPress)
        onClick != null -> Modifier.clickable(onClick = onClick)
        else -> Modifier
    }
    // Room for the header and a tag, kept up front: the card stays put, the tag shifts inside.
    Column(modifier = modifier.heightIn(min = seatBoxHeight(avatar, dimens.seatTagSlot, dimens.zero))) {
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
                turnEndsAtMillis = card.turnEndsAtMillis,
                turnTotalMs = card.turnTotalMs,
                talking = card.talking,
                talkingMuted = card.talkingMuted,
                avatar = avatar,
                seat = card.seat,
                isMe = card.isMe,
                details = details,
                pillEnd = pillEnd,
                // The switches sized to the card, a gap between them as dense as the card.
                sideControls = sideControls?.let { side ->
                    {
                        CompositionLocalProvider(LocalSeatSwitchSize provides seatSwitchSize(avatar)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(gap),
                                content = side,
                            )
                        }
                    }
                },
                trailing = if (!card.isMe && card.canSpeak) {
                    {
                        TableMuteButton(
                            muted = card.mutedForMe,
                            description = LudoStrings.ludoMuteFor(card.name),
                            onToggle = onToggleHear,
                            modifier = Modifier.size(seatButtonSize(avatar)),
                            micOn = card.micOn,
                        )
                    }
                } else {
                    null
                },
            )
            // The tag (when one shows) right under the header.
            if (underPill != null) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(dimens.seatTagSlot).padding(start = avatar + Spacing.Xs),
                    contentAlignment = Alignment.CenterStart,
                ) { underPill() }
            } else {
                ArenaSeatTagSlot(text = badge, tint = badgeTint, avatar = avatar)
            }
        }
    }
}

/** The avatar a table card really draws for [avatarSize]: never under the compact seat's (for sizing room beside it). */
@Composable
@ReadOnlyComposable
fun tableCardAvatar(avatarSize: Dp): Dp = avatarSize.coerceAtLeast(YapNPlayTheme.dimens.monoSeatAvatarCompact)

private const val FADED_ALPHA = 0.4f
