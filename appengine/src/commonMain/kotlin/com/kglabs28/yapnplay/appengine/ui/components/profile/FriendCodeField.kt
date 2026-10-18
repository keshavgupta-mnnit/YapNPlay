package com.kglabs28.yapnplay.appengine.ui.components.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.glassPill
import com.kglabs28.yapnplay.appengine.core.ui.Strings

/**
 * The glass pill for typing a friend's ID (like the room-code field, but
 * letters and digits from the keyboard): spaced capitals, a faint hint,
 * red rim on error. The screen cleans what's typed.
 */
@Composable
fun FriendCodeField(
    code: String,
    isError: Boolean,
    onCodeChange: (String) -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val typography = YapNPlayTheme.typography
    val textStyle = typography.title.copy(color = colors.TextPrimary, letterSpacing = typography.title.fontSize / 2)
    BasicTextField(
        value = code,
        onValueChange = onCodeChange,
        singleLine = true,
        textStyle = textStyle,
        cursorBrush = SolidColor(colors.GameZoneAccent),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Characters,
            autoCorrectEnabled = false,
            keyboardType = KeyboardType.Ascii,
            imeAction = ImeAction.Done,
        ),
        keyboardActions = KeyboardActions(onDone = { onDone() }),
        modifier = modifier,
        decorationBox = { inner ->
            Box(
                modifier = Modifier
                    .height(YapNPlayTheme.dimens.codeFieldHeight)
                    .glassPill(glowColor = if (isError) colors.Error else colors.IconMuted, surface = colors.RoomsInput)
                    .padding(horizontal = Spacing.Md),
                contentAlignment = Alignment.CenterStart,
            ) {
                if (code.isEmpty()) BasicText(text = Strings.profileCodeHint, style = textStyle.copy(color = colors.RoomsPlaceholderDigits))
                inner()
            }
        },
    )
}
