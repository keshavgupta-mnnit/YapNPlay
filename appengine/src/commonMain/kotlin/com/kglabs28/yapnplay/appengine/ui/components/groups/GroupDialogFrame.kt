package com.kglabs28.yapnplay.appengine.ui.components.groups

import com.kglabs28.yapnplay.appengine.core.designsystem.AppBrushes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.Dp
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.glassPanel
import androidx.compose.foundation.layout.Arrangement

/** The frosted purple glass all group dialogs sit on (as in the design). */
@Composable
fun GroupDialogFrame(width: Dp, modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    val colors = YapNPlayTheme.colors
    Column(
        modifier = modifier
            .width(width)
            .glassPanel(
                glowColor = colors.SocialZoneAccent,
                surfaceBrush = AppBrushes.dialogCreateGroup,
            )
            .padding(Spacing.Md),
        verticalArrangement = Arrangement.spacedBy(Spacing.Sm),
        content = content,
    )
}
