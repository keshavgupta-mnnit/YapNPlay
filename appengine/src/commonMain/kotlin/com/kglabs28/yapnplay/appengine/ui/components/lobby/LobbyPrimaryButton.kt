package com.kglabs28.yapnplay.appengine.ui.components.lobby

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.rememberTextMeasurer
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.screens.playroomlobby.LobbyPrimaryButtonUi

/**
 * The lobby main button, from the START MATCH design: only the top-left
 * and bottom-right corners are cut, and a thin outline of the same shape
 * runs around it with a small gap. Glowing gold body, big white label and
 * a warm sub-line ("4/9 PLAYERS READY"). START MATCH and an active READY /
 * WATCHING are gold; NOT READY / NOT WATCHING are dark glass with a gold
 * rim; disabled is filled grey and ignores taps.
 */
@Composable
fun LobbyPrimaryButton(
    button: LobbyPrimaryButtonUi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    val cut = dimens.lobbyStartCut
    val gap = dimens.lobbyStartOutlineGap
    // The outline is larger by the gap, so its cut grows with it to stay parallel.
    val outerShape = CutCornerShape(topStart = cut + gap, topEnd = dimens.zero, bottomEnd = cut + gap, bottomStart = dimens.zero)
    val shape = CutCornerShape(topStart = cut, topEnd = dimens.zero, bottomEnd = cut, bottomStart = dimens.zero)
    val lit = button.enabled && (button.isStart || button.active)
    val rim = if (button.enabled) colors.StartButtonRim else colors.StartButtonDisabledRim
    val body = when {
        !button.enabled -> Brush.verticalGradient(listOf(colors.StartButtonDisabledTop, colors.StartButtonDisabledBottom))
        lit -> Brush.verticalGradient(listOf(colors.StartButtonTop, colors.StartButtonBottom))
        else -> Brush.verticalGradient(listOf(colors.RoomsSmallButton, colors.RoomsPanel))
    }

    Box(
        modifier = modifier
            .height(dimens.lobbyStartHeight)
            .then(
                if (lit) {
                    Modifier.shadow(dimens.glowBlur, outerShape, ambientColor = colors.StartButtonGlow, spotColor = colors.StartButtonGlow)
                } else {
                    Modifier
                },
            )
            .border(dimens.hairline, rim.copy(alpha = 0.7f), outerShape)
            .padding(gap),
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .clip(shape)
                .background(body)
                .border(dimens.borderThin, rim, shape)
                .clickable(enabled = button.enabled, role = Role.Button, onClick = onClick)
                .padding(horizontal = cut),
            contentAlignment = Alignment.Center,
        ) {
            // The label shrinks to fit a narrow column instead of being cut.
            val measurer = rememberTextMeasurer()
            val fullWidth = measurer.measure(button.label, typography.startLabel, maxLines = 1).size.width
            val scale = if (fullWidth > constraints.maxWidth && fullWidth > 0) constraints.maxWidth.toFloat() / fullWidth else 1f
            val labelStyle = typography.startLabel.copy(fontSize = typography.startLabel.fontSize * scale)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                BasicText(
                    text = button.label,
                    style = labelStyle.copy(
                        color = if (button.enabled) colors.TextPrimary else colors.TextSecondary,
                        shadow = if (lit) Shadow(color = colors.StartButtonGlow, blurRadius = labelStyle.fontSize.value) else null,
                    ),
                    maxLines = 1,
                )
                BasicText(
                    text = button.sublabel,
                    style = typography.micro.copy(
                        fontWeight = typography.startLabel.fontWeight,
                        color = when {
                            !button.enabled -> colors.TextSecondary
                            lit -> colors.StartButtonSub
                            else -> colors.StartButtonRim
                        },
                    ),
                    maxLines = 1,
                )
            }
        }
    }
}
