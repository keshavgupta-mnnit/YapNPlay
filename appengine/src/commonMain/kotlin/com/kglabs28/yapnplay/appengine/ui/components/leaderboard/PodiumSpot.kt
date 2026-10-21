package com.kglabs28.yapnplay.appengine.ui.components.leaderboard

import androidx.compose.ui.platform.LocalDensity
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.zIndex
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.designsystem.AppBrushes
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon
import com.kglabs28.yapnplay.appengine.ui.components.common.ShrinkText

/**
 * One podium place, as in the design, stacked from the bottom: the
 * medal-lit box "1st" / "2nd" / "3rd" exactly [baseHeight] tall, the frosted
 * name card on it (name with voice waves when they talked, the coins they
 * won, the title chip, all centred), the avatar in a ring of its medal
 * overlapping the card's top, and a floating crown over the winner.
 * [avatarSize] and [baseHeight] come from the podium's height, so 1st always
 * stands tallest and nothing is squeezed out.
 */
@Composable
fun PodiumSpot(entry: LeaderboardEntryUi, medal: Color, avatarSize: Dp, baseHeight: Dp, modifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    val cardShape = RoundedCornerShape(dimens.cardRadius)
    val stepShape = RoundedCornerShape(dimens.boardBoxRadius * 2)
    val overlap = avatarSize / OVERLAP_DIVISOR
    Column(modifier = modifier.fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom) {
        // The step is laid out first (it always keeps its height); the avatar and card get what's left above it.
        Column(
            modifier = Modifier.weight(1f, fill = false).padding(horizontal = dimens.boardStepPad),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom,
        ) {
            // The avatar overlaps the top of its card (pulled down onto it); the crown floats over it and takes no room.
            Box(modifier = Modifier.offset(y = overlap).zIndex(1f), contentAlignment = Alignment.TopCenter) {
                LeaderboardAvatar(avatarId = entry.avatarId, size = avatarSize, micOn = entry.micOn)
                if (entry.place == 1) Crown(avatarSize)
            }
            // Frosted card, no border: name (voice waves while they talked), coins won, title.
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppBrushes.podiumCard, cardShape)
                    .padding(top = overlap + Spacing.Xxxs, bottom = Spacing.Xs, start = Spacing.Xs, end = Spacing.Xs),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.Xxxs),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.Xxs, Alignment.CenterHorizontally)) {
                    if (entry.micOn) AppIcon(imageVector = Icons.Rounded.GraphicEq, tint = colors.LiveGreen, size = dimens.iconSmall)
                    ShrinkText(
                        text = entry.name.uppercase(),
                        style = typography.label.copy(color = if (entry.isMe) colors.Cyan else Color.White, fontWeight = FontWeight.Black, textAlign = TextAlign.Center),
                        minScale = NAME_MIN_SCALE,
                        modifier = Modifier.weight(1f, fill = false),
                    )
                    if (entry.micOn) AppIcon(imageVector = Icons.Rounded.GraphicEq, tint = colors.LiveGreen, size = dimens.iconSmall)
                }
                // The coins the server paid this player (shown once the result arrives).
                if (entry.coins.isNotEmpty()) ShrinkText(
                    text = entry.coins,
                    style = typography.label.copy(color = colors.LiveGreen, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center),
                )
                LeaderboardTitleChip(title = entry.title, place = entry.place)
            }
            Box(Modifier.height(Spacing.Xs))
        }
        // The frosted platform step, with the medal box set in it.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(AppBrushes.podiumStep, stepShape)
                .border(dimens.hairline, Color.White.copy(alpha = STEP_RIM), stepShape)
                .padding(dimens.boardStepPad),
        ) {
            PodiumBox(
                entry = entry,
                medal = medal,
                frame = if (entry.place == 1) dimens.boardBoxFrame * WINNER_RIM else dimens.boardBoxFrame,
                height = baseHeight,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

/** The crown over the winner, bobbing like the game logo in a room, sized to the avatar. */
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
        // Sitting on the avatar's head, above its ring.
        modifier = Modifier.offset(y = -crown * CROWN_RISE + YapNPlayTheme.dimens.boardRing * bob * CROWN_BOB).zIndex(2f),
    )
}

/**
 * The podium box as in the design: smoky dark glass, a bright medal frame
 * with a thin inner line, and the place number in metal (white into the
 * medal colour) with its suffix raised.
 */
@Composable
private fun PodiumBox(entry: LeaderboardEntryUi, medal: Color, frame: Dp, height: Dp, modifier: Modifier = Modifier) {
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    val shape = RoundedCornerShape(dimens.boardBoxRadius)
    val inner = RoundedCornerShape(dimens.boardBoxRadius)
    // The number fits its box: as large as the design's on a tall box, smaller on a short one.
    val placeSize = with(LocalDensity.current) { minOf(typography.headline.fontSize.toPx() * PLACE_SCALE, height.toPx() * PLACE_OF_BOX).toSp() }
    Box(
        modifier = modifier
            .height(height)
            .shadow(elevation = dimens.promptGlow, shape = shape, ambientColor = medal, spotColor = medal)
            .background(AppBrushes.podiumBase, shape)
            .border(frame, Brush.verticalGradient(listOf(Color.White.copy(alpha = RIM_SHINE), medal, medal.copy(alpha = METAL_DEEP))), shape)
            .padding(dimens.boardBoxInset)
            .border(dimens.hairline, medal.copy(alpha = INNER_RIM), inner),
        contentAlignment = Alignment.Center,
    ) {
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

private const val CROWN = "👑"
/** Crown height ÷ avatar size, and how much of it rises above the avatar. */
private const val CROWN_SIZE = 0.5f
private const val CROWN_RISE = 0.7f
private const val CROWN_BOB = 1.5f
private const val OVERLAP_DIVISOR = 3
/** Long names shrink further before they'd be cut. */
private const val NAME_MIN_SCALE = 0.45f
private const val RIM_SHINE = 0.6f
private const val PLACE_SCALE = 1.5f
/** The place number's size ÷ its box's height. */
private const val PLACE_OF_BOX = 0.62f
/** "st" / "nd" / "rd" ÷ the number's size. */
private const val SUFFIX_SCALE = 0.45f
private const val PLACE_GLOW = 16f
private const val INNER_RIM = 0.55f
private const val METAL_DEEP = 0.85f
private const val NUMBER_GLOW_ALPHA = 0.6f
private const val WINNER_RIM = 1.3f
private const val STEP_RIM = 0.14f
