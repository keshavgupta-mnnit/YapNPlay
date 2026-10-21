package com.kglabs28.yapnplay.appengine.ui.components.table

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayColors
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.headlineFontFamily
import com.kglabs28.yapnplay.appengine.core.ui.MonoStrings
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * The action marks a MONO card can carry. They are drawn as vectors rather
 * than typed as text so every device renders exactly the same shape.
 */
enum class MonoCardIcon {
    /** Two diagonal arrows: reverse (with a label: Wild Reverse +4's circling arrows). */
    REVERSE,
    /** A barred ring: skip. */
    SKIP,
    /** A barred ring around ALL: skip everyone. */
    SKIP_ALL,
    /** A down arrow under three bars: discard all. */
    DISCARD_ALL,
    /** The four-colour wheel with a compass needle: Roulette. */
    ROULETTE,
    /** A four-colour gem: a colour wild. */
    WILD,
}

/**
 * Everything a MONO card face needs in order to draw itself, already
 * resolved by the game's own mapper; the component knows nothing about the
 * rules. The corner indices show [label] when there is one (else the icon);
 * the centre shows the [icon] when there is one (else the label).
 */
data class MonoCardLook(
    val label: String = "",
    /** The card's dark jewel fill. */
    val fill: Color,
    /** The bright rim and glow of the same hue. */
    val rim: Color,
    /** True for a multi-colour card: a 2×2 quadrant split and a four-colour rim. */
    val prism: Boolean = false,
    val icon: MonoCardIcon? = null,
)

/**
 * A MONO card, drawn to the card designs:
 *
 *  - Portrait, a metallic bevel frame around jewel-tinted smoked glass with a
 *    diagonal sheen; a trapezoid notch at the top carrying `MONO`.
 *  - Exactly two corner indices, in small slanted tabs: top-left and
 *    bottom-right (inverted).
 *  - A flat-top octagon of dark faceted glass with a glowing double rim,
 *    holding a large silver metallic number or the action mark.
 *  - A multi-colour card splits its glass into four quadrants (never ribbons)
 *    and its octagon rim runs through all four colours.
 *  - The back: four converging metallic ribbons, corner brackets, a neon
 *    four-colour octagon and the silver `MONO` wordmark breaking out of it;
 *    Complete Chaos adds the crimson COMPLETE CHAOS plate.
 *
 * Sizing is entirely the caller's (pass it through [modifier]); every detail
 * is a fraction of the card, and the finest detail drops away on tiny cards.
 */
@Composable
fun MonoCardFace(
    look: MonoCardLook,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    isPlayable: Boolean = true,
    faceDown: Boolean = false,
    chaos: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    val density = LocalDensity.current
    val liftPx = with(density) { YapNPlayTheme.dimens.monoCardLift.toPx() }
    val smallPx = with(density) { SMALL_CARD.toPx() }
    val measurer = rememberTextMeasurer()
    val font = headlineFontFamily()
    val shaped = modifier
        .aspectRatio(CARD_RATIO)
        .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
        // A card the table would refuse is pushed back, never hidden.
        .alpha(if (isPlayable || faceDown) 1f else DIM_ALPHA)
        // A selected card stands up out of the hand.
        .graphicsLayer { translationY = if (isSelected) -liftPx else 0f }

    Canvas(modifier = shaped) {
        val text = CardText(measurer, font)
        if (faceDown) drawBack(chaos, text, detail = size.width >= smallPx) else drawFront(look, text, detail = size.width >= smallPx)
    }
}

/** Text drawing inside the card canvas (sizes in px, converted here). */
private class CardText(val measurer: TextMeasurer, val font: FontFamily) {
    fun layout(scope: DrawScope, text: String, sizePx: Float, brush: Brush, maxWidth: Float, tracking: Float = 0f): TextLayoutResult {
        var px = sizePx
        var result = measure(scope, text, px, brush, tracking)
        // Shrink to fit: never wraps, never spills past its room.
        while (result.size.width > maxWidth && px > MIN_TEXT_PX) {
            px *= FIT_STEP
            result = measure(scope, text, px, brush, tracking)
        }
        return result
    }

    private fun measure(scope: DrawScope, text: String, px: Float, brush: Brush, tracking: Float) = with(scope) {
        measurer.measure(
            text,
            TextStyle(brush = brush, fontFamily = font, fontWeight = FontWeight.Bold, fontSize = px.toSp(), letterSpacing = if (tracking == 0f) TextUnit.Unspecified else (tracking).toSp()),
            softWrap = false,
            maxLines = 1,
        )
    }
}

private fun DrawScope.drawCentred(layout: TextLayoutResult, centre: Offset) {
    drawText(layout, topLeft = Offset(centre.x - layout.size.width / 2f, centre.y - layout.size.height / 2f))
}

