package com.kglabs28.yapnplay.appengine.ui.components.profile

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.ui.components.common.ModalCard

/** "Edit Name" / "Edit Username": the field's label, the field, its error and a "12/24" count; Cancel / Save. */
@Composable
fun ProfileEditCard(
    title: String,
    label: String,
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
    val colors = YapNPlayTheme.colors
    val typography = YapNPlayTheme.typography
    ModalCard(
        surfaceBrush = Brush.verticalGradient(listOf(YapNPlayTheme.colors.GlassDialogTop, YapNPlayTheme.colors.GlassDialogBottom)),
        title = title,
        onClose = onClose,
        modifier = modifier,
        actions = {
            ProfileActionButton(text = Strings.profileCancel, kind = ProfileButtonKind.NEUTRAL, onClick = onClose, modifier = Modifier.weight(1f))
            ProfileActionButton(text = Strings.profileSave, kind = ProfileButtonKind.CONFIRM, onClick = onSave, enabled = !saving, modifier = Modifier.weight(1f))
        },
    ) {
        BasicText(text = label, style = typography.label.copy(color = colors.TextSecondary))
        ProfileTextField(
            value = value,
            onValueChange = onValueChange,
            hint = hint,
            maxLength = maxLength,
            isError = error != null,
            onDone = { if (!saving) onSave() },
        )
        Row(modifier = Modifier.fillMaxWidth()) {
            BasicText(text = error.orEmpty(), style = typography.micro.copy(color = colors.Error), modifier = Modifier.weight(1f))
            Spacer(Modifier)
            BasicText(text = Strings.profileCounter(value.length, maxLength), style = typography.micro.copy(color = colors.TextSecondary))
        }
    }
}
