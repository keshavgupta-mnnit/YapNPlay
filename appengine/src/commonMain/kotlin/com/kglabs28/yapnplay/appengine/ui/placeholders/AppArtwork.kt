package com.kglabs28.yapnplay.appengine.ui.placeholders

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage

/**
 * An app image: the [url] from AppManager, with the built-in [image] as
 * Coil's placeholder (while loading), error (if it fails) and fallback
 * (when the URL is empty).
 */
@Composable
fun AppArtwork(
    image: BuiltInImage,
    url: String,
    contentScale: ContentScale,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    val local = builtInPainter(image)
    AsyncImage(
        model = url.ifBlank { null },
        contentDescription = contentDescription,
        modifier = modifier,
        placeholder = local,
        error = local,
        fallback = local,
        contentScale = contentScale,
    )
}
