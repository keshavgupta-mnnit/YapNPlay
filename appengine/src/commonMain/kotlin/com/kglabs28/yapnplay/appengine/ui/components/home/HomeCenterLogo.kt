package com.kglabs28.yapnplay.appengine.ui.components.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.kglabs28.yapnplay.appengine.core.designsystem.HeadlineFontFamily
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings

/**
 * Center logo and tagline for the top bar matching the mockup ("YapNPlay - Talk More. Play Your Way.").
 */
@Composable
fun HomeCenterLogo(
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        BasicText(
            text = Strings.homeTitle,
            style = YapNPlayTheme.typography.title.copy(
                fontFamily = HeadlineFontFamily,
                fontWeight = FontWeight.Bold,
                color = colors.TextPrimary,
            ),
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
