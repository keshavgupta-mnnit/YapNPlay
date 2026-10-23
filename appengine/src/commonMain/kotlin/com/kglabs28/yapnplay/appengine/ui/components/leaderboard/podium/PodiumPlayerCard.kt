package com.kglabs28.yapnplay.appengine.ui.components.leaderboard.podium

import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.zIndex
import com.kglabs28.yapnplay.appengine.core.designsystem.AppBrushes
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.TableStrings
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon
import com.kglabs28.yapnplay.appengine.ui.components.leaderboard.LeaderboardAvatar
import com.kglabs28.yapnplay.appengine.ui.components.leaderboard.LeaderboardEntryUi
import com.kglabs28.yapnplay.appengine.ui.components.leaderboard.LeaderboardTitleChip

/**
 * A podium player, the same for every place: the crown over 1st only, the
 * avatar in a ring of its medal overlapping a frosted card, and in the card
 * voice symbol · name · voice symbol (two lines, then "…"; a tap slides the
 * full name along), the coins won (a loss in red) and the title chip.
 */
@Composable
fun PodiumPlayerCard(entry: LeaderboardEntryUi, modifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    var sliding by remember { mutableStateOf(false) }
    Box(modifier = modifier.width(dimens.podiumCardWidth), contentAlignment = Alignment.TopCenter) {
        Column(
            modifier = Modifier
                .padding(top = dimens.podiumAvatarAbove)
                .fillMaxWidth()
                .height(dimens.podiumCardHeight)
                .background(AppBrushes.podiumCard, RoundedCornerShape(dimens.cardRadius))
                // The part of the avatar inside the card is kept clear.
                .padding(top = dimens.podiumAvatar - dimens.podiumAvatarAbove, start = Spacing.Xxs, end = Spacing.Xxs, bottom = Spacing.Xs),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.Xxxs)) {
                AppIcon(imageVector = Icons.Rounded.GraphicEq, tint = colors.LiveGreen, size = dimens.podiumSignal)
                BasicText(
                    text = entry.name.uppercase(),
                    style = typography.podiumName.copy(color = if (entry.isMe) colors.Cyan else Color.White, textAlign = TextAlign.Center),
                    maxLines = if (sliding) 1 else NAME_LINES,
                    overflow = if (sliding) TextOverflow.Clip else TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .clickable(interactionSource = null, indication = null) { sliding = !sliding }
                        .then(if (sliding) Modifier.basicMarquee() else Modifier),
                )
                AppIcon(imageVector = Icons.Rounded.GraphicEq, tint = colors.LiveGreen, size = dimens.podiumSignal)
            }
            BasicText(
                text = entry.coins.ifEmpty { TableStrings.leaderboardNoCoins },
                style = typography.podiumCoins.copy(color = if (entry.coins.startsWith("-")) colors.PlayerRed else colors.LiveGreen, textAlign = TextAlign.Center),
                maxLines = 1,
            )
            LeaderboardTitleChip(title = entry.title, place = entry.place, textStyle = typography.podiumChip)
        }
        Box(modifier = Modifier.zIndex(1f), contentAlignment = Alignment.TopCenter) {
            LeaderboardAvatar(avatarId = entry.avatarId, size = dimens.podiumAvatar, micOn = false, medal = podiumMedal(entry.place))
            if (entry.place == 1) PodiumCrown()
        }
    }
}

private const val NAME_LINES = 2
