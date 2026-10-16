package com.kglabs28.yapnplay.appengine.ui.components.common

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/**
 * Ambient background wrapper matching the cozy sci-fi atmosphere of the home mockup.
 */
@Composable
fun CozyHomeBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val colors = YapNPlayTheme.colors
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.Background),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Game Zone ambient radial glow (Left side)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        colors.GameZoneGlow.copy(alpha = 0.22f),
                        Color.Transparent,
                    ),
                    center = Offset(width * 0.28f, height * 0.55f),
                    radius = width * 0.35f,
                ),
                center = Offset(width * 0.28f, height * 0.55f),
                radius = width * 0.35f,
            )

            // Social Zone ambient radial glow (Right side)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        colors.SocialZoneGlow.copy(alpha = 0.25f),
                        colors.SocialZoneAccent.copy(alpha = 0.15f),
                        Color.Transparent,
                    ),
                    center = Offset(width * 0.75f, height * 0.55f),
                    radius = width * 0.38f,
                ),
                center = Offset(width * 0.75f, height * 0.55f),
                radius = width * 0.38f,
            )
        }
        content()
    }
}
