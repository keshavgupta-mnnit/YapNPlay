package com.kglabs28.yapnplay.appengine.ui.components.playrooms

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.glassPill
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon

/**
 * Glass pill text field for a room code: spaced-out characters, a
 * faint "123456" hint, QR glyph on the right, red rim on error.
 * Normalising the input is the view model's job.
 */
@Composable
fun RoomCodeField(
    code: String,
    isError: Boolean,
    onCodeChange: (String) -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val textStyle = YapNPlayTheme.typography.title.copy(
        letterSpacing = YapNPlayTheme.typography.title.fontSize / 2,
    )

    BasicTextField(
        value = code,
        onValueChange = onCodeChange,
        singleLine = true,
        textStyle = textStyle.copy(color = colors.TextPrimary),
        cursorBrush = SolidColor(colors.GameZoneAccent),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.NumberPassword,
            imeAction = ImeAction.Done,
        ),
        keyboardActions = KeyboardActions(onDone = { onDone() }),
        modifier = modifier
            .height(dimens.codeFieldHeight)
            .glassPill(
                glowColor = if (isError) colors.Error else colors.IconMuted,
                surface = colors.RoomsInput,
            ),
        decorationBox = { inner ->
            Row(
                modifier = Modifier.padding(horizontal = Spacing.Md),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    if (code.isEmpty()) {
                        BasicText(
                            text = Strings.roomsCodeHint,
                            style = textStyle.copy(color = colors.RoomsPlaceholderDigits),
                        )
                    }
                    inner()
                }
                AppIcon(
                    imageVector = Icons.Rounded.QrCodeScanner,
                    tint = colors.IconMuted,
                )
            }
        },
    )
}
