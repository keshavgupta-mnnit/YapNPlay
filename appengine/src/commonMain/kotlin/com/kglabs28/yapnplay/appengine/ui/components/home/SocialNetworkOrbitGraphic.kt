package com.kglabs28.yapnplay.appengine.ui.components.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import com.kglabs28.yapnplay.appengine.core.designsystem.HeadlineFontFamily
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.ui.components.PresenceDot

/**
 * Social orbit graphic matching the "PLAY WITH FRIENDS" mockup.
 * Displays central user avatar connected by orbital lines to satellite friends and "+ Invite Friends" node.
 */
@Composable
fun SocialNetworkOrbitGraphic(
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        // Connected Orbital Network Lines
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width * 0.45f, size.height * 0.5f)
            val pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f)

            val targets = listOf(
                Offset(size.width * 0.22f, size.height * 0.25f),
                Offset(size.width * 0.22f, size.height * 0.75f),
                Offset(size.width * 0.68f, size.height * 0.25f),
                Offset(size.width * 0.68f, size.height * 0.75f),
                Offset(size.width * 0.88f, size.height * 0.5f),
            )

            for (target in targets) {
                drawLine(
                    color = colors.SocialZoneAccent.copy(alpha = 0.5f),
                    start = center,
                    end = target,
                    strokeWidth = 1.5f,
                    pathEffect = pathEffect,
                )
            }
        }

        // Central Avatar Node ("YOU")
        Box(
            modifier = Modifier.align(Alignment.Center).padding(end = Spacing.Lg),
            contentAlignment = Alignment.Center,
        ) {
            FriendAvatarNode(
                glyph = "🕶️",
                borderColor = colors.SocialZoneAccent,
                size = dimens.voiceToggle,
            )
        }

        // Top Left Friend Node
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = Spacing.Lg, start = Spacing.Lg),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                FriendAvatarNode(glyph = "👩", borderColor = colors.Pink, size = dimens.timerRing)
                OnlineBadge()
            }
        }

        // Bottom Left Friend Node
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = Spacing.Lg, start = Spacing.Lg),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                FriendAvatarNode(glyph = "👧", borderColor = colors.Pink, size = dimens.timerRing)
                OnlineBadge()
            }
        }

        // Top Right Friend Node
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = Spacing.Lg, start = Spacing.Xl),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                FriendAvatarNode(glyph = "👨‍🚀", borderColor = colors.SocialZoneAccent, size = dimens.timerRing)
                OnlineBadge()
            }
        }

        // Bottom Right Friend Node
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = Spacing.Lg, start = Spacing.Xl),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                FriendAvatarNode(glyph = "🧔", borderColor = colors.SocialZoneAccent, size = dimens.timerRing)
                OnlineBadge()
            }
        }

        // Far Right Dashed "Invite Friends" Node
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = Spacing.Sm),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(dimens.badgeXLarge)
                        .clip(CircleShape)
                        .background(colors.Panel.copy(alpha = 0.5f))
                        .border(dimens.borderThin, colors.TextDisabled, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    BasicText(
                        text = "+",
                        style = YapNPlayTheme.typography.title.copy(
                            fontFamily = HeadlineFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = colors.TextPrimary,
                        ),
                    )
                }
                BasicText(
                    text = Strings.homeInviteFriends,
                    style = YapNPlayTheme.typography.caption.copy(
                        color = colors.TextSecondary,
                    ),
                )
            }
        }
    }
}

@Composable
private fun FriendAvatarNode(
    glyph: String,
    borderColor: Color,
    size: Dp,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(colors.Panel)
            .border(dimens.avatarRing, borderColor, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        BasicText(
            text = glyph,
            style = YapNPlayTheme.typography.title,
        )
    }
}

@Composable
private fun OnlineBadge(
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    Row(
        modifier = modifier.padding(top = Spacing.Xxs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Xxs),
    ) {
        PresenceDot(online = true, onlineColor = colors.Success)
        BasicText(
            text = Strings.homeOnlineStatus,
            style = YapNPlayTheme.typography.caption.copy(
                color = colors.Success,
                fontWeight = FontWeight.Medium,
            ),
        )
    }
}
