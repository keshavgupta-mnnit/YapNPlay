package com.kglabs28.yapnplay.appengine.ui.components.table

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.glassPill
import com.kglabs28.yapnplay.appengine.core.ui.MonoStrings
import com.kglabs28.yapnplay.appengine.ui.components.profile.PresetAvatar

/**
 * The Rule of 7's choice: every opponent as a glass tile (their avatar,
 * name and how many cards they hold); a tap trades hands with them.
 */
@Composable
fun MonoSwapChoice(
    targets: List<MonoSwapTargetUi>,
    onPick: (seat: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(Spacing.Sm, Alignment.CenterHorizontally)) {
        targets.forEach { target ->
            Column(
                modifier = Modifier
                    .glassPill(glowColor = colors.SocialZoneAccent, surface = colors.RoomsPanel, shape = RoundedCornerShape(dimens.cardRadius))
                    .clickable(role = Role.Button) { onPick(target.seat) }
                    .padding(horizontal = Spacing.Sm, vertical = Spacing.Xs),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.Xxs),
            ) {
                PresetAvatar(avatarId = target.avatarId, size = dimens.monoSwapAvatar)
                BasicText(
                    text = target.name,
                    style = typography.label.copy(color = colors.TextPrimary, fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                BasicText(text = MonoStrings.monoSwapCards(target.cards), style = typography.micro.copy(color = colors.TextSecondary), maxLines = 1)
            }
        }
    }
}
