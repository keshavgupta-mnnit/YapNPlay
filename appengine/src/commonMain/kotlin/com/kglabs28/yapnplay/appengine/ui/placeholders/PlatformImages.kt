package com.kglabs28.yapnplay.appengine.ui.placeholders

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import com.kglabs28.yapnplay.appengine.appmanager.helpers.BackgroundsAppManagerHelper
import org.koin.compose.koinInject

/**
 * The app's images, common to every platform: each backdrop and game art
 * reads its URL from AppManager (BackgroundsAppManagerHelper) and falls
 * back to the built-in picture. Callers never change when assets do.
 */
@Composable
fun HomeLogoArtwork(modifier: Modifier = Modifier, contentDescription: String? = null) {
    // Square art with transparent margins: crop to the caller's wide box so the wordmark fills it.
    Image(painter = builtInPainter(BuiltInImage.APP_LOGO), contentDescription = contentDescription, modifier = modifier, contentScale = ContentScale.Crop)
}

@Composable
fun GameZoneArtwork(modifier: Modifier = Modifier, contentDescription: String? = null, urls: BackgroundsAppManagerHelper = koinInject()) {
    AppArtwork(BuiltInImage.GAME_ZONE_CARD, urls.gameZoneCard(), ContentScale.Fit, modifier, contentDescription)
}

@Composable
fun FriendZoneArtwork(modifier: Modifier = Modifier, contentDescription: String? = null, urls: BackgroundsAppManagerHelper = koinInject()) {
    AppArtwork(BuiltInImage.FRIEND_ZONE_CARD, urls.friendZoneCard(), ContentScale.Fit, modifier, contentDescription)
}

/** The leaderboard's celebration background (boards, confetti, sparks), stretched to the screen. */
@Composable
fun LeaderboardArtwork(modifier: Modifier = Modifier, contentDescription: String? = null, urls: BackgroundsAppManagerHelper = koinInject()) {
    AppArtwork(BuiltInImage.LEADERBOARD_BACKGROUND, urls.leaderboard(), ContentScale.FillBounds, modifier, contentDescription)
}

@Composable
fun HomeBackgroundArtwork(modifier: Modifier = Modifier, contentDescription: String? = null, urls: BackgroundsAppManagerHelper = koinInject()) {
    AppArtwork(BuiltInImage.HOME_BACKGROUND, urls.home(), ContentScale.Crop, modifier, contentDescription)
}

/** Royal Ludo's game art (carousel, lobby, every game picker, leaderboard). */
@Composable
fun RoyalLudoArtwork(modifier: Modifier = Modifier, contentDescription: String? = null, urls: BackgroundsAppManagerHelper = koinInject()) {
    AppArtwork(BuiltInImage.ROYAL_LUDO_ART, urls.royalLudoArt(), ContentScale.Crop, modifier, contentDescription)
}

/** City Bazaar's game art (carousel, lobby, every game picker, leaderboard). */
@Composable
fun CityBazaarArtwork(modifier: Modifier = Modifier, contentDescription: String? = null, urls: BackgroundsAppManagerHelper = koinInject()) {
    AppArtwork(BuiltInImage.CITY_BAZAAR_ART, urls.cityBazaarArt(), ContentScale.Crop, modifier, contentDescription)
}

@Composable
fun MonoArtwork(modifier: Modifier = Modifier, contentDescription: String? = null, urls: BackgroundsAppManagerHelper = koinInject()) {
    AppArtwork(BuiltInImage.MONO_ART, urls.cityBazaarArt(), ContentScale.Crop, modifier, contentDescription)
}

@Composable
fun MonoChaosArtwork(modifier: Modifier = Modifier, contentDescription: String? = null, urls: BackgroundsAppManagerHelper = koinInject()) {
    AppArtwork(BuiltInImage.MONO_CHAOS_ART, urls.cityBazaarArt(), ContentScale.Crop, modifier, contentDescription)
}

/** The card-table room (a lit oval table in a cosy lounge), behind a card game's table. */
@Composable
fun GameTableArenaArtwork(modifier: Modifier = Modifier, contentDescription: String? = null) {
    Image(painter = builtInPainter(BuiltInImage.GAME_TABLE_ARENA), contentDescription = contentDescription, modifier = modifier, contentScale = ContentScale.Crop)
}

/** The board games' room (a lit rug in a cosy lounge), behind Royal Ludo and City Royale. */
@Composable
fun GameArenaArtwork(modifier: Modifier = Modifier, contentDescription: String? = null) {
    Image(painter = builtInPainter(BuiltInImage.GAME_ARENA), contentDescription = contentDescription, modifier = modifier, contentScale = ContentScale.Crop)
}

/** The MONO card back, upright, stretched to the card it backs. */
@Composable
fun MonoCardBackArtwork(modifier: Modifier = Modifier, contentDescription: String? = null) {
    Image(painter = builtInPainter(BuiltInImage.MONO_CARD_BACK), contentDescription = contentDescription, modifier = modifier, contentScale = ContentScale.FillBounds)
}

/** Preset avatar number [index] (0-based, see AvatarPresets), filling [modifier]. */
@Composable
fun PresetAvatarArtwork(index: Int, modifier: Modifier = Modifier, contentDescription: String? = null) {
    // Portrait on a transparent square: fill the circle.
    Image(painter = avatarPainter(index), contentDescription = contentDescription, modifier = modifier, contentScale = ContentScale.Crop)
}
