package com.kglabs28.yapnplay.appengine.ui.components.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.ui.placeholders.HomeLogoArtwork

/** The top bar's left end: the logo, with the app's name and tagline beside it. */
@Composable
fun TopBarBrand(modifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    val typography = YapNPlayTheme.typography
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.Xxs)) {
        HomeLogoArtwork(modifier = Modifier.size(YapNPlayTheme.dimens.topBarBrandLogo), contentDescription = Strings.homeLogoTitle)
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            BasicText(text = Strings.homeLogoTitle, style = typography.topBarBrand.copy(color = colors.TextPrimary), maxLines = 1)
            BasicText(text = Strings.homeLogoTagline, style = typography.topBarTagline.copy(color = colors.HomeTextSoft), maxLines = 1)
        }
    }
}
