package com.kglabs28.yapnplay.appengine.ui.components.profile

import com.kglabs28.yapnplay.appengine.ui.components.common.ShrinkText
import com.kglabs28.yapnplay.appengine.core.ui.ProfileStrings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon

/** A line of the profile the user can change: the text with a small pencil; a tap edits it. */
@Composable
fun ProfileEditableText(text: String, style: TextStyle, onEdit: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.clickable(onClick = onEdit),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Xs),
    ) {
        ShrinkText(text = text, style = style, modifier = Modifier.weight(1f, fill = false))
        AppIcon(imageVector = Icons.Rounded.Edit, tint = YapNPlayTheme.colors.IconMuted, size = YapNPlayTheme.dimens.iconSmall, contentDescription = ProfileStrings.profileEdit)
    }
}
