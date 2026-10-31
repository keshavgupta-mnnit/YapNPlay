package com.kglabs28.yapnplay.appengine.ui.components.table

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import com.kglabs28.yapnplay.appengine.ui.components.common.ShrinkText
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
    // Tiles as tall as the colour picker's buttons and sharing the card's width, so every MONO choice looks alike.
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Spacing.Xs, Alignment.CenterHorizontally)) {
        targets.forEach { target ->
            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(dimens.ctaHeight)
                    .glassPill(glowColor = colors.SocialZoneAccent, surface = colors.RoomsPanel, shape = RoundedCornerShape(dimens.cardRadius))
                    .clickable(role = Role.Button) { onPick(target.seat) }
                    .padding(horizontal = Spacing.Xs),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.Xxs),
            ) {
                PresetAvatar(avatarId = target.avatarId, size = dimens.monoSwapAvatar)
                Column(modifier = Modifier.weight(1f)) {
                    ShrinkText(text = target.name, style = typography.label.copy(color = colors.TextPrimary, fontWeight = FontWeight.Bold))
                    BasicText(text = MonoStrings.monoSwapCards(target.cards), style = typography.micro.copy(color = colors.TextSecondary), maxLines = 1)
                }
            }
        }
    }
}
