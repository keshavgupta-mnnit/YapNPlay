package com.kglabs28.yapnplay.appengine.ui.components.leaderboard

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
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon

/**
 * One podium place (the design): a crown over the winner, the avatar in its
 * glowing ring, a frosted card with the name (voice waves when they talked),
 * "+350 PTS" and their title, on a glowing box "1st" / "2nd" / "3rd" in
 * gold, silver or bronze, as tall as [baseHeight].
 */
@Composable
fun PodiumSpot(entry: LeaderboardEntryUi, medal: Color, avatarSize: Dp, baseHeight: Dp, modifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    val cardShape = RoundedCornerShape(dimens.cardRadius)
    val baseShape = RoundedCornerShape(dimens.cardRadius)
    Column(modifier = modifier.width(dimens.boardSpotWidth), horizontalAlignment = Alignment.CenterHorizontally) {
        if (entry.place == 1) BasicText(text = CROWN, style = typography.headline.copy(fontSize = typography.headline.fontSize * CROWN_SCALE))
        LeaderboardAvatar(avatarId = entry.avatarId, size = avatarSize, micOn = entry.micOn, modifier = Modifier.offset(y = avatarSize / OVERLAP_DIVISOR))
        // The frosted name card, tucked under the avatar.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(Color.White.copy(alpha = FROST_TOP), Color.White.copy(alpha = FROST_BOTTOM))), cardShape)
                .border(dimens.hairline, Color.White.copy(alpha = RIM), cardShape)
                .padding(top = avatarSize / OVERLAP_DIVISOR + Spacing.Xxs, bottom = Spacing.Xs, start = Spacing.Xs, end = Spacing.Xs),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.Xxxs),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.Xxs)) {
                if (entry.micOn) AppIcon(imageVector = Icons.Rounded.GraphicEq, tint = colors.LiveGreen, size = dimens.iconSmall)
                BasicText(
                    text = entry.name.uppercase(),
                    style = typography.title.copy(color = if (entry.isMe) colors.Cyan else Color.White, fontWeight = FontWeight.Black),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (entry.micOn) AppIcon(imageVector = Icons.Rounded.GraphicEq, tint = colors.LiveGreen, size = dimens.iconSmall)
            }
            BasicText(text = entry.score, style = typography.label.copy(color = colors.LiveGreen, fontWeight = FontWeight.Bold))
            LeaderboardTitleChip(title = entry.title, place = entry.place)
        }
        Box(Modifier.height(Spacing.Xs))
        // The podium box.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(baseHeight)
                .shadow(elevation = dimens.promptGlow, shape = baseShape, ambientColor = medal, spotColor = medal)
                .background(Brush.verticalGradient(listOf(BASE_TOP, BASE_BOTTOM)), baseShape)
                .border(dimens.promptRim, Brush.verticalGradient(listOf(Color.White.copy(alpha = RIM_SHINE), medal)), baseShape),
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
private const val OVERLAP_DIVISOR = 3
private const val FROST_TOP = 0.16f
private const val FROST_BOTTOM = 0.06f
private const val RIM = 0.2f
private const val RIM_SHINE = 0.6f
private const val PLACE_SCALE = 1.5f
private const val PLACE_GLOW = 16f
private val BASE_TOP = Color(0xFF2A2F45)
private val BASE_BOTTOM = Color(0xFF12141F)