private val silver: Brush get() = Brush.verticalGradient(listOf(YapNPlayColors.MonoSilverTop, YapNPlayColors.MonoSilverMid, YapNPlayColors.MonoSilverBottom))
private val metal: Brush get() = Brush.linearGradient(listOf(YapNPlayColors.MonoMetalLight, YapNPlayColors.MonoMetalDark, YapNPlayColors.MonoMetalLight, YapNPlayColors.MonoMetalDark))
private val prismColours get() = listOf(YapNPlayColors.MonoRedRim, YapNPlayColors.MonoGreenRim, YapNPlayColors.MonoGoldRim, YapNPlayColors.MonoBlueRim, YapNPlayColors.MonoRedRim)

// ───────────────────────────── front ─────────────────────────────

private fun DrawScope.drawFront(look: MonoCardLook, text: CardText, detail: Boolean) {
    val w = size.width
    val h = size.height
    val radius = CornerRadius(w * CORNER_RADIUS)
    val inset = w * FRAME_INSET
    val body = roundRect(0f, 0f, w, h, w * CORNER_RADIUS)

    // Smoked glass, tinted with the card's jewel colour (a quadrant split for a multi-colour card).
    drawPath(body, YapNPlayColors.MonoCardBase)
    clipPath(body) {
        if (look.prism) {
            drawQuadrants(w, h)
        } else {
            drawRect(Brush.linearGradient(listOf(look.fill.copy(alpha = FILL_TOP), look.fill.copy(alpha = FILL_BOTTOM)), Offset.Zero, Offset(w, h)))
        }
        // The diagonal glass sheen across the top right.
        val sheen = Path().apply {
            moveTo(w * SHEEN_A, 0f); lineTo(w * SHEEN_B, 0f); lineTo(w * SHEEN_C, h); lineTo(w * SHEEN_D, h); close()
        }
        drawPath(sheen, Color.White.copy(alpha = SHEEN_ALPHA))
    }
    // The metallic bevel frame and its thin inner line.
    drawRoundRect(metal, size = size, cornerRadius = radius, style = Stroke(w * FRAME_WIDTH))
    drawRoundRect(
        look.rim.copy(alpha = if (look.prism) PRISM_LINE_ALPHA else INNER_LINE_ALPHA),
        topLeft = Offset(inset, inset),
        size = Size(w - inset * 2, h - inset * 2),
        cornerRadius = CornerRadius(w * (CORNER_RADIUS - FRAME_INSET)),
        style = Stroke(w * INNER_LINE),
    )

    drawOctagon(look, w, h)
    drawCentreMark(look, text, w, h)
    if (detail) {
        drawNotch(text, w)
        drawIndex(look, text, w, h)
        rotate(HALF_TURN, pivot = Offset(w / 2, h / 2)) { drawIndex(look, text, w, h) }
    }
}

/** Four dark jewel quadrants: red, green / blue, gold. */
private fun DrawScope.drawQuadrants(w: Float, h: Float) {
    val half = Size(w / 2, h / 2)
    drawRect(Brush.linearGradient(listOf(YapNPlayColors.MonoRedFill, YapNPlayColors.MonoRedFill.copy(alpha = QUADRANT_FADE))), Offset.Zero, half)
    drawRect(Brush.linearGradient(listOf(YapNPlayColors.MonoGreenFill, YapNPlayColors.MonoGreenFill.copy(alpha = QUADRANT_FADE))), Offset(w / 2, 0f), half)
    drawRect(Brush.linearGradient(listOf(YapNPlayColors.MonoBlueFill, YapNPlayColors.MonoBlueFill.copy(alpha = QUADRANT_FADE))), Offset(0f, h / 2), half)
    drawRect(Brush.linearGradient(listOf(YapNPlayColors.MonoGoldFill, YapNPlayColors.MonoGoldFill.copy(alpha = QUADRANT_FADE))), Offset(w / 2, h / 2), half)
}

/** The `MONO` notch hanging from the top edge. */
private fun DrawScope.drawNotch(text: CardText, w: Float) {
    val top = w * FRAME_INSET * NOTCH_TOP
    val bottom = w * NOTCH_DEPTH
    val tab = Path().apply {
        moveTo(w * NOTCH_TOP_LEFT, top); lineTo(w * (1 - NOTCH_TOP_LEFT), top)
        lineTo(w * (1 - NOTCH_BOTTOM_LEFT), bottom); lineTo(w * NOTCH_BOTTOM_LEFT, bottom); close()
    }
    drawPath(tab, Brush.verticalGradient(listOf(YapNPlayColors.MonoMetalDark, YapNPlayColors.MonoCardBase), top, bottom))
    drawPath(tab, metal, style = Stroke(w * INNER_LINE * 1.5f))
    val label = text.layout(this, MonoStrings.monoWordmark, w * NOTCH_TEXT, Brush.verticalGradient(listOf(YapNPlayColors.MonoSilverMid, YapNPlayColors.MonoSilverBottom)), w * (1 - NOTCH_BOTTOM_LEFT * 2), w * NOTCH_TRACKING)
    drawCentred(label, Offset(w / 2, (top + bottom) / 2))
}

