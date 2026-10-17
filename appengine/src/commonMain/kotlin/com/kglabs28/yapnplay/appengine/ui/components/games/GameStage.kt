package com.kglabs28.yapnplay.appengine.ui.components.games

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.features.gamecatalog.domain.GameCatalogItem
import com.kglabs28.yapnplay.appengine.ui.placeholders.GameArt
import kotlin.math.PI
import kotlin.math.floor
import kotlin.math.sin

/**
 * The chosen game on a hologram stage (lobby, after the leader picks):
 * glowing title on top, the game art floating gently in a light beam
 * that rises from a neon platform, with specks drifting up the beam.
 * Everything is drawn in code, so any game's art gets the same stage.
 */
@Composable
fun GameStage(
    game: GameCatalogItem,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val typography = YapNPlayTheme.typography
    val transition = rememberInfiniteTransition()
    val drift by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(AppConstants.STAGE_PARTICLE_CYCLE_MS, easing = LinearEasing)),
    )
    val bob by transition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(AppConstants.STAGE_BOB_CYCLE_MS), RepeatMode.Reverse),
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.Xxxs),
    ) {
        // Selected game's name: white fill over a coloured outline, glowing.
        val outlinePx = with(LocalDensity.current) { YapNPlayTheme.dimens.titleOutline.toPx() }
        Box(contentAlignment = Alignment.Center) {
            BasicText(
                text = game.name,
                style = typography.heroWord.copy(
                    color = colors.StageAccent,
                    textAlign = TextAlign.Center,
                    drawStyle = Stroke(width = outlinePx),
                    shadow = Shadow(color = colors.StageAccent, blurRadius = typography.heroWord.fontSize.value),
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            BasicText(
                text = game.name,
                style = typography.heroWord.copy(
                    color = colors.TextPrimary,
                    textAlign = TextAlign.Center,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        BasicText(
            text = Strings.gameTaglines(game.taglines),
            style = typography.caption.copy(color = colors.RoomsSubtitle, textAlign = TextAlign.Center),
            maxLines = 1,
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            val palette = StagePalette(
                light = colors.StageLight,
                accent = colors.StageAccent,
                top = colors.StagePlatformTop,
                sideTop = colors.StagePlatformSideTop,
                sideBottom = colors.StagePlatformSideBottom,
            )
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawBeam(palette)
                drawSpecks(palette, drift)
                drawPlatform(palette)
            }
            val bobPx = YapNPlayTheme.dimens.stageBob
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth(ART_WIDTH_FRACTION)
                    .fillMaxHeight(ART_HEIGHT_FRACTION)
                    .graphicsLayer {
                        translationY = bob * bobPx.toPx()
                        scaleX = ART_SCALE
                        scaleY = ART_SCALE
                    },
                contentAlignment = Alignment.Center,
            ) {
                GameArt(artKey = game.artKey)
            }
        }
    }
}

/** Platform centre, width and the height of its top ellipse. */
private fun DrawScope.platform(): Triple<Offset, Float, Float> {
    val width = size.width * PLATFORM_WIDTH_FRACTION
    val height = width * PLATFORM_ASPECT
    val center = Offset(size.width / 2, size.height - height * 1.1f)
    return Triple(center, width, height)
}

private fun DrawScope.drawBeam(p: StagePalette) {
    val (center, width, height) = platform()
    val bottomHalf = width * 0.36f
    val topHalf = width * 0.56f
    val top = size.height * 0.06f
    val beam = Path().apply {
        moveTo(center.x - bottomHalf, center.y)
        lineTo(center.x - topHalf, top)
        lineTo(center.x + topHalf, top)
        lineTo(center.x + bottomHalf, center.y)
        close()
    }
    drawPath(
        path = beam,
        brush = Brush.verticalGradient(
            0f to p.light.copy(alpha = 0f),
            0.55f to p.light.copy(alpha = 0.10f),
            1f to p.light.copy(alpha = 0.32f),
            startY = top,
            endY = center.y,
        ),
    )
    // Bright core where the light leaves the platform.
    drawOval(
        brush = Brush.radialGradient(
            listOf(p.light.copy(alpha = 0.55f), p.light.copy(alpha = 0f)),
            center = center,
            radius = bottomHalf,
        ),
        topLeft = Offset(center.x - bottomHalf, center.y - height / 2),
        size = Size(bottomHalf * 2, height),
    )
}

private fun DrawScope.drawSpecks(p: StagePalette, drift: Float) {
    val (center, width, _) = platform()
    val top = size.height * 0.06f
    val span = center.y - top
    val radius = width * 0.006f + 1f
    repeat(SPECK_COUNT) { i ->
        // Fixed pseudo-random lane and speed per speck, so it never jumps.
        val lane = (sin(i * 12.9898f) * 43758.547f).let { it - floor(it) }
        val speed = 0.6f + 0.4f * ((sin(i * 78.233f) * 9631.3f).let { it - floor(it) })
        val t = ((drift * speed) + i.toFloat() / SPECK_COUNT) % 1f
        val y = center.y - span * t
        val halfWidth = width * (0.36f + 0.2f * t)
        val x = center.x + (lane * 2 - 1) * halfWidth * 0.9f
        val alpha = sin(t * PI.toFloat()) * 0.9f
        drawCircle(p.light.copy(alpha = alpha), radius = radius, center = Offset(x, y))
    }
}

private fun DrawScope.drawPlatform(p: StagePalette) {
    val (center, width, height) = platform()
    val depth = height * 0.45f
    val left = center.x - width / 2
    // Side of the disc.
    drawRect(
        brush = Brush.verticalGradient(listOf(p.sideTop, p.sideBottom), startY = center.y, endY = center.y + depth),
        topLeft = Offset(left, center.y),
        size = Size(width, depth),
    )
    drawOval(p.sideBottom, topLeft = Offset(left, center.y + depth - height / 2), size = Size(width, height))
    // Top face.
    drawOval(p.top, topLeft = Offset(left, center.y - height / 2), size = Size(width, height))
    // Neon rims: soft glow strokes, then the sharp line.
    val outer = Size(width * 0.92f, height * 0.92f)
    val outerTopLeft = Offset(center.x - outer.width / 2, center.y - outer.height / 2)
    listOf(10f to 0.10f, 6f to 0.2f, 2.5f to 0.9f).forEach { (stroke, alpha) ->
        drawOval(p.light.copy(alpha = alpha), outerTopLeft, outer, style = Stroke(width = stroke))
    }
    val inner = Size(width * 0.62f, height * 0.62f)
    val innerTopLeft = Offset(center.x - inner.width / 2, center.y - inner.height / 2)
    listOf(8f to 0.12f, 2f to 0.8f).forEach { (stroke, alpha) ->
        drawOval(p.accent.copy(alpha = alpha), innerTopLeft, inner, style = Stroke(width = stroke))
    }
    // Rim light along the front edge of the side.
    drawOval(
        p.accent.copy(alpha = 0.6f),
        topLeft = Offset(left, center.y + depth - height / 2),
        size = Size(width, height),
        style = Stroke(width = 2f),
    )
}

/** Theme colours handed to the draw code (DrawScope can't read the theme). */
private class StagePalette(
    val light: Color,
    val accent: Color,
    val top: Color,
    val sideTop: Color,
    val sideBottom: Color,
)

private const val PLATFORM_WIDTH_FRACTION = 0.62f
private const val PLATFORM_ASPECT = 0.2f
private const val ART_WIDTH_FRACTION = 0.72f
private const val ART_HEIGHT_FRACTION = 0.68f
private const val SPECK_COUNT = 26

/** Game art drawn 25% larger than its box on the stage. */
private const val ART_SCALE = 1.25f
