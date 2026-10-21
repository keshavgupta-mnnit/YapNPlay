package com.kglabs28.yapnplay.appengine.ui.components.leaderboard

import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import androidx.compose.runtime.getValue
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.RepeatMode
import com.kglabs28.yapnplay.appengine.core.designsystem.AppBrushes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.zIndex
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon

/**
 * One podium place (the design): a floating crown over the winner, the
 * avatar in a ring of its medal, a frosted card rimmed in the medal with the
 * name (voice waves when they talked), "+350 PTS" and their title, on a
 * medal-lit box "1st" / "2nd" / "3rd" as tall as [baseHeight] (1st tallest).
 */
@Composable
fun PodiumSpot(entry: LeaderboardEntryUi, medal: Color, avatarSize: Dp, baseHeight: Dp, modifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    val cardShape = RoundedCornerShape(dimens.cardRadius)
    val baseShape = RoundedCornerShape(dimens.cardRadius)
    // The winner's rims are thicker.
    val rim = if (entry.place == 1) dimens.promptRim * WINNER_RIM else dimens.promptRim
    Column(modifier = modifier.width(dimens.boardSpotWidth), horizontalAlignment = Alignment.CenterHorizontally) {
        if (entry.place == 1) {
            // The crown bobs up and down like the game logo in a room.
            val bob by rememberInfiniteTransition().animateFloat(
                initialValue = -1f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(tween(AppConstants.STAGE_BOB_CYCLE_MS), RepeatMode.Reverse),
            )
            BasicText(
                text = CROWN,
                style = typography.headline.copy(fontSize = typography.headline.fontSize * CROWN_SCALE),
                modifier = Modifier.offset(y = avatarSize / CROWN_TUCK + dimens.boardRing * bob * CROWN_BOB),
            )
        }
        // The avatar overlaps the top of its card (pulled down onto it).
        LeaderboardAvatar(avatarId = entry.avatarId, size = avatarSize, micOn = entry.micOn, medal = medal, modifier = Modifier.offset(y = avatarSize / OVERLAP_DIVISOR).zIndex(1f))
        // The frosted name card, tucked under the avatar.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = dimens.promptGlow, shape = cardShape, ambientColor = medal, spotColor = medal)
                .background(Color.Black.copy(alpha = CARD_DARK), cardShape)
                .background(Brush.verticalGradient(listOf(medal.copy(alpha = FROST_TOP), Color.White.copy(alpha = FROST_BOTTOM))), cardShape)
                .border(rim, Brush.verticalGradient(listOf(medal, medal.copy(alpha = RIM))), cardShape)
                .padding(top = avatarSize / OVERLAP_DIVISOR + Spacing.Xxs, bottom = Spacing.Xs, start = Spacing.Xs, end = Spacing.Xs),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.Xxxs),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.Xxs)) {
                if (entry.micOn) AppIcon(imageVector = Icons.Rounded.GraphicEq, tint = colors.LiveGreen, size = dimens.iconSmall)
                BasicText(
                    text = entry.name.uppercase(),
                    style = typography.label.copy(color = if (entry.isMe) colors.Cyan else Color.White, fontWeight = FontWeight.Black),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (entry.micOn) AppIcon(imageVector = Icons.Rounded.GraphicEq, tint = colors.LiveGreen, size = dimens.iconSmall)
            }
            BasicText(text = entry.score, style = typography.label.copy(color = colors.LiveGreen, fontWeight = FontWeight.Bold))
            if (entry.coins.isNotEmpty()) BasicText(text = entry.coins, style = typography.label.copy(color = colors.Gold, fontWeight = FontWeight.Bold))
            LeaderboardTitleChip(title = entry.title, place = entry.place)
        }
        Box(Modifier.height(Spacing.Xxs))
        // The podium box.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(baseHeight)
                .shadow(elevation = dimens.promptGlow, shape = baseShape, ambientColor = medal, spotColor = medal)
                .background(AppBrushes.podiumBase, baseShape)
                .background(Brush.verticalGradient(listOf(medal.copy(alpha = BASE_TINT), Color.Transparent)), baseShape)
                .border(rim, Brush.verticalGradient(listOf(Color.White.copy(alpha = RIM_SHINE), medal)), baseShape),
            contentAlignment = Alignment.Center,
        ) {
            val number = entry.place.toString()
            BasicText(
                text = buildAnnotatedString {
                    append(number)
                    withStyle(SpanStyle(fontSize = typography.title.fontSize, baselineShift = BaselineShift.Superscript)) { append(entry.placeLabel.removePrefix(number)) }
                },
                style = typography.headline.copy(
                    color = medal,
                    fontWeight = FontWeight.Black,
                    fontSize = typography.headline.fontSize * PLACE_SCALE,
                    shadow = Shadow(color = medal, blurRadius = PLACE_GLOW),
                ),
            )
        }
    }
}

private const val CROWN = "👑"
private const val CROWN_SCALE = 1.6f
private const val CROWN_TUCK = 5
private const val OVERLAP_DIVISOR = 3
private const val CARD_DARK = 0.45f
private const val FROST_TOP = 0.12f
private const val FROST_BOTTOM = 0.06f
private const val RIM = 0.2f
private const val RIM_SHINE = 0.6f
private const val PLACE_SCALE = 1.5f
private const val PLACE_GLOW = 16f
private const val BASE_TINT = 0.35f
private const val WINNER_RIM = 1.6f
private const val CROWN_BOB = 1.5f
