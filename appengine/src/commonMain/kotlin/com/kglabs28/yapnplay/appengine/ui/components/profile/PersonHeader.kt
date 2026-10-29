package com.kglabs28.yapnplay.appengine.ui.components.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.PresenceDot
import com.kglabs28.yapnplay.appengine.ui.screens.profile.ProfilePersonUi

/** The top of a person's card: big avatar with its online dot, name, their ID pill. */
@Composable
fun PersonHeader(person: ProfilePersonUi, onCopyCode: () -> Unit, modifier: Modifier = Modifier, idPillModifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(Spacing.Xxs)) {
        Box(contentAlignment = Alignment.BottomEnd) {
            PresetAvatar(avatarId = person.avatarId, size = dimens.personAvatar, rimColor = colors.Cyan)
            PresenceDot(online = person.online, size = dimens.statusDot, onlineColor = colors.LiveGreen)
        }
        BasicText(text = person.name, style = typography.title.copy(color = colors.TextPrimary, fontWeight = FontWeight.Bold), maxLines = 1)
        ProfileIdPill(code = person.userCode, copied = false, onCopy = onCopyCode, modifier = idPillModifier)
    }
}
