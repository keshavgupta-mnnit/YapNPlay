package com.kglabs28.yapnplay.appengine.ui.placeholders

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.kglabs28.yapnplay.appengine.R

@Composable
actual fun HomeLogoArtwork(modifier: Modifier, contentDescription: String?) {
    Image(
        painter = painterResource(id = R.drawable.ic_app_logo),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = ContentScale.Fit,
    )
}

@Composable
actual fun GameZoneArtwork(modifier: Modifier, contentDescription: String?) {
    Image(
        painter = painterResource(id = R.drawable.bg_home_game_zone_card),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = ContentScale.Fit,
    )
}

@Composable
actual fun FriendZoneArtwork(modifier: Modifier, contentDescription: String?) {
    Image(
        painter = painterResource(id = R.drawable.bg_home_friend_zone_card),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = ContentScale.Fit,
    )
}

@Composable
actual fun HomeBackgroundArtwork(modifier: Modifier, contentDescription: String?) {
    Image(
        painter = painterResource(id = R.drawable.bg_home_main),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = ContentScale.Crop,
    )
}
