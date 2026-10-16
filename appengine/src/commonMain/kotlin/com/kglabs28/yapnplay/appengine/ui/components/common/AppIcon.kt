package com.kglabs28.yapnplay.appengine.ui.components.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/**
 * Tinted vector icon without Material: the one way the app draws icons.
 * Decorative by default; pass [contentDescription] when it carries meaning.
 */
@Composable
fun AppIcon(
    imageVector: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier,
    size: Dp = YapNPlayTheme.dimens.iconMedium,
    contentDescription: String? = null,
) {
    Image(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = modifier.size(size),
        colorFilter = ColorFilter.tint(tint),
    )
}
