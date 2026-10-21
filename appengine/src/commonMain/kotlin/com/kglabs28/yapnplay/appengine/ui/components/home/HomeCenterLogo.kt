package com.kglabs28.yapnplay.appengine.ui.components.home

import androidx.compose.ui.unit.Dp
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.ui.placeholders.HomeLogoArtwork

/**
 * App logo with the tagline underneath, for the top bar's left edge.
 */
@Composable
fun HomeCenterLogo(
    modifier: Modifier = Modifier,
    logoWidth: Dp = YapNPlayTheme.dimens.logoWidth,
    logoHeight: Dp = YapNPlayTheme.dimens.logoHeight,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        HomeLogoArtwork(
            modifier = Modifier.size(width = logoWidth, height = logoHeight),
            contentDescription = Strings.homeLogoTitle,
        )
        BasicText(
            text = Strings.homeLogoTagline,
            style = YapNPlayTheme.typography.micro.copy(
                color = colors.TextPrimary,
                fontWeight = FontWeight.Medium,
            ),
        )
    }
}
