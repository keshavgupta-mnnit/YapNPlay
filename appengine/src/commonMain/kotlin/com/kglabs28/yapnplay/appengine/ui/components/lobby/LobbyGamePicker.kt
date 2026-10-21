package com.kglabs28.yapnplay.appengine.ui.components.lobby

import com.kglabs28.yapnplay.appengine.core.ui.RoomStrings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.features.gamecatalog.domain.GameCatalogItem
import com.kglabs28.yapnplay.appengine.ui.components.common.GlassPillButton
import com.kglabs28.yapnplay.appengine.ui.components.games.GameCarousel
import com.kglabs28.yapnplay.appengine.ui.components.games.GameGrid
import com.kglabs28.yapnplay.appengine.ui.components.games.GamePickerViewMode
import com.kglabs28.yapnplay.appengine.ui.components.gamezone.ViewModeToggle

/**
 * Lobby phase 1 — choosing a game: the same carousel/grid as the Game
 * Zone, sized to the lobby space. The leader gets SELECT GAME under the
 * carousel; in the grid a tile tap selects. Everyone else sees that the
 * leader is choosing.
 */
@Composable
fun LobbyGamePicker(
    games: List<GameCatalogItem>,
    focusedIndex: Int,
    viewMode: GamePickerViewMode,
    canSelect: Boolean,
    onFocusChange: (Int) -> Unit,
    onSelect: (Int) -> Unit,
    onToggleViewMode: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.Xs),
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            when (viewMode) {
                GamePickerViewMode.GRID -> GameGrid(
                    games = games,
                    focusedIndex = focusedIndex,
                    onGameClick = { if (canSelect) onSelect(it) else onFocusChange(it) },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = YapNPlayTheme.dimens.smallButtonHeight + Spacing.Xs),
                )
                GamePickerViewMode.CAROUSEL -> GameCarousel(
                    games = games,
                    focusedIndex = focusedIndex,
                    onFocusChange = onFocusChange,
                    modifier = Modifier.fillMaxSize(),
                )
            }
            ViewModeToggle(
                isGrid = viewMode == GamePickerViewMode.GRID,
                onToggle = onToggleViewMode,
                // Part of the game area, so game-gold like Change Game.
                accent = YapNPlayTheme.colors.StartButtonGlow,
                contentColor = YapNPlayTheme.colors.StartButtonSub,
                modifier = Modifier.align(Alignment.TopEnd),
            )
        }
        if (canSelect && viewMode == GamePickerViewMode.CAROUSEL) {
            // Same tinted glass as Grid View / Change Game, in green.
            GlassPillButton(
                text = RoomStrings.lobbySelectGame,
                onClick = { onSelect(focusedIndex) },
                leadingIcon = Icons.Rounded.CheckCircle,
                trailingIcon = Icons.Rounded.ChevronRight,
                accent = colors.ReadyGreen,
                contentColor = colors.ReadyGreen,
                height = YapNPlayTheme.dimens.ctaHeight,
                iconSize = YapNPlayTheme.dimens.iconMedium,
                textStyle = YapNPlayTheme.typography.button,
                modifier = Modifier.fillMaxWidth(AppConstants.LOBBY_SELECT_WIDTH_FRACTION),
            )
        } else if (!canSelect) {
            BasicText(
                text = RoomStrings.lobbyLeaderChoosing,
                style = YapNPlayTheme.typography.label.copy(color = colors.TextSecondary),
            )
        }
    }
}
