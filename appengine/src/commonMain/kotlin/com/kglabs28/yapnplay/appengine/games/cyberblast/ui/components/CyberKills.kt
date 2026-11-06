package com.kglabs28.yapnplay.appengine.games.cyberblast.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.CyberBlastStrings

/** How many players someone has hit: a small crosshair in their [colour] and the number. */
@Composable
fun CyberKills(kills: String, colour: Color, modifier: Modifier = Modifier, size: Dp = YapNPlayTheme.dimens.blastHeart) {
    Row(
        modifier = modifier.semantics { contentDescription = CyberBlastStrings.blastKillsLabel },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Xxxs),
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val r = this.size.minDimension / 2
            val line = r * LINE
            drawCircle(colour, r * RING, center, style = Stroke(line))
            drawLine(colour, Offset(center.x, 0f), Offset(center.x, center.y - r * GAP), line)
            drawLine(colour, Offset(center.x, center.y + r * GAP), Offset(center.x, this.size.height), line)
            drawLine(colour, Offset(0f, center.y), Offset(center.x - r * GAP, center.y), line)
            drawLine(colour, Offset(center.x + r * GAP, center.y), Offset(this.size.width, center.y), line)
            drawCircle(colour, r * DOT, center)
        }
        BasicText(text = kills, style = YapNPlayTheme.typography.caption.copy(color = YapNPlayTheme.colors.TextPrimary), maxLines = 1)
    }
}

private const val RING = 0.62f
private const val LINE = 0.18f
private const val GAP = 0.35f
private const val DOT = 0.14f
