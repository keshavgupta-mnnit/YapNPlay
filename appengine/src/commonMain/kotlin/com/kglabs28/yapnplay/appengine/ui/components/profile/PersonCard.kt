package com.kglabs28.yapnplay.appengine.ui.components.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.PresenceDot
import com.kglabs28.yapnplay.appengine.ui.components.table.tableFrostedGlass
import com.kglabs28.yapnplay.appengine.ui.screens.profile.ProfilePersonUi

/**
 * Someone on a frosted white glass card: avatar with an online dot, name,
 * @username, their ID and online / offline, then [actions] (Block and
 * Remove for a friend, Decline and Accept for a request).
 */
@Composable
fun PersonCard(
    person: ProfilePersonUi,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    Column(
        modifier = modifier
            .width(dimens.personCardWidth)
            .tableFrostedGlass(tint = colors.AvatarRingSilver, shape = RoundedCornerShape(dimens.cardRadius), radius = dimens.cardRadius)
            .padding(Spacing.Sm),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.Xxs),
    ) {
        Box(contentAlignment = Alignment.BottomEnd) {
            PresetAvatar(avatarId = person.avatarId, size = dimens.personCardAvatar)
            PresenceDot(online = person.online, size = dimens.statusDot, onlineColor = colors.LiveGreen)
        }
        BasicText(text = person.name, style = typography.cardTitle.copy(color = colors.TextPrimary, fontWeight = FontWeight.Bold), maxLines = 1, overflow = TextOverflow.Ellipsis)
        if (person.username.isNotEmpty()) BasicText(text = person.username, style = typography.label.copy(color = colors.TextSecondary), maxLines = 1)
        BasicText(text = person.userCode, style = typography.label.copy(color = colors.Cyan, fontWeight = FontWeight.Bold))
        BasicText(text = person.status, style = typography.micro.copy(color = if (person.online) colors.LiveGreen else colors.TextSecondary))
        Row(modifier = Modifier.fillMaxWidth().padding(top = Spacing.Xs), horizontalArrangement = Arrangement.spacedBy(Spacing.Xs), content = actions)
    }
}
