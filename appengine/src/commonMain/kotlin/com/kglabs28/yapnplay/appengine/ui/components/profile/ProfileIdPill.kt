package com.kglabs28.yapnplay.appengine.ui.components.profile

import androidx.compose.ui.semantics.Role
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.rounded.Share
import com.kglabs28.yapnplay.appengine.core.ui.ProfileStrings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.glassPill
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon

/** "YapNPlay ID  K7M2PX ⧉ ⤴": the ID in a glass pill; a tap copies it ([copied] shows "Copied"); [onShare] adds Share. */
@Composable
fun ProfileIdPill(
    code: String,
    copied: Boolean,
    onCopy: () -> Unit,
    modifier: Modifier = Modifier,
    onShare: (() -> Unit)? = null,
) {
    val colors = YapNPlayTheme.colors
    val typography = YapNPlayTheme.typography
    Row(
        modifier = modifier
            .height(YapNPlayTheme.dimens.smallButtonHeight)
            .glassPill(glowColor = colors.IconMuted, surface = colors.RoomsInput)
            .clickable(onClick = onCopy)
            .padding(horizontal = Spacing.Xxs),
        verticalAlignment = Alignment.CenterVertically,
        // Centred when the pill is given a width (as wide as the buttons on a friend's card).
        horizontalArrangement = Arrangement.spacedBy(Spacing.Xs, Alignment.CenterHorizontally),
    ) {
        BasicText(
            text = ProfileStrings.profileYapId,
            style = typography.micro.copy(color = colors.TextSecondary),
            modifier = Modifier.padding(start = Spacing.Xs),
        )
        BasicText(text = code, style = typography.sectionTitle.copy(color = colors.TextPrimary, fontWeight = FontWeight.Bold))
        if (copied) {
            BasicText(text = ProfileStrings.profileCopied, style = typography.micro.copy(color = colors.LiveGreen), modifier = Modifier.padding(end = Spacing.Xs))
        } else {
            AppIcon(imageVector = Icons.Rounded.ContentCopy, tint = colors.IconMuted, size = YapNPlayTheme.dimens.iconSmall, contentDescription = ProfileStrings.profileCopyCode, modifier = Modifier.padding(end = Spacing.Xs))
        }
        onShare?.let {
            AppIcon(
                imageVector = Icons.Rounded.Share,
                tint = colors.Cyan,
                size = YapNPlayTheme.dimens.iconSmall,
                contentDescription = ProfileStrings.profileShareCode,
                modifier = Modifier.clip(CircleShape).clickable(role = Role.Button, onClick = it).padding(end = Spacing.Xs),
            )
        }
    }
}
