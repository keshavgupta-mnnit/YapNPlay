package com.kglabs28.yapnplay.appengine.core.designsystem

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/**
 * YapNPlay shapes. Panels: 16dp radius. Buttons: 12dp cut corners.
 * Chips and avatars: pill and circle.
 */
data class YapNPlayShapes(
    val panel: Shape = RoundedCornerShape(16.dp),
    val button: Shape = CutCornerShape(12.dp),
    val pill: Shape = CircleShape,
    val avatar: Shape = CircleShape,
)
