package com.kglabs28.yapnplay.appengine.ui.components.gamezone

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.ViewCarousel
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.ui.components.common.PillButton

/**
 * "Grid View" / "Carousel" switch for game pickers. Shows the view you
 * will switch TO.
 */
@Composable
fun ViewModeToggle(
    isGrid: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens

    PillButton(
        text = if (isGrid) Strings.gameZoneCarouselView else Strings.gameZoneGridView,
        onClick = onToggle,
        gradient = listOf(colors.RoomsSmallButton, colors.RoomsPanel),
        contentColor = colors.TextPrimary,
        rimColor = colors.SocialZoneAccent,
        height = dimens.smallButtonHeight,
        leadingIcon = if (isGrid) Icons.Rounded.ViewCarousel else Icons.Rounded.GridView,
        trailingIcon = null,
        textStyle = YapNPlayTheme.typography.sectionTitle,
        modifier = modifier,
    )
}
