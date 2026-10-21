package com.kglabs28.yapnplay.appengine.games.royalludo.ui.components.players

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.screens.match.LudoPlayerCardUi

/** A finishing place in the player's colour; "Away" / "Bot" in grey. */
@Composable
fun LudoPlayerCardUi.badgeTint(): Color = if (placeLabel != null) colour.tint() else YapNPlayTheme.colors.IconMuted
