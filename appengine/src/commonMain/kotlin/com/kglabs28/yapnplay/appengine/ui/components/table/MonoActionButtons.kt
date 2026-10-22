package com.kglabs28.yapnplay.appengine.ui.components.table

import com.kglabs28.yapnplay.appengine.ui.components.common.FitText
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Style
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.MonoStrings
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon

/**
 * The two callout buttons, side by side in the callout's place: CAUGHT
 * (red) catches whoever is down to one card without calling, MONO (orange
 * glass) calls it for the user. Both are always pressable and never light
 * up: remembering when to press is part of the game.
 */
@Composable
fun MonoCallButtons(
    onCaught: () -> Unit,
    onMono: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val each = (dimens.arenaCallWidth - Spacing.Xs) / 2
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(Spacing.Xs)) {
        ArenaPillButton(
            text = MonoStrings.monoCaughtButton,
            icon = null,
            fill = listOf(colors.ArenaCallFillTop, colors.ArenaCallFillBottom),
            rim = colors.ArenaCallNeon,
            glow = 0f,
            enabled = true,
            onClick = onCaught,
            width = each,
        )
        ArenaPillButton(
            text = MonoStrings.monoMonoButton,
            icon = null,
            fill = listOf(colors.StartButtonTop.copy(alpha = ORANGE_GLASS), colors.StartButtonBottom.copy(alpha = ORANGE_GLASS)),
            rim = colors.StartButtonRim,
            glow = 0f,
            enabled = true,
            onClick = onMono,
            width = each,
        )
    }
}

/**
 * The Draw button (design): a violet glass pill with a card icon. Under a
 * stack it reads "Take +N" and takes the penalty instead. Always
 * pressable — the player may draw even holding a playable card.
 */
@Composable
fun MonoDrawButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    ArenaPillButton(
        text = text,
        icon = Icons.Rounded.Style,
        fill = listOf(colors.ArenaDrawFillTop, colors.ArenaDrawFillBottom),
        rim = colors.ArenaDrawRim,
        glow = 0f,
        enabled = true,
        onClick = onClick,
        width = YapNPlayTheme.dimens.arenaDrawWidth,
        modifier = modifier,
    )
}

/** The shared look of the table's big buttons: a glowing glass pill, icon then label. */
@Composable
private fun ArenaPillButton(
    text: String,
    icon: ImageVector?,
    fill: List<Color>,
    rim: Color,
    glow: Float,
    enabled: Boolean,
    onClick: () -> Unit,
    width: Dp,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val shape = RoundedCornerShape(percent = 50)
    Row(
        modifier = modifier
            .width(width)
            .height(dimens.arenaActionHeight)
            .alpha(if (enabled) 1f else DISABLED_ALPHA)
            .drawBehind {
                if (glow <= 0f) return@drawBehind
                val radius = CornerRadius(size.height / 2)
                // A soft halo outside the pill, faintest at the edge.
                HALO.forEach { (spread, alpha) ->
                    val grow = size.height * spread
                    drawRoundRect(
                        rim,
                        topLeft = androidx.compose.ui.geometry.Offset(-grow / 2, -grow / 2),
                        size = androidx.compose.ui.geometry.Size(size.width + grow, size.height + grow),
                        cornerRadius = CornerRadius(radius.x + grow / 2),
                        alpha = alpha * glow,
                        style = Stroke(size.height * HALO_STROKE),
                    )
                }
            }
            .clip(shape)
            .background(Brush.verticalGradient(fill))
            .border(dimens.borderThin, rim, shape)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .padding(horizontal = if (icon == null) Spacing.Xs else Spacing.Md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Xs, Alignment.CenterHorizontally),
    ) {
        icon?.let { AppIcon(imageVector = it, tint = colors.TextPrimary, size = dimens.iconMedium) }
        // A narrow button shrinks its word rather than cutting it.
        FitText(
            text = text,
            style = YapNPlayTheme.typography.title.copy(color = colors.TextPrimary, fontWeight = FontWeight.Black),
            modifier = Modifier.weight(1f, fill = false),
        )
    }
}

private const val DISABLED_ALPHA = 0.45f
/** MONO's orange, see-through like glass. */
private const val ORANGE_GLASS = 0.75f
private const val HALO_STROKE = 0.08f
private val HALO = listOf(0.36f to 0.18f, 0.22f to 0.3f, 0.1f to 0.5f)
