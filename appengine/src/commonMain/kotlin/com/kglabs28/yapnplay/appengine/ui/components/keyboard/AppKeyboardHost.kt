package com.kglabs28.yapnplay.appengine.ui.components.keyboard

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.common.BlockBackPress

/**
 * Hosts the app's own keyboard over everything (dialogs included): while a
 * field has it open, the keys sit at the bottom; a tap above them or Back
 * closes it. Wraps the whole app once (in YapNPlayRoot).
 */
@Composable
fun AppKeyboardHost(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val controller = remember { AppKeyboardController() }
    CompositionLocalProvider(LocalAppKeyboard provides controller) {
        Box(modifier = modifier.fillMaxSize()) {
            content()
            val request = controller.request
            if (request != null) {
                // Above the keys: a light shade; a tap there closes the keyboard (and isn't passed on).
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(YapNPlayTheme.colors.Background.copy(alpha = SHADE_ALPHA))
                        .pointerInput(Unit) {
                            awaitEachGesture {
                                val down = awaitFirstDown(requireUnconsumed = false)
                                if (!down.isConsumed) controller.close()
                                down.consume()
                            }
                        },
                )
                AppKeyboard(
                    request = request,
                    onType = controller::type,
                    onDone = controller::done,
                    onClose = { controller.close() },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .widthIn(max = YapNPlayTheme.dimens.appKeyboardMaxWidth)
                        .fillMaxWidth()
                        .height(YapNPlayTheme.dimens.appKeyboardHeight),
                )
            }
            BlockBackPress(enabled = request != null, onBack = { controller.close() })
        }
    }
}

private const val SHADE_ALPHA = 0.25f
