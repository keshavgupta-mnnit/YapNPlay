package com.kglabs28.yapnplay.appengine.ui.components.moderation

import com.kglabs28.yapnplay.appengine.core.ui.Strings
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Chat
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.FeedbackStrings
import com.kglabs28.yapnplay.appengine.ui.components.common.GlassPillButton
import com.kglabs28.yapnplay.appengine.ui.components.common.ModalCard

/**
 * Feedback & support from the settings: write to us, see what was sent (and
 * the replies), or rate the app. The animated emojis' credit sits in small
 * grey text at the bottom centre, just off the card's edge.
 */
@Composable
fun FeedbackOptionsCard(
    onShare: () -> Unit,
    onHistory: () -> Unit,
    onRate: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val height = YapNPlayTheme.dimens.ctaHeight
    Box(modifier = modifier) {
        ModalCard(title = FeedbackStrings.feedbackTitle, subtitle = FeedbackStrings.feedbackIntro, onClose = onClose) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.Sm)) {
                GlassPillButton(text = FeedbackStrings.feedbackShare, onClick = onShare, leadingIcon = Icons.Rounded.Edit, accent = colors.LiveGreen, contentColor = colors.LiveGreen, height = height, modifier = Modifier.fillMaxWidth())
                GlassPillButton(text = FeedbackStrings.feedbackSeeMine, onClick = onHistory, leadingIcon = Icons.AutoMirrored.Rounded.Chat, accent = colors.Cyan, contentColor = colors.Cyan, height = height, modifier = Modifier.fillMaxWidth())
                GlassPillButton(text = FeedbackStrings.feedbackRate, onClick = onRate, leadingIcon = Icons.Rounded.Star, accent = colors.Gold, contentColor = colors.Gold, height = height, modifier = Modifier.fillMaxWidth())
            }
        }
        BasicText(
            text = Strings.emojiArtCredit,
            style = YapNPlayTheme.typography.micro.copy(color = colors.IconMuted),
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = Spacing.Xxs),
        )
    }
}
