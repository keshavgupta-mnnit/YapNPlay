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
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
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
import androidx.compose.ui.zIndex
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.designsystem.AppBrushes
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayColors
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.TableStrings
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon

/**
 * One podium place, built bottom-up: the medal cube (its height set by the
 * place: 1st tallest), and on it the [PodiumPlayer] — the same for every
 * place — so the three players stand at different heights but each shows
 * the same things.
 */
@Composable
fun PodiumSpot(
    entry: LeaderboardEntryUi,
    medal: Color,
    avatarSize: Dp,
    cardHeight: Dp,
    cubeHeight: Dp,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
    ) {
        PodiumPlayer(
            entry = entry,
            medal = medal,
            avatarSize = avatarSize,
            cardHeight = cardHeight,
            modifier = Modifier.fillMaxWidth().padding(horizontal = YapNPlayTheme.dimens.boardStepPad),
        )
        Spacer(Modifier.height(Spacing.Xs))
        PodiumCube(entry = entry, medal = medal, height = cubeHeight, modifier = Modifier.fillMaxWidth())
    }
}

/**
 * Everything a podium player shows, always all of it: a crown floating
 * over 1st, the avatar in its medal ring overlapping the top of a frosted
 * card, and in the card a row of voice signal · name · voice signal (the
 * name takes two lines, then "…"; a tap slides the full name along), the
 * coins they won ("0 coins" when none) and their title chip. Every card is
 * [cardHeight] tall.
 */
@Composable
fun PodiumPlayer(
    entry: LeaderboardEntryUi,
    medal: Color,
    avatarSize: Dp,
    cardHeight: Dp,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    val overlap = avatarSize / OVERLAP_DIVISOR
    val cardShape = RoundedCornerShape(dimens.cardRadius)
    var sliding by remember { mutableStateOf(false) }
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.offset(y = overlap).zIndex(1f), contentAlignment = Alignment.TopCenter) {
            LeaderboardAvatar(avatarId = entry.avatarId, size = avatarSize, micOn = false, medal = medal)
            if (entry.place == 1) Crown(avatarSize)
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(cardHeight)
                .background(AppBrushes.podiumCard, cardShape)
                .padding(top = overlap + Spacing.Xxs, bottom = Spacing.Xs, start = Spacing.Xs, end = Spacing.Xs),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
        ) {
            // Signal · name · signal; the signals light green for players who talked.
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.Xxs)) {
                val signal = if (entry.micOn) colors.LiveGreen else colors.IconMuted
                AppIcon(imageVector = Icons.Rounded.GraphicEq, tint = signal, size = dimens.iconSmall)
                val nameStyle = typography.label.copy(color = if (entry.isMe) colors.Cyan else Color.White, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
                BasicText(
                    text = entry.name.uppercase(),
                    style = nameStyle,
                    maxLines = if (sliding) 1 else NAME_LINES,
                    overflow = if (sliding) TextOverflow.Clip else TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .clickable(interactionSource = null, indication = null) { sliding = !sliding }
                        .then(if (sliding) Modifier.basicMarquee() else Modifier),
                )
                AppIcon(imageVector = Icons.Rounded.GraphicEq, tint = signal, size = dimens.iconSmall)
            }
            BasicText(
                text = entry.coins.ifEmpty { TableStrings.leaderboardNoCoins },
                style = typography.label.copy(color = colors.LiveGreen, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center),
                maxLines = 1,
            )
            LeaderboardTitleChip(title = entry.title, place = entry.place)
        }
    }
}

/** The crown over the winner, bobbing gently, high above the avatar's ring. */
@Composable
private fun Crown(avatarSize: Dp) {
    val bob by rememberInfiniteTransition().animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(AppConstants.STAGE_BOB_CYCLE_MS), RepeatMode.Reverse),
    )
    val crown = avatarSize * CROWN_SIZE
    val fontSize = with(LocalDensity.current) { crown.toSp() }
    BasicText(
        text = CROWN,
        style = YapNPlayTheme.typography.headline.copy(fontSize = fontSize),
        modifier = Modifier.offset(y = -crown * CROWN_RISE + YapNPlayTheme.dimens.boardRing * bob * CROWN_BOB).zIndex(2f),
    )
}

/**
 * The podium cube, drawn as one solid: a lit top face running back from
 * the front face's top edge (narrower at the back, as seen from the
 * front and above), the smoky front face, one medal outline round the
 * whole silhouette with a bright ridge where the faces meet, and the
 * place in metal on the front.
 */
