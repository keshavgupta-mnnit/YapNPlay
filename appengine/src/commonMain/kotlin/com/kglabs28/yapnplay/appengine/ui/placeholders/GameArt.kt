package com.kglabs28.yapnplay.appengine.ui.placeholders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon

/**
 * Art keys games declare in their showcase. One place maps a key to art.
 */
object GameArtKeys {
    const val ROYAL_LUDO = "royal-ludo"
    const val CITY_BAZAAR = "city-bazaar"
}

/**
 * A game's picker art by its art key. Royal Ludo and City Bazaar have
 * their own art; a new game without art yet gets a glowing glyph (see
 * docs/placeholder-assets.md). Add a branch when its art lands.
 */
@Composable
fun GameArt(
    artKey: String,
    modifier: Modifier = Modifier,
) {
    when (artKey) {
        GameArtKeys.ROYAL_LUDO -> RoyalLudoArtwork(modifier = modifier.fillMaxSize())
        GameArtKeys.CITY_BAZAAR -> CityBazaarArtwork(modifier = modifier.fillMaxSize())
        else -> GlyphArt(modifier)
    }
}

@Composable
private fun GlyphArt(modifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.radialGradient(listOf(colors.Purple.copy(alpha = 0.35f), colors.Transparent))),
        contentAlignment = Alignment.Center,
    ) {
        AppIcon(imageVector = Icons.Rounded.SportsEsports, tint = colors.Purple, size = YapNPlayTheme.dimens.gameArtGlyph)
    }
}
