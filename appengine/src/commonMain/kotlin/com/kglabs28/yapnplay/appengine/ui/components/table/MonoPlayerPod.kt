package com.kglabs28.yapnplay.appengine.ui.components.table

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.MonoStrings

/**
 * One player at the table: a frosted-glass pod carrying their avatar,
 * level badge, name and voice wave, with a miniature fan of face-down
 * cards and their count docked to the right edge. The pod's border
 * shrinks as their turn runs out, and a finished or away player wears a
 * badge on the top edge.
 *
 * [compact] is for the narrow left and right rails of the U.
 */
@Composable
fun MonoPlayerPod(
    card: MonoPlayerPodUi,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    val tint = card.tint
    val shape = RoundedCornerShape(dimens.ludoCardRadius)
    val fanWidth = if (compact) dimens.monoPodFanCompact else dimens.monoPodFan

    Box(modifier = modifier, contentAlignment = Alignment.TopCenter) {
        Row(
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
                .padding(horizontal = Spacing.Sm, vertical = Spacing.Xxs),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.Xxs),
        ) {
            TableCardAvatar(
                tint = tint,
                level = card.level,
                avatarId = card.avatarId,
                size = if (compact) dimens.monoPodAvatarCompact else dimens.monoPodAvatar,
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Spacing.Xxxs),
            ) {
                BasicText(
                    text = card.name,
                    style = typography.cardTitle.copy(color = tint),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (card.canSpeak) TableTalkWave(talking = card.talking)
            }
            MonoCardFan(
                count = card.cards,
                cardWidth = fanWidth,
                eliminated = card.eliminated,
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
        // A player on one card who called MONO! wears a pulsing shield.
        if (card.calledMono) {
            MonoCalledBadge(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = Spacing.Xs),
            )
        }
    }
}

/** A player, resolved for the pod. */
data class MonoPlayerPodUi(
    val seat: Int,
    val name: String,
    val avatarId: String = "",
    /** The level in the avatar's badge ("" for a CPU). */
    val level: String,
    /** The colour their name and frame wear. */
    val tint: Color,
    val isMe: Boolean,
    val isTurn: Boolean,
    val talking: Boolean,
    val cards: Int,
    val timerFraction: Float = 1f,
    val placeLabel: String? = null,
    val statusLabel: String? = null,
    val canSpeak: Boolean = true,
    /** Knocked out at the 25-card threshold. */
    val eliminated: Boolean = false,
    /** Down to one card with the callout made. */
    val calledMono: Boolean = false,
)

/**
 * The miniature fan of face-down cards docked to a pod, with the count on
 * a high-contrast badge. It never grows past [FAN_CARDS] wide: the badge
 * carries the exact number.
 */
@Composable
fun MonoCardFan(
    count: Int,
    cardWidth: Dp,
    modifier: Modifier = Modifier,
    eliminated: Boolean = false,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val shown = count.coerceIn(0, FAN_CARDS)

    Box(
        modifier = modifier.width(cardWidth + FAN_OVERLAP * (shown - 1).coerceAtLeast(0)),
        contentAlignment = Alignment.CenterStart,
    ) {
        val faceDownLook = MonoCardLook(
            fill = colors.MonoBackBase,
            rim = colors.MonoBackRim,
        )
        repeat(shown) { index ->
            MonoCardFace(
                look = faceDownLook,
                faceDown = true,
                modifier = Modifier
                    .width(cardWidth)
                    .graphicsLayer { translationX = FAN_OVERLAP.toPx() * index },
            )
        }
        // The count: the one number a card player needs.
        BasicText(
            text = count.toString(),
            style = YapNPlayTheme.typography.label.copy(
                color = if (eliminated) colors.TextDisabled else colors.TextPrimary,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
            ),
            maxLines = 1,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .clip(YapNPlayTheme.shapes.pill)
                .background(if (eliminated) colors.Panel else colors.Error)
                .border(dimens.hairline, colors.TextPrimary.copy(alpha = BADGE_RIM_ALPHA), YapNPlayTheme.shapes.pill)
                .padding(horizontal = Spacing.Xs, vertical = Spacing.Xxxs),
        )
    }
}

/** The pulsing MONO! shield worn by a player on one card. */
@Composable
fun MonoCalledBadge(modifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val pulse = rememberInfiniteTransition(label = "mono-call")
    val alpha by pulse.animateFloat(
        initialValue = PULSE_MIN,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(PULSE_MS),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "mono-call-alpha",
    )
    BasicText(
        text = MonoStrings.monoCalled,
        style = YapNPlayTheme.typography.label.copy(
            color = colors.TextPrimary,
            fontWeight = FontWeight.Black,
        ),
        modifier = modifier
            .alpha(alpha)
            .clip(YapNPlayTheme.shapes.pill)
            .background(colors.Error)
            .border(dimens.hairline, colors.TextPrimary, YapNPlayTheme.shapes.pill)
            .padding(horizontal = Spacing.Sm, vertical = Spacing.Xxxs),
    )
}

private val FAN_OVERLAP = 5.dp
private const val FAN_CARDS = 3
private const val TIMER_DARKEN = 0.3f
private const val BADGE_DARKEN = 0.45f
private const val BADGE_RIM_ALPHA = 0.6f
private const val PULSE_MIN = 0.45f
private const val PULSE_MS = 700
