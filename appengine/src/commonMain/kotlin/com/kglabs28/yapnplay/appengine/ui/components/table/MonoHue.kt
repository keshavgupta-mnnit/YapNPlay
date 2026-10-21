package com.kglabs28.yapnplay.appengine.ui.components.table

import androidx.compose.ui.graphics.Color
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayColors

/**
 * The four card hues, named without reference to either game's model.
 * A card component only ever needs the hue, never the card.
 */
enum class MonoHue {
    RED,
    GREEN,
    BLUE,
    GOLD,
}

/** The dark jewel fill behind the frame and the notch tab. */
val MonoHue.fill: Color
    get() = when (this) {
        MonoHue.RED -> YapNPlayColors.MonoRedFill
        MonoHue.GREEN -> YapNPlayColors.MonoGreenFill
        MonoHue.BLUE -> YapNPlayColors.MonoBlueFill
        MonoHue.GOLD -> YapNPlayColors.MonoGoldFill
    }

/** The bright rim and glow of the same hue. */
val MonoHue.rim: Color
    get() = when (this) {
        MonoHue.RED -> YapNPlayColors.MonoRedRim
        MonoHue.GREEN -> YapNPlayColors.MonoGreenRim
        MonoHue.BLUE -> YapNPlayColors.MonoBlueRim
        MonoHue.GOLD -> YapNPlayColors.MonoGoldRim
    }

/**
 * The ring the table glows in while [hue] is the colour in force. A
 * prism (no single hue) falls back to the idle steel ring.
 */
val MonoHue?.ring: Color
    get() = this?.rim ?: YapNPlayColors.MonoRingIdle
