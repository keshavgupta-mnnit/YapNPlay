package com.kglabs28.yapnplay.appengine.games.citybaazar.ui.components

/** Everything the City Baazar dialogs can ask for, in one place. */
class CityBaazarModalActions(
    val onDismiss: () -> Unit,
    val onBuy: () -> Unit,
    val onDecline: () -> Unit,
    val onConfirmBuild: () -> Unit,
    val onSkipBuild: () -> Unit,
    val onRoll: () -> Unit,
    val onPayJailFine: () -> Unit,
    val onStayInJail: () -> Unit,
    val onToggleSale: (assetId: String) -> Unit,
    val onConfirmSale: () -> Unit,
    val onStartAuction: () -> Unit,
    val onBid: () -> Unit,
    val onPassAuction: () -> Unit,
    val onDeclareBankruptcy: () -> Unit,
    val onConfirmLeave: () -> Unit,
    val onDone: () -> Unit,
    val onOpenAppSettings: () -> Unit,
    val onToggleEffects: () -> Unit,
    val onToggleMusic: () -> Unit,
    val onOpenReport: () -> Unit,
    val onSelectReportPerson: (userId: String) -> Unit,
    val onSubmitReport: () -> Unit,
    val onVote: (agree: Boolean) -> Unit,
    val onWatchAd: () -> Unit,
)
