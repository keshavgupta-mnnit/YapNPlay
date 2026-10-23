package com.kglabs28.yapnplay.appengine.ui.components.lobby

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.RoomStrings
import com.kglabs28.yapnplay.appengine.core.ui.TableStrings
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon
import com.kglabs28.yapnplay.appengine.ui.components.common.ModalCard
import com.kglabs28.yapnplay.appengine.ui.components.common.PillTabs
import com.kglabs28.yapnplay.appengine.ui.components.common.ShrinkText
import com.kglabs28.yapnplay.appengine.ui.components.common.TierLook
import com.kglabs28.yapnplay.appengine.ui.components.common.TierLooks
import com.kglabs28.yapnplay.appengine.ui.components.profile.PresetAvatar
import com.kglabs28.yapnplay.appengine.ui.screens.playroomlobby.RoomBoardRowUi
import com.kglabs28.yapnplay.appengine.ui.screens.playroomlobby.RoomBoardTab
import com.kglabs28.yapnplay.appengine.ui.screens.playroomlobby.RoomLeaderboardUi

/**
 * The room's leaderboard: Most wins / Most coins tabs, then every member
 * ranked; 1st to 4th wear the league picker's diamond, gold, silver and
 * bronze glass, the rest a plain row.
 */
@Composable
fun RoomLeaderboardCard(board: RoomLeaderboardUi, onTab: (RoomBoardTab) -> Unit, onClose: () -> Unit, modifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    ModalCard(title = RoomStrings.boardTitle, onClose = onClose, modifier = modifier) {
        PillTabs(
            tabs = listOf(RoomBoardTab.WINS to RoomStrings.boardTabWins, RoomBoardTab.COINS to RoomStrings.boardTabCoins),
            selected = board.tab,
            onSelect = onTab,
        )
        val note = board.error ?: RoomStrings.boardEmpty.takeIf { !board.loading && board.rows.isEmpty() }
        note?.let { BasicText(text = it, style = YapNPlayTheme.typography.label.copy(color = if (it == board.error) colors.Error else colors.TextSecondary)) }
        Column(
            modifier = Modifier.fillMaxWidth().heightIn(max = YapNPlayTheme.dimens.profileListMaxHeight).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Spacing.Xs),
        ) {
            board.rows.forEach { RoomBoardRow(it, TierLooks.forPlace(it.place)) }
        }
    }
}

@Composable
private fun RoomBoardRow(row: RoomBoardRowUi, tier: TierLook?) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    val shape = RoundedCornerShape(dimens.cardRadius)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(if (tier != null) Brush.linearGradient(tier.fill) else Brush.linearGradient(listOf(colors.ListRow, colors.ListRow)))
            .then(if (tier != null) Modifier.border(dimens.promptRim, tier.rim.copy(alpha = RIM_ALPHA), shape) else Modifier)
            .padding(horizontal = Spacing.Sm, vertical = Spacing.Xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Sm),
    ) {
        BasicText(
            text = TableStrings.placeLabel(row.place),
            style = typography.title.copy(color = tier?.rim ?: colors.TextSecondary, fontWeight = FontWeight.Black),
            modifier = Modifier.width(dimens.boardPlaceWidth),
        )
        PresetAvatar(avatarId = row.avatarId, size = dimens.personRowAvatar)
        Column(modifier = Modifier.weight(1f)) {
            ShrinkText(text = row.name, style = typography.label.copy(color = if (row.isMe) colors.Cyan else colors.TextPrimary, fontWeight = FontWeight.Bold))
            ShrinkText(text = row.detail, style = typography.micro.copy(color = colors.TextSecondary))
        }
        if (tier?.royal == true) AppIcon(imageVector = Icons.Rounded.WorkspacePremium, tint = tier.rim, size = dimens.iconSmall)
        BasicText(text = row.value, style = typography.sectionTitle.copy(color = tier?.rim ?: colors.TextPrimary, fontWeight = FontWeight.Black))
    }
}

private const val RIM_ALPHA = 0.85f
