package com.kglabs28.yapnplay.appengine.ui.components.lobby

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.common.ModalCard
import com.kglabs28.yapnplay.appengine.ui.components.common.verticalScrollWithBar
import com.kglabs28.yapnplay.appengine.ui.screens.playroomlobby.LobbySheet

/**
 * A Quick Play party's Add Friends: online friends (or phones around)
 * as rows, each with what they are doing and Add +. Adding asks them at
 * once (a popup on their screen); the row then shows "Invited".
 */
@Composable
fun LobbyInvitePlayersDialog(
    sheet: LobbySheet.InvitePlayers,
    onAdd: (id: String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val typography = YapNPlayTheme.typography
    ModalCard(title = sheet.title, onClose = onClose, modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = YapNPlayTheme.dimens.lobbyInviteListMaxHeight)
                .verticalScrollWithBar(),
            verticalArrangement = Arrangement.spacedBy(Spacing.Xs),
        ) {
            if (!sheet.loading && sheet.rows.isEmpty()) {
                BasicText(text = sheet.emptyText, style = typography.label.copy(color = colors.TextSecondary))
            }
            sheet.rows.forEach { row -> InvitePlayerRow(row = row, onAdd = { onAdd(row.id) }) }
        }
        sheet.error?.let { BasicText(text = it, style = typography.label.copy(color = colors.Error)) }
    }
}
