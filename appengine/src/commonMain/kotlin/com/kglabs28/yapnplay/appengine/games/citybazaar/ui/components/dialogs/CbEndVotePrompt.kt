package com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.dialogs

import com.kglabs28.yapnplay.appengine.core.ui.CityBazaarStrings
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbModal
import com.kglabs28.yapnplay.appengine.ui.components.profile.ProfileActionButton
import com.kglabs28.yapnplay.appengine.ui.components.profile.ProfileButtonKind

/** "END THE GAME?": someone asked to end; end or keep playing, the votes so far and the time left. Only players still in can vote. */
@Composable
fun CbEndVotePrompt(modal: CbModal.EndVote, onVote: (agree: Boolean) -> Unit, modifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    val typography = YapNPlayTheme.typography
    CbPromptFrame(
        title = buildAnnotatedString { append(CityBazaarStrings.cbEndVoteTitle) },
        accent = colors.Error,
        countdown = modal.countdown,
        modifier = modifier,
        buttons = if (modal.canVote) {
            {
                ProfileActionButton(text = CityBazaarStrings.cbEndVoteKeep, kind = ProfileButtonKind.CONFIRM, onClick = { onVote(false) }, leadingIcon = Icons.Rounded.Check, modifier = Modifier.weight(1f))
                ProfileActionButton(text = CityBazaarStrings.cbEndVoteEnd, kind = ProfileButtonKind.DANGER, onClick = { onVote(true) }, leadingIcon = Icons.Rounded.Flag, modifier = Modifier.weight(1f))
            }
        } else {
            null
        },
    ) {
        BasicText(text = modal.body, style = typography.title.copy(color = colors.TextPrimary, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center))
        BasicText(text = modal.tally, style = typography.label.copy(color = colors.Cyan, textAlign = TextAlign.Center))
        modal.status?.let { CbDialogNote(text = it) }
    }
}
