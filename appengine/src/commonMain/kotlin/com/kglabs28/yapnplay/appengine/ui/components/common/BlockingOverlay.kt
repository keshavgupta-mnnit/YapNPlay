package com.kglabs28.yapnplay.appengine.ui.components.common

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.pointerInput
import com.kglabs28.yapnplay.appengine.core.designsystem.MotionTokens
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/**
 * Holds the one blocking modal currently shown (or none). Screens never
 * touch it directly; they call [BlockingOverlay].
 */
class BlockingOverlayState {
    var content: (@Composable () -> Unit)? by mutableStateOf(null)
}

val LocalBlockingOverlay = staticCompositionLocalOf { BlockingOverlayState() }

/**
 * App-level host for blocking modals. Wraps the whole app once (in
 * YapNPlayRoot). While a modal is up, everything behind it — top bar,
 * tabs, any screen — is blurred and darkened, every touch outside the
 * modal is swallowed, and system Back does nothing; the modal can only be
 * left through its own buttons. No screen has to disable its own controls.
 */
@Composable
fun BlockingOverlayHost(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val state = remember { BlockingOverlayState() }
    val modal = state.content
    val colors = YapNPlayTheme.colors
    val blurRadius by animateDpAsState(
        targetValue = if (modal != null) YapNPlayTheme.dimens.backdropBlur else YapNPlayTheme.dimens.zero,
        animationSpec = tween(MotionTokens.STANDARD_MS),
    )

    CompositionLocalProvider(LocalBlockingOverlay provides state) {
        Box(modifier = modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxSize().blur(blurRadius)) {
                content()
            }
            if (modal != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(colors.Background.copy(alpha = 0.6f), colors.Background.copy(alpha = 0.8f)),
                            ),
                        )
                        // Swallow every touch so nothing behind can be used.
                        .pointerInput(Unit) {
                            awaitEachGesture { awaitFirstDown(requireUnconsumed = false).consume() }
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    modal()
                }
            }
        }
        BlockBackPress(enabled = modal != null)
    }
}

/**
 * Shows [content] as the app's blocking modal while [visible]: centred,
 * over a frosted cover of the whole app, not dismissable by tapping
 * outside or by Back. [content] stays live (recomposes with the caller).
 */
@Composable
fun BlockingOverlay(
    visible: Boolean,
    content: @Composable () -> Unit,
) {
    val host = LocalBlockingOverlay.current
    val latest by rememberUpdatedState(content)
    DisposableEffect(visible, host) {
        if (visible) host.content = { latest() }
        onDispose { if (visible) host.content = null }
    }
}
