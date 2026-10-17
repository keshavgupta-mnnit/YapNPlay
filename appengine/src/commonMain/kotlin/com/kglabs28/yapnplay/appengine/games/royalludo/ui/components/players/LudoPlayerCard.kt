package com.kglabs28.yapnplay.appengine.games.royalludo.ui.components.players

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.style.TextOverflow
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.components.glass.ludoFrostedGlass
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.components.glass.turnTimerBorder
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.screens.match.LudoPlayerCardUi

/**
 * A player's card in thick neutral frosted glass (design): avatar framed
 * in their colour, their name in that colour, and "Level" with the
 * level number on one line. Only during
 * their turn does a border of their colour run round the card, shrinking
 * as time runs out. The user's own card adds [controls] under it.
 */
@Composable
fun LudoPlayerCard(
    card: LudoPlayerCardUi,
    modifier: Modifier = Modifier,
    controls: @Composable ColumnScope.() -> Unit = {},
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    val tint = card.colour.tint()
    val shape = RoundedCornerShape(dimens.ludoCardRadius)

    Column(
        modifier = modifier
            .graphicsLayer { alpha = if (card.left) LEFT_ALPHA else 1f }
            .ludoFrostedGlass(tint = colors.AvatarRingSilver, shape = shape, radius = dimens.ludoCardRadius, glow = dimens.zero)
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
            LudoCardAvatar(tint = tint, talking = card.talking)
            Column(modifier = Modifier.weight(1f)) {
                BasicText(
                    text = card.name,
                    style = typography.sectionTitle.copy(color = tint),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.Xs)) {
                    BasicText(text = Strings.ludoLevelLabel, style = typography.label.copy(color = colors.TextSecondary))
                    BasicText(text = card.level, style = typography.label.copy(color = colors.TextPrimary))
                }
            }
        }
        controls()
    }
}

private const val LEFT_ALPHA = 0.4f
private const val TIMER_DARKEN = 0.3f
