package com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.players

import androidx.compose.foundation.background
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.coerceAtMost
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.ui.components.table.TableCardAvatar
import com.kglabs28.yapnplay.appengine.ui.components.table.TableMuteButton
import com.kglabs28.yapnplay.appengine.ui.components.table.TableTalkWave
import com.kglabs28.yapnplay.appengine.ui.components.table.tableFrostedGlass
import com.kglabs28.yapnplay.appengine.ui.components.table.turnTimerBorder
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.board.tint
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbPlayerCardUi
import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.RupeeFormat

/**
 * A player's HUD pod (design): frosted glass, avatar framed in their colour
 * with the level badge, name in that colour and the live balance in gold.
 * A shrinking border in their colour runs round it during their turn; a
 * pill on the top edge says "In Jail", "Away", "Bankrupt" or their place.
 * Others' cards have the mute button; the user's adds [controls]. A tap
 * ([onClick]) opens everything about the player.
 */
@Composable
fun CityBazaarPlayerCard(
    card: CbPlayerCardUi,
    modifier: Modifier = Modifier,
    onToggleHear: () -> Unit = {},
    onClick: () -> Unit = {},
    avatarSize: Dp = YapNPlayTheme.dimens.cbCardAvatar,
    /** Tighter padding and smaller text, for many players. */
    compact: Boolean = false,
    controls: @Composable ColumnScope.() -> Unit = {},
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    val tint = card.colour.tint()
    val shape = RoundedCornerShape(dimens.ludoCardRadius)
    // The balance counts to its new value: green going up, red going down.
    val shownCash by animateIntAsState(card.cash, tween(AppConstants.CB_CASH_COUNT_MS))
    val cashColour = when {
        shownCash < card.cash -> colors.CbGain
        shownCash > card.cash -> colors.CbLoss
        else -> colors.CbCash
    }
    Box(modifier = modifier, contentAlignment = Alignment.TopCenter) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dimens.ludoPlaceBadgeHeight / 2)
                .graphicsLayer { alpha = if (card.out) OUT_ALPHA else 1f }
                .tableFrostedGlass(tint = colors.AvatarRingSilver, shape = shape, radius = dimens.ludoCardRadius, glow = dimens.zero)
                .clickable(onClick = onClick)
                .turnTimerBorder(active = card.isTurn, fraction = card.timerFraction, tint = lerp(tint, colors.Background, TIMER_DARKEN), radius = dimens.ludoCardRadius, width = dimens.ludoTimerStroke)
                .padding(if (compact) Spacing.Xxs else Spacing.Xs),
            verticalArrangement = Arrangement.spacedBy(Spacing.Xs),
        ) {
            // Room under the avatar for its level badge, clear of the turn border.
            Row(
                modifier = Modifier.padding(bottom = dimens.ludoLevelBadgeHeight / 2),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.Xs),
            ) {
                TableCardAvatar(tint = tint, level = card.level, size = avatarSize)
                // Middle column: name, balance and the voice wave.
                Column(modifier = Modifier.weight(1f)) {
                    val nameStyle = if (compact) typography.micro.copy(fontWeight = FontWeight.Bold) else typography.cardTitle
                    val cashStyle = if (compact) typography.micro else typography.label
                    BasicText(text = card.name, style = nameStyle.copy(color = tint), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    BasicText(text = Strings.cbRupees(RupeeFormat.digits(shownCash)), style = cashStyle.copy(color = cashColour, fontWeight = FontWeight.Bold), maxLines = 1)
                    TableTalkWave(talking = card.talking)
                }
                if (!card.isMe) {
                    TableMuteButton(
                        muted = card.mutedForMe,
                        description = Strings.ludoMuteFor(card.name),
                        onToggle = onToggleHear,
                        modifier = if (compact) Modifier.size(avatarSize.coerceAtMost(dimens.ludoMuteButton)) else Modifier,
                    )
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
