package com.kglabs28.yapnplay.appengine.ui.components.home

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
 * Center app logo and tagline for the top bar.
 */
@Composable
fun HomeCenterLogo(
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        HomeLogoArtwork(
            modifier = Modifier.size(width = dimens.logoWidth, height = dimens.logoHeight),
            contentDescription = Strings.homeLogoTitle,
        )
        BasicText(
            text = Strings.homeLogoTagline,
            style = YapNPlayTheme.typography.caption.copy(
                color = colors.TextSecondary,
                fontWeight = FontWeight.Medium,
            ),
        )
    }
}
