package com.kglabs28.yapnplay.appengine.ui.components.table

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.games.mono.identity.MonoConstants
import com.kglabs28.yapnplay.appengine.games.monochaos.identity.MonoChaosConstants

/**
 * The face of a MONO card, built to the card spec: 2:3 portrait, a
 * corner index top-left and bottom-right, a flat-top octagon at the
 * centre, and — on a Prism — a 2×2 quadrant split. Only card backs carry
 * the diagonal ribbons.
 *
 * [cardColor] is the card's own colour (null or a Prism gets [prismFace]).
 * [isSelected] lifts it out of the hand; [dimmed] pushes a card the table
 * will not accept back without hiding it. Size it through [modifier].
 */
@Composable
fun MonoCardFace(
    glyph: String,
    cardColor: Color,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    isPlayable: Boolean = true,
    faceDown: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val shape = RoundedCornerShape(dimens.cardRadius)
    val prism = cardColor == prismFace

    Box(
        modifier = modifier
            .aspectRatio(CARD_RATIO)
            .clip(shape)
            .background(if (faceDown) colors.Panel else cardColor)
            .border(
                width = if (isSelected) dimens.timerStroke else dimens.hairline,
                color = when {
                    isSelected -> colors.GameZoneAccent
                    faceDown -> colors.TextDisabled.copy(alpha = RIM_ALPHA)
                    else -> colors.TextPrimary.copy(alpha = RIM_ALPHA)
                },
                shape = shape,
            )
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            // A card the table would refuse is pushed back, not hidden.
            .alpha(if (isPlayable || faceDown) 1f else DIM_ALPHA)
            // A selected card stands up out of the hand.
            .padding(if (isSelected) dimens.zero else dimens.monoCardPad),
        contentAlignment = Alignment.Center,
    ) {
        if (faceDown) {
            MonoCardBackArt(modifier = Modifier.fillMaxSize())
            return@Box
        }
        if (prism) {
            MonoPrismQuadrants(modifier = Modifier.fillMaxSize())
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(dimens.monoCardPad),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            // Corner index, top left.
            BasicText(
                text = glyph,
                style = YapNPlayTheme.typography.micro.copy(
                    color = colors.TextPrimary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start,
                ),
                maxLines = 1,
                modifier = Modifier.align(Alignment.Start),
            )
            // The centre medallion: a flat-top octagon.
            Box(
                modifier = Modifier
                    .size(dimens.monoCardMedallion)
                    .clip(RoundedCornerShape(dimens.buttonCut))
                    .background(if (prism) colors.Panel.copy(alpha = PANEL_ALPHA) else colors.Panel),
                contentAlignment = Alignment.Center,
            ) {
                BasicText(
                    text = glyph,
                    style = YapNPlayTheme.typography.caption.copy(
                        color = colors.TextPrimary,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    ),
                    maxLines = 1,
                )
            }
            // Corner index, bottom right.
            BasicText(
                text = glyph,
                style = YapNPlayTheme.typography.micro.copy(
                    color = colors.TextPrimary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End,
                ),
                maxLines = 1,
                modifier = Modifier.align(Alignment.End),
            )
        }
    }
}

/** A Prism card's 2×2 quadrant split. */
@Composable
private fun MonoPrismQuadrants(modifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val half = w / 2f
        listOf(
            Color(0xFF6E4BE0),
            Color(0xFF2A8FD9),
            Color(0xFFD93A8F),
            Color(0xFF3ADBED),
        ).forEachIndexed { index, tint ->
            val left = index % 2 == 0
            val top = index < 2
            val path = Path().apply {
                moveTo(if (left) 0f else w, if (top) 0f else h)
                lineTo(if (left) half else half + half, if (top) 0f else h)
                lineTo(if (left) half else half + half, if (top) half else h - half)
                lineTo(if (left) 0f else w, if (top) half else h - half)
                close()
            }
            drawPath(path, tint.copy(alpha = QUADRANT_ALPHA))
        }
        // The cross where the quadrants meet.
        drawLine(
            color = colors.TextPrimary.copy(alpha = SPLIT_ALPHA),
            start = androidx.compose.ui.geometry.Offset(half, 0f),
            end = androidx.compose.ui.geometry.Offset(half, h),
            strokeWidth = SPLIT_WIDTH,
        )
        drawLine(
            color = colors.TextPrimary.copy(alpha = SPLIT_ALPHA),
            start = androidx.compose.ui.geometry.Offset(0f, half),
            end = androidx.compose.ui.geometry.Offset(w, half),
            strokeWidth = SPLIT_WIDTH,
        )
    }
}

