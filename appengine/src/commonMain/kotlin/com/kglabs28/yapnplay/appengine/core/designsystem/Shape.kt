package com.kglabs28.yapnplay.appengine.core.designsystem

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

/**
 * YapNPlay shapes. Panels: 16dp radius. Buttons: 12dp cut corners.
 * Hexagon for level badge. Chips and avatars: pill and circle.
 */
data class YapNPlayShapes(
    val panel: Shape = RoundedCornerShape(16.dp),
    val button: Shape = CutCornerShape(12.dp),
    val pill: Shape = CircleShape,
    val avatar: Shape = CircleShape,
    val hexagon: Shape = GenericShape { size, _ ->
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val radius = size.width / 2f
        val pi = 3.141592653589793f
        for (i in 0 until 6) {
            val angle = (i * 60 - 30) * (pi / 180f)
            val x = centerX + radius * cos(angle)
            val y = centerY + radius * sin(angle)
            if (i == 0) moveTo(x, y) else lineTo(x, y)
        }
        close()
    },
)
