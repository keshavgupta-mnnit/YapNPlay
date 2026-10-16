package com.kglabs28.yapnplay.appengine.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.VisualTransformation
import com.kglabs28.yapnplay.appengine.core.common.TextUtils
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/**
 * Single-line text input (display name, room code). Hint text comes from
 * resources via the caller; error text turns the border red.
 */
@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    hint: String = "",
    enabled: Boolean = true,
    isError: Boolean = false,
    maxLength: Int = Int.MAX_VALUE,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val border = when {
        isError -> colors.Error
        else -> colors.Cyan.copy(alpha = 0.6f)
    }
    BasicTextField(
        value = value,
        onValueChange = { onValueChange(TextUtils.capLength(it, maxLength)) },
        enabled = enabled,
        textStyle = YapNPlayTheme.typography.body.copy(
            color = if (enabled) colors.TextPrimary else colors.TextDisabled,
        ),
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        cursorBrush = SolidColor(colors.Cyan),
        modifier = modifier
            .clip(YapNPlayTheme.shapes.button)
            .background(if (enabled) colors.Panel else colors.Raised)
            .border(dimens.borderThin, border, YapNPlayTheme.shapes.button)
            .defaultMinSize(minHeight = dimens.buttonHeight),
        decorationBox = { inner ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.Md),
                contentAlignment = Alignment.CenterStart,
            ) {
                if (value.isEmpty() && hint.isNotEmpty()) {
                    BasicText(
                        text = hint,
                        style = YapNPlayTheme.typography.body.copy(color = colors.TextDisabled),
                    )
                }
                inner()
            }
        },
    )
}
