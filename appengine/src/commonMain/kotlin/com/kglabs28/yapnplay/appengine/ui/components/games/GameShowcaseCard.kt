package com.kglabs28.yapnplay.appengine.ui.components.games

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.features.gamecatalog.domain.GameCatalogItem
import com.kglabs28.yapnplay.appengine.ui.placeholders.GameArt

/**
 * One game in a picker: art on top, glowing name, "Match • Play • Win"
 * taglines. Carousel pages and grid tiles both use it; the carousel
 * shrinks and dims side pages from outside; small grid tiles pass a
 * smaller [nameStyle].
 */
@Composable
fun GameShowcaseCard(
    game: GameCatalogItem,
    modifier: Modifier = Modifier,
    nameStyle: TextStyle = YapNPlayTheme.typography.heroWord,
    /** The "Match · Play · Win" line under the name; the grid leaves it out. */
    showTagline: Boolean = true,
) {
    val colors = YapNPlayTheme.colors
    val typography = YapNPlayTheme.typography

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.Xxxs),
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            GameArt(artKey = game.artKey)
        }
        BasicText(
            text = game.name,
            style = nameStyle.copy(
                color = colors.TextPrimary,
                textAlign = TextAlign.Center,
                shadow = Shadow(color = colors.SocialZoneAccent, blurRadius = nameStyle.fontSize.value),
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (showTagline) {
            BasicText(
                text = Strings.gameTaglines(game.taglines),
                style = typography.caption.copy(color = colors.RoomsSubtitle, textAlign = TextAlign.Center),
                maxLines = 1,
            )
        }
    }
}
