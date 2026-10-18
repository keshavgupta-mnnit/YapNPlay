package com.kglabs28.yapnplay.appengine.games.royalludo.ui.components.players

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.style.TextOverflow
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.ui.components.table.tableFrostedGlass
import com.kglabs28.yapnplay.appengine.ui.components.table.turnTimerBorder
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.screens.match.LudoPlayerCardUi
import com.kglabs28.yapnplay.appengine.ui.components.table.TableCardAvatar
import com.kglabs28.yapnplay.appengine.ui.components.table.TableMuteButton
import com.kglabs28.yapnplay.appengine.ui.components.table.TableTalkWave

/**
 * A player's card in neutral frosted glass: avatar framed in their
 * colour with their level on a badge, and their name in bold in that
 * colour, all on one centred row. During
 * their turn a solid border of their colour runs round the card and
 * shrinks as time runs out. Once they finish, a "1st"/"2nd" badge sits on
 * the card's top edge ("Away" or "Bot" there while the server plays for them). Other players' cards have a round mute button
 * ([onToggleHear]): red while the user has muted them, grey otherwise.
 * The user's own card adds [controls] under it.
 */
@Composable
fun LudoPlayerCard(
    card: LudoPlayerCardUi,
    modifier: Modifier = Modifier,
    onToggleHear: () -> Unit = {},
    controls: @Composable ColumnScope.() -> Unit = {},
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    val tint = card.colour.tint()
    val shape = RoundedCornerShape(dimens.ludoCardRadius)

    Box(modifier = modifier, contentAlignment = Alignment.TopCenter) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dimens.ludoPlaceBadgeHeight / 2)
                .graphicsLayer { alpha = if (card.left) LEFT_ALPHA else 1f }
                .tableFrostedGlass(tint = colors.AvatarRingSilver, shape = shape, radius = dimens.ludoCardRadius, glow = dimens.zero)
                .turnTimerBorder(
                    active = card.isTurn,
                    fraction = card.timerFraction,
                    tint = lerp(tint, colors.Background, TIMER_DARKEN),
                    radius = dimens.ludoCardRadius,
                    width = dimens.ludoTimerStroke,
                )
                .padding(Spacing.Sm),
            verticalArrangement = Arrangement.spacedBy(Spacing.Sm),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.Sm)) {
                TableCardAvatar(tint = tint, level = card.level, avatarId = card.avatarId)
                // Name, and the voice wave under it.
                Column(modifier = Modifier.weight(1f)) {
                    BasicText(
                        text = card.name,
                        style = typography.cardTitle.copy(color = tint),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (card.canSpeak) TableTalkWave(talking = card.talking)
                }
                if (!card.isMe && card.canSpeak) {
                    TableMuteButton(muted = card.mutedForMe, description = Strings.ludoMuteFor(card.name), onToggle = onToggleHear)
                }
            }
            controls()
        }
        (card.placeLabel ?: card.statusLabel)?.let { label ->
            // A finishing place in the player's colour; "Away" / "Bot" in grey.
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

private const val LEFT_ALPHA = 0.4f
private const val TIMER_DARKEN = 0.3f
private const val BADGE_DARKEN = 0.45f
