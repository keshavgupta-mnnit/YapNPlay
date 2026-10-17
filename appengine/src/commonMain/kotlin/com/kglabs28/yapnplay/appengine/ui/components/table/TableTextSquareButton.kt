package com.kglabs28.yapnplay.appengine.ui.components.table

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/** A square top-bar button with a short [text] ("3D"), the size of settings and exit, frosted like the match clock. */
@Composable
fun TableTextSquareButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val radius = dimens.ludoGlossRadius
    val shape = RoundedCornerShape(radius)
    Box(
        modifier = modifier
            .size(dimens.ludoSquareButton)
            .tableFrostedGlass(tint = colors.IconMuted, shape = shape, radius = radius, glow = dimens.zero)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        BasicText(text = text, style = YapNPlayTheme.typography.title.copy(color = colors.TextPrimary))
    }
}
