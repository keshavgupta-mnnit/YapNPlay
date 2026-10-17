package com.kglabs28.yapnplay.appengine.games.elements.dice.ui

import androidx.compose.ui.graphics.Color

/**
 * Colours of the glass die: [tints] drift slowly across the surface and
 * the glow; [deep] is the darkest glass tone; [pip] fills the pips.
 */
data class DicePalette(val tints: List<Color>, val deep: Color, val pip: Color)
