package com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.board

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.painter.Painter

/** A tile icon, [size] px square, centred on [centre], in one flat colour (no glow). */
fun DrawScope.drawBoardIcon(painter: Painter, centre: Offset, size: Float, tint: Color) {
    translate(centre.x - size / 2, centre.y - size / 2) {
        with(painter) { draw(Size(size, size), colorFilter = ColorFilter.tint(tint)) }
    }
}
