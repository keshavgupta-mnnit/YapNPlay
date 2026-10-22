package com.kglabs28.yapnplay.appengine.ui.components.table

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.MicOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
 * The user's two switches just under their seat's pill (design): Mic and
 * Auto, each a round icon with its name beside it, filled dark pink while
 * on.
 */
@Composable
fun ArenaSeatSwitches(
    micOn: Boolean,
    autoplay: Boolean,
    onToggleMic: () -> Unit,
    onToggleAutoplay: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(Spacing.Md)) {
        ArenaRoundSwitch(
            icon = if (micOn) Icons.Rounded.Mic else Icons.Rounded.MicOff,
            label = MonoStrings.monoMicShort,
            description = MonoStrings.monoMic,
            on = micOn,
            onClick = onToggleMic,
        )
        ArenaRoundSwitch(
            icon = Icons.Rounded.Bolt,
            label = MonoStrings.monoAutoShort,
            description = MonoStrings.monoAutoplay,
            on = autoplay,
            onClick = onToggleAutoplay,
        )
    }
}

/** A round glass switch with its name beside it: filled dark pink when on. */
@Composable
private fun ArenaRoundSwitch(icon: ImageVector, label: String, description: String, on: Boolean, onClick: () -> Unit) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    Row(
        modifier = Modifier
            .clickable(role = Role.Button, onClick = onClick)
            .semantics { contentDescription = description },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Xs),
    ) {
        Box(
            modifier = Modifier
                .size(dimens.arenaSwitchSize)
                .clip(CircleShape)
                .background(if (on) colors.ArenaSwitchOn else colors.ArenaGlassBottom)
                .border(dimens.borderThin, if (on) colors.ArenaSwitchOnRim else colors.ArenaGlassRim, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            AppIcon(imageVector = icon, tint = if (on) colors.TextPrimary else colors.IconMuted, size = dimens.iconMedium)
        }
        BasicText(
            text = label,
            style = YapNPlayTheme.typography.label.copy(color = if (on) colors.TextPrimary else colors.TextSecondary, fontWeight = FontWeight.Bold),
            maxLines = 1,
        )
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
    Column(
        modifier = modifier
            .alpha(if (enabled) 1f else DISABLED_ALPHA)
            .clickable(enabled = enabled, role = Role.Button, interactionSource = null, indication = null, onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.Xs),
    ) {
        Canvas(
            modifier = Modifier
                .size(dimens.arenaDiceIcon)
                .graphicsLayer {
                    rotationZ = DICE_TURN + bob * DICE_SWING
                    translationY = -bob * dimens.arenaDiceIcon.toPx() * DICE_HOP
                },
        ) { drawFunDice(colors.ArenaDrawFillTop) }
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
