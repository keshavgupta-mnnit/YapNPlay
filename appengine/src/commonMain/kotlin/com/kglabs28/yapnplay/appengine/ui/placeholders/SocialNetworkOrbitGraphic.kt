package com.kglabs28.yapnplay.appengine.ui.placeholders

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/**
 * "PLAY WITH FRIENDS" card graphic. Temporary static art until final
 * assets land — swap the drawable, nothing else changes.
 */
@Composable
fun SocialNetworkOrbitGraphic(
    modifier: Modifier = Modifier,
) {
    val dimens = YapNPlayTheme.dimens

    Box(
        modifier = modifier
            .fillMaxSize()
            .heightIn(max = dimens.graphicMaxHeight),
        contentAlignment = Alignment.Center,
    ) {
        FriendZoneArtwork(modifier = Modifier.fillMaxSize())
    }
}
