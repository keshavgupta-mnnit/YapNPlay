package com.kglabs28.yapnplay.appengine.ui.components.common

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput

/** Wraps the app: a touch anywhere stops the sliding text (the touch still goes through). */
@Composable
fun SlidingTextHost(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val state = remember { SlidingTextState() }
    CompositionLocalProvider(LocalSlidingText provides state) {
        Box(
            modifier = modifier.pointerInput(state) {
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
                    state.onAnyTouch()
                }
            },
        ) { content() }
    }
}
