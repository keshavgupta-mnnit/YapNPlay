package com.kglabs28.yapnplay.appengine.ui.placeholders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Adjust
import androidx.compose.material.icons.rounded.Brush as BrushIcon
import androidx.compose.material.icons.rounded.Extension
import androidx.compose.material.icons.rounded.Quiz
import androidx.compose.material.icons.rounded.Spellcheck
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.material.icons.rounded.Style
import androidx.compose.material.icons.rounded.Terrain
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
    const val CITY_BAAZAR = "city-baazar"
    const val CARD_CLASH = "card-clash"
    const val ISLAND_QUEST = "island-quest"
    const val BLOCK_MERGE = "block-merge"
    const val CARROM_STRIKE = "carrom-strike"
    const val DOODLE_DASH = "doodle-dash"
    const val QUIZ_QUEST = "quiz-quest"
    const val WORD_WARS = "word-wars"
}

/**
 * A game's picker art by its art key. Royal Ludo and City Baazar have
 * their own art; games without art yet get a glowing glyph (see
 * docs/placeholder-assets.md). Swap a branch for real art when it lands.
 */
@Composable
fun GameArt(
    artKey: String,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    when (artKey) {
        GameArtKeys.ROYAL_LUDO -> RoyalLudoArtwork(modifier = modifier.fillMaxSize())
        GameArtKeys.CITY_BAAZAR -> CityBaazarArtwork(modifier = modifier.fillMaxSize())
        GameArtKeys.CARD_CLASH -> GlyphArt(Icons.Rounded.Style, colors.Pink, modifier)
        GameArtKeys.ISLAND_QUEST -> GlyphArt(Icons.Rounded.Terrain, colors.GameZoneAccent, modifier)
        GameArtKeys.BLOCK_MERGE -> GlyphArt(Icons.Rounded.Extension, colors.Cyan, modifier)
        GameArtKeys.CARROM_STRIKE -> GlyphArt(Icons.Rounded.Adjust, colors.Orange, modifier)
        GameArtKeys.DOODLE_DASH -> GlyphArt(Icons.Rounded.BrushIcon, colors.Gold, modifier)
        GameArtKeys.QUIZ_QUEST -> GlyphArt(Icons.Rounded.Quiz, colors.SocialZoneAccent, modifier)
        GameArtKeys.WORD_WARS -> GlyphArt(Icons.Rounded.Spellcheck, colors.LiveGreen, modifier)
        else -> GlyphArt(Icons.Rounded.SportsEsports, colors.Purple, modifier)
    }
}

@Composable
private fun GlyphArt(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.radialGradient(listOf(tint.copy(alpha = 0.35f), colors.Transparent))),
        contentAlignment = Alignment.Center,
    ) {
        AppIcon(imageVector = icon, tint = tint, size = YapNPlayTheme.dimens.gameArtGlyph)
    }
}
