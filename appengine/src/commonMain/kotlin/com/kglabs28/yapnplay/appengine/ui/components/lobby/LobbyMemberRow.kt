package com.kglabs28.yapnplay.appengine.ui.components.lobby

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextOverflow
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.ui.components.PresenceDot
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon
import com.kglabs28.yapnplay.appengine.ui.components.common.IconCircle
import com.kglabs28.yapnplay.appengine.ui.components.common.VoiceWave
import com.kglabs28.yapnplay.appengine.ui.screens.playroomlobby.LobbyMemberUi

/**
 * One member card: avatar with presence dot, name (+ "You"), admin crown,
 * then the talking wave — or "Mic off"-style text — and the ready status.
 * The leader card has a golden border; offline members are greyed out.
 */
@Composable
fun LobbyMemberRow(
    member: LobbyMemberUi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    val shape = RoundedCornerShape(dimens.rowRadius)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(dimens.lobbyMemberRow)
            .graphicsLayer { alpha = if (member.online) 1f else OFFLINE_ALPHA }
            .clip(shape)
            .background(colors.RoomsRow.copy(alpha = 0.85f))
            .border(
                width = if (member.isLeader) dimens.leaderBorder else dimens.hairline,
                color = if (member.isLeader) colors.Gold else colors.GlassShine.copy(alpha = 0.08f),
                shape = shape,
            )
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.Xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Xs),
    ) {
        Box(contentAlignment = Alignment.BottomEnd) {
            IconCircle(
                imageVector = Icons.Rounded.Person,
                fill = colors.RoomsSmallButton,
                tint = colors.IconMuted,
                size = dimens.lobbyAvatar,
                iconSize = dimens.iconMedium,
                rimColor = colors.SocialZoneAccent,
            )
            PresenceDot(online = member.online, size = dimens.statusDotSmall, onlineColor = colors.LiveGreen)
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Spacing.Xxxs),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.Xxs),
            ) {
                BasicText(
                    text = member.name,
                    style = typography.itemTitle.copy(color = colors.TextPrimary),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )
                if (member.isMe) {
                    BasicText(text = Strings.lobbyYou, style = typography.micro.copy(color = colors.TextSecondary))
                }
                if (member.isAdmin) {
                    AppIcon(imageVector = Icons.Rounded.WorkspacePremium, tint = colors.Gold, size = dimens.iconSmall)
                    BasicText(text = Strings.lobbyAdmin, style = typography.micro.copy(color = colors.Gold))
                }
            }
            if (member.talking) {
                VoiceWave(modifier = Modifier.fillMaxWidth(WAVE_WIDTH_FRACTION))
            } else {
                member.voiceLabel?.let {
                    BasicText(text = it, style = typography.micro.copy(color = colors.TextSecondary), maxLines = 1)
                }
            }
        }
        LobbyStatusChip(status = member.status)
    }
}

private const val OFFLINE_ALPHA = 0.45f
private const val WAVE_WIDTH_FRACTION = 0.6f