/** The top-left index in its slanted tab (the bottom-right one is this, turned round). */
private fun DrawScope.drawIndex(look: MonoCardLook, text: CardText, w: Float, h: Float) {
    val x0 = w * FRAME_INSET
    val y0 = w * FRAME_INSET
    val x1 = w * INDEX_TAB_WIDTH
    val y1 = w * INDEX_TAB_HEIGHT
    val tab = Path().apply {
        moveTo(x0, y0); lineTo(x1, y0); lineTo(x1 - (y1 - y0) * INDEX_SLANT, y1); lineTo(x0, y1); close()
    }
    val tint = if (look.prism) YapNPlayColors.MonoSilverMid else look.rim
    drawPath(tab, tint.copy(alpha = INDEX_TAB_ALPHA))
    val centre = Offset((x0 + x1 - (y1 - y0) * INDEX_SLANT / 2) / 2, (y0 + y1) / 2)
    if (look.label.isNotEmpty()) {
        val brush = if (look.prism) silver else Brush.verticalGradient(listOf(lerp(tint, Color.White, INDEX_LIGHTEN), tint))
        drawCentred(text.layout(this, look.label, w * INDEX_TEXT, brush, x1 - x0 - w * INDEX_PAD), centre)
    } else {
        look.icon?.let { drawIcon(it, look, text, centre, (y1 - y0) * INDEX_ICON) }
    }
}

/** The flat-top octagon: metal ring, glow, bright double rim, faceted dark glass. */
private fun DrawScope.drawOctagon(look: MonoCardLook, w: Float, h: Float) {
    val c = Offset(w / 2, h * OCTAGON_CENTRE)
    val outer = octagon(c, w * OCTAGON_OUTER)
    val ring = octagon(c, w * OCTAGON_RING)
    val inner = octagon(c, w * OCTAGON_INNER)
    val rimBrush: Brush = if (look.prism) Brush.sweepGradient(prismColours, c) else Brush.linearGradient(listOf(look.rim, look.rim))
    drawPath(outer, YapNPlayColors.MonoOctagonEdge.copy(alpha = OCTAGON_SHADE))
    drawPath(outer, metal, style = Stroke(w * OCTAGON_METAL))
    // Glow behind the rim.
    drawPath(ring, rimBrush, alpha = GLOW_ALPHA, style = Stroke(w * GLOW_WIDTH, join = StrokeJoin.Round))
    // Faceted dark glass.
    drawPath(ring, Brush.radialGradient(listOf(YapNPlayColors.MonoOctagonCore, YapNPlayColors.MonoOctagonEdge), c, w * OCTAGON_RING))
    clipPath(ring) {
        octagonPoints(c, w * OCTAGON_RING).forEach { p -> drawLine(Color.White.copy(alpha = FACET_ALPHA), c, p, w * FACET_WIDTH) }
    }
    drawPath(ring, rimBrush, style = Stroke(w * RIM_WIDTH, join = StrokeJoin.Round))
    drawPath(inner, rimBrush, alpha = INNER_RIM_ALPHA, style = Stroke(w * INNER_LINE, join = StrokeJoin.Round))
}

/** The big silver number / value, or the action mark. */
private fun DrawScope.drawCentreMark(look: MonoCardLook, text: CardText, w: Float, h: Float) {
    val c = Offset(w / 2, h * OCTAGON_CENTRE)
    val room = w * OCTAGON_INNER * 2 * MARK_ROOM
    val icon = look.icon
    when {
        icon != null && !(icon == MonoCardIcon.REVERSE && look.label.isNotEmpty()) -> drawIcon(icon, look, text, c, w * ICON_SIZE)
        icon == MonoCardIcon.REVERSE -> {
            // Wild Reverse +4: the value inside circling arrows.
            drawCirclingArrows(c, w * ICON_SIZE, YapNPlayColors.MonoSilverMid)
            drawCentred(text.layout(this, look.label, w * CIRCLED_TEXT, silver, room * CIRCLED_ROOM), c)
        }
        else -> {
            val px = when {
                look.label.length <= 1 -> w * NUMBER_TEXT
                look.label.length <= 2 -> w * VALUE_TEXT
                else -> w * LONG_VALUE_TEXT
            }
            val layout = text.layout(this, look.label, px, silver, room)
            // A soft dark drop under the metal letters.
            translate(w * SHADOW_DROP, w * SHADOW_DROP) {
                drawCentred(text.layout(this, look.label, px, Brush.linearGradient(listOf(Color.Black.copy(alpha = SHADOW_ALPHA), Color.Black.copy(alpha = SHADOW_ALPHA))), room), c)
            }
            drawCentred(layout, c)
        }
    }
}

