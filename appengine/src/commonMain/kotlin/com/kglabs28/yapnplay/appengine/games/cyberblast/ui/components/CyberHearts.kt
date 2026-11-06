package com.kglabs28.yapnplay.appengine.games.cyberblast.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.Dp
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/** A player's hearts under their name: [lives] of [max] filled in their [colour], the rest dark. */
@Composable
fun CyberHearts(lives: Int, max: Int, colour: Color, modifier: Modifier = Modifier, size: Dp = YapNPlayTheme.dimens.blastHeart) {
    val empty = YapNPlayTheme.colors.BlastHeartEmpty
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(Spacing.Xxxs)) {
        repeat(max) { i ->
            Canvas(modifier = Modifier.size(size)) {
                val w = this.size.width
                val h = this.size.height
                val heart = Path().apply {
                    moveTo(w / 2, h * BOTTOM)
                    cubicTo(w * -0.1f, h * 0.5f, w * 0.1f, h * -0.05f, w / 2, h * NOTCH)
                    cubicTo(w * 0.9f, h * -0.05f, w * 1.1f, h * 0.5f, w / 2, h * BOTTOM)
                    close()
                }
                drawPath(heart, if (i < lives) colour else empty)
                if (i < lives) drawCircle(Color.White.copy(alpha = SHINE_ALPHA), w * SHINE, Offset(w * 0.3f, h * 0.32f))
            }
        }
    }
}

private const val BOTTOM = 0.92f
private const val NOTCH = 0.28f
private const val SHINE = 0.1f
private const val SHINE_ALPHA = 0.5f
