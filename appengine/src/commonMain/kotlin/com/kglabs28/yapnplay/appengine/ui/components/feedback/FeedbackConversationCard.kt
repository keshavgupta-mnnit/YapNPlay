package com.kglabs28.yapnplay.appengine.ui.components.feedback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.FeedbackStrings
import com.kglabs28.yapnplay.appengine.ui.components.common.ModalCard
import com.kglabs28.yapnplay.appengine.ui.components.table.arenaGlass
import com.kglabs28.yapnplay.appengine.ui.screens.feedback.FeedbackBubbleUi
import com.kglabs28.yapnplay.appengine.ui.screens.feedback.FeedbackConversationUi

/** A conversation with the team, read-only: the player's messages on the left, the team's replies on the right. */
@Composable
fun FeedbackConversationCard(conversation: FeedbackConversationUi, onClose: () -> Unit, modifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    ModalCard(title = conversation.title, onClose = onClose, modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth().heightIn(max = YapNPlayTheme.dimens.profileListMaxHeight).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Spacing.Xs),
        ) {
            conversation.messages.forEach { FeedbackBubble(it) }
        }
        if (conversation.waiting) {
            BasicText(text = FeedbackStrings.feedbackNoReplyYet, style = YapNPlayTheme.typography.label.copy(color = colors.TextSecondary))
        }
    }
}

@Composable
private fun FeedbackBubble(message: FeedbackBubbleUi) {
    val colors = YapNPlayTheme.colors
    val typography = YapNPlayTheme.typography
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = if (message.fromTeam) Alignment.End else Alignment.Start) {
        Column(
            modifier = Modifier
                .fillMaxWidth(BUBBLE_WIDTH)
                .arenaGlass(YapNPlayTheme.shapes.panel, rim = if (message.fromTeam) colors.Cyan else colors.ArenaGlassRim)
                .padding(horizontal = Spacing.Sm, vertical = Spacing.Xs),
            verticalArrangement = Arrangement.spacedBy(Spacing.Xxxs),
        ) {
            BasicText(
                text = "${message.who} · ${message.date}",
                style = typography.micro.copy(color = if (message.fromTeam) colors.Cyan else colors.TextSecondary, fontWeight = FontWeight.Bold),
            )
            BasicText(text = message.body, style = typography.label.copy(color = colors.TextPrimary))
        }
    }
}

/** A message takes this much of the card's width (so the two sides read as a chat). */
private const val BUBBLE_WIDTH = 0.85f
