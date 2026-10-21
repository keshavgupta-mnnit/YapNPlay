package com.kglabs28.yapnplay.appengine.ui.components.groups

import com.kglabs28.yapnplay.appengine.core.ui.RoomStrings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Share
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.glassPill
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon
import com.kglabs28.yapnplay.appengine.ui.components.common.IconCircle
import com.kglabs28.yapnplay.appengine.ui.components.profile.ProfileActionButton
import com.kglabs28.yapnplay.appengine.ui.components.profile.ProfileButtonKind
import com.kglabs28.yapnplay.appengine.ui.screens.playrooms.GroupCreatedUi

/** "Group Created!": a green tick, who was invited, the code, the invite link (tap to copy), then Share and Open Group. */
@Composable
fun GroupCreatedDialog(
    created: GroupCreatedUi,
    onCopyLink: () -> Unit,
    onShare: () -> Unit,
    onOpen: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    GroupDialogFrame(width = dimens.modalWidth * WIDTH_FACTOR, modifier = modifier) {
        GroupDialogTitle(start = "", end = RoomStrings.groupCreatedTitle, onClose = onClose)
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(Spacing.Xs)) {
            IconCircle(imageVector = Icons.Rounded.Check, fill = colors.LiveGreen, tint = colors.CtaGreenContent, size = dimens.groupIconButton, iconSize = dimens.iconMedium)
            BasicText(text = created.body, style = typography.label.copy(color = colors.TextSecondary, textAlign = TextAlign.Center))
            BasicText(text = RoomStrings.groupCode(created.code), style = typography.sectionTitle.copy(color = colors.TextPrimary, fontWeight = FontWeight.Bold))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimens.smallButtonHeight)
                    .glassPill(glowColor = colors.Cyan, surface = colors.RoomsInput)
                    .clickable(onClick = onCopyLink)
                    .padding(horizontal = Spacing.Sm),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.Xs),
            ) {
                BasicText(text = created.link, style = typography.micro.copy(color = colors.Cyan), maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                AppIcon(imageVector = Icons.Rounded.ContentCopy, tint = colors.IconMuted, size = dimens.iconSmall, contentDescription = RoomStrings.groupCopyLink)
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Spacing.Sm)) {
            ProfileActionButton(text = RoomStrings.groupShare, kind = ProfileButtonKind.NEUTRAL, onClick = onShare, leadingIcon = Icons.Rounded.Share, modifier = Modifier.weight(1f))
            ProfileActionButton(text = RoomStrings.groupOpen, kind = ProfileButtonKind.CONFIRM, onClick = onOpen, leadingIcon = Icons.Rounded.ChevronRight, modifier = Modifier.weight(1f))
        }
    }
}

private const val WIDTH_FACTOR = 1.25f
