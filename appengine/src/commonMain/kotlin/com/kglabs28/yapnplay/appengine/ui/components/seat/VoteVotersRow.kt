package com.kglabs28.yapnplay.appengine.ui.components.seat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.TableStrings
import com.kglabs28.yapnplay.appengine.ui.components.common.ShrinkText
import com.kglabs28.yapnplay.appengine.ui.components.profile.PresetAvatar

/**
 * Who voted for what, side by side under a vote dialog's buttons: [left]
 * under the left choice, [right] under the right one, each with a small
 * avatar and name per person.
 */
@Composable
fun VoteVotersRow(leftLabel: String, left: List<VoterUi>, rightLabel: String, right: List<VoterUi>, modifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Spacing.Md)) {
        VoterColumn(leftLabel, left, colors.TextSecondary, Modifier.weight(1f))
        VoterColumn(rightLabel, right, colors.Warning, Modifier.weight(1f))
    }
}

@Composable
private fun VoterColumn(label: String, voters: List<VoterUi>, tint: Color, modifier: Modifier) {
    val typography = YapNPlayTheme.typography
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(Spacing.Xxs)) {
        ShrinkText(text = TableStrings.votersHeader(label, voters.size), style = typography.label.copy(color = tint))
        voters.forEach { voter ->
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.Xxs)) {
                PresetAvatar(avatarId = voter.avatarId, size = YapNPlayTheme.dimens.voterAvatar, rimColor = tint)
                ShrinkText(text = voter.name, style = typography.micro.copy(color = YapNPlayTheme.colors.TextPrimary))
            }
        }
    }
}
