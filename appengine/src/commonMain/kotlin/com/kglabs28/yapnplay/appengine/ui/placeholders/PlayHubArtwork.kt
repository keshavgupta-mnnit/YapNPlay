package com.kglabs28.yapnplay.appengine.ui.placeholders

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import com.kglabs28.yapnplay.appengine.core.common.AppConstants

/**
 * A Quick Play hub card's picture (randoms, friends, nearby), zoomed past
 * the picture's clear margin so the art fills its slot (never past it on a
 * tablet). Placeholder art for now: swap img_play_hub_*.png in
 * composeResources/drawable and nothing else changes.
 */
@Composable
fun PlayHubArtwork(
    image: BuiltInImage,
    modifier: Modifier = Modifier,
) {
    SlotFittedArt(
        phoneZoom = AppConstants.PLAY_HUB_ART_ZOOM,
        visibleWidth = VISIBLE_WIDTH,
        visibleHeight = VISIBLE_HEIGHT,
        modifier = modifier,
    ) {
        Image(
            painter = builtInPainter(image),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = it,
        )
    }
}

/** How much of the placeholder pictures the art covers (the rest is clear margin). */
private const val VISIBLE_WIDTH = 0.8f
private const val VISIBLE_HEIGHT = 0.75f
