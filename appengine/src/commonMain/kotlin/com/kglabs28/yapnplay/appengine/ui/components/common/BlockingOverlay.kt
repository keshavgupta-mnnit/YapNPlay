package com.kglabs28.yapnplay.appengine.ui.components.common

import com.kglabs28.yapnplay.appengine.ui.components.ads.AdPill
import androidx.compose.foundation.layout.padding
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
 * The blocking modals asking to be shown, newest on top; only the top one
 * is drawn. Each modal adds and removes only its own entry, so one closing
 * never hides another that is still up (e.g. the end-of-match leaderboard).
 * Screens never touch it directly; they call [BlockingOverlay].
 */
class BlockingOverlayState {
    private class Entry(val token: Any, val content: @Composable () -> Unit, val onDismiss: (() -> Unit)?, val showAd: Boolean)

    private val entries = mutableStateListOf<Entry>()

    val content: (@Composable () -> Unit)? get() = entries.lastOrNull()?.content

    /** Set for dismissable modals: tap outside or Back closes them. */
    val onDismiss: (() -> Unit)? get() = entries.lastOrNull()?.onDismiss

    /** The top modal is a decision (leave, end the game, a vote): an ad shows at the bottom centre under it. */
    val showAd: Boolean get() = entries.lastOrNull()?.showAd == true

    fun show(token: Any, content: @Composable () -> Unit, onDismiss: (() -> Unit)?, showAd: Boolean = false) {
        val index = entries.indexOfFirst { it.token === token }
        val entry = Entry(token, content, onDismiss, showAd)
        if (index >= 0) entries[index] = entry else entries.add(entry)
    }

    fun hide(token: Any) {
        entries.removeAll { it.token === token }
    }
}

val LocalBlockingOverlay = staticCompositionLocalOf { BlockingOverlayState() }

/** True inside the modal layer (set by [BlockingOverlayHost] around the modal it draws). */
val LocalOnModalLayer = staticCompositionLocalOf { false }

/**
 * Shows [content] on the app's blurred, darkened modal layer wherever it is placed: a dialog built on
 * this can never sit bare on a screen with the table showing through it, in any game.
 */
@Composable
fun OnModalLayer(content: @Composable () -> Unit) {
    if (LocalOnModalLayer.current) content() else BlockingOverlay(visible = true, onDismiss = null, content = content)
}

/**
 * App-level host for blocking modals. Wraps the whole app once (in
 * YapNPlayRoot). While a modal is up, everything behind it — top bar,
 * tabs, any screen — is blurred and darkened, every touch outside the
 * modal is swallowed, and system Back does nothing; the modal can only be
 * left through its own buttons. No screen has to disable its own controls.
 * A dismissable modal also closes on a tap outside it or on Back.
 */
@Composable
fun BlockingOverlayHost(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val state = remember { BlockingOverlayState() }
    val modal = state.content
    val onDismiss = state.onDismiss
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
                        // Swallow every touch so nothing behind can be used; a tap
                        // that the modal did not take dismisses it (if allowed).
                        .pointerInput(onDismiss) {
                            awaitEachGesture {
                                val down = awaitFirstDown(requireUnconsumed = false)
                                val outside = !down.isConsumed
                                down.consume()
                                val up = waitForUpOrCancellation()
                                if (outside && up != null) onDismiss?.invoke()
                            }
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    // Marks touches inside the modal so the scrim ignores them.
                    Box(
                        modifier = Modifier.pointerInput(Unit) {
                            awaitEachGesture { awaitFirstDown(requireUnconsumed = false).consume() }
                        },
                    ) {
                        CompositionLocalProvider(LocalOnModalLayer provides true) { modal() }
                    }
                    // A decision's ad, at the bottom centre; its taps are its own (never "outside").
                    if (state.showAd) {
                        AdPill(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = Spacing.Xxxs)
                                .pointerInput(Unit) { awaitEachGesture { awaitFirstDown(requireUnconsumed = false).consume() } },
                        )
                    }
                }
            }
        }
        BlockBackPress(enabled = modal != null, onBack = { onDismiss?.invoke() })
    }
}

/**
 * Shows [content] as the app's blocking modal while [visible]: centred,
 * over a frosted cover of the whole app. With [onDismiss], a tap outside
 * or Back calls it; without, the modal only closes through its buttons.
 * [content] stays live (recomposes with the caller).
 */
@Composable
fun BlockingOverlay(
    visible: Boolean,
    onDismiss: (() -> Unit)? = null,
    /** A decision (leave, end the game, a vote, exit): an ad at the bottom centre while it is up. */
    showAd: Boolean = false,
    content: @Composable () -> Unit,
) {
    val host = LocalBlockingOverlay.current
    val latest by rememberUpdatedState(content)
    val latestDismiss by rememberUpdatedState(onDismiss)
    val dismissable = onDismiss != null
    val token = remember { Any() }
    DisposableEffect(visible, dismissable, showAd, host) {
        if (visible) host.show(token, { latest() }, if (dismissable) ({ latestDismiss?.invoke() }) else null, showAd)
        onDispose { host.hide(token) }
    }
}
