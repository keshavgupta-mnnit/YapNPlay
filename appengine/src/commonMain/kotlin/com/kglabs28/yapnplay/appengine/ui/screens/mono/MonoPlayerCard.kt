package com.kglabs28.yapnplay.appengine.ui.screens.mono

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.table.TableCardAvatar
import com.kglabs28.yapnplay.appengine.ui.components.table.TableTalkWave
import com.kglabs28.yapnplay.appengine.ui.components.table.tableFrostedGlass
import com.kglabs28.yapnplay.appengine.ui.components.table.turnTimerBorder

/**
 * A MONO player's card, in the same frosted glass as the Royal Ludo
 * table: their avatar framed in their colour with the level on a badge,
 * their name in bold in that colour, and how many cards they hold. While
 * it is their turn a solid border of their colour runs round the card
 * and shrinks as the turn runs out. "1st"/"2nd" or "Away"/"Bot" sits on
 * the top edge once they finish or the server plays for them.
 */
@Composable
fun MonoPlayerCard(
    card: MonoPlayerCardUi,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    val tint = card.tint
    val shape = RoundedCornerShape(dimens.ludoCardRadius)

    Box(modifier = modifier, contentAlignment = Alignment.TopCenter) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dimens.ludoPlaceBadgeHeight / 2)
                .tableFrostedGlass(
                    tint = colors.AvatarRingSilver,
                    shape = shape,
                    radius = dimens.ludoCardRadius,
                    glow = dimens.zero,
                )
                .turnTimerBorder(
                    active = card.isTurn,
                    fraction = card.timerFraction,
                    tint = lerp(tint, colors.Background, TIMER_DARKEN),
                    radius = dimens.ludoCardRadius,
                    width = dimens.ludoTimerStroke,
                )
                .padding(Spacing.Sm),
            verticalArrangement = Arrangement.spacedBy(Spacing.Xxs),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.Sm),
            ) {
                TableCardAvatar(tint = tint, level = card.level, avatarId = card.avatarId)
                Column(modifier = Modifier.weight(1f)) {
                    BasicText(
                        text = card.name,
                        style = typography.cardTitle.copy(color = tint),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (card.canSpeak) TableTalkWave(talking = card.talking)
                }
            }
            // How many cards they are holding — the one number a card
            // game player cares about.
            BasicText(
                text = card.cardsLabel,
                style = typography.cardTitle.copy(color = colors.TextPrimary, fontWeight = FontWeight.Bold),
                maxLines = 1,
            )
        }
        (card.placeLabel ?: card.statusLabel)?.let { label ->
            val badgeTint = if (card.placeLabel != null) tint else colors.IconMuted
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

/** A MONO player, resolved for the card. */
data class MonoPlayerCardUi(
    val seat: Int,
    val name: String,
    val avatarId: String = "",
    /** Level number on the avatar's badge ("" for a CPU: no badge). */
    val level: String,
    /** The colour their name and frame wear. */
    val tint: Color,
    val isMe: Boolean,
    val isTurn: Boolean,
    val talking: Boolean,
    /** How many cards they hold. */
    val cards: Int,
    /** 1 → 0 while it is this player's turn. */
    val timerFraction: Float = 1f,
    /** "1st", "2nd"… once they have finished; null while playing. */
    val placeLabel: String? = null,
    /** "Away" or "Bot" while the server plays for them; null otherwise. */
    val statusLabel: String? = null,
    /** False for a CPU: no voice wave (it never talks). */
    val canSpeak: Boolean = true,
) {
    /** "7 cards", "1 card" — the count on the card. */
    val cardsLabel: String get() = if (cards == 1) "1 card" else "$cards cards"
}

private const val TIMER_DARKEN = 0.3f
private const val BADGE_DARKEN = 0.45f