package com.kglabs28.yapnplay.appengine.ui.components.lobby

import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayColors
import com.kglabs28.yapnplay.appengine.ui.components.common.ShrinkText
import com.kglabs28.yapnplay.appengine.core.ui.RoomStrings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MicOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.PresenceDot
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon
import com.kglabs28.yapnplay.appengine.ui.components.common.IconCircle
import com.kglabs28.yapnplay.appengine.ui.components.common.VoiceWave
import com.kglabs28.yapnplay.appengine.ui.components.profile.PresetAvatar
import com.kglabs28.yapnplay.appengine.ui.screens.playroomlobby.LobbyMemberUi

/**
 * One member as a glass card (from the member-list design): avatar in a
 * ring — gold with a crown above it for the admin — a divider, then the
 * name with the ready chip fixed on top, and the bottom line: the
 * multicolour wave while they talk, plus a small red crossed mic at the
 * bottom right when their mic is off.
 * The leader card has a golden border; offline members get a dark card.
 */
@Composable
fun LobbyMemberRow(
    member: LobbyMemberUi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val shape = RoundedCornerShape(dimens.rowRadius)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(dimens.lobbyMemberRow)
            // No drop shadow: it would show through the see-through card.
            .clip(shape)
            .background(
                if (member.online) {
                    Brush.verticalGradient(listOf(colors.MemberCardTop, colors.MemberCardBottom))
                } else {
                    SolidColor(colors.MemberCardOffline)
                },
            )
            .border(
                width = if (member.isLeader) dimens.leaderBorder else dimens.hairline,
                color = when {
                    member.isLeader -> colors.Gold
                    member.online -> colors.MemberCardRim
                    else -> colors.MemberCardRim.copy(alpha = 0.15f)
                },
                shape = shape,
            )
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.Xs)
            // Offline: dark card, dimmed content.
            .graphicsLayer { alpha = if (member.online) 1f else OFFLINE_CONTENT_ALPHA },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Xs),
    ) {
        MemberAvatar(member = member)
        Box(
            modifier = Modifier
                .width(dimens.hairline)
                .fillMaxHeight(DIVIDER_HEIGHT_FRACTION)
                .background(colors.GlassShine.copy(alpha = 0.15f)),
        )
        // Name + ready always on top; the voice line always at the bottom.
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(vertical = Spacing.Xxs),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ShrinkText(
                    text = if (member.isMe) "${member.name} (${RoomStrings.lobbyYou})" else member.name,
                    style = YapNPlayTheme.typography.title.copy(color = colors.TextPrimary),
                    modifier = Modifier.weight(1f),
                )
                LobbyStatusChip(status = member.status)
            }
            Row(
                modifier = Modifier.height(dimens.voiceWaveHeight),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.Xs),
            ) {
                // Talking: live colour wave. Talking on mute: it moves, in grey. Silent or muted: still and dark grey.
                if (member.talking) {
                    VoiceWave(modifier = Modifier.weight(1f))
                } else if (member.talkingMuted) {
                    VoiceWave(
                        modifier = Modifier.weight(1f),
                        colors = listOf(YapNPlayColors.ArenaTalkMutedLight, YapNPlayColors.ArenaTalkMutedDark),
                    )
                } else {
                    VoiceWave(
                        modifier = Modifier.weight(1f),
                        animated = false,
                        colors = listOf(colors.VoiceWaveMuted, colors.VoiceWaveMuted),
                    )
                }
                if (member.micOff) MicOffBadge()
            }
        }
    }
}

/** Avatar with a ring (gold for the admin), crown above it, presence dot. */
@Composable
private fun MemberAvatar(
    member: LobbyMemberUi,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens

    Box(modifier = modifier.fillMaxHeight(), contentAlignment = Alignment.Center) {
        Box(contentAlignment = Alignment.BottomEnd) {
            PresetAvatar(
                avatarId = member.avatarId,
                size = dimens.lobbyAvatar,
                rimColor = if (member.isAdmin) colors.Gold else colors.AvatarRingSilver,
            )
            PresenceDot(online = member.online, size = dimens.statusDotSmall, onlineColor = colors.LiveGreen)
        }
        // The admin wears a crown on top of their avatar.
        if (member.isAdmin) {
            val crownSp = with(LocalDensity.current) { dimens.crownSize.toSp() }
            BasicText(
                text = ADMIN_CROWN,
                style = YapNPlayTheme.typography.label.copy(fontSize = crownSp),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = -dimens.crownSize * CROWN_RISE)
                    .semantics { contentDescription = RoomStrings.lobbyAdmin },
            )
        }
    }
}

/** Small red crossed mic at the card's bottom right. */
@Composable
private fun MicOffBadge() {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    IconCircle(
        imageVector = Icons.Rounded.MicOff,
        fill = colors.Error,
        tint = colors.TextPrimary,
        size = dimens.memberVoiceIcon,
        iconSize = dimens.iconTiny,
        rimColor = colors.Error.copy(alpha = 0.6f),
        modifier = Modifier.semantics { contentDescription = RoomStrings.lobbyMicOff },
    )
}

private const val OFFLINE_CONTENT_ALPHA = 0.5f
private const val DIVIDER_HEIGHT_FRACTION = 0.6f

private const val ADMIN_CROWN = "\uD83D\uDC51"
/** How far the crown rises above the avatar's top (× its size). */
private const val CROWN_RISE = 0.45f