private fun DrawScope.drawIcon(icon: MonoCardIcon, look: MonoCardLook, text: CardText, c: Offset, s: Float) {
    val tint = if (look.prism) YapNPlayColors.MonoSilverMid else look.rim
    val stroke = s * ICON_STROKE
    when (icon) {
        MonoCardIcon.REVERSE -> {
            // Two chunky diagonal arrows: up-right and down-left.
            // Side by side across the diagonal, pointing opposite ways.
            drawDiagonalArrow(c - Offset(s * ARROW_SHIFT, s * ARROW_SHIFT), s, tint, up = true)
            drawDiagonalArrow(c + Offset(s * ARROW_SHIFT, s * ARROW_SHIFT), s, tint, up = false)
        }
        MonoCardIcon.SKIP -> drawSkipRing(c, s, tint, stroke)
        MonoCardIcon.SKIP_ALL -> {
            // A smaller barred ring with ALL underneath.
            val ring = s * ALL_RING
            drawSkipRing(c - Offset(0f, s * ALL_RAISE), ring, tint, ring * ICON_STROKE)
            drawCentred(text.layout(this, MonoStrings.monoAll, s * ALL_TEXT, Brush.verticalGradient(listOf(lerp(tint, Color.White, INDEX_LIGHTEN), tint)), s * ALL_ROOM), Offset(c.x, c.y + s * ALL_DROP))
        }
        MonoCardIcon.DISCARD_ALL -> {
            // Three bars over a silver down arrow.
            val barW = s * BAR_WIDTH
            repeat(BARS) { i ->
                val y = c.y - s * BAR_TOP + i * s * BAR_GAP
                drawLine(silver, Offset(c.x - barW / 2, y), Offset(c.x + barW / 2, y), s * BAR_THICK, StrokeCap.Round)
            }
            val shaft = Path().apply {
                moveTo(c.x - s * SHAFT_HALF, c.y - s * SHAFT_TOP); lineTo(c.x + s * SHAFT_HALF, c.y - s * SHAFT_TOP)
                lineTo(c.x + s * SHAFT_HALF, c.y + s * SHAFT_BOTTOM); lineTo(c.x + s * HEAD_HALF, c.y + s * SHAFT_BOTTOM)
                lineTo(c.x, c.y + s * HEAD_TIP); lineTo(c.x - s * HEAD_HALF, c.y + s * SHAFT_BOTTOM)
                lineTo(c.x - s * SHAFT_HALF, c.y + s * SHAFT_BOTTOM); close()
            }
            drawPath(shaft, silver)
        }
        MonoCardIcon.ROULETTE -> {
            // The four-colour wheel, a dark hub and a silver compass needle.
            val r = s / 2
            val colours = listOf(YapNPlayColors.MonoRedRim, YapNPlayColors.MonoGreenRim, YapNPlayColors.MonoGoldRim, YapNPlayColors.MonoBlueRim)
            colours.forEachIndexed { i, colour ->
                drawArc(colour, startAngle = ROULETTE_START + i * QUARTER_TURN, sweepAngle = QUARTER_TURN, useCenter = true, topLeft = c - Offset(r, r), size = Size(r * 2, r * 2))
            }
            drawCircle(YapNPlayColors.MonoOctagonEdge, r, c, style = Stroke(s * WHEEL_RIM))
            val needle = Path().apply {
                moveTo(c.x + r * NEEDLE_REACH, c.y - r * NEEDLE_REACH); lineTo(c.x + r * NEEDLE_SIDE, c.y + r * NEEDLE_SIDE)
                lineTo(c.x - r * NEEDLE_REACH, c.y + r * NEEDLE_REACH); lineTo(c.x - r * NEEDLE_SIDE, c.y - r * NEEDLE_SIDE); close()
            }
            drawPath(needle, silver)
            drawCircle(YapNPlayColors.MonoOctagonEdge, s * HUB, c)
        }
        MonoCardIcon.WILD -> {
            // A four-colour gem: tall diamond, split into four facets.
            val top = Offset(c.x, c.y - s * GEM_TALL)
            val bottom = Offset(c.x, c.y + s * GEM_TALL)
            val left = Offset(c.x - s * GEM_WIDE, c.y - s * GEM_WAIST)
            val right = Offset(c.x + s * GEM_WIDE, c.y - s * GEM_WAIST)
            val mid = Offset(c.x, c.y - s * GEM_WAIST)
            drawPath(triangle(top, left, mid), YapNPlayColors.MonoRedRim)
            drawPath(triangle(top, right, mid), YapNPlayColors.MonoGreenRim)
            drawPath(triangle(left, bottom, mid), YapNPlayColors.MonoBlueRim)
            drawPath(triangle(right, bottom, mid), YapNPlayColors.MonoGoldRim)
            drawPath(Path().apply { moveTo(top.x, top.y); lineTo(right.x, right.y); lineTo(bottom.x, bottom.y); lineTo(left.x, left.y); close() }, Color.White.copy(alpha = GEM_EDGE), style = Stroke(s * GEM_LINE))
            drawLine(Color.White.copy(alpha = GEM_EDGE), left, right, s * GEM_LINE)
        }
    }
}

