package com.kglabs28.yapnplay.appengine.ui.components.groups

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.ui.components.common.GlassIconButton

/** A centred two-tone dialog title ("CREATE NEW" white, "GROUP" green) with the close cross on the right. */
@Composable
fun GroupDialogTitle(start: String, end: String, onClose: () -> Unit, modifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    Box(modifier = modifier.fillMaxWidth()) {
        BasicText(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = colors.TextPrimary)) { append(start) }
                withStyle(SpanStyle(color = colors.GameZoneAccent)) { append(end) }
            },
            style = YapNPlayTheme.typography.headline,
            modifier = Modifier.align(Alignment.Center),
        )
        GlassIconButton(
            imageVector = Icons.Rounded.Close,
            contentDescription = Strings.lobbyClose,
            onClick = onClose,
            size = dimens.modalCloseButton,
            iconSize = dimens.modalCloseIcon,
            tint = colors.TextPrimary,
            modifier = Modifier.align(Alignment.CenterEnd),
        )
    }
}
