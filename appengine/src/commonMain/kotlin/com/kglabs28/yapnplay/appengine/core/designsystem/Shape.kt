package com.kglabs28.yapnplay.appengine.core.designsystem

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Shape

/**
 * YapNPlay shapes. Corner radii read the scaled dimens, so they grow
 * with the window like everything else. Shield, pill and circle are
 * proportional and need no scaling.
 */
class YapNPlayShapes {
    private val dimens = YapNPlayDimens()
    val panel: Shape get() = RoundedCornerShape(dimens.panelRadius)
    val button: Shape get() = CutCornerShape(dimens.buttonCut)
    val pill: Shape = CircleShape
    val avatar: Shape = CircleShape
    /** A crest: flat top with rounded shoulders, sides narrowing to a point at the bottom (the level badge). */
    val shield: Shape = GenericShape { size, _ ->
        val w = size.width
        val h = size.height
        moveTo(w * SHIELD_SHOULDER, 0f)
        lineTo(w * (1 - SHIELD_SHOULDER), 0f)
        quadraticTo(w, 0f, w, h * SHIELD_SHOULDER)
        lineTo(w, h * SHIELD_SIDE)
        quadraticTo(w, h * SHIELD_CURVE, w / 2, h)
        quadraticTo(0f, h * SHIELD_CURVE, 0f, h * SHIELD_SIDE)
        lineTo(0f, h * SHIELD_SHOULDER)
        quadraticTo(0f, 0f, w * SHIELD_SHOULDER, 0f)
        close()
    }
}

/** The shield's shoulder rounding, where its straight sides end, and how its bottom curves in (× its size). */
private const val SHIELD_SHOULDER = 0.18f
private const val SHIELD_SIDE = 0.5f
private const val SHIELD_CURVE = 0.85f