private fun DrawScope.drawSkipRing(c: Offset, s: Float, tint: Color, stroke: Float) {
    val r = s / 2 - stroke / 2
    drawCircle(tint, r, c, style = Stroke(stroke))
    val d = r * SLASH_REACH
    drawLine(tint, Offset(c.x - d, c.y - d), Offset(c.x + d, c.y + d), stroke, StrokeCap.Round)
}

/** A chunky arrow along the diagonal, pointing up-right ([up]) or down-left. */
private fun DrawScope.drawDiagonalArrow(c: Offset, s: Float, tint: Color, up: Boolean) {
    val shaft = s * ARROW_LENGTH
    rotate(if (up) -EIGHTH_TURN else HALF_TURN - EIGHTH_TURN, pivot = c) {
        val body = Path().apply {
            val half = s * ARROW_THICK / 2
            moveTo(c.x - shaft / 2, c.y - half); lineTo(c.x + shaft / 2 - s * ARROW_HEAD, c.y - half)
            lineTo(c.x + shaft / 2 - s * ARROW_HEAD, c.y - s * ARROW_HEAD_HALF); lineTo(c.x + shaft / 2, c.y)
            lineTo(c.x + shaft / 2 - s * ARROW_HEAD, c.y + s * ARROW_HEAD_HALF); lineTo(c.x + shaft / 2 - s * ARROW_HEAD, c.y + half)
            lineTo(c.x - shaft / 2, c.y + half); close()
        }
        drawPath(body, Brush.verticalGradient(listOf(lerp(tint, Color.White, ARROW_SHINE), tint), c.y - s * ARROW_HEAD_HALF, c.y + s * ARROW_HEAD_HALF))
    }
}

/** Two arcs chasing each other round (Wild Reverse +4). */
private fun DrawScope.drawCirclingArrows(c: Offset, s: Float, tint: Color) {
    val r = s / 2
    val stroke = s * CIRCLE_STROKE
    listOf(CIRCLE_A, CIRCLE_A + HALF_TURN).forEach { start ->
        drawArc(tint, start, CIRCLE_SWEEP, false, c - Offset(r, r), Size(r * 2, r * 2), style = Stroke(stroke, cap = StrokeCap.Round))
        val end = (start + CIRCLE_SWEEP) * PI.toFloat() / HALF_TURN
        val tip = Offset(c.x + r * cos(end), c.y + r * sin(end))
        val dir = end + PI.toFloat() / 2
        val head = Path().apply {
            moveTo(tip.x + cos(dir) * stroke * HEAD_LEN, tip.y + sin(dir) * stroke * HEAD_LEN)
            lineTo(tip.x + cos(end) * stroke * HEAD_WIDE, tip.y + sin(end) * stroke * HEAD_WIDE)
            lineTo(tip.x - cos(end) * stroke * HEAD_WIDE, tip.y - sin(end) * stroke * HEAD_WIDE); close()
        }
        drawPath(head, tint)
    }
}

// ───────────────────────────── back ─────────────────────────────

