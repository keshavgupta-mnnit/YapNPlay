package com.kglabs28.yapnplay.appengine.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings

/**
 * Temporary stand-in for screens built in later phases.
 */
@Composable
fun PlaceholderScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(YapNPlayTheme.colors.Background),
        contentAlignment = Alignment.Center,
    ) {
        BasicText(
            text = Strings.homeTitle,
            style = YapNPlayTheme.typography.headline.copy(color = YapNPlayTheme.colors.TextPrimary),
        )
    }
}
