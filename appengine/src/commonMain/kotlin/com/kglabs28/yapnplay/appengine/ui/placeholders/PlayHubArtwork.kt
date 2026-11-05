package com.kglabs28.yapnplay.appengine.ui.placeholders

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale

/**
 * A Quick Play hub card's picture (randoms, friends, nearby). Placeholder
 * art for now: swap img_play_hub_*.png in composeResources/drawable and
 * nothing else changes.
 */
@Composable
fun PlayHubArtwork(
    image: BuiltInImage,
    modifier: Modifier = Modifier,
) {
    Image(
        painter = builtInPainter(image),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = modifier,
    )
}