private fun DrawScope.drawBack(chaos: Boolean, text: CardText, detail: Boolean) {
    val w = size.width
    val h = size.height
    val body = roundRect(0f, 0f, w, h, w * CORNER_RADIUS)
    drawPath(body, Brush.linearGradient(listOf(YapNPlayColors.MonoMetalDark, YapNPlayColors.MonoBackBase), Offset.Zero, Offset(w, h)))
    val inset = w * FRAME_INSET
    val panel = roundRect(inset, inset, w - inset, h - inset, w * (CORNER_RADIUS - FRAME_INSET))
    clipPath(panel) {
        drawRect(YapNPlayColors.MonoBackBase)
        // The converging ribbons: a secondary pair underneath, then one from each corner.
        drawRibbon(Offset(-w * RIBBON_OVER, h * SECOND_LEFT), Offset(w / 2, h / 2), w * RIBBON_THIN, YapNPlayColors.MonoGreenFill, YapNPlayColors.MonoGreenRim)
        drawRibbon(Offset(w * (1 + RIBBON_OVER), h * SECOND_RIGHT), Offset(w / 2, h / 2), w * RIBBON_THIN, YapNPlayColors.MonoGoldFill, YapNPlayColors.MonoGoldRim)
        drawRibbon(Offset(-w * RIBBON_OVER, -w * RIBBON_OVER), Offset(w / 2, h / 2), w * RIBBON_WIDE, YapNPlayColors.MonoRedFill, YapNPlayColors.MonoRedRim)
        drawRibbon(Offset(w * (1 + RIBBON_OVER), -w * RIBBON_OVER), Offset(w / 2, h / 2), w * RIBBON_WIDE, YapNPlayColors.MonoBlueFill, YapNPlayColors.MonoBlueRim)
        drawRibbon(Offset(-w * RIBBON_OVER, h + w * RIBBON_OVER), Offset(w / 2, h / 2), w * RIBBON_WIDE, YapNPlayColors.MonoGreenFill, YapNPlayColors.MonoGreenRim)
        drawRibbon(Offset(w * (1 + RIBBON_OVER), h + w * RIBBON_OVER), Offset(w / 2, h / 2), w * RIBBON_WIDE, YapNPlayColors.MonoGoldFill, YapNPlayColors.MonoGoldRim)
        val sheen = Path().apply { moveTo(w * SHEEN_A, 0f); lineTo(w * SHEEN_B, 0f); lineTo(w * SHEEN_C, h); lineTo(w * SHEEN_D, h); close() }
        drawPath(sheen, Color.White.copy(alpha = SHEEN_ALPHA))
    }
    drawRoundRect(metal, size = size, cornerRadius = CornerRadius(w * CORNER_RADIUS), style = Stroke(w * FRAME_WIDTH))
    drawPath(panel, YapNPlayColors.MonoMetalDark, style = Stroke(w * INNER_LINE * 2))
    if (detail) drawCornerBrackets(w, h)

    // The neon octagon.
    val c = Offset(w / 2, h / 2)
    val ring = octagon(c, w * BACK_OCTAGON)
    drawPath(octagon(c, w * BACK_OCTAGON_OUTER), YapNPlayColors.MonoOctagonEdge)
    drawPath(octagon(c, w * BACK_OCTAGON_OUTER), metal, style = Stroke(w * OCTAGON_METAL))
    val neon = Brush.sweepGradient(prismColours, c)
    drawPath(ring, neon, alpha = GLOW_ALPHA, style = Stroke(w * GLOW_WIDTH, join = StrokeJoin.Round))
    drawPath(ring, Brush.radialGradient(listOf(YapNPlayColors.MonoOctagonCore, YapNPlayColors.MonoOctagonEdge), c, w * BACK_OCTAGON))
    clipPath(ring) { octagonPoints(c, w * BACK_OCTAGON).forEach { p -> drawLine(Color.White.copy(alpha = FACET_ALPHA), c, p, w * FACET_WIDTH) } }
    drawPath(ring, neon, style = Stroke(w * RIM_WIDTH, join = StrokeJoin.Round))

    // The silver wordmark, wider than the octagon.
    val wordY = if (chaos) h * CHAOS_WORD_Y else h / 2
    val word = text.layout(this, MonoStrings.monoWordmark, w * BACK_WORD, silver, w * BACK_WORD_ROOM, w * BACK_TRACKING)
    translate(w * SHADOW_DROP, w * SHADOW_DROP) {
        drawCentred(text.layout(this, MonoStrings.monoWordmark, w * BACK_WORD, Brush.linearGradient(listOf(Color.Black.copy(alpha = SHADOW_ALPHA), Color.Black.copy(alpha = SHADOW_ALPHA))), w * BACK_WORD_ROOM, w * BACK_TRACKING), Offset(w / 2, wordY))
    }
    drawCentred(word, Offset(w / 2, wordY))

    if (chaos) {
        // The crimson COMPLETE CHAOS plate under the wordmark, pointed at both ends.
        val py = h * CHAOS_PLATE_Y
        val half = w * CHAOS_PLATE_HALF
        val tall = w * CHAOS_PLATE_TALL
        val tip = w * CHAOS_PLATE_TIP
        val plate = Path().apply {
            moveTo(w / 2 - half, py - tall / 2); lineTo(w / 2 + half, py - tall / 2); lineTo(w / 2 + half + tip, py)
            lineTo(w / 2 + half, py + tall / 2); lineTo(w / 2 - half, py + tall / 2); lineTo(w / 2 - half - tip, py); close()
        }
        drawPath(plate, Brush.verticalGradient(listOf(YapNPlayColors.MonoChaosPlateLight, YapNPlayColors.MonoChaosPlate), py - tall / 2, py + tall / 2))
        drawPath(plate, YapNPlayColors.MonoChaosPlateRim, style = Stroke(w * INNER_LINE * 1.5f))
        drawCentred(text.layout(this, MonoStrings.monoChaosPlate, w * CHAOS_TEXT, silver, half * 2 * CHAOS_TEXT_ROOM, w * CHAOS_TRACKING), Offset(w / 2, py))
    }
}

