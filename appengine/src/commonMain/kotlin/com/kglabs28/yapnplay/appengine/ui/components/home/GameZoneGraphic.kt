package com.kglabs28.yapnplay.appengine.ui.components.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/**
 * Graphic illustration for the "ENTER GAME ZONE" card matching home.png.
 * Features a glowing sonar/radar scope in the center, flanked by floating sci-fi D-pad,
 * 3D cubes/dice, and arcade button knobs.
 */
@Composable
fun GameZoneGraphic(
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        // Central Radar Scope
        Canvas(modifier = Modifier.size(dimens.graphicSize)) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val maxRadius = size.width / 2f - 4f

            // Crosshair lines
            drawLine(
                color = colors.GameZoneAccent.copy(alpha = 0.25f),
                start = Offset(center.x, 0f),
                end = Offset(center.x, size.height),
                strokeWidth = 1.5f,
            )
            drawLine(
                color = colors.GameZoneAccent.copy(alpha = 0.25f),
                start = Offset(0f, center.y),
                end = Offset(size.width, center.y),
                strokeWidth = 1.5f,
            )

            // Concentric Glowing Rings
            val ringCount = 4
            for (i in 1..ringCount) {
                val r = (maxRadius / ringCount) * i
                drawCircle(
                    color = colors.GameZoneAccent.copy(alpha = if (i == ringCount) 0.9f else 0.4f),
                    center = center,
                    radius = r,
                    style = Stroke(width = if (i == ringCount) 3f else 1.5f),
                )
            }

            // Radar Sector Wedge (Sonar sweep line)
            drawArc(
                color = colors.GameZoneAccent.copy(alpha = 0.35f),
                startAngle = 210f,
                sweepAngle = 75f,
                useCenter = true,
            )

            // Radar Blip Dots
            val blipOffsets = listOf(
                Offset(center.x - maxRadius * 0.4f, center.y - maxRadius * 0.3f),
                Offset(center.x + maxRadius * 0.5f, center.y - maxRadius * 0.2f),
                Offset(center.x - maxRadius * 0.6f, center.y + maxRadius * 0.4f),
                Offset(center.x + maxRadius * 0.35f, center.y + maxRadius * 0.5f),
            )
            for (blip in blipOffsets) {
                drawCircle(color = colors.GameZoneAccent, radius = 3.5f, center = blip)
                drawCircle(color = colors.GameZoneAccent.copy(alpha = 0.3f), radius = 8f, center = blip)
            }
        }

        // Top Left Floating Circuit Cube
        FloatingCubePlaceholder(
            modifier = Modifier.align(Alignment.TopStart).padding(Spacing.Sm),
        )

        // Top Right Floating Circuit Cube
        FloatingCubePlaceholder(
            modifier = Modifier.align(Alignment.TopEnd).padding(Spacing.Sm),
        )

        // Bottom Left Sci-Fi D-Pad Widget
        SciFiDpadWidget(
            modifier = Modifier.align(Alignment.BottomStart).padding(Spacing.Sm),
        )

        // Bottom Right Metallic Arcade Buttons
        ArcadeButtonsWidget(
            modifier = Modifier.align(Alignment.BottomEnd).padding(Spacing.Sm),
        )
    }
}

@Composable
private fun FloatingCubePlaceholder(
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    Box(
        modifier = modifier
            .size(dimens.chipHeight)
            .clip(RoundedCornerShape(dimens.buttonCut))
            .background(colors.Panel)
            .border(dimens.hairline, colors.GameZoneAccent.copy(alpha = 0.6f), RoundedCornerShape(dimens.buttonCut)),
        contentAlignment = Alignment.Center,
    ) {
        BasicText(
            text = "🎲",
            style = YapNPlayTheme.typography.title,
        )
    }
}

@Composable
private fun SciFiDpadWidget(
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    Box(
        modifier = modifier
            .size(dimens.voiceToggle)
            .clip(RoundedCornerShape(dimens.buttonCut))
            .background(colors.Panel.copy(alpha = 0.9f))
            .border(dimens.borderThin, colors.GameZoneAccent, RoundedCornerShape(dimens.buttonCut)),
        contentAlignment = Alignment.Center,
    ) {
        BasicText(
            text = "🎮",
            style = YapNPlayTheme.typography.title,
        )
    }
}

@Composable
private fun ArcadeButtonsWidget(
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Spacing.Xxs),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(dimens.badgeSize)
                .clip(CircleShape)
                .background(colors.Raised)
                .border(dimens.borderThin, colors.GameZoneAccent, CircleShape),
        )
        Box(
            modifier = Modifier
                .size(dimens.badgeMedium)
                .clip(CircleShape)
                .background(colors.Raised)
                .border(dimens.avatarRing, colors.GameZoneAccent, CircleShape),
        )
    }
}
