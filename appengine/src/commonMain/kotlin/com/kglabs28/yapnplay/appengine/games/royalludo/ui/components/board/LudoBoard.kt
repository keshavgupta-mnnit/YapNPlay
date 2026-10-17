package com.kglabs28.yapnplay.appengine.games.royalludo.ui.components.board

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntOffset
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.LudoColour
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.screens.match.LudoPieceUi
import kotlin.math.roundToInt

/**
 * The tilted neon Ludo board with its glass pieces. Pieces hop square by
 * square and captured ones glide home (see [PieceMotionState]); tapping
 * a selectable piece calls [onPieceClick]. [centre] is drawn standing on
 * the home triangles (the dice).
 */
@Composable
fun LudoBoard(
    pieces: List<LudoPieceUi>,
    rotation: Int,
    onPieceClick: (LudoPieceUi) -> Unit,
    modifier: Modifier = Modifier,
    /** "1st", "2nd"… shown inside finished players' bases, by colour. */
    places: Map<LudoColour, String> = emptyMap(),
    /** A tap that hit no selectable piece. */
    onEmptyTap: () -> Unit = {},
    centre: @Composable (Modifier) -> Unit = {},
) {
    val colours = ludoBoardColours()
    val targets = remember(pieces) { LudoPieceLayout.targets(pieces) }
    val motions = pieces.associate { piece ->
        key(piece.id) { piece.id to rememberPieceMotion(targets.getValue(piece.id).position, piece.motion) }
    }
    val scales = targets.mapValues { it.value.scale }
    val measurer = rememberTextMeasurer()
    val placeStyle = YapNPlayTheme.typography.heroWord
    val placesByColour = places.mapKeys { it.key.ordinal }
    val pulse by rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(AppConstants.LUDO_LEGAL_PULSE_MS), RepeatMode.Reverse),
    )
    val glow by rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(AppConstants.LUDO_NEON_BREATH_MS), RepeatMode.Reverse),
    )
    val latestPieces by rememberUpdatedState(pieces)
    val latestMotions by rememberUpdatedState(motions)
    val latestClick by rememberUpdatedState(onPieceClick)
    val latestEmptyTap by rememberUpdatedState(onEmptyTap)

    BoxWithConstraints(modifier) {
        val width = constraints.maxWidth.toFloat()
        val height = constraints.maxHeight.toFloat()
        val projection = remember(width, height, rotation) { LudoBoardProjection.fit(Size(width, height), rotation) }
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(projection) {
                    detectTapGestures { tap ->
                        LudoPieceLayout.hitTest(tap, latestPieces, { latestMotions.getValue(it).current }, projection)
                            ?.let(latestClick) ?: latestEmptyTap()
                    }
                },
        ) {
            drawLudoBoard(projection, colours, glow)
            drawBasePlaces(projection, placesByColour, colours, measurer, placeStyle)
            drawLudoPieces(projection, pieces, motions, scales, colours, pulse)
        }
        val dice = projection.diceRect()
        with(LocalDensity.current) {
            centre(
                Modifier
                    .offset { IntOffset(dice.left.roundToInt(), dice.top.roundToInt()) }
                    .size(dice.width.toDp()),
            )
        }
    }
}
