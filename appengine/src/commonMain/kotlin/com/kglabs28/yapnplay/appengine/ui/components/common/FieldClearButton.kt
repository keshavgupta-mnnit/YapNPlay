package com.kglabs28.yapnplay.appengine.ui.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings

/** The small round × at the end of a text field (shown while it's active and not empty); a tap empties it. */
@Composable
fun FieldClearButton(onClear: () -> Unit, modifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    Box(
        modifier = modifier
            .size(dimens.fieldClearButton)
            .clip(CircleShape)
            .background(colors.IconMuted.copy(alpha = FILL_ALPHA))
            .clickable(role = Role.Button, onClick = onClear),
        contentAlignment = Alignment.Center,
    ) {
        AppIcon(imageVector = Icons.Rounded.Close, tint = colors.TextPrimary, size = dimens.iconSmall, contentDescription = Strings.clearField)
    }
}

private const val FILL_ALPHA = 0.35f
