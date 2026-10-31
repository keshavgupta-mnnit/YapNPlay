package com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components

import com.kglabs28.yapnplay.appengine.ui.components.seat.SeatHandoverDialog
import com.kglabs28.yapnplay.appengine.ui.components.moderation.EndVoteUi
import com.kglabs28.yapnplay.appengine.ui.components.moderation.EndVoteCard
import com.kglabs28.yapnplay.appengine.ui.components.seat.EndGameConfirmDialog
import com.kglabs28.yapnplay.appengine.ui.components.seat.LeaveMatchDialog
import com.kglabs28.yapnplay.appengine.core.ui.TableStrings
import com.kglabs28.yapnplay.appengine.core.ui.LudoStrings
import com.kglabs28.yapnplay.appengine.core.ui.CityBazaarStrings
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.dialogs.CbBuildPrompt
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.dialogs.CbAuctionPrompt
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.dialogs.CbBuyPrompt
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
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.dialogs.CbDialogFrame
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.dialogs.CbDialogNote
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.dialogs.CbRateTable
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
        is CbModal.Buy -> CbBuyPrompt(modal = modal, onNo = actions.onDecline, onYes = actions.onBuy, modifier = modifier)
        is CbModal.Build -> CbBuildPrompt(modal = modal, onNo = actions.onSkipBuild, onYes = actions.onConfirmBuild, modifier = modifier)
        // The keep-playing and end votes in the same dialog as every table's.
        is CbModal.ExtensionVote -> SeatPickerDialog(
            title = CityBazaarStrings.cbExtendTitle,
            message = modal.body,
            options = emptyList(),
            selectedIndex = null,
            onSelect = {},
            secondaryLabel = CityBazaarStrings.cbExtendEnd,
            onSecondary = { actions.onVoteExtension(false) },
            primaryLabel = CityBazaarStrings.cbExtendContinue,
            onPrimary = { actions.onVoteExtension(true) },
            modifier = modifier,
            countdown = modal.countdown,
            footer = listOfNotNull(modal.tally, modal.status),
            showActions = modal.canVote,
        )
        is CbModal.EndVote -> EndVoteCard(EndVoteUi(modal.body, modal.tally, modal.status, modal.canVote, modal.countdown), onVote = actions.onVoteEnd, modifier = modifier)
        is CbModal.Seat -> SeatHandoverDialog(modal.modal, actions.onSeat, modifier)
        CbModal.ConfirmEnd -> EndGameConfirmDialog(onCancel = actions.onDismiss, onEnd = actions.onConfirmEnd, modifier = modifier)
        is CbModal.JailRoll -> CbDialogFrame(
            title = CityBazaarStrings.cbJailTitle,
            accent = colors.Warning,
            countdown = modal.countdown,
            modifier = modifier,
            buttons = {
                CbGlassAction(text = CityBazaarStrings.cbHalfRent, onClick = actions.onStayInJail, primary = false, modifier = Modifier.weight(1f))
                CbGlassAction(text = CityBazaarStrings.cbPayFine, onClick = actions.onPayJailFine, primary = false, enabled = modal.canPay, modifier = Modifier.weight(1f))
                CbGlassAction(text = CityBazaarStrings.cbRollDoubles, onClick = actions.onRoll, modifier = Modifier.weight(1f))
            },
        ) {
            CbDialogNote(text = modal.body)
        }
        is CbModal.JailChoice -> CbDialogFrame(
            title = CityBazaarStrings.cbJailChoiceTitle,
            accent = colors.Warning,
            countdown = modal.countdown,
            modifier = modifier,
            buttons = {
                CbGlassAction(text = CityBazaarStrings.cbHalfRent, onClick = actions.onStayInJail, primary = false, modifier = Modifier.weight(1f))
                CbGlassAction(text = CityBazaarStrings.cbPayFine, onClick = actions.onPayJailFine, enabled = modal.canPay, modifier = Modifier.weight(1f))
            },
        ) {
            CbDialogNote(text = CityBazaarStrings.cbJailChoiceBody)
        }
        is CbModal.Debt -> CbDialogFrame(
            title = CityBazaarStrings.cbDebtTitle,
            accent = colors.CbLoss,
            countdown = modal.countdown,
            modifier = modifier,
            buttons = {
                CbGlassAction(text = CityBazaarStrings.cbBankrupt, onClick = actions.onDeclareBankruptcy, primary = false, modifier = Modifier.weight(1f))
                CbGlassAction(text = CityBazaarStrings.cbAuction, onClick = actions.onStartAuction, primary = false, enabled = modal.canAuction, modifier = Modifier.weight(1f))
                CbGlassAction(text = CityBazaarStrings.cbSellAndPay, onClick = actions.onConfirmSale, enabled = modal.enough, modifier = Modifier.weight(1f))
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
        is CbModal.Auction -> CbAuctionPrompt(modal = modal, onPass = actions.onPassAuction, onBid = actions.onBid, modifier = modifier)
        is CbModal.Result -> CbDialogFrame(
            title = LudoStrings.ludoResultTitle,
            accent = colors.Gold,
            modifier = modifier,
            buttons = { CbGlassAction(text = LudoStrings.ludoDone, onClick = actions.onDone, modifier = Modifier.weight(1f)) },
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
        is CbModal.LeaveConfirm -> LeaveMatchDialog(
            onStay = actions.onDismiss,
            onLeave = actions.onConfirmLeave,
            message = modal.dialog.message,
            options = modal.dialog.options,
            selectedIndex = modal.dialog.selectedIndex,
            onSelect = actions.onPickSeatTaker,
            countdown = modal.dialog.countdown,
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
            onEndGame = if (modal.canProposeEnd) actions.onProposeEnd else null,
            endGameLabel = TableStrings.endGameButton,
            onClose = actions.onDismiss,
            rules = CityBazaarStrings.cbRules,
            modifier = modifier,
        )
        is CbModal.Report -> ReportPlayerCard(
            options = modal.people.map { LudoStrings.ludoReportOption(it.name, it.role) },
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
            onWatchAd = actions.onWatchAd.takeIf { modal.adOffered },
            onClose = actions.onDismiss,
            modifier = modifier,
        )
    }
}
