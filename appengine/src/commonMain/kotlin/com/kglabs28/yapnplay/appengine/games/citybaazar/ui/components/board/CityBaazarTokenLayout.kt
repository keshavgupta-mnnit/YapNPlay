package com.kglabs28.yapnplay.appengine.games.citybaazar.ui.components.board

import androidx.compose.ui.geometry.Offset
import com.kglabs28.yapnplay.appengine.games.citybaazar.ui.screens.match.CbTokenUi
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/** Where each token stands: the tile's middle, or a small ring when tokens share a tile. */
object CityBaazarTokenLayout {
    fun target(token: CbTokenUi, all: List<CbTokenUi>): Offset {
        val centre = CityBaazarBoardProjection.tileCentre(token.tile)
        val sharing = all.filter { it.tile == token.tile }
        if (sharing.size < 2) return centre
        val i = sharing.indexOfFirst { it.seat == token.seat }
        val angle = 2 * PI * i / sharing.size
        return centre + Offset((cos(angle) * SPREAD).toFloat(), (sin(angle) * SPREAD).toFloat())
    }

    private const val SPREAD = 0.22
}
