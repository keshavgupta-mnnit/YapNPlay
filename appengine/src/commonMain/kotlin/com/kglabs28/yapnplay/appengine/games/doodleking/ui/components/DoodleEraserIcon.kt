package com.kglabs28.yapnplay.appengine.games.doodleking.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/** An eraser: a tilted block with its rubber tip, and the line it leaves. Tinted like any icon. */
val DoodleEraserIcon: ImageVector = ImageVector.Builder(
    name = "DoodleEraser",
    defaultWidth = 24.dp,
    defaultHeight = 24.dp,
    viewportWidth = 24f,
    viewportHeight = 24f,
).apply {
    // The block.
    path(fill = SolidColor(Color.Black)) {
        moveTo(14.6f, 3.4f)
        lineTo(20.6f, 9.4f)
        lineTo(11.0f, 19.0f)
        lineTo(5.0f, 13.0f)
        close()
    }
    // Its rubber tip, outlined.
    path(stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f, strokeLineJoin = StrokeJoin.Round) {
        moveTo(5.0f, 13.0f)
        lineTo(3.4f, 14.6f)
        lineTo(7.8f, 19.0f)
        lineTo(11.0f, 19.0f)
    }
    // The line on the paper.
    path(stroke = SolidColor(Color.Black), strokeLineWidth = 1.8f) {
        moveTo(11.0f, 21.0f)
        lineTo(21.0f, 21.0f)
    }
}.build()
