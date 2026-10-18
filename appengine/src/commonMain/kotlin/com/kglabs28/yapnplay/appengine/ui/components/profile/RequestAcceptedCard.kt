package com.kglabs28.yapnplay.appengine.ui.components.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.ui.components.common.IconCircle
import com.kglabs28.yapnplay.appengine.ui.components.common.ModalCard

/** "Friend Request Accepted": a green tick, "You are now friends with Arjun", Great!. */
@Composable
fun RequestAcceptedCard(name: String, onDone: () -> Unit, modifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    val typography = YapNPlayTheme.typography
    ModalCard(
        surfaceBrush = Brush.verticalGradient(listOf(YapNPlayTheme.colors.GlassDialogTop, YapNPlayTheme.colors.GlassDialogBottom)),title = Strings.profileAcceptedTitle, onClose = onDone, modifier = modifier) {
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(Spacing.Xs)) {
            IconCircle(imageVector = Icons.Rounded.Check, fill = colors.LiveGreen, tint = colors.CtaGreenContent, size = YapNPlayTheme.dimens.personRowAvatar, iconSize = YapNPlayTheme.dimens.iconMedium)
            BasicText(text = Strings.profileAcceptedBody, style = typography.label.copy(color = colors.TextSecondary, textAlign = TextAlign.Center))
            BasicText(text = name, style = typography.title.copy(color = colors.TextPrimary, fontWeight = FontWeight.Bold))
            ProfileActionButton(text = Strings.profileGreat, kind = ProfileButtonKind.CONFIRM, onClick = onDone, modifier = Modifier.fillMaxWidth())
        }
    }
}
