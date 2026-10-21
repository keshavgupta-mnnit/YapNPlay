package com.kglabs28.yapnplay.appengine.ui.components.leaderboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/** 4th place onwards, as in the design: place, ringed avatar, NAME, points and the title pill on a frosted row. */
@Composable
fun LeaderboardRow(entry: LeaderboardEntryUi, modifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    val shape = RoundedCornerShape(dimens.cardRadius)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = ROW_DARK), shape)
            .background(Color.White.copy(alpha = FILL), shape)
            .border(dimens.hairline, if (entry.isMe) colors.Cyan else Color.White.copy(alpha = RIM), shape)
            .padding(horizontal = Spacing.Sm, vertical = Spacing.Xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Sm),
    ) {
        BasicText(text = entry.placeLabel, style = typography.title.copy(color = colors.TextSecondary, fontWeight = FontWeight.Bold))
        LeaderboardAvatar(avatarId = entry.avatarId, size = dimens.boardAvatarRow, micOn = entry.micOn)
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(Spacing.Xxxs)) {
            BasicText(text = entry.name.uppercase(), style = typography.label.copy(color = if (entry.isMe) colors.Cyan else Color.White, fontWeight = FontWeight.Black), maxLines = 1, overflow = TextOverflow.Ellipsis)
            BasicText(text = entry.score, style = typography.label.copy(color = colors.Cyan, fontWeight = FontWeight.Bold))
            if (entry.coins.isNotEmpty()) BasicText(text = entry.coins, style = typography.label.copy(color = colors.Gold, fontWeight = FontWeight.Bold))
            LeaderboardTitleChip(title = entry.title, place = entry.place)
        }
    }
}

private const val ROW_DARK = 0.35f
private const val FILL = 0.06f
private const val RIM = 0.15f
