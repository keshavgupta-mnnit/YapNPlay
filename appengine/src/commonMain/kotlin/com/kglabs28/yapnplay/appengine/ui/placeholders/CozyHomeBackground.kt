package com.kglabs28.yapnplay.appengine.ui.placeholders

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import com.kglabs28.yapnplay.appengine.core.designsystem.MotionTokens
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.revealWhen

/**
 * Dashboard background: neon room art with the content on top. When
 * [frosted], the art blurs and a dark glass layer fades in over the whole
 * screen (both animate when tabs switch). Blur needs Android 12+; older
 * phones get the dark layer only. Swap the drawable when final art
 * lands — nothing else changes.
 */
@Composable
fun CozyHomeBackground(
    modifier: Modifier = Modifier,
    frosted: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colors = YapNPlayTheme.colors
    val blurRadius by animateDpAsState(
        targetValue = if (frosted) YapNPlayTheme.dimens.backdropBlur else YapNPlayTheme.dimens.zero,
        animationSpec = tween(MotionTokens.STANDARD_MS),
    )

    Box(modifier = modifier.fillMaxSize()) {
        HomeBackgroundArtwork(modifier = Modifier.fillMaxSize().blur(blurRadius))
        Box(
            modifier = Modifier
                .fillMaxSize()
                
                .revealWhen(frosted)
                .background(
                    Brush.verticalGradient(
                        listOf(colors.Background.copy(alpha = 0.55f), colors.Background.copy(alpha = 0.78f)),
                    ),
                ),
        )
        content()
    }
}