@Composable
private fun PodiumCube(entry: LeaderboardEntryUi, medal: Color, height: Dp, modifier: Modifier = Modifier) {
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    val density = LocalDensity.current
    BoxWithConstraints(modifier = modifier.height(height)) {
        val topDepth = maxWidth * TOP_DEPTH
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val top = w * TOP_DEPTH
            val inset = w * TOP_INSET
            val radius = CornerRadius(dimens.boardBoxRadius.toPx())
            val line = dimens.boardBoxFrame.toPx() * if (entry.place == 1) WINNER_RIM else 1f
            // A soft medal glow round the whole cube.
            drawRoundRect(medal, topLeft = Offset(-line, top - line), size = Size(w + line * 2, size.height - top + line * 2), cornerRadius = radius, alpha = GLOW_ALPHA, style = Stroke(line * GLOW_WIDTH))
            // Top face.
            val face = Path().apply {
                moveTo(inset, 0f); lineTo(w - inset, 0f); lineTo(w, top); lineTo(0f, top); close()
            }
            drawPath(face, Brush.verticalGradient(listOf(medal.copy(alpha = TOP_FAR), medal.copy(alpha = TOP_NEAR)), 0f, top))
            drawPath(face, Brush.verticalGradient(listOf(Color.White.copy(alpha = 0f), Color.White.copy(alpha = TOP_SHINE)), 0f, top))
            // Front face.
            drawRoundRect(
                Brush.verticalGradient(listOf(YapNPlayColors.BgPodiumBaseTop, YapNPlayColors.BgPodiumBaseBottom), top, size.height),
                topLeft = Offset(0f, top),
                size = Size(w, size.height - top),
                cornerRadius = radius,
            )
            // One outline round the silhouette, then the ridge between the faces.
            val outline = Path().apply {
                moveTo(inset, 0f); lineTo(w - inset, 0f); lineTo(w, top)
                lineTo(w, size.height); lineTo(0f, size.height); lineTo(0f, top); close()
            }
            drawPath(outline, Brush.verticalGradient(listOf(Color.White.copy(alpha = RIM_SHINE), medal, medal.copy(alpha = METAL_DEEP))), style = Stroke(line, join = StrokeJoin.Round))
            drawLine(Color.White.copy(alpha = RIDGE_ALPHA), Offset(0f, top), Offset(w, top), strokeWidth = line)
            // A thin inner line on the front.
            val pad = dimens.boardBoxInset.toPx() + line
            drawRoundRect(medal.copy(alpha = INNER_RIM), topLeft = Offset(pad, top + pad), size = Size(w - pad * 2, size.height - top - pad * 2), cornerRadius = radius, style = Stroke(dimens.hairline.toPx()))
        }
        // The place, centred on the front face.
        val frontHeight = height - topDepth
        Box(
            modifier = Modifier.fillMaxSize().padding(top = topDepth),
            contentAlignment = Alignment.Center,
        ) {
            val placeSize = with(density) { minOf(typography.headline.fontSize.toPx() * PLACE_SCALE, frontHeight.toPx() * PLACE_OF_BOX).toSp() }
            val number = entry.place.toString()
            BasicText(
                text = buildAnnotatedString {
                    append(number)
                    withStyle(SpanStyle(fontSize = placeSize * SUFFIX_SCALE, baselineShift = BaselineShift.Superscript)) { append(entry.placeLabel.removePrefix(number)) }
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

private const val CROWN = "👑"
/** Crown height ÷ avatar size, and how far it rises above the avatar. */
private const val CROWN_SIZE = 0.5f
private const val CROWN_RISE = 1.05f
private const val CROWN_BOB = 1.5f
private const val OVERLAP_DIVISOR = 3
/** A long name wraps to this many lines before "…". */
private const val NAME_LINES = 2
// The cube.
/** The top face's depth and how much narrower its back edge is, × the cube's width. */
private const val TOP_DEPTH = 0.16f
private const val TOP_INSET = 0.08f
private const val TOP_FAR = 0.25f
private const val TOP_NEAR = 0.6f
private const val TOP_SHINE = 0.25f
private const val RIDGE_ALPHA = 0.7f
private const val GLOW_ALPHA = 0.25f
private const val GLOW_WIDTH = 3f
private const val RIM_SHINE = 0.6f
private const val PLACE_SCALE = 1.5f
/** The place number's size ÷ the front face's height. */
private const val PLACE_OF_BOX = 0.55f
/** "st" / "nd" / "rd" ÷ the number's size. */
private const val SUFFIX_SCALE = 0.45f
private const val PLACE_GLOW = 16f
private const val INNER_RIM = 0.55f
private const val METAL_DEEP = 0.85f
private const val NUMBER_GLOW_ALPHA = 0.6f
private const val WINNER_RIM = 1.3f
