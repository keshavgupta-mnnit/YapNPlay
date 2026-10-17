package com.kglabs28.yapnplay.appengine.games.citybaazar.ui.components.players

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.ui.components.table.TableCardAvatar
import com.kglabs28.yapnplay.appengine.ui.components.table.TableMuteButton
import com.kglabs28.yapnplay.appengine.ui.components.table.tableFrostedGlass
import com.kglabs28.yapnplay.appengine.ui.components.table.turnTimerBorder
import com.kglabs28.yapnplay.appengine.games.citybaazar.ui.components.board.tint
import com.kglabs28.yapnplay.appengine.games.citybaazar.ui.screens.match.CbPlayerCardUi

/**
 * A player's HUD pod (design): frosted glass, avatar framed in their colour
 * with the level badge, name in that colour and the live balance in gold.
 * A shrinking border in their colour runs round it during their turn; a
 * pill on the top edge says "In Jail", "Away", "Bankrupt" or their place.
 * Others' cards have the mute button; the user's adds [controls].
 */
@Composable
fun CityBaazarPlayerCard(
    card: CbPlayerCardUi,
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
                .graphicsLayer { alpha = if (card.out) OUT_ALPHA else 1f }
                .tableFrostedGlass(tint = colors.AvatarRingSilver, shape = shape, radius = dimens.ludoCardRadius, glow = dimens.zero)
                .turnTimerBorder(active = card.isTurn, fraction = card.timerFraction, tint = lerp(tint, colors.Background, TIMER_DARKEN), radius = dimens.ludoCardRadius, width = dimens.ludoTimerStroke)
                .padding(Spacing.Xs),
            verticalArrangement = Arrangement.spacedBy(Spacing.Xs),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.Xs)) {
                TableCardAvatar(tint = tint, talking = card.talking, level = card.level, size = dimens.cbCardAvatar)
                Column(modifier = Modifier.weight(1f)) {
                    BasicText(text = card.name, style = typography.cardTitle.copy(color = tint), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    BasicText(text = card.cash, style = typography.title.copy(color = colors.CbCash, fontWeight = FontWeight.Bold), maxLines = 1)
                }
                if (!card.isMe) {
                    TableMuteButton(muted = card.mutedForMe, description = Strings.ludoMuteFor(card.name), onToggle = onToggleHear)
                }
            }
            controls()
        }
        card.status?.let { status ->
            BasicText(
                text = status,
                style = typography.label.copy(color = colors.TextPrimary),
                modifier = Modifier
                    .clip(YapNPlayTheme.shapes.pill)
                    .background(lerp(tint, colors.Background, BADGE_DARKEN))
                    .border(dimens.hairline, tint, YapNPlayTheme.shapes.pill)
                    .padding(horizontal = Spacing.Sm, vertical = Spacing.Xxxs),
            )
        }
    }
}

private const val OUT_ALPHA = 0.4f
private const val TIMER_DARKEN = 0.3f
private const val BADGE_DARKEN = 0.45f
