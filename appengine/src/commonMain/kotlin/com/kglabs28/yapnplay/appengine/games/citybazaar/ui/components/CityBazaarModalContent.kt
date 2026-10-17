package com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.ui.components.common.ModalCard
import com.kglabs28.yapnplay.appengine.ui.components.common.PillButton
import com.kglabs28.yapnplay.appengine.ui.components.moderation.MatchSettingsCard
import com.kglabs28.yapnplay.appengine.ui.components.moderation.PenaltyCard
import com.kglabs28.yapnplay.appengine.ui.components.moderation.ReportPlayerCard
import com.kglabs28.yapnplay.appengine.ui.components.moderation.ReportVoteCard
import com.kglabs28.yapnplay.appengine.ui.components.seat.SeatPickerDialog
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.board.tileHeaderTint
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.board.tint
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.dialogs.CbAssetCard
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.dialogs.CbGlassAction
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.dialogs.CbPropertyCard
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.dialogs.CbDialogFrame
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.dialogs.CbDialogNote
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.dialogs.CbRateTable
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.dialogs.CbSheetTable
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbModal
import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.TileKind

/** The dialog for [modal]: balance-sheet decisions, debts, jail, rate cards, results, settings and reports. */
@Composable
fun CityBazaarModalContent(
    modal: CbModal,
    actions: CityBazaarModalActions,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val typography = YapNPlayTheme.typography
    when (modal) {
        is CbModal.Buy -> CbDialogFrame(
            title = modal.title,
            accent = colors.CbCash,
            chip = tileHeaderTint(modal.group, modal.kind),
            countdown = modal.countdown,
            modifier = modifier,
            buttons = {
                CbGlassAction(text = Strings.cbNo, onClick = actions.onDecline, primary = false, modifier = Modifier.weight(1f))
                CbGlassAction(text = Strings.cbYes, onClick = actions.onBuy, enabled = modal.canAfford, modifier = Modifier.weight(1f))
            },
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.Sm)) {
                CbRateTable(rows = modal.rates, modifier = Modifier.weight(1f))
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(Spacing.Xs)) {
                    CbSheetTable(rows = modal.sheet)
                    CbDialogNote(text = if (modal.canAfford) modal.note else Strings.cbCantAfford)
                }
            }
        }
        is CbModal.Build -> CbDialogFrame(
            title = modal.title,
            accent = colors.CbGain,
            chip = tileHeaderTint(modal.group, TileKind.CITY),
            countdown = modal.countdown,
            modifier = modifier,
            buttons = {
                CbGlassAction(text = Strings.cbNo, onClick = actions.onSkipBuild, primary = false, modifier = Modifier.weight(1f))
                CbGlassAction(text = Strings.cbYes, onClick = actions.onConfirmBuild, enabled = modal.canAfford, modifier = Modifier.weight(1f))
            },
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.Sm)) {
                CbRateTable(rows = modal.rates, modifier = Modifier.weight(1f))
                CbSheetTable(rows = modal.sheet, modifier = Modifier.weight(1f))
            }
            CbDialogNote(text = if (modal.canAfford) modal.note else Strings.cbCantAfford)
        }
        is CbModal.JailRoll -> CbDialogFrame(
            title = Strings.cbJailTitle,
            accent = colors.Warning,
            countdown = modal.countdown,
            modifier = modifier,
            buttons = { CbGlassAction(text = Strings.cbRollDoubles, onClick = actions.onRoll, modifier = Modifier.weight(1f)) },
        ) {
            CbDialogNote(text = modal.body)
        }
        is CbModal.JailChoice -> CbDialogFrame(
            title = Strings.cbJailChoiceTitle,
            accent = colors.Warning,
            countdown = modal.countdown,
            modifier = modifier,
            buttons = {
                CbGlassAction(text = Strings.cbHalfRent, onClick = actions.onStayInJail, primary = false, modifier = Modifier.weight(1f))
                CbGlassAction(text = Strings.cbPayFine, onClick = actions.onPayJailFine, enabled = modal.canPay, modifier = Modifier.weight(1f))
            },
        ) {
            CbDialogNote(text = Strings.cbJailChoiceBody)
        }
        is CbModal.Debt -> CbDialogFrame(
            title = Strings.cbDebtTitle,
            accent = colors.CbLoss,
            countdown = modal.countdown,
            modifier = modifier,
            buttons = {
                CbGlassAction(text = Strings.cbBankrupt, onClick = actions.onDeclareBankruptcy, primary = false, modifier = Modifier.weight(1f))
                CbGlassAction(text = Strings.cbAuction, onClick = actions.onStartAuction, primary = false, enabled = modal.canAuction, modifier = Modifier.weight(1f))
                CbGlassAction(text = Strings.cbSellAndPay, onClick = actions.onConfirmSale, enabled = modal.enough, modifier = Modifier.weight(1f))
            },
        ) {
            CbDialogNote(text = modal.body)
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(Spacing.Sm),
            ) {
                modal.assets.forEach { asset ->
                    CbAssetCard(
                        name = asset.name,
                        header = tileHeaderTint(asset.group, asset.kind),
                        line = asset.action,
                        value = asset.value,
                        selected = asset.id in modal.selected,
                        onClick = { actions.onToggleSale(asset.id) },
                    )
                }
            }
            BasicText(text = modal.tracker, style = typography.label.copy(color = if (modal.enough) colors.CbGain else colors.CbLoss))
        }
        is CbModal.Auction -> CbDialogFrame(
            title = modal.title,
            accent = colors.CbCash,
            chip = tileHeaderTint(modal.card.group, modal.card.kind),
            countdown = modal.countdown,
            modifier = modifier,
            buttons = {
                if (!modal.isSeller) {
                    CbGlassAction(text = Strings.cbPass, onClick = actions.onPassAuction, primary = false, modifier = Modifier.weight(1f))
                    CbGlassAction(text = modal.bidLabel, onClick = actions.onBid, enabled = modal.canBid, modifier = Modifier.weight(1f))
                }
            },
        ) {
            CbDialogNote(text = modal.body)
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.Sm)) {
                CbPropertyCard(card = modal.card)
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(Spacing.Xs)) {
                    modal.colourLines.forEach { BasicText(text = it, style = typography.label.copy(color = colors.TextSecondary)) }
                    BasicText(text = modal.leader, style = typography.label.copy(color = colors.CbCash))
                    if (!modal.isSeller) CbSheetTable(rows = modal.sheet)
                }
            }
        }
        // The cards themselves, no frame around them; a tap outside closes them.
        is CbModal.Properties -> Row(
            modifier = modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(Spacing.Sm),
            verticalAlignment = Alignment.Top,
        ) {
            modal.cards.forEach { CbPropertyCard(card = it) }
        }
        is CbModal.CardTable -> CbDialogFrame(
            title = modal.title,
            accent = colors.CbLuck,
            modifier = modifier,
            buttons = { CbGlassAction(text = Strings.cbClose, onClick = actions.onDismiss, primary = false, modifier = Modifier.weight(1f)) },
        ) {
            CbDialogNote(text = Strings.cbCardTableBody)
            CbRateTable(rows = modal.rows)
        }
        is CbModal.Result -> CbDialogFrame(
            title = Strings.ludoResultTitle,
            accent = colors.Gold,
            modifier = modifier,
            buttons = { CbGlassAction(text = Strings.ludoDone, onClick = actions.onDone, modifier = Modifier.weight(1f)) },
        ) {
            modal.rows.forEach { row ->
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.Sm), modifier = Modifier.fillMaxWidth()) {
                    BasicText(text = row.place, style = typography.title.copy(color = colors.Gold))
                    Box(Modifier.size(YapNPlayTheme.dimens.statusDot).background(row.colour.tint(), CircleShape))
                    BasicText(text = row.name, style = typography.title.copy(color = colors.TextPrimary), modifier = Modifier.weight(1f))
                    BasicText(text = row.worth, style = typography.title.copy(color = colors.CbCash, textAlign = TextAlign.End))
                }
            }
        }
        CbModal.LeaveConfirm -> SeatPickerDialog(
            title = Strings.ludoLeaveTitle,
            message = Strings.cbLeaveBody,
            options = emptyList(),
            selectedIndex = null,
            onSelect = {},
            secondaryLabel = Strings.seatCancel,
            onSecondary = actions.onDismiss,
            primaryLabel = Strings.seatExit,
            onPrimary = actions.onConfirmLeave,
            modifier = modifier,
        )
        CbModal.MicPermissionNeeded -> ModalCard(
            title = Strings.micPermissionTitle,
            subtitle = Strings.micPermissionBody,
            onClose = actions.onDismiss,
            modifier = modifier,
            actions = {
                PillButton(
                    text = Strings.micPermissionOpenSettings,
                    onClick = actions.onOpenAppSettings,
                    gradient = listOf(colors.CtaGreenStart, colors.CtaGreenEnd),
                    contentColor = colors.CtaGreenContent,
                    leadingIcon = Icons.Rounded.Settings,
                    trailingIcon = null,
                    textStyle = typography.sectionTitle,
                )
            },
        )
        is CbModal.Settings -> MatchSettingsCard(
            effectsOn = modal.effectsOn,
            musicOn = modal.musicOn,
            onToggleEffects = actions.onToggleEffects,
            onToggleMusic = actions.onToggleMusic,
            onReport = actions.onOpenReport,
            onClose = actions.onDismiss,
            modifier = modifier,
        )
        is CbModal.Report -> ReportPlayerCard(
            options = modal.people.map { Strings.ludoReportOption(it.name, it.role) },
            selectedIndex = modal.people.indexOfFirst { it.userId == modal.selectedUserId }.takeIf { it >= 0 },
            warning = modal.warning,
            error = modal.error,
            onSelect = { index -> actions.onSelectReportPerson(modal.people[index].userId) },
            onSubmit = actions.onSubmitReport,
            onClose = actions.onDismiss,
            modifier = modifier,
        )
        is CbModal.Vote -> ReportVoteCard(
            title = modal.title,
            body = modal.body,
            secondsLeft = modal.secondsLeft,
            onAgree = { actions.onVote(true) },
            onDisagree = { actions.onVote(false) },
            modifier = modifier,
        )
        is CbModal.ReportOutcome -> ModalCard(title = modal.title, subtitle = modal.body, onClose = actions.onDismiss, modifier = modifier)
        is CbModal.Penalty -> PenaltyCard(
            title = modal.title,
            body = modal.body,
            watchingAd = modal.watchingAd,
            onWatchAd = actions.onWatchAd,
            onClose = actions.onDismiss,
            modifier = modifier,
        )
    }
}
