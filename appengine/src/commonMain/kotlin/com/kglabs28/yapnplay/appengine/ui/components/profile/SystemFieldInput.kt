package com.kglabs28.yapnplay.appengine.ui.components.profile

import com.kglabs28.yapnplay.appengine.ui.components.keyboard.LocalFocusedField
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.common.ShrinkText
import kotlinx.coroutines.launch

/**
 * The text inside [ProfileTextField] when the player chose the phone's
 * keyboard: a real text box (the phone's keyboard opens on a tap) that asks
 * to be scrolled into view when it gets focus and again once the keyboard
 * is up, so the keyboard never hides it.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SystemFieldInput(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    maxLength: Int,
    lines: Int,
    style: TextStyle,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val inView = remember { BringIntoViewRequester() }
    val scope = rememberCoroutineScope()
    val focusedField = LocalFocusedField.current
    val token = remember { Any() }
    var focused by remember { mutableStateOf(false) }
    val placed = remember { arrayOfNulls<LayoutCoordinates>(1) }
    val keyboardHeight = WindowInsets.ime.getBottom(LocalDensity.current)
    LaunchedEffect(focused, keyboardHeight) { if (focused) inView.bringIntoView() }
    DisposableEffect(token) { onDispose { focusedField.clear(token) } }
    BasicTextField(
        value = value,
        onValueChange = { onValueChange(it.take(maxLength)) },
        textStyle = style,
        singleLine = lines == 1,
        maxLines = lines,
        cursorBrush = SolidColor(colors.Cyan),
        keyboardOptions = KeyboardOptions(imeAction = if (lines == 1) ImeAction.Done else ImeAction.Default),
        keyboardActions = KeyboardActions(onDone = { onDone() }),
        modifier = modifier
            .bringIntoViewRequester(inView)
            // Where the focused box is, so a tap anywhere else closes the keyboard.
            .onGloballyPositioned { placed[0] = it }
            .onFocusEvent {
                focused = it.isFocused
                if (it.isFocused) {
                    focusedField.set(token, placed[0])
                    scope.launch { inView.bringIntoView() }
                } else {
                    focusedField.clear(token)
                }
            },
        decorationBox = { inner ->
            Box(contentAlignment = if (lines == 1) Alignment.CenterStart else Alignment.TopStart) {
                if (value.isEmpty()) ShrinkText(text = hint, style = style.copy(color = colors.RoomsPlaceholderDigits))
                inner()
            }
        },
    )
}
