package com.kglabs28.yapnplay.appengine.ui.components.games

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.glassPanel
import com.kglabs28.yapnplay.appengine.features.gamecatalog.domain.GameCatalogItem

/**
 * Reusable game grid (Grid View): every game as a small glass tile; the
 * [focusedIndex] tile glows green. A tap reports through [onGameClick].
 */
@Composable
fun GameGrid(
    games: List<GameCatalogItem>,
    focusedIndex: Int,
    onGameClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors

    LazyVerticalGrid(
        columns = GridCells.Fixed(AppConstants.GAME_GRID_COLUMNS),
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Sm),
        verticalArrangement = Arrangement.spacedBy(Spacing.Sm),
    ) {
        itemsIndexed(games, key = { _, game -> game.id }) { index, game ->
            GameShowcaseCard(
                game = game,
                nameStyle = YapNPlayTheme.typography.sectionTitle,
                modifier = Modifier
                    .aspectRatio(1f)
                    .glassPanel(
                        glowColor = if (index == focusedIndex) colors.GameZoneAccent else colors.IconMuted,
                        surface = colors.RoomsPanel.copy(alpha = 0.85f),
                    )
                    .clickable { onGameClick(index) }
                    .padding(Spacing.Xxs),
            )
        }
    }
}
