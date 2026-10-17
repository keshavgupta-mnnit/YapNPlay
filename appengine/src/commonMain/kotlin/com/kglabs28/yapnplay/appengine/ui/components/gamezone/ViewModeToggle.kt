package com.kglabs28.yapnplay.appengine.ui.components.gamezone

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.ViewCarousel
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.ui.components.common.GlassPillButton

/**
 * "Grid View" / "Carousel" switch for game pickers. Shows the view you
 * will switch TO.
 */
@Composable
fun ViewModeToggle(
    isGrid: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    accent: Color = YapNPlayTheme.colors.SocialZoneAccent,
    contentColor: Color = YapNPlayTheme.colors.TextPrimary,
) {
    GlassPillButton(
        text = if (isGrid) Strings.gameZoneCarouselView else Strings.gameZoneGridView,
        onClick = onToggle,
        leadingIcon = if (isGrid) Icons.Rounded.ViewCarousel else Icons.Rounded.GridView,
        accent = accent,
        contentColor = contentColor,
        modifier = modifier,
    )
}
