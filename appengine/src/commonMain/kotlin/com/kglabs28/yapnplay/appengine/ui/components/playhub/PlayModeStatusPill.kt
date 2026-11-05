package com.kglabs28.yapnplay.appengine.ui.components.playhub

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.glassPill
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon
import com.kglabs28.yapnplay.appengine.ui.components.common.ShrinkText

/**
 * The small glass pill under a hub card's title ("8 Friends Online", "No
 * internet needed"): [icon] in [tint], the text, then anything [trailing]
 * (the friends' faces).
 */
@Composable
fun PlayModeStatusPill(
    icon: ImageVector,
    text: String,
    tint: Color,
    modifier: Modifier = Modifier,
    trailing: @Composable RowScope.() -> Unit = {},
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    Row(
        modifier = modifier
            .height(dimens.playHubPillHeight)
            .glassPill(glowColor = tint, surface = colors.RoomsPanel)
            .padding(horizontal = Spacing.Sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Xs),
    ) {
        AppIcon(imageVector = icon, tint = tint, size = dimens.iconSmall)
        ShrinkText(text = text, style = YapNPlayTheme.typography.label.copy(color = colors.TextPrimary))
        trailing()
    }
}
