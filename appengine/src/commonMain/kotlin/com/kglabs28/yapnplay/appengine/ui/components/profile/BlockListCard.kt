package com.kglabs28.yapnplay.appengine.ui.components.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import com.kglabs28.yapnplay.appengine.ui.components.common.glassScrollbar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.ui.components.common.EmptyStateText
import com.kglabs28.yapnplay.appengine.ui.components.common.GlassPillButton
import com.kglabs28.yapnplay.appengine.ui.components.common.ModalCard
import com.kglabs28.yapnplay.appengine.ui.screens.profile.ProfileBlockedUi

/** "Block List": everyone the user blocked, when, and Unblock. */
@Composable
fun BlockListCard(
    blocked: List<ProfileBlockedUi>,
    onUnblock: (userId: String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val listScroll = rememberScrollState()
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val rowShape = RoundedCornerShape(dimens.cbRowRadius)
    ModalCard(
        surfaceBrush = Brush.verticalGradient(listOf(YapNPlayTheme.colors.GlassDialogTop, YapNPlayTheme.colors.GlassDialogBottom)),title = Strings.profileBlockListTitle, onClose = onClose, modifier = modifier.width(dimens.profileWideDialogWidth)) {
        if (blocked.isEmpty()) {
            EmptyStateText(text = Strings.profileNoBlocked, modifier = Modifier.fillMaxWidth())
        } else {
            Column(
                modifier = Modifier.fillMaxWidth().heightIn(max = dimens.profileListMaxHeight).glassScrollbar(listScroll).verticalScroll(listScroll).padding(end = YapNPlayTheme.dimens.scrollbarWidth * 2),
                verticalArrangement = Arrangement.spacedBy(Spacing.Sm),
            ) {
                blocked.forEach { user ->
                    PersonRow(avatarId = user.avatarId, name = user.name, username = user.username, detail = user.blockedOn, modifier = Modifier.fillMaxWidth().clip(rowShape).background(YapNPlayTheme.colors.ListRow).padding(Spacing.Xs)) {
                        GlassPillButton(text = Strings.profileUnblock, onClick = { onUnblock(user.userId) }, accent = colors.SocialZoneAccent)
                    }
                }
            }
        }
    }
}
