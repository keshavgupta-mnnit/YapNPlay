package com.kglabs28.yapnplay.appengine.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.kglabs28.yapnplay.appengine.core.common.AppConstants

/**
 * Room-code entry: a hidden text field driving a [RoomCodeDisplay].
 * Upper-cases input automatically.
 */
@Composable
fun RoomCodeInput(
    code: String,
    onCodeChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    hint: String = "",
    enabled: Boolean = true,
    isError: Boolean = false,
) {
    Column(modifier = modifier) {
        RoomCodeDisplay(
            code = code,
            modifier = Modifier,
        )
        AppTextField(
            value = code,
            onValueChange = { onCodeChange(it.uppercase()) },
            hint = hint,
            enabled = enabled,
            isError = isError,
            maxLength = AppConstants.ROOM_CODE_LENGTH,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
            modifier = Modifier,
        )
    }
}
