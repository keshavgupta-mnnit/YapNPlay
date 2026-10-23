package com.kglabs28.yapnplay.appengine.ui.components.leaderboard.podium

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.withStyle
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayColors
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.TableStrings

/**
 * The box under [place]: an outer glass cube in the place's medal colour
 * (see [drawPodiumGlassCube]; neighbouring cubes merge into one platform)
 * and, set in from its edges, a flat rounded plate of glassy black with a
 * medal outline and the place ("1st") in metal.
 */
@Composable
fun PodiumBox(place: Int, modifier: Modifier = Modifier) {
    val dimens = YapNPlayTheme.dimens
    val medal = podiumMedal(place)
    val height = podiumBoxHeight(place)
    Box(
        modifier = modifier
            .size(dimens.podiumWidth, height)
            .drawBehind { drawPodiumGlassCube(medal, showLeft = place == 2, showRight = place == 3, top = dimens.podiumTopFace.toPx(), corner = dimens.podiumCorner.toPx()) }
            .padding(start = dimens.podiumPlateInset, end = dimens.podiumPlateInset, top = dimens.podiumTopFace + dimens.podiumPlateInset / 2, bottom = dimens.podiumPlateInset),
        contentAlignment = Alignment.Center,
    ) {
        PodiumPlate(medal = medal, winner = place == 1)
        PodiumPlaceNumber(place = place, medal = medal, plateHeight = height - dimens.podiumTopFace - dimens.podiumPlateInset * PLATE_INSETS)
    }
}

/** The glassy black plate with its medal outline (a thicker one for the winner). */
@Composable
private fun PodiumPlate(medal: Color, winner: Boolean) {
    val dimens = YapNPlayTheme.dimens
    Canvas(modifier = Modifier.fillMaxSize()) {
        val line = dimens.boardBoxFrame.toPx() * if (winner) WINNER_RIM else 1f
        val corner = CornerRadius(dimens.podiumCorner.toPx())
        drawRoundRect(Brush.verticalGradient(listOf(YapNPlayColors.SnackGlassTop, YapNPlayColors.SnackGlassBottom)), cornerRadius = corner)
        // A soft sheen over the upper half, so it reads as glass.
        drawRoundRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = PLATE_SHEEN), Color.Transparent), 0f, size.height / 2), cornerRadius = corner)
        val rim = Brush.verticalGradient(listOf(Color.White.copy(alpha = RIM_SHINE), medal, medal.copy(alpha = METAL_DEEP)))
        drawRoundRect(rim, topLeft = Offset(line / 2, line / 2), size = Size(size.width - line, size.height - line), cornerRadius = corner, style = Stroke(line))
    }
}

/** "1st" in metal, as big as the plate allows. */
@Composable
private fun PodiumPlaceNumber(place: Int, medal: Color, plateHeight: androidx.compose.ui.unit.Dp) {
    val typography = YapNPlayTheme.typography
    val size = with(LocalDensity.current) { minOf(typography.headline.fontSize.toPx() * PLACE_SCALE, plateHeight.toPx() * PLACE_OF_PLATE).toSp() }
    val number = place.toString()
    BasicText(
        text = buildAnnotatedString {
            append(number)
            withStyle(SpanStyle(fontSize = size * SUFFIX_SCALE, baselineShift = BaselineShift.Superscript)) { append(TableStrings.placeLabel(place).removePrefix(number)) }
        },
        style = typography.headline.copy(
            brush = Brush.verticalGradient(listOf(Color.White, medal, medal.copy(alpha = METAL_DEEP))),
            fontWeight = FontWeight.Black,
            fontSize = size,
            shadow = Shadow(color = medal.copy(alpha = NUMBER_GLOW_ALPHA), blurRadius = PLACE_GLOW),
        ),
    )
}

/** The plate's insets taken off the box's height (half at the top, one at the bottom). */
private const val PLATE_INSETS = 1.5f
private const val PLATE_SHEEN = 0.08f
private const val RIM_SHINE = 0.6f
private const val METAL_DEEP = 0.85f
private const val WINNER_RIM = 1.3f
/** The place number: at most this × the headline size, and this share of the plate's height. */
private const val PLACE_SCALE = 1.5f
private const val PLACE_OF_PLATE = 0.55f
private const val SUFFIX_SCALE = 0.45f
private const val PLACE_GLOW = 16f
private const val NUMBER_GLOW_ALPHA = 0.6f
