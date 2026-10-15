package com.kglabs28.yapnplay.appengine.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/** Small online/offline dot. Green when online, dim when not. */
@Composable
fun PresenceDot(
    online: Boolean,
    modifier: Modifier = Modifier,
    size: Dp? = null,
    onlineColor: Color = YapNPlayTheme.colors.Success,
) {
    val colors = YapNPlayTheme.colors
    Box(
        modifier = modifier
            .size(size ?: YapNPlayTheme.dimens.statusDot)
            .clip(YapNPlayTheme.shapes.avatar)
            .background(if (online) onlineColor else colors.TextDisabled)
            .border(YapNPlayTheme.dimens.hairline, colors.Background, YapNPlayTheme.shapes.avatar),
    )
}
