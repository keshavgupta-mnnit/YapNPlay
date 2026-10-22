package com.kglabs28.yapnplay.appengine.ui.components.leaderboard

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.designsystem.AppBrushes
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayColors
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.TableStrings
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon

/**
 * The fixed sizes the podium is designed at. [LeaderboardPodium] scales
 * the whole thing down evenly when the screen is shorter, so these
 * proportions hold on every phone.
 */
object PodiumSizes {
    /** Every box is this wide; a player's card is a little narrower, so neighbours never touch. */
    val width: Dp = 124.dp
    val cardWidth: Dp = 110.dp
    /** The box heights: 1st, 2nd, 3rd. */
    fun boxHeight(place: Int): Dp = when (place) {
        1 -> 132.dp
        2 -> 100.dp
        else -> 83.dp
    }
    /** The outer glass cube's top face (the card stands on it, hiding its far edge). */
    val topFace: Dp = 18.dp
    /** How far the card comes down over the box's top face. */
    val cardOverTop: Dp = 12.dp
    val cardHeight: Dp = 112.dp
    val avatar: Dp = 68.dp
    /** How much of the avatar rises above the card. */
    val avatarAbove: Dp = 40.dp
    val crown: Dp = 32.dp
    /** The crown sits wholly above the avatar, its foot on the avatar's top edge. */
    val crownAbove: Dp = 38.dp
    /** The tallest stack (1st): crown, avatar, card (over the top face), box. */
    val tallest: Dp get() = crownAbove + avatarAbove + cardHeight - cardOverTop + boxHeight(1)
}

/**
 * One place on the podium: its player's card (none when nobody took that
 * place) standing on its box, bottom-aligned, so the three players stand
 * at the heights their boxes set.
 */
@Composable
fun PodiumPosition(place: Int, entry: LeaderboardEntryUi?, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.width(PodiumSizes.width),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
    ) {
        if (entry != null) {
            // Stands on the cube: its foot covers the top face's far edge, so the cube reads as solid.
            PodiumPlayerCard(
                entry = entry,
                isFirst = place == 1,
                modifier = Modifier.offset(y = PodiumSizes.cardOverTop).zIndex(1f),
            )
        }
        PodiumBox(place = place)
    }
}

/**
 * Everything a podium player shows, the same for every place: the crown
 * over 1st only, the avatar in a ring of its medal overlapping a frosted
 * card, and in the card voice signal · name · voice signal (two lines,
 * then "…"; a tap slides the full name along), the coins won and the tag.
 */
@Composable
fun PodiumPlayerCard(entry: LeaderboardEntryUi, isFirst: Boolean, modifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    val medal = medalOf(entry.place)
    val cardShape = RoundedCornerShape(dimens.cardRadius)
    var sliding by remember { mutableStateOf(false) }
    Box(modifier = modifier.width(PodiumSizes.cardWidth), contentAlignment = Alignment.TopCenter) {
        Column(
            modifier = Modifier
                .padding(top = PodiumSizes.avatarAbove)
                .fillMaxWidth()
                .height(PodiumSizes.cardHeight)
                .background(AppBrushes.podiumCard, cardShape)
                .padding(top = PodiumSizes.avatar - PodiumSizes.avatarAbove, start = Spacing.Xxs, end = Spacing.Xxs, bottom = Spacing.Xs),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
        ) {
            // Signal · name · signal (the signals are decoration).
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.Xxxs)) {
                AppIcon(imageVector = Icons.Rounded.GraphicEq, tint = colors.LiveGreen, size = SIGNAL)
                BasicText(
                    text = entry.name.uppercase(),
                    style = typography.label.copy(color = if (entry.isMe) colors.Cyan else Color.White, fontWeight = FontWeight.Black, textAlign = TextAlign.Center),
                    maxLines = if (sliding) 1 else NAME_LINES,
                    overflow = if (sliding) TextOverflow.Clip else TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .clickable(interactionSource = null, indication = null) { sliding = !sliding }
                        .then(if (sliding) Modifier.basicMarquee() else Modifier),
                )
                AppIcon(imageVector = Icons.Rounded.GraphicEq, tint = colors.LiveGreen, size = SIGNAL)
            }
            BasicText(
                text = entry.coins.ifEmpty { TableStrings.leaderboardNoCoins },
                // A lost stake in red.
                style = typography.title.copy(color = if (entry.coins.startsWith("-")) colors.PlayerRed else colors.LiveGreen, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center),
                maxLines = 1,
            )
            LeaderboardTitleChip(title = entry.title, place = entry.place, textStyle = typography.label)
        }
        Box(modifier = Modifier.zIndex(1f), contentAlignment = Alignment.TopCenter) {
            LeaderboardAvatar(avatarId = entry.avatarId, size = PodiumSizes.avatar, micOn = false, medal = medal)
            if (isFirst) Crown()
        }
    }
}