/** A metallic ribbon from [from] to [to]: dark edges, a bright core. */
private fun DrawScope.drawRibbon(from: Offset, to: Offset, width: Float, dark: Color, light: Color) {
    val dx = to.x - from.x
    val dy = to.y - from.y
    val len = kotlin.math.sqrt(dx * dx + dy * dy)
    val nx = -dy / len * width / 2
    val ny = dx / len * width / 2
    val band = Path().apply {
        moveTo(from.x + nx, from.y + ny); lineTo(to.x + nx, to.y + ny); lineTo(to.x - nx, to.y - ny); lineTo(from.x - nx, from.y - ny); close()
    }
    drawPath(band, Brush.linearGradient(listOf(dark, light, dark), Offset(from.x + nx, from.y + ny), Offset(from.x - nx, from.y - ny)))
}

/** Small metal L-brackets in the four corners, each with a jewel dot. */
private fun DrawScope.drawCornerBrackets(w: Float, h: Float) {
    val i = w * BRACKET_INSET
    val l = w * BRACKET_LENGTH
    val stroke = w * INNER_LINE * 1.5f
    val dots = listOf(YapNPlayColors.MonoGreenRim, YapNPlayColors.MonoRedRim, YapNPlayColors.MonoGreenRim, YapNPlayColors.MonoBlueRim)
    listOf(Offset(i, i) to Offset(1f, 1f), Offset(w - i, i) to Offset(-1f, 1f), Offset(i, h - i) to Offset(1f, -1f), Offset(w - i, h - i) to Offset(-1f, -1f))
        .forEachIndexed { k, (p, d) ->
            drawLine(YapNPlayColors.MonoMetalLight, p, Offset(p.x + d.x * l, p.y), stroke)
            drawLine(YapNPlayColors.MonoMetalLight, p, Offset(p.x, p.y + d.y * l), stroke)
            drawCircle(dots[k].copy(alpha = DOT_ALPHA), w * DOT, Offset(p.x + d.x * w * DOT * 2, p.y + d.y * w * DOT * 2))
        }
}

// ───────────────────────────── shapes ─────────────────────────────

private fun roundRect(l: Float, t: Float, r: Float, b: Float, radius: Float): Path = Path().apply {
    addRoundRect(androidx.compose.ui.geometry.RoundRect(l, t, r, b, CornerRadius(radius)))
}

/** A regular flat-top octagon of half-width [half] around [c]. */
private fun octagon(c: Offset, half: Float): Path = Path().apply {
    val p = octagonPoints(c, half)
    moveTo(p[0].x, p[0].y)
    p.drop(1).forEach { lineTo(it.x, it.y) }
    close()
}

private fun octagonPoints(c: Offset, half: Float): List<Offset> {
    val cut = half * OCTAGON_CUT
    return listOf(
        Offset(c.x - half + cut, c.y - half), Offset(c.x + half - cut, c.y - half), Offset(c.x + half, c.y - half + cut), Offset(c.x + half, c.y + half - cut),
        Offset(c.x + half - cut, c.y + half), Offset(c.x - half + cut, c.y + half), Offset(c.x - half, c.y + half - cut), Offset(c.x - half, c.y - half + cut),
    )
}

private fun triangle(a: Offset, b: Offset, c: Offset) = Path().apply { moveTo(a.x, a.y); lineTo(b.x, b.y); lineTo(c.x, c.y); close() }

/** Card width ÷ height, as in the card designs. */
private const val CARD_RATIO = 0.6f

// Body and frame (fractions of the card width).
private const val CORNER_RADIUS = 0.075f
private const val FRAME_WIDTH = 0.03f
private const val FRAME_INSET = 0.045f
private const val INNER_LINE = 0.007f
private const val INNER_LINE_ALPHA = 0.55f
private const val PRISM_LINE_ALPHA = 0.35f
private const val FILL_TOP = 0.85f
private const val FILL_BOTTOM = 0.45f
private const val QUADRANT_FADE = 0.75f
private const val SHEEN_A = 0.52f
private const val SHEEN_B = 0.82f
private const val SHEEN_C = 0.28f
private const val SHEEN_D = -0.02f
private const val SHEEN_ALPHA = 0.06f

// The MONO notch.
private const val NOTCH_TOP = 0.6f
private const val NOTCH_DEPTH = 0.135f
private const val NOTCH_TOP_LEFT = 0.29f
private const val NOTCH_BOTTOM_LEFT = 0.35f
private const val NOTCH_TEXT = 0.075f
private const val NOTCH_TRACKING = 0.004f

