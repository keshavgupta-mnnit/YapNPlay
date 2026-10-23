package com.kglabs28.yapnplay.appengine.ui.components.profile

import com.kglabs28.yapnplay.appengine.ui.components.common.ShrinkText
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/** One person in a list: avatar, name and one more line, then [trailing] buttons. */
@Composable
fun PersonRow(
    avatarId: String,
    name: String,
    detail: String,
    modifier: Modifier = Modifier,
    trailing: @Composable RowScope.() -> Unit = {},
) {
    val colors = YapNPlayTheme.colors
    val typography = YapNPlayTheme.typography
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.Sm)) {
        PresetAvatar(avatarId = avatarId, size = YapNPlayTheme.dimens.personRowAvatar)
        Column(modifier = Modifier.weight(1f)) {
            ShrinkText(text = name, style = typography.label.copy(color = colors.TextPrimary, fontWeight = FontWeight.Bold))
            BasicText(text = detail, style = typography.micro.copy(color = colors.TextSecondary), maxLines = 1)
        }
        trailing()
    }
}
