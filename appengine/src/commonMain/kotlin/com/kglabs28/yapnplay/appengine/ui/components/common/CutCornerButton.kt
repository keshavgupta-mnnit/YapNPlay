package com.kglabs28.yapnplay.appengine.ui.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.semantics.Role
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/**
 * The START MATCH button design at dialog size: top-left and bottom-right
 * corners cut, a thin outline of the same shape a small gap outside. [lit]
 * is the glowing gold body; otherwise dark glass with a gold rim.
 * Disabled is grey and ignores taps.
 */
@Composable
fun CutCornerButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    lit: Boolean = true,
    enabled: Boolean = true,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val cut = dimens.cutButtonCut
    val gap = dimens.lobbyStartOutlineGap
    val outerShape = CutCornerShape(topStart = cut + gap, topEnd = dimens.zero, bottomEnd = cut + gap, bottomStart = dimens.zero)
    val shape = CutCornerShape(topStart = cut, topEnd = dimens.zero, bottomEnd = cut, bottomStart = dimens.zero)
    val glowing = enabled && lit
    val rim = if (enabled) colors.StartButtonRim else colors.StartButtonDisabledRim
    val body = when {
        !enabled -> Brush.verticalGradient(listOf(colors.StartButtonDisabledTop, colors.StartButtonDisabledBottom))
        lit -> Brush.verticalGradient(listOf(colors.StartButtonTop, colors.StartButtonBottom))
        else -> Brush.verticalGradient(listOf(colors.RoomsSmallButton, colors.RoomsPanel))
    }
    Box(
        modifier = modifier
            .height(dimens.cutButtonHeight)
            .widthIn(min = dimens.cutButtonMinWidth)
            .then(
                if (glowing) Modifier.shadow(dimens.glowBlur, outerShape, ambientColor = colors.StartButtonGlow, spotColor = colors.StartButtonGlow) else Modifier,
            )
            .border(dimens.hairline, rim.copy(alpha = OUTLINE_ALPHA), outerShape)
            .padding(gap),
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(shape)
                .background(body)
                .border(dimens.borderThin, rim, shape)
                .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        )
        BasicText(
            text = text.uppercase(),
            style = YapNPlayTheme.typography.title.copy(
                color = when {
                    !enabled -> colors.TextSecondary
                    lit -> colors.TextPrimary
                    else -> colors.StartButtonRim
                },
                shadow = if (glowing) Shadow(color = colors.StartButtonGlow, blurRadius = SHADOW_BLUR) else null,
            ),
            maxLines = 1,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = Spacing.Lg),
        )
    }
}

private const val OUTLINE_ALPHA = 0.7f
private const val SHADOW_BLUR = 12f
