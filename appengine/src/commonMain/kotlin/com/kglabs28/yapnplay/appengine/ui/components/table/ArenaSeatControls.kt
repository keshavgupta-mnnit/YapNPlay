package com.kglabs28.yapnplay.appengine.ui.components.table

import androidx.compose.foundation.background
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.Canvas
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.RepeatMode
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.MicOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.MonoStrings
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon

/**
 * The user's two switches just under their seat's pill (design): a row as
 * wide as the pill (give it the pill's width through [modifier]) with Mic
 * and Auto as two round buttons, a gap between them, centred. On, Mic fills
 * dark pink and Auto dark blue.
 */
@Composable
fun ArenaSeatSwitches(
    micOn: Boolean,
    autoplay: Boolean,
    onToggleMic: () -> Unit,
    onToggleAutoplay: () -> Unit,
    modifier: Modifier = Modifier,
    /** The space between the two buttons. */
    gap: Dp = Spacing.Xs,
    /** Each button's size: under a seat card, sized to that card (see [seatSwitchSize]). */
    size: Dp = LocalSeatSwitchSize.current ?: YapNPlayTheme.dimens.arenaSwitchMax,
) {
    val colors = YapNPlayTheme.colors
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(gap, Alignment.CenterHorizontally)) {
        ArenaRoundSwitch(
            icon = if (micOn) Icons.Rounded.Mic else Icons.Rounded.MicOff,
            description = MonoStrings.monoMic,
            on = micOn,
            fill = colors.ArenaSwitchOn,
            rim = colors.ArenaSwitchOnRim,
            onClick = onToggleMic,
            size = size,
        )
        ArenaRoundSwitch(
            icon = Icons.Rounded.SportsEsports,
            description = MonoStrings.monoAutoplay,
            on = autoplay,
            fill = colors.ArenaAutoOn,
            rim = colors.ArenaAutoOnRim,
            onClick = onToggleAutoplay,
            size = size,
        )
    }
}

/** The Mic / Auto buttons' size under a seat card with an [avatar] that big. */
fun seatSwitchSize(avatar: Dp): Dp = avatar * SWITCH_OF_AVATAR

/** The size of the switches under the seat card being drawn; null outside one. */
val LocalSeatSwitchSize = staticCompositionLocalOf<Dp?> { null }

private const val SWITCH_OF_AVATAR = 0.62f

/** A round glass switch: [fill] while on. */
@Composable
private fun ArenaRoundSwitch(icon: ImageVector, description: String, on: Boolean, fill: Color, rim: Color, onClick: () -> Unit, size: Dp, modifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(if (on) fill else colors.ArenaGlassBottom)
                .border(dimens.borderThin, if (on) rim else colors.ArenaGlassRim, CircleShape)
                .clickable(role = Role.Button, onClick = onClick)
                .semantics { contentDescription = description },
            contentAlignment = Alignment.Center,
        ) {
            AppIcon(imageVector = icon, tint = if (on) colors.TextPrimary else colors.IconMuted, size = dimens.iconMedium)
        }
    }
}

/**
 * The dice button at the bottom right (design): a chunky white die (five
 * pips, a lit top and a shaded side) that bobs while the user can roll,
 * and its label in a violet pill under it ("Roll Dice", or "Move 4" while
 * a piece must be picked). No background; the whole of it takes the tap;
 * faded while it can't roll.
 */
@Composable
fun ArenaDiceButton(
    label: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    /** The label pill beside the die instead of under it (a bottom bar). */
    labelBeside: Boolean = false,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val bob = if (enabled) {
        val wobble = rememberInfiniteTransition(label = "dice-bob")
        wobble.animateFloat(-1f, 1f, infiniteRepeatable(tween(BOB_MS), RepeatMode.Reverse), label = "dice-bob-value").value
    } else {
        0f
    }
    val pill = RoundedCornerShape(percent = 50)
    val press = modifier
        // Faded as one flat picture: plain transparency let the die's side show through its face (looked chipped).
        .graphicsLayer {
            alpha = if (enabled) 1f else DISABLED_ALPHA
            compositingStrategy = CompositingStrategy.Offscreen
        }
        .clickable(enabled = enabled, role = Role.Button, interactionSource = null, indication = null, onClick = onClick)
    val die: @Composable () -> Unit = {
        Canvas(
            modifier = Modifier
                .size(dimens.arenaDiceIcon)
                .graphicsLayer {
                    rotationZ = DICE_TURN + bob * DICE_SWING
                    translationY = -bob * dimens.arenaDiceIcon.toPx() * DICE_HOP
                },
        ) { drawFunDice(colors.ArenaDrawFillTop) }
    }
    val text: @Composable () -> Unit = {
        BasicText(
            text = label,
            style = YapNPlayTheme.typography.title.copy(color = colors.TextPrimary, fontWeight = FontWeight.Black),
            maxLines = 1,
            modifier = Modifier
                .clip(pill)
                .background(Brush.verticalGradient(listOf(colors.ArenaDrawFillTop, colors.ArenaDrawFillBottom)))
                .border(dimens.borderThin, colors.ArenaDrawRim, pill)
                .padding(horizontal = Spacing.Md, vertical = Spacing.Xxs),
        )
    }
    if (labelBeside) {
        Row(modifier = press, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.Xs)) {
            die()
            text()
        }
    } else {
        Column(modifier = press, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(Spacing.Xs)) {
            die()
            text()
        }
    }
}

/** A white die seen a little from above: a shaded side, a lit face and five round pips in [pip]. */
private fun DrawScope.drawFunDice(pip: Color) {
    val s = size.minDimension
    val depth = s * DICE_DEPTH
    val face = s - depth
    val radius = CornerRadius(face * DICE_CORNER)
    // The side, peeking out below and to the right.
    drawRoundRect(Color(0xFFB9B3D6), topLeft = Offset(depth, depth), size = Size(face, face), cornerRadius = radius)
    // The face: white into a soft lavender.
    drawRoundRect(Brush.linearGradient(listOf(Color.White, Color(0xFFE6E1FA)), Offset.Zero, Offset(face, face)), size = Size(face, face), cornerRadius = radius)
    drawRoundRect(Color.White.copy(alpha = 0.9f), size = Size(face, face), cornerRadius = radius, style = Stroke(face * 0.03f))
    val r = face * PIP
    val lo = face * 0.28f
    val hi = face * 0.72f
    val mid = face / 2
    listOf(Offset(lo, lo), Offset(hi, lo), Offset(mid, mid), Offset(lo, hi), Offset(hi, hi)).forEach { c ->
        drawCircle(pip, r, c)
        drawCircle(Color.White.copy(alpha = 0.35f), r * 0.4f, c - Offset(r * 0.3f, r * 0.3f))
    }
}

private const val DISABLED_ALPHA = 0.45f
private const val BOB_MS = 650
private const val DICE_TURN = -12f
private const val DICE_SWING = 6f
private const val DICE_HOP = 0.05f
private const val DICE_DEPTH = 0.12f
private const val DICE_CORNER = 0.2f
private const val PIP = 0.085f