// Corner index tabs.
private const val INDEX_TAB_WIDTH = 0.27f
private const val INDEX_TAB_HEIGHT = 0.19f
private const val INDEX_SLANT = 0.55f
private const val INDEX_TAB_ALPHA = 0.22f
private const val INDEX_TEXT = 0.12f
private const val INDEX_PAD = 0.03f
private const val INDEX_ICON = 0.62f
private const val INDEX_LIGHTEN = 0.25f

// The octagon (half-widths as fractions of the card width).
private const val OCTAGON_CENTRE = 0.5f
private const val OCTAGON_OUTER = 0.39f
private const val OCTAGON_RING = 0.33f
private const val OCTAGON_INNER = 0.285f
private const val OCTAGON_CUT = 0.42f
private const val OCTAGON_METAL = 0.012f
private const val OCTAGON_SHADE = 0.85f
private const val GLOW_WIDTH = 0.06f
private const val GLOW_ALPHA = 0.35f
private const val RIM_WIDTH = 0.016f
private const val INNER_RIM_ALPHA = 0.45f
private const val FACET_ALPHA = 0.045f
private const val FACET_WIDTH = 0.006f

// The centre mark.
private const val MARK_ROOM = 0.9f
private const val NUMBER_TEXT = 0.42f
private const val VALUE_TEXT = 0.32f
private const val LONG_VALUE_TEXT = 0.26f
private const val CIRCLED_TEXT = 0.16f
private const val CIRCLED_ROOM = 0.45f
private const val SHADOW_DROP = 0.012f
private const val SHADOW_ALPHA = 0.55f
private const val ICON_SIZE = 0.42f
private const val ICON_STROKE = 0.14f
private const val ALL_TEXT = 0.24f
private const val ALL_RING = 0.7f
private const val ALL_RAISE = 0.14f
private const val ALL_DROP = 0.38f
private const val ALL_ROOM = 0.55f
private const val SLASH_REACH = 0.7f
private const val ARROW_SHIFT = 0.15f
private const val ARROW_LENGTH = 0.66f
private const val ARROW_THICK = 0.13f
private const val ARROW_HEAD = 0.26f
private const val ARROW_HEAD_HALF = 0.17f
private const val ARROW_SHINE = 0.35f
private const val BARS = 3
private const val BAR_WIDTH = 0.38f
private const val BAR_TOP = 0.42f
private const val BAR_GAP = 0.1f
private const val BAR_THICK = 0.055f
private const val SHAFT_HALF = 0.09f
private const val SHAFT_TOP = 0.12f
private const val SHAFT_BOTTOM = 0.22f
private const val HEAD_HALF = 0.26f
private const val HEAD_TIP = 0.48f
private const val ROULETTE_START = 180f
private const val QUARTER_TURN = 90f
private const val WHEEL_RIM = 0.06f
private const val NEEDLE_REACH = 0.62f
private const val NEEDLE_SIDE = 0.1f
private const val HUB = 0.07f
private const val GEM_TALL = 0.5f
private const val GEM_WIDE = 0.32f
private const val GEM_WAIST = 0.12f
private const val GEM_LINE = 0.025f
private const val GEM_EDGE = 0.45f
private const val CIRCLE_STROKE = 0.1f
private const val CIRCLE_A = 200f
private const val CIRCLE_SWEEP = 120f
private const val HEAD_LEN = 1.6f
private const val HEAD_WIDE = 1.1f
private const val HALF_TURN = 180f
private const val EIGHTH_TURN = 45f

// The back.
private const val RIBBON_WIDE = 0.2f
private const val RIBBON_THIN = 0.12f
private const val RIBBON_OVER = 0.1f
private const val SECOND_LEFT = 0.3f
private const val SECOND_RIGHT = 0.36f
private const val BACK_OCTAGON = 0.3f
private const val BACK_OCTAGON_OUTER = 0.35f
private const val BACK_WORD = 0.25f
private const val BACK_WORD_ROOM = 0.86f
private const val BACK_TRACKING = 0.004f
private const val CHAOS_WORD_Y = 0.47f
private const val CHAOS_PLATE_Y = 0.565f
private const val CHAOS_PLATE_HALF = 0.27f
private const val CHAOS_PLATE_TALL = 0.085f
private const val CHAOS_PLATE_TIP = 0.04f
private const val CHAOS_TEXT = 0.055f
private const val CHAOS_TEXT_ROOM = 0.92f
private const val CHAOS_TRACKING = 0.003f
private const val BRACKET_INSET = 0.075f
private const val BRACKET_LENGTH = 0.12f
private const val DOT = 0.018f
private const val DOT_ALPHA = 0.8f

// Behaviour.
private const val DIM_ALPHA = 0.38f
private const val MIN_TEXT_PX = 4f
private const val FIT_STEP = 0.92f
private val SMALL_CARD = 40.dp



