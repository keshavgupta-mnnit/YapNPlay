package com.kglabs28.yapnplay.appengine.games.citybaazar.ui.components.board

import androidx.compose.ui.geometry.Offset

/** A token's animated spot: grid position and hop height (0–1). */
data class TokenSpot(val seat: Int, val grid: Offset, val lift: Float)
