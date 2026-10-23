package com.kglabs28.yapnplay.appengine.ui.components.moderation

import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.TableStrings
import com.kglabs28.yapnplay.appengine.ui.components.common.ModalCard
import com.kglabs28.yapnplay.appengine.ui.components.profile.ProfileActionButton
import com.kglabs28.yapnplay.appengine.ui.components.profile.ProfileButtonKind

/** "END THE GAME?" at any table: keep playing or end, the votes so far and the time left. Only players still in can vote. */
@Composable
fun EndVoteCard(vote: EndVoteUi, onVote: (agree: Boolean) -> Unit, modifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    val typography = YapNPlayTheme.typography
    ModalCard(
        title = TableStrings.endVoteTitle,
        subtitle = vote.body,
        modifier = modifier,
        actions = {
            if (vote.canVote) {
                ProfileActionButton(text = TableStrings.endVoteKeep, kind = ProfileButtonKind.CONFIRM, onClick = { onVote(false) }, leadingIcon = Icons.Rounded.Check)
                ProfileActionButton(text = TableStrings.endVoteEnd, kind = ProfileButtonKind.DANGER, onClick = { onVote(true) }, leadingIcon = Icons.Rounded.Flag)
            }
        },
    ) {
        BasicText(text = vote.tally, style = typography.label.copy(color = colors.Cyan))
        vote.status?.let { BasicText(text = it, style = typography.label.copy(color = colors.TextSecondary)) }
        BasicText(text = vote.countdown, style = typography.label.copy(color = colors.Gold))
    }
}
