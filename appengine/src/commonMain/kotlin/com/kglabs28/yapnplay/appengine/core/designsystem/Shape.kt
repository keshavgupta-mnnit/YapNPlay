package com.kglabs28.yapnplay.appengine.core.designsystem

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Shape
import kotlin.math.cos
import kotlin.math.sin

/**
 * YapNPlay shapes. Corner radii read the scaled dimens, so they grow
 * with the window like everything else. Hexagon, pill and circle are
 * proportional and need no scaling.
 */
class YapNPlayShapes {
    private val dimens = YapNPlayDimens()
    val panel: Shape get() = RoundedCornerShape(dimens.panelRadius)
    val button: Shape get() = CutCornerShape(dimens.buttonCut)
    val pill: Shape = CircleShape
    val avatar: Shape = CircleShape
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
    }
}