/** A card back's diagonal ribbons (fronts never carry them). */
@Composable
private fun MonoCardBackArt(modifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val step = h / RIBBON_COUNT
        repeat(RIBBON_COUNT) { index ->
            val top = index * step
            drawLine(
                color = colors.LudoTeal.copy(alpha = RIBBON_ALPHA),
                start = androidx.compose.ui.geometry.Offset(0f, top),
                end = androidx.compose.ui.geometry.Offset(w, top + h),
                strokeWidth = RIBBON_WIDTH,
            )
        }
        // The same ribbons the other way, so the back reads as a lattice.
        repeat(RIBBON_COUNT) { index ->
            val top = index * step
            drawLine(
                color = colors.LudoTeal.copy(alpha = RIBBON_ALPHA),
                start = androidx.compose.ui.geometry.Offset(w, top),
                end = androidx.compose.ui.geometry.Offset(0f, top + h),
                strokeWidth = RIBBON_WIDTH,
            )
        }
        // A rim so the back has an edge against the table.
        drawRect(
            color = colors.LudoTeal.copy(alpha = RIM_ALPHA),
            style = Stroke(width = BACK_RIM),
        )
    }
}

/** The dark prism face every Prism card wears. */
val prismFace: Color get() = MonoCardColours.PRISM

/** The jewel tone behind a colour card; null (a Prism) gets [prismFace]. */
fun monoCardColour(color: MonoConstants.Color?): Color = when (color) {
    MonoConstants.Color.RED -> MonoCardColours.RED
    MonoConstants.Color.GREEN -> MonoCardColours.GREEN
    MonoConstants.Color.BLUE -> MonoCardColours.BLUE
    MonoConstants.Color.GOLD -> MonoCardColours.GOLD
    null -> MonoCardColours.PRISM
}

/**
 * The same jewel tones for MONO: Complete Chaos. Both games wear the
 * same colours, so a card never changes shade between them.
 */
fun monoChaosCardColour(color: MonoChaosConstants.Color?): Color = when (color) {
    MonoChaosConstants.Color.RED -> MonoCardColours.RED
    MonoChaosConstants.Color.GREEN -> MonoCardColours.GREEN
    MonoChaosConstants.Color.BLUE -> MonoCardColours.BLUE
    MonoChaosConstants.Color.GOLD -> MonoCardColours.GOLD
    null -> MonoCardColours.PRISM
}

/** Card colours live here so both MONO games' card faces share them. */
object MonoCardColours {
    val RED = Color(0xFF7A1224)
    val GREEN = Color(0xFF0F5232)
    val BLUE = Color(0xFF103466)
    val GOLD = Color(0xFF6E4B0E)
    val PRISM = Color(0xFF221F46)
}

/** Cards are 2:3 portrait. */
private const val CARD_RATIO = 2f / 3f
private const val DIM_ALPHA = 0.38f
private const val RIM_ALPHA = 0.35f
private const val PANEL_ALPHA = 0.45f
private const val QUADRANT_ALPHA = 0.30f
private const val SPLIT_ALPHA = 0.5f
private const val SPLIT_WIDTH = 1.5f
private const val RIBBON_COUNT = 5
private const val RIBBON_ALPHA = 0.16f
private const val RIBBON_WIDTH = 6f
private const val BACK_RIM = 2f