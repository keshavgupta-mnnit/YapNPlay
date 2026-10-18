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
        // Square art with transparent margins: crop to the caller's
        // wide box so the wordmark fills it.
        contentScale = ContentScale.Crop,
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

@Composable
actual fun RoyalLudoArtwork(modifier: Modifier, contentDescription: String?) {
    Image(
        painter = painterResource(id = R.drawable.ic_game_royal_ludo),
        contentDescription = contentDescription,
        modifier = modifier,
        // Wide art: fill the card, trimming the edges.
        contentScale = ContentScale.Crop,
    )
}

@Composable
actual fun CityBazaarArtwork(modifier: Modifier, contentDescription: String?) {
    Image(
        painter = painterResource(id = R.drawable.ic_game_city_bazaar),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = ContentScale.Crop,
    )
}

/** The avatar set, in AvatarPresets order. */
private val avatarDrawables = listOf(
    R.drawable.ic_avatar_01, R.drawable.ic_avatar_02, R.drawable.ic_avatar_03,
    R.drawable.ic_avatar_04, R.drawable.ic_avatar_05, R.drawable.ic_avatar_06,
    R.drawable.ic_avatar_07, R.drawable.ic_avatar_08, R.drawable.ic_avatar_09,
    R.drawable.ic_avatar_10, R.drawable.ic_avatar_11, R.drawable.ic_avatar_12,
    R.drawable.ic_avatar_13, R.drawable.ic_avatar_14, R.drawable.ic_avatar_15,
    R.drawable.ic_avatar_16, R.drawable.ic_avatar_17, R.drawable.ic_avatar_18,
)

@Composable
actual fun PresetAvatarArtwork(index: Int, modifier: Modifier, contentDescription: String?) {
    Image(
        painter = painterResource(id = avatarDrawables[index % avatarDrawables.size]),
        contentDescription = contentDescription,
        modifier = modifier,
        // Portrait on a transparent square: fill the circle.
        contentScale = ContentScale.Crop,
    )
}
