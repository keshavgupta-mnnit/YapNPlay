package com.kglabs28.yapnplay.appengine.ui.components.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import com.kglabs28.yapnplay.appengine.ui.components.common.FieldClearButton
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.glassPill

/** The design's glass input: a rounded dark field with a soft rim (red on error) and a faint hint. */
@Composable
fun ProfileTextField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    maxLength: Int,
    isError: Boolean,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val style = YapNPlayTheme.typography.label.copy(color = colors.TextPrimary)
    var focused by remember { mutableStateOf(false) }
    BasicTextField(
        value = value,
        onValueChange = { onValueChange(it.take(maxLength)) },
        singleLine = true,
        textStyle = style,
        cursorBrush = SolidColor(colors.Cyan),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { onDone() }),
        modifier = modifier.fillMaxWidth().onFocusChanged { focused = it.isFocused },
        decorationBox = { inner ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimens.ctaHeight)
                    .glassPill(glowColor = if (isError) colors.Error else colors.IconMuted, surface = colors.RoomsInput, shape = RoundedCornerShape(dimens.cbRowRadius))
                    .padding(horizontal = Spacing.Sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                    if (value.isEmpty()) BasicText(text = hint, style = style.copy(color = colors.RoomsPlaceholderDigits))
                    inner()
                }
                // While typing: × empties the field.
                if (focused && value.isNotEmpty()) FieldClearButton(onClear = { onValueChange("") })
            }
        },
    )
}