/** The crown over the winner, bobbing gently on the avatar's head. */
@Composable
private fun Crown() {
    val bob by rememberInfiniteTransition().animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(AppConstants.STAGE_BOB_CYCLE_MS), RepeatMode.Reverse),
    )
    val fontSize = with(LocalDensity.current) { PodiumSizes.crown.toSp() }
    BasicText(
        text = CROWN,
        style = YapNPlayTheme.typography.headline.copy(fontSize = fontSize),
        modifier = Modifier.offset(y = -PodiumSizes.crownAbove + YapNPlayTheme.dimens.boardRing * bob).zIndex(2f),
    )
}

/**
 * The podium box for [place], as designed: an outer cube of glass tinted in
 * the place's medal colour (1st 132 dp, 2nd 100 dp, 3rd 75 dp tall, all
 * 124 dp wide) — a lit top face running back, the front below, rounded
 * corners — whose side touching a neighbour has no edge (2nd's right,
 * 3rd's left, both of 1st's), so the three glass cubes merge into one
 * platform. Inside it, 8 dp in, a flat rounded plate of glassy black with
 * a medal outline, and the place in metal.
 */
@Composable
fun PodiumBox(place: Int, modifier: Modifier = Modifier) {
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    val medal = medalOf(place)
    val height = PodiumSizes.boxHeight(place)
    val showLeft = place == 2
    val showRight = place == 3
    Box(
        modifier = modifier
            .size(PodiumSizes.width, height)
            .drawBehind { drawGlassCube(medal, showLeft, showRight, PodiumSizes.topFace.toPx(), CORNER.toPx()) }
            .padding(start = STEP_INSET, end = STEP_INSET, top = PodiumSizes.topFace + STEP_INSET / 2, bottom = STEP_INSET),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val line = dimens.boardBoxFrame.toPx() * if (place == 1) WINNER_RIM else 1f
            val corner = CornerRadius(PLATE_CORNER.toPx())
            drawRoundRect(Brush.verticalGradient(listOf(YapNPlayColors.SnackGlassTop, YapNPlayColors.SnackGlassBottom)), cornerRadius = corner)
            // A soft sheen over the plate's upper half, so it reads as glass.
            drawRoundRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = PLATE_SHEEN), Color.Transparent), 0f, size.height / 2), cornerRadius = corner)
            val rim = Brush.verticalGradient(listOf(Color.White.copy(alpha = RIM_SHINE), medal, medal.copy(alpha = METAL_DEEP)))
            drawRoundRect(rim, topLeft = Offset(line / 2, line / 2), size = Size(size.width - line, size.height - line), cornerRadius = corner, style = Stroke(line))
        }
        // The place, centred on the plate.
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            val front = height - PodiumSizes.topFace - STEP_INSET * 1.5f
            val placeSize = with(LocalDensity.current) { minOf(typography.headline.fontSize.toPx() * PLACE_SCALE, front.toPx() * PLACE_OF_BOX).toSp() }
            val label = placeLabel(place)
            val number = place.toString()
            BasicText(
                text = buildAnnotatedString {
                    append(number)
                    withStyle(SpanStyle(fontSize = placeSize * SUFFIX_SCALE, baselineShift = BaselineShift.Superscript)) { append(label.removePrefix(number)) }
                },
                style = typography.headline.copy(
                    brush = Brush.verticalGradient(listOf(Color.White, medal, medal.copy(alpha = METAL_DEEP))),
                    fontWeight = FontWeight.Black,
                    fontSize = placeSize,
                    shadow = Shadow(color = medal.copy(alpha = NUMBER_GLOW_ALPHA), blurRadius = PLACE_GLOW),
                ),
            )
        }
    }
}

