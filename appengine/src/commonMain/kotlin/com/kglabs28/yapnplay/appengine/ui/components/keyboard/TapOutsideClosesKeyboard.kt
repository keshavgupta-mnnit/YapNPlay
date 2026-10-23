package com.kglabs28.yapnplay.appengine.ui.components.keyboard

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalFocusManager

/**
 * Wraps the whole app once (in YapNPlayRoot): a touch anywhere outside the
 * text box that has the phone's keyboard closes the keyboard. The touch
 * still reaches whatever was under it.
 */
@Composable
fun TapOutsideClosesKeyboard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val focusManager = LocalFocusManager.current
    val field = remember { FocusedFieldBounds() }
    val area = remember { arrayOfNulls<LayoutCoordinates>(1) }
    CompositionLocalProvider(LocalFocusedField provides field) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .onGloballyPositioned { area[0] = it }
                .pointerInput(Unit) {
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
                        val box = field.bounds ?: return@awaitEachGesture
                        val at = area[0]?.localToWindow(down.position) ?: return@awaitEachGesture
                        if (!box.contains(at)) focusManager.clearFocus(force = true)
                    }
                },
        ) {
            content()
        }
    }
}
