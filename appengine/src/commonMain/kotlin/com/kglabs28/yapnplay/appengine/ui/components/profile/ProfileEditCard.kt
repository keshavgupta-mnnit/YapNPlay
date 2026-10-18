package com.kglabs28.yapnplay.appengine.ui.components.profile

import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.ui.components.AppTextField
import com.kglabs28.yapnplay.appengine.ui.components.common.GlassPillButton
import com.kglabs28.yapnplay.appengine.ui.components.common.ModalCard

/** Editing the name or @username: one field, its error under it, Save. */
@Composable
fun ProfileEditCard(
    title: String,
    value: String,
    hint: String,
    maxLength: Int,
    error: String?,
    saving: Boolean,
    onValueChange: (String) -> Unit,
    onSave: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ModalCard(
        title = title,
        onClose = onClose,
        modifier = modifier,
        actions = {
            GlassPillButton(text = Strings.profileSave, onClick = onSave, leadingIcon = Icons.Rounded.Check, enabled = !saving, height = YapNPlayTheme.dimens.ctaHeight)
        },
    ) {
        AppTextField(
            value = value,
            onValueChange = onValueChange,
            hint = hint,
            isError = error != null,
            maxLength = maxLength,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        )
        error?.let { BasicText(text = it, style = YapNPlayTheme.typography.micro.copy(color = YapNPlayTheme.colors.Error)) }
    }
}