/**
 * The outer glass cube in [tint]: a lit top face and the front, both
 * see-through. Where a neighbour stands the top face runs straight up
 * (no slant), so the faces of neighbouring cubes meet without a gap and
 * the three merge into one platform; only the open sides slant back, get
 * an edge, and round their bottom corner.
 */
private fun DrawScope.drawGlassCube(tint: Color, showLeft: Boolean, showRight: Boolean, top: Float, corner: Float) {
    val w = size.width
    val h = size.height
    val inset = w * OUTER_TOP_INSET
    val leftInset = if (showLeft) inset else 0f
    val rightInset = if (showRight) inset else 0f
    val round = PathEffect.cornerPathEffect(corner)
    val face = Path().apply { moveTo(leftInset, 0f); lineTo(w - rightInset, 0f); lineTo(w, top); lineTo(0f, top); close() }
    drawPath(face, Brush.verticalGradient(listOf(tint.copy(alpha = GLASS_TOP_FAR), tint.copy(alpha = GLASS_TOP_NEAR)), 0f, top))
    val bl = if (showLeft) corner else 0f
    val br = if (showRight) corner else 0f
    val front = Path().apply {
        addRoundRect(RoundRect(0f, top, w, h, bottomLeftCornerRadius = CornerRadius(bl), bottomRightCornerRadius = CornerRadius(br)))
    }
    drawPath(front, Brush.verticalGradient(listOf(tint.copy(alpha = GLASS_FRONT_TOP), tint.copy(alpha = GLASS_FRONT_BOTTOM)), top, h))
    // A sheen across the glass's upper half.
    drawPath(front, Brush.verticalGradient(listOf(Color.White.copy(alpha = GLASS_SHEEN), Color.Transparent), top, top + (h - top) / 2))
    val rim = tint.copy(alpha = GLASS_RIM)
    val line = GLASS_LINE * density
    // The near edge of the top face, the bottom, and only the sides that don't meet a neighbour.
    drawLine(Color.White.copy(alpha = GLASS_RIDGE), Offset(0f, top), Offset(w, top), line)
    // The bottom edge, turning up the open sides round their rounded corners.
    val edge = Path().apply {
        if (showLeft) {
            moveTo(inset, 0f); lineTo(0f, top); lineTo(0f, h - line / 2)
        } else {
            moveTo(0f, h - line / 2)
        }
        lineTo(w, h - line / 2)
        if (showRight) {
            lineTo(w, top); lineTo(w - inset, 0f)
        }
    }
    drawPath(edge, rim, style = Stroke(line, join = StrokeJoin.Round, pathEffect = round))
}

/** Gold, silver, bronze. */
@Composable
private fun medalOf(place: Int): Color = when (place) {
    1 -> YapNPlayTheme.colors.MedalGold
    2 -> YapNPlayTheme.colors.MedalSilver
    else -> YapNPlayTheme.colors.MedalBronze
}

private fun placeLabel(place: Int): String = when (place) {
    1 -> "1st"
    2 -> "2nd"
    else -> "3rd"
}

private const val CROWN = "👑"
private const val NAME_LINES = 2
private val SIGNAL = 15.dp
private val STEP_INSET = 8.dp
/** The corner rounding of the glass, and of the plate inside. */
private val CORNER = 8.dp
private val PLATE_CORNER = 8.dp
private const val PLATE_SHEEN = 0.08f
private const val RIM_SHINE = 0.6f
/** The outer glass cube: how much narrower its top face's far edge is, its tint at each part, its lines. */
private const val OUTER_TOP_INSET = 0.07f
private const val GLASS_TOP_FAR = 0.3f
private const val GLASS_TOP_NEAR = 0.6f
private const val GLASS_FRONT_TOP = 0.38f
private const val GLASS_FRONT_BOTTOM = 0.16f
private const val GLASS_SHEEN = 0.12f
private const val GLASS_RIM = 0.85f
private const val GLASS_RIDGE = 0.6f
private const val GLASS_LINE = 1.5f
private const val PLACE_SCALE = 1.5f
private const val PLACE_OF_BOX = 0.55f
private const val SUFFIX_SCALE = 0.45f
private const val PLACE_GLOW = 16f
private const val METAL_DEEP = 0.85f
private const val NUMBER_GLOW_ALPHA = 0.6f
private const val WINNER_RIM = 1.3f
