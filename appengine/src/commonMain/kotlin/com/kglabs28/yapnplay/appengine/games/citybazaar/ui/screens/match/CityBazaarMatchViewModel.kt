package com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match

import kotlin.time.Duration.Companion.milliseconds
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbMoneyFlightUi
import com.kglabs28.yapnplay.appengine.core.common.TablePreferences
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbTransactionUi
import com.kglabs28.yapnplay.appengine.core.common.CrashReporter
import com.kglabs28.yapnplay.appengine.ui.components.leaderboard.LeaderboardUi
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.mapper.CityBazaarFeedMapper
import com.kglabs28.yapnplay.appengine.core.audio.TurnAlarm
import com.kglabs28.yapnplay.appengine.ui.components.table.TableFeedTone
import com.kglabs28.yapnplay.appengine.ui.components.table.TableFeedLineUi
import com.kglabs28.yapnplay.appengine.ui.components.table.PlayerFriendActionUi
import com.kglabs28.yapnplay.appengine.features.profile.usecase.PlayerFriendUseCases
import com.kglabs28.yapnplay.appengine.core.network.MatchGoneException
import com.kglabs28.yapnplay.appengine.core.audio.GameAudio
import com.kglabs28.yapnplay.appengine.core.audio.GameSound
import com.kglabs28.yapnplay.appengine.core.common.AppClock
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.core.common.DispatcherProvider
import com.kglabs28.yapnplay.appengine.core.mvi.MviViewModel
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.features.moderation.domain.ModerationRules
import com.kglabs28.yapnplay.appengine.features.moderation.domain.Penalty
import com.kglabs28.yapnplay.appengine.features.moderation.domain.ReportVoteRequest
import com.kglabs28.yapnplay.appengine.features.moderation.usecase.ModerationUseCases
import com.kglabs28.yapnplay.appengine.features.voice.usecase.ObserveLocallyMutedUseCase
import com.kglabs28.yapnplay.appengine.features.voice.usecase.ObserveMySpeakingUseCase
import com.kglabs28.yapnplay.appengine.features.voice.usecase.SetMicOpenUseCase
import com.kglabs28.yapnplay.appengine.features.voice.usecase.ObserveMicOnUseCase
import com.kglabs28.yapnplay.appengine.features.voice.usecase.SetMicOnUseCase
import com.kglabs28.yapnplay.appengine.features.voice.usecase.SetLocallyMutedUseCase
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.CityBazaarColour
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.CityBazaarCommand
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.CityBazaarEvent
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.CityBazaarMatch
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.CityBazaarPresence
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.CityBazaarTurnPhase
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.CityBazaarUpdate
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.usecase.ObserveCityBazaarMatchUseCase
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.usecase.ObserveCityBazaarSpeakingUseCase
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.usecase.SendCityBazaarCommandUseCase
import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.CITY_BAZAAR_TURN_SECONDS
import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.CardDeck
import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.CityBazaarBoard
import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.CityBazaarCards
import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.CityGroup
import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.MoneyReason
import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.TileKind
import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.TILE_COUNT
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.mapper.CityBazaarModerationUiMapper
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.mapper.CityBazaarUiMapper
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * City Bazaar table. Server updates are queued and played event by event:
 * the dice tumble in the roller's colour, the token hops tile by tile,
 * money moves show on the cards, the user's own money shows as a short
 * balance sheet. The user's decisions (buy, build, jail, debt) appear as
 * balance-sheet dialogs on their turn; any running auction shows to every
 * player (bid or pass; the seller watches). The user's cities sit below in
 * colour stacks; open cards light their tiles on the board.
 */
class CityBazaarMatchViewModel(
    private val observeMatch: ObserveCityBazaarMatchUseCase,
    private val observeSpeaking: ObserveCityBazaarSpeakingUseCase,
    private val sendCommand: SendCityBazaarCommandUseCase,
    private val observeMuted: ObserveLocallyMutedUseCase,
    private val setMuted: SetLocallyMutedUseCase,
    private val observeMySpeaking: ObserveMySpeakingUseCase,
    private val setMicOpen: SetMicOpenUseCase,
    private val observeMicOn: ObserveMicOnUseCase,
    private val setMicOn: SetMicOnUseCase,
    private val moderation: ModerationUseCases,
    private val playerFriends: PlayerFriendUseCases,
    private val audio: GameAudio,
    private val tablePrefs: TablePreferences,
    private val clock: AppClock,
    dispatchers: DispatcherProvider,
) : MviViewModel<CityBazaarMatchState, CityBazaarMatchIntent, CityBazaarMatchEffect>(CityBazaarMatchState(), dispatchers) {

    private var matchId: String? = null
    private var match: CityBazaarMatch? = null
    private var speaking: Set<String> = emptySet()
    /** The user speaking into their mic, and whether that mic is open (on at the table, app in front). */
    private var mySpeaking = false
    /** The app-wide mic switch the table follows. */
    private var micSwitch = false
    private var micOpen = false
    private var mutedForMe: Set<String> = emptySet()
    /** What the board and cards show (lags the server while events play). */
    private val shownPositions = mutableMapOf<Int, Int>()
    private val shownCash = mutableMapOf<Int, Int>()
    private val shownOwners = mutableMapOf<Int, Int>()
    private val shownLevels = mutableMapOf<Int, Int>()
    private var playing = false
    private var rollId = 0
    private var die1 = 1
    private var die2 = 1
    private var diceSeat: Int? = null
    private var userModal: CbModal? = null
    /** What the side panel shows: a tile's card, a player, a pile's table, or the user's cities (of [panelGroup], or all). */
    private var panelTile: Int? = null
    private var panelSeat: Int? = null
    private var panelDeck: CardDeck? = null
    private var panelCities = false
    private var panelBank = false
    private var panelGroup: CityGroup? = null
    private var vote: ReportVoteRequest? = null
    private var penalty: Penalty? = null
    private var saleSelection: Set<String> = emptySet()
    /** The auction (tile) the user passed on: no more prompts for it. */
    private var passedAuction: Int? = null
    private var toast: CbToastUi? = null
    private var toastId = 0L
    private var toastJob: Job? = null
    private var transaction: CbTransactionUi? = null
    private var transactionId = 0L
    private var transactionJob: Job? = null
    private var moneyFlight: CbMoneyFlightUi? = null
    /** Every event line so far, newest first; the latest one shows as a snackbar for a moment. */
    private val history = ArrayDeque<TableFeedLineUi>()
    private var feedId = 0L
    private var feedSnack: TableFeedLineUi? = null
    private var feedJob: Job? = null
    private var historyOpen = false
    private var startAnnounced = false
    private var waitAnnounced = false
    private var leaderboard: LeaderboardUi? = null
    /** The deck of the last card drawn (its money event names it). */
    private var lastDeck: String? = null
    private var lastCardText: String? = null
    private val turnAlarm = TurnAlarm { sound(it) }
    /** The card the user just drew, so the money that follows is titled with it. */
    private var cardTitle: String? = null
    private var foreground = true
    private var awayJob: Job? = null
    private var markedAway = false
    private val queue = Channel<CityBazaarUpdate>(Channel.UNLIMITED)
    private var jobs: List<Job> = emptyList()

    override suspend fun handleIntent(intent: CityBazaarMatchIntent) {
        when (intent) {
            is CityBazaarMatchIntent.Load -> load(intent.matchId)
            CityBazaarMatchIntent.Roll -> if (state.value.canRoll) send(CityBazaarCommand.Roll)
            CityBazaarMatchIntent.Buy -> send(CityBazaarCommand.Buy)
            CityBazaarMatchIntent.Decline -> send(CityBazaarCommand.Decline)
            CityBazaarMatchIntent.ConfirmBuild -> (state.value.modal as? CbModal.Build)?.let { send(CityBazaarCommand.Build(it.tile)) }
            CityBazaarMatchIntent.SkipBuild -> send(CityBazaarCommand.SkipBuild)
            CityBazaarMatchIntent.PayJailFine -> send(CityBazaarCommand.PayJailFine)
            CityBazaarMatchIntent.StayInJail -> send(CityBazaarCommand.StayInJail)
            is CityBazaarMatchIntent.ToggleSale -> {
                saleSelection = if (intent.assetId in saleSelection) saleSelection - intent.assetId else saleSelection + intent.assetId
                render()
            }
            CityBazaarMatchIntent.ConfirmSale -> {
                // Buildings first: a city can only be sold bare.
                val picked = saleSelection.sortedBy { if (CityBazaarUiMapper.isBuilding(it)) 0 else 1 }
                saleSelection = emptySet()
                picked.forEach { id ->
                    val tile = CityBazaarUiMapper.assetTile(id)
                    send(if (CityBazaarUiMapper.isBuilding(id)) CityBazaarCommand.SellBuilding(tile) else CityBazaarCommand.SellTile(tile))
                }
            }
            CityBazaarMatchIntent.StartAuction -> saleSelection.singleOrNull()?.takeIf { !CityBazaarUiMapper.isBuilding(it) }?.let {
                saleSelection = emptySet()
                send(CityBazaarCommand.StartAuction(CityBazaarUiMapper.assetTile(it)))
            }
            CityBazaarMatchIntent.Bid -> send(CityBazaarCommand.Bid)
            CityBazaarMatchIntent.PassAuction -> {
                passedAuction = match?.auction?.tile
                render()
            }
            CityBazaarMatchIntent.DeclareBankruptcy -> send(CityBazaarCommand.DeclareBankruptcy)
            is CityBazaarMatchIntent.TileClicked -> match?.let { m ->
                if (state.value.modal != null) return@let
                if (isShowing(tile = intent.tile)) {
                    showPanel()
                    return@let
                }
                // In the side panel: Luck / Shagun Chest tiles their 2–12 table, others their card.
                when (CityBazaarBoard.tile(intent.tile).kind) {
                    TileKind.LUCK -> showPanel(deck = CardDeck.LUCK)
                    TileKind.CHEST -> showPanel(deck = CardDeck.CHEST)
                    else -> if (CityBazaarUiMapper.tileInfo(m, intent.tile) != null) showPanel(tile = intent.tile, seat = null)
                }
            }
            is CityBazaarMatchIntent.OpenStack -> showPanel(cities = true, group = intent.group)
            is CityBazaarMatchIntent.OpenDeck -> showPanel(deck = intent.deck)
            // A second tap on the same card closes its panel.
            is CityBazaarMatchIntent.PlayerClicked -> if (isShowing(seat = intent.seat)) showPanel() else showPanel(tile = null, seat = intent.seat)
            is CityBazaarMatchIntent.PanelPropertyClicked -> showPanel(tile = intent.tile, seat = null)
            CityBazaarMatchIntent.DismissPanel -> {
                // The tap that closed it may also land on the same card / tile: that tap must not reopen it.
                closedSeat = panelSeat
                closedTile = panelTile
                closedAt = clock.now()
                showPanel(tile = null, seat = null)
            }
            CityBazaarMatchIntent.ToggleView -> setState { copy(threeD = !threeD) }
            CityBazaarMatchIntent.OpenMyCities -> showPanel(cities = true)
            CityBazaarMatchIntent.ToggleAutoplay -> toggleAutoplay((match?.me?.let { it.autoplay || it.presence == CityBazaarPresence.BOT } == true)) { send(CityBazaarCommand.SetAutoplay(it)) }
            // The mic switch is the user's, everywhere: this table follows it.
            is CityBazaarMatchIntent.ToggleMic -> when {
                micSwitch -> setMicOn(false)
                intent.permissionGranted -> setMicOn(true)
                else -> emitEffect(CityBazaarMatchEffect.RequestMicPermission)
            }
            is CityBazaarMatchIntent.MicPermissionResult ->
                if (intent.granted) setMicOn(true) else showModal(CbModal.MicPermissionNeeded)
            CityBazaarMatchIntent.OpenAppSettings -> {
                showModal(null)
                emitEffect(CityBazaarMatchEffect.OpenAppSettings)
            }
            is CityBazaarMatchIntent.ToggleHear -> match?.player(intent.seat)?.let { setMuted(it.userId, it.userId !in mutedForMe) }
            // Not the user, a CPU or a seat the CPU plays for someone who left.
            is CityBazaarMatchIntent.PlayerLongPressed -> match?.player(intent.seat)
                ?.takeIf { !it.isMe && !it.isCpu && it.presence != CityBazaarPresence.BOT }
                ?.let { openFriendAction(it.userId, it.name, it.avatarId) }
            CityBazaarMatchIntent.ConfirmFriendAction -> confirmFriendAction()
            CityBazaarMatchIntent.DismissFriendAction -> setState { copy(friendAction = null) }
            CityBazaarMatchIntent.OpenBank -> showPanel(bank = true)
            is CityBazaarMatchIntent.VoteExtension -> send(CityBazaarCommand.VoteExtension(intent.keepPlaying))
            CityBazaarMatchIntent.OpenHistory -> {
                historyOpen = true
                render()
            }
            CityBazaarMatchIntent.DismissHistory -> {
                historyOpen = false
                render()
            }
            CityBazaarMatchIntent.ExitClicked -> showModal(CbModal.LeaveConfirm)
            CityBazaarMatchIntent.ConfirmLeave -> {
                showModal(null)
                if (match?.me != null) send(CityBazaarCommand.Leave)
                emitEffect(CityBazaarMatchEffect.Close)
            }
            CityBazaarMatchIntent.DismissModal -> if (matchGone) emitEffect(CityBazaarMatchEffect.Close) else showModal(null)
            CityBazaarMatchIntent.OpenSettings -> showSettings()
            CityBazaarMatchIntent.ToggleEffects -> {
                audio.setEffectsOn(!audio.settings.value.effectsOn)
                showSettings()
            }
            CityBazaarMatchIntent.ToggleMoneyPopups -> {
                tablePrefs.setMoneyPopups(!tablePrefs.moneyPopups.value)
                showSettings()
            }
            CityBazaarMatchIntent.ToggleMusic -> {
                audio.setMusicOn(!audio.settings.value.musicOn)
                showSettings()
            }
            CityBazaarMatchIntent.OpenReport -> showReport(selected = null, error = null)
            is CityBazaarMatchIntent.SelectReportPerson -> showReport(selected = intent.userId, error = null)
            CityBazaarMatchIntent.SubmitReport -> submitReport()
            is CityBazaarMatchIntent.Vote -> vote?.let {
                vote = null
                render()
                moderation.castVote(it.reportId, intent.agree)
            }
            CityBazaarMatchIntent.WatchAd -> watchAd()
            is CityBazaarMatchIntent.ScreenVisible -> onScreenVisible(intent.visible)
            CityBazaarMatchIntent.Done -> emitEffect(CityBazaarMatchEffect.Close)
            CityBazaarMatchIntent.ShareResult -> leaderboard?.let { emitEffect(CityBazaarMatchEffect.ShareText(Strings.leaderboardShareText(it.game, it.myPlace))) }
        }
    }

    /** The backend no longer has this match; closing the message closes the table. */
    private var matchGone = false

    private fun load(id: String) {
        if (matchId == id) return
        matchId = id
        jobs.forEach { it.cancel() }
        jobs = listOf(
            scope.launch {
                try {
                    observeMatch(id).collect { queue.send(it) }
                } catch (e: MatchGoneException) {
                    // The server lost the match (e.g. it restarted): say so, then close the table.
                    matchGone = true
                    showModal(CbModal.ReportOutcome(Strings.matchGoneTitle, Strings.matchGoneBody))
                }
            },
            scope.launch { for (update in queue) play(update) },
            scope.launch { observeSpeaking(id).collect { speaking = it; render() } },
            scope.launch { observeMySpeaking().collect { mySpeaking = it; render() } },
            scope.launch { observeMicOn().collect { micSwitch = it; syncMic() } },
            scope.launch { observeMuted().collect { mutedForMe = it; render() } },
            scope.launch { moderation.voteRequests().collect { vote = it; render() } },
            scope.launch { moderation.outcomes().collect { showModal(CityBazaarModerationUiMapper.outcome(it)) } },
            scope.launch {
                moderation.penalty().collect { new ->
                    val fresh = new != null && penalty == null
                    penalty = new
                    if (fresh) showPenalty(watchingAd = false) else if (new == null && userModal is CbModal.Penalty) showModal(null)
                }
            },
            scope.launch {
                while (true) {
                    val request = vote
                    if (request != null && clock.now() >= request.expiresAt) vote = null
                    render()
                    delay(AppConstants.LUDO_TIMER_TICK_MS)
                }
            },
        )
    }

    /** Plays [update]'s events in order with sound, then shows the new turn. */
    private suspend fun play(update: CityBazaarUpdate) {
        // First snapshot, or in the background: straight to the latest table.
        if (match == null || !foreground) {
            reset(update.match)
            return
        }
        playing = true
        val m = update.match
        var breathed = 0L
        for (event in update.events) {
            if (event is CityBazaarEvent.Card) {
                lastDeck = CityBazaarFeedMapper.deckLabel(event.cardId)
                lastCardText = CityBazaarCards.find(event.cardId)?.text
            }
            val line = runCatching { CityBazaarFeedMapper.line(event, m, lastDeck) }
                .onFailure { CrashReporter.record(it, "CityBazaarFeed.line") }
                .getOrNull()
            // One message per event: the user's own as a popup (when on), everyone else's as the
            // bottom snackbar; all in the history. Then a short breath before the next one.
            val mine = involvesMe(event, m)
            val popup = mine && tablePrefs.moneyPopups.value
            if (popup) CityBazaarUiMapper.notice(m, event)?.let { showTransaction(it) }
            line?.let { feed(if (popup) it.copy(quiet = true) else it) }
            val shown = popup && (event is CityBazaarEvent.Money || CityBazaarUiMapper.notice(m, event) != null) || (line != null && !line.quiet && !popup)
            when (event) {
                is CityBazaarEvent.Rolled -> {
                    die1 = event.die1
                    die2 = event.die2
                    diceSeat = event.seat
                    rollId++
                    sound(GameSound.DICE_ROLL)
                    render()
                    delay(AppConstants.DICE_ROLL_MS + AppConstants.LUDO_DICE_REST_MS)
                }
                is CityBazaarEvent.Moved -> walk(event)
                is CityBazaarEvent.Money -> money(event, m, line?.text)
                is CityBazaarEvent.Card -> card(event, m)
                is CityBazaarEvent.Bought -> {
                    shownOwners[event.tile] = event.seat
                    render()
                }
                is CityBazaarEvent.Built -> {
                    shownLevels[event.tile] = event.level
                    render()
                }
                is CityBazaarEvent.Sold -> {
                    val level = shownLevels[event.tile] ?: 0
                    if (level > 0) shownLevels[event.tile] = level - 1 else shownOwners.remove(event.tile)
                    render()
                }
                is CityBazaarEvent.Jailed -> {
                    sound(GameSound.CAPTURE)
                }
                is CityBazaarEvent.AuctionEnded -> {
                    val name = CityBazaarBoard.tile(event.tile).name
                    val line = event.winner?.let { w ->
                        shownOwners[event.tile] = w
                        Strings.cbAuctionWon(m.player(w)?.name.orEmpty(), name, CityBazaarUiMapper.rupees(event.amount))
                    } ?: Strings.cbAuctionBank(name, CityBazaarUiMapper.rupees(event.amount))
                    passedAuction = null
                }
                else -> Unit
            }
            // Same as the server's pause: 2.5 s per message, at most 8 s per update.
            if (shown && breathed < AppConstants.CB_EVENT_BREATH_MAX_MS) {
                delay(AppConstants.CB_EVENT_BREATH_MS)
                breathed += AppConstants.CB_EVENT_BREATH_MS
            }
        }
        playing = false
        reset(m)
    }

    /** The token hops tile by tile (or jumps straight to jail). */
    private suspend fun walk(event: CityBazaarEvent.Moved) {
        if (event.jump || event.steps == 0) {
            shownPositions[event.seat] = event.to
            render()
            delay(AppConstants.CB_HOP_MS.toLong() * 2)
            return
        }
        val step = if (event.steps > 0) 1 else -1
        var at = shownPositions[event.seat] ?: event.from
        repeat(abs(event.steps)) {
            at = (at + step + TILE_COUNT) % TILE_COUNT
            shownPositions[event.seat] = at
            sound(GameSound.HOP)
            render()
            delay(AppConstants.CB_HOP_MS.toLong())
        }
    }

    /** Money moves on the cards; the user's own (rent, tax, cards, START, fines) shows as a balance sheet. */
    /**
     * Money moves on the cards, and every transfer (between players or with
     * the bank) shows as the transaction popup for a few seconds, from the
     * user's side when it's theirs.
     */
    private suspend fun money(event: CityBazaarEvent.Money, m: CityBazaarMatch, line: String?) {
        val mySeat = m.me?.seat
        val seat = when {
            mySeat != null && (event.from == mySeat || event.to == mySeat) -> mySeat
            event.from != null -> event.from
            else -> event.to
        }
        val before = seat?.let { shownCash[it] } ?: 0
        event.from?.let { shownCash[it] = (shownCash[it] ?: 0) - event.amount }
        event.to?.let { shownCash[it] = (shownCash[it] ?: 0) + event.amount }
        val mine = mySeat != null && (event.from == mySeat || event.to == mySeat)
        if (mine && tablePrefs.moneyPopups.value) {
            CityBazaarUiMapper.transaction(m, event, seat, before, line, lastDeck, lastCardText)?.let { showTransaction(it) }
        } else if (mine && event.reason != MoneyReason.BANKRUPTCY) {
            // Popups off: the amount flies from the payer to the receiver.
            transactionId++
            transactionJob?.cancel()
            transaction = null
            moneyFlight = CbMoneyFlightUi(transactionId, event.from, event.to, CityBazaarUiMapper.rupees(event.amount))
            transactionJob = scope.launch {
                delay(AppConstants.CB_MONEY_FLIGHT_MS)
                moneyFlight = null
                render()
            }
        }
        render()
    }

    /** Shows [tx] as the popup for a few seconds (a newer one replaces it). */
    private fun showTransaction(tx: CbTransactionUi) {
        transactionId++
        transaction = tx.copy(id = transactionId)
        transactionJob?.cancel()
        transactionJob = scope.launch {
            delay(AppConstants.CB_RENT_NOTICE_MS)
            transaction = null
            render()
        }
        render()
    }

    private fun reasonLabel(reason: MoneyReason): String = when (reason) {
        MoneyReason.RENT -> Strings.cbRentLand
        MoneyReason.TAX -> Strings.cbTaxTitle
        MoneyReason.CLUB -> Strings.cbClubTitle
        MoneyReason.JAIL_FINE -> Strings.cbJailFineTitle
        MoneyReason.LATE_ROLL -> Strings.cbLateFineLabel
        else -> Strings.cbChange
    }

    private suspend fun card(event: CityBazaarEvent.Card, m: CityBazaarMatch) {
        val spec = CityBazaarCards.find(event.cardId) ?: return
        val deck = if (spec.deck == CardDeck.LUCK) Strings.cbLuckLabel else Strings.cbChestLabel
        if (event.seat == m.me?.seat) {
            cardTitle = Strings.cbCardTitle(deck, spec.text)
        } else {
        }
    }

    /** Shows the server's table as it is (after any animation). */
    private fun reset(newMatch: CityBazaarMatch) {
        val justFinished = newMatch.phase == CityBazaarTurnPhase.FINISHED && match?.phase != CityBazaarTurnPhase.FINISHED
        match = newMatch
        syncMic()
        cardTitle = null
        shownPositions.clear()
        shownCash.clear()
        newMatch.players.forEach {
            shownPositions[it.seat] = it.position
            shownCash[it.seat] = it.cash
        }
        shownOwners.clear()
        shownOwners.putAll(newMatch.owners)
        shownLevels.clear()
        shownLevels.putAll(newMatch.levels)
        diceSeat = newMatch.currentSeat
        if (newMatch.phase != CityBazaarTurnPhase.DEBT) saleSelection = emptySet()
        // Round 1: "waiting for everyone" while the server holds the start, then "the game has started".
        if (!startAnnounced && newMatch.round <= 1 && newMatch.phase != CityBazaarTurnPhase.FINISHED) {
            if (newMatch.turnEndsAt != null) {
                startAnnounced = true
                feed(TableFeedLineUi(0, Strings.cbFeedStarted, TableFeedTone.INFO))
            } else if (!waitAnnounced) {
                waitAnnounced = true
                feed(TableFeedLineUi(0, Strings.cbFeedWaiting, TableFeedTone.INFO))
            }
        }
        if (justFinished) {
            sound(GameSound.WIN)
            leaderboard = CityBazaarUiMapper.leaderboard(newMatch, CityBazaarUiMapper.elapsed(clock.now() - newMatch.startedAt))
        }
        render()
    }

    private fun render() {
        val m = match ?: return
        val me = m.me
        if (autoplayWanted != null && autoplayWanted == (me?.let { it.autoplay || it.presence == CityBazaarPresence.BOT } == true)) autoplayWanted = null
        // The mic listens only while it's on at the table and the app is in front.
        val wantMic = foreground && (me?.micOn == true)
        if (wantMic != micOpen) {
            micOpen = wantMic
            setMicOpen(wantMic)
        }
        // Others' talking comes from the server; the user's own from their mic.
        val talkers = if (micOpen && mySpeaking) speaking + m.viewerUserId else speaking
        val now = clock.now()
        val myTurn = me != null && !playing && !me.bankrupt && m.currentSeat == me.seat && !me.autoplay && me.presence == CityBazaarPresence.PLAYING
        val remainingMs = m.turnEndsAt?.let { (it - now).inWholeMilliseconds.coerceAtLeast(0) } ?: 0L
        val countdown = CityBazaarUiMapper.countdown(remainingMs / MILLIS)
        turnAlarm.update(myTurn, remainingMs)
        val cards = CityBazaarUiMapper.cards(
            m,
            CityBazaarUiMapper.CardInputs(talkers, mutedForMe, if (playing) null else remainingMs, CITY_BAZAAR_TURN_SECONDS, shownCash),
        )
        val (myCard, left, right) = CityBazaarUiMapper.sides(m, cards)
        val colourOf: (Int) -> CityBazaarColour? = { seat -> m.player(seat)?.colour }
        val shownMe = me?.copy(cash = shownCash[me.seat] ?: me.cash)
        // The user's turn decision, as a balance-sheet dialog.
        val decision: CbModal? = if (myTurn && shownMe != null) {
            when (m.phase) {
                CityBazaarTurnPhase.BUY -> m.pendingTile?.let { CityBazaarUiMapper.buy(shownMe, it, countdown) }
                CityBazaarTurnPhase.BUILD -> m.pendingTile?.let { CityBazaarUiMapper.build(m, shownMe, it, countdown) }
                CityBazaarTurnPhase.JAIL_ROLL -> CityBazaarUiMapper.jailRoll(m, shownMe, countdown)
                CityBazaarTurnPhase.JAIL_CHOICE -> CityBazaarUiMapper.jailChoice(shownMe, countdown)
                CityBazaarTurnPhase.DEBT -> CityBazaarUiMapper.debt(m, shownMe, saleSelection, countdown)
                else -> null
            }
        } else {
            null
        }
        // A running auction shows to every player (not to watchers, nor after passing it).
        val auction = m.auction?.takeIf { !playing && shownMe != null && !shownMe.bankrupt && it.tile != passedAuction }?.let { a ->
            CityBazaarUiMapper.auction(m, a, shownMe!!, CityBazaarUiMapper.countdown((a.endsAt - now).inWholeSeconds))
        }
        // Rounds are over: everyone sees the continue-or-end vote (only players still in can vote).
        val extension = m.extensionVote?.takeIf { m.phase == CityBazaarTurnPhase.EXTENSION_VOTE && !playing }?.let {
            CityBazaarUiMapper.extensionVote(m, it, CityBazaarUiMapper.countdown((it.endsAt - now).inWholeSeconds.coerceAtLeast(0)))
        }
        val modal = vote?.let { CityBazaarModerationUiMapper.vote(it, now) } ?: auction ?: extension ?: decision ?: userModal
        val panel = panelTile?.let { CityBazaarUiMapper.tileInfo(m, it) }
            ?: panelSeat?.let { CityBazaarUiMapper.playerPanel(m, it, shownCash[it] ?: 0) }
            ?: panelDeck?.let { CityBazaarUiMapper.cardTable(it) }
            ?: (if (panelCities) CityBazaarUiMapper.properties(m, panelGroup) else null)
            ?: if (panelBank) CityBazaarUiMapper.bankPanel(m) else null
        setState {
            copy(
                loaded = true,
                title = Strings.cbTitle,
                subtitle = m.roundLimit?.let { limit -> m.title + " · " + Strings.cbRoundOf(m.round, limit) } ?: m.title,
                audience = CityBazaarUiMapper.audience(m, talkers, mutedForMe),
                roomMatch = m.isRoomMatch,
                spectatorCount = m.spectatorCount.toString(),
                spectatorLabel = Strings.ludoSpectators,
                elapsed = if (m.phase == CityBazaarTurnPhase.FINISHED && elapsed.isNotEmpty()) elapsed else CityBazaarUiMapper.elapsed(now - m.startedAt),
                tiles = CityBazaarUiMapper.tiles(shownOwners, shownLevels, colourOf),
                tokens = CityBazaarUiMapper.tokens(m, shownPositions),
                highlight = when (modal) {
                    is CbModal.Buy -> setOf(modal.tile)
                    is CbModal.Build -> setOf(modal.tile)
                    is CbModal.Auction -> setOf(modal.tile)
                    else -> when (panel) {
                        is CbPanel.Property -> setOf(panel.card.tile)
                        is CbPanel.Player -> panel.cities.map { it.tile }.toSet()
                        is CbPanel.Bank -> panel.cities.map { it.tile }.toSet()
                        is CbPanel.Cities -> panel.cities.map { it.tile }.toSet()
                        is CbPanel.Deck -> CityBazaarBoard.tilesOf(if (panel.deck == CardDeck.LUCK) TileKind.LUCK else TileKind.CHEST).toSet()
                        null -> emptySet()
                    }
                },
                dice = CbDiceUi(die1, die2, rollId, diceSeat?.let(colourOf)),
                myCard = myCard,
                leftCards = left,
                rightCards = right,
                stacks = CityBazaarUiMapper.stacks(m),
                spectating = me == null,
                canRoll = myTurn && (m.phase == CityBazaarTurnPhase.ROLL || m.phase == CityBazaarTurnPhase.JAIL_ROLL),
                rollLabel = if (m.phase == CityBazaarTurnPhase.JAIL_ROLL && myTurn) Strings.cbRollDoubles else Strings.cbRollDice,
                // What the user just set wins until the server agrees (no waiting on the round trip).
                autoplay = autoplayWanted ?: (me?.autoplay == true || me?.presence == CityBazaarPresence.BOT),
                // The app-wide mic switch, at once (the server catches up through syncMic).
                micOn = me != null && micSwitch,
                // The view model's own values (inside copy, bare names would be the old state's).
                toast = this@CityBazaarMatchViewModel.toast,
                transaction = this@CityBazaarMatchViewModel.transaction,
                moneyFlight = this@CityBazaarMatchViewModel.moneyFlight,
                feedSnack = this@CityBazaarMatchViewModel.feedSnack,
                history = this@CityBazaarMatchViewModel.history.toList(),
                historyOpen = this@CityBazaarMatchViewModel.historyOpen,
                leaderboard = this@CityBazaarMatchViewModel.leaderboard,
                modal = modal,
                panel = panel,
            )
        }
    }

    /** Adds [line] to the history and (unless it's quiet) shows it at the bottom for a moment. */
    private fun feed(line: TableFeedLineUi) {
        feedId++
        val item = line.copy(id = feedId)
        history.addFirst(item)
        while (history.size > HISTORY_LIMIT) history.removeLast()
        if (!item.quiet) {
            feedSnack = item
            feedJob?.cancel()
            feedJob = scope.launch {
                delay(AppConstants.CB_TOAST_MS)
                feedSnack = null
                render()
            }
        }
        render()
    }

    private fun showToast(next: CbToastUi) {
        toastId++
        toast = next.copy(id = toastId)
        toastJob?.cancel()
        toastJob = scope.launch {
            delay(AppConstants.CB_TOAST_MS)
            toast = null
            render()
        }
        render()
    }

    private fun showPanel(tile: Int? = null, seat: Int? = null, deck: CardDeck? = null, cities: Boolean = false, group: CityGroup? = null, bank: Boolean = false) {
        panelBank = bank
        panelTile = tile
        panelSeat = seat
        panelDeck = deck
        panelCities = cities
        panelGroup = group
        render()
    }

    private fun showModal(modal: CbModal?) {
        userModal = modal
        render()
    }

    private fun showSettings() {
        val s = audio.settings.value
        showModal(CbModal.Settings(effectsOn = s.effectsOn, musicOn = s.musicOn, moneyPopupsOn = tablePrefs.moneyPopups.value))
    }

    private fun showReport(selected: String?, error: String?) {
        val m = match ?: return
        val voters = CityBazaarModerationUiMapper.voters(m)
        showModal(
            CbModal.Report(
                people = CityBazaarModerationUiMapper.reportablePeople(m),
                selectedUserId = selected,
                warning = Strings.ludoReportWarning(ModerationRules.requiredAgreements(voters)),
                error = error ?: if (ModerationRules.canReport(voters)) null else Strings.ludoReportNeedPeople,
            ),
        )
    }

    private suspend fun submitReport() {
        val m = match ?: return
        val report = userModal as? CbModal.Report ?: return
        val person = report.people.firstOrNull { it.userId == report.selectedUserId } ?: return
        when (moderation.report(m.matchId, person.userId, person.name, CityBazaarModerationUiMapper.voters(m))) {
            is AppResult.Success -> showModal(null)
            is AppResult.Failure -> showReport(report.selectedUserId, Strings.ludoReportNeedPeople)
        }
    }

    private fun showPenalty(watchingAd: Boolean) {
        val p = penalty ?: return
        showModal(CityBazaarModerationUiMapper.penalty(p, clock.now(), watchingAd))
    }

    private suspend fun watchAd() {
        showPenalty(watchingAd = true)
        if (moderation.clearPenaltyWithAd() is AppResult.Failure) showPenalty(watchingAd = false)
    }

    /** Background: sound stops, the mic goes off, and after a short grace the server plays for the user. */
    private suspend fun onScreenVisible(visible: Boolean) {
        foreground = visible
        // The mic is off at the table while the app is away, and back on (if switched on) when it returns.
        syncMic()
        awayJob?.cancel()
        if (visible) {
            audio.startMusic()
            if (markedAway) {
                markedAway = false
                send(CityBazaarCommand.SetAway(false))
            }
        } else {
            audio.stopMusic()
            awayJob = scope.launch {
                delay(AppConstants.LUDO_AWAY_GRACE_MS)
                markedAway = true
                send(CityBazaarCommand.SetAway(true))
            }
        }
    }

    /** Tells the table the user's mic state: the app-wide switch, off while the app is in the background. */
    private fun syncMic() {
        val m = match ?: return
        val current = m.me?.micOn ?: return
        val want = micSwitch && foreground
        if (current != want) scope.launch { send(CityBazaarCommand.SetMic(want)) }
    }

    override fun onCleared() {
        if (micOpen) setMicOpen(false)
    }

    private fun sound(sound: GameSound) {
        if (foreground) audio.play(sound)
    }

    private suspend fun send(command: CityBazaarCommand) {
        val id = matchId ?: return
        sendCommand(id, command)
    }

    private companion object {
        const val MILLIS = 1_000L
        /** Money the dialogs already showed (buying, building, selling). */
        val QUIET = setOf(MoneyReason.BUY, MoneyReason.BUILD, MoneyReason.SELL, MoneyReason.BANKRUPTCY)
        /** Payments everyone is told about. */
        const val HISTORY_LIMIT = 150
        val PAYMENTS = setOf(MoneyReason.RENT, MoneyReason.TAX, MoneyReason.CLUB, MoneyReason.JAIL_FINE, MoneyReason.LATE_ROLL, MoneyReason.CARD, MoneyReason.AUCTION)
    }

    /** Long press on [player]'s card: confirm adding them (or removing them if already a friend). */
    private suspend fun openFriendAction(userId: String, name: String, avatarId: String) {
        val isFriend = userId in playerFriends.friendIds()
        setState { copy(friendAction = PlayerFriendActionUi(userId, name, avatarId, isFriend)) }
    }

    private suspend fun confirmFriendAction() {
        val action = state.value.friendAction ?: return
        if (action.busy) return
        setState { copy(friendAction = action.copy(busy = true, error = null)) }
        when (playerFriends.toggle(action.userId, action.isFriend)) {
            is AppResult.Success -> setState { copy(friendAction = null) }
            is AppResult.Failure -> setState { copy(friendAction = action.copy(busy = false, error = Strings.playerFriendFailed)) }
        }
    }

    /** Autoplay as the user just set it, shown at once until the server agrees (or gives up after a few seconds). */
    private var autoplayWanted: Boolean? = null
    private var autoplayJob: Job? = null

    private suspend fun toggleAutoplay(serverValue: Boolean, send: suspend (Boolean) -> Unit) {
        val next = !(autoplayWanted ?: serverValue)
        autoplayWanted = next
        autoplayJob?.cancel()
        autoplayJob = scope.launch {
            delay(AppConstants.TOGGLE_CONFIRM_MS)
            autoplayWanted = null
            render()
        }
        render()
        send(next)
    }

    /** Whether [event] is about the user (their money, card, jail, rest…). */
    private fun involvesMe(event: CityBazaarEvent, m: CityBazaarMatch): Boolean {
        val me = m.me?.seat ?: return false
        return when (event) {
            is CityBazaarEvent.Money -> event.from == me || event.to == me
            is CityBazaarEvent.Card -> event.seat == me
            is CityBazaarEvent.Jailed -> event.seat == me
            is CityBazaarEvent.Freed -> event.seat == me
            is CityBazaarEvent.Rested -> event.seat == me
            is CityBazaarEvent.Bankrupt -> event.seat == me
            is CityBazaarEvent.Built -> event.seat == me
            is CityBazaarEvent.HalfRent -> event.seat == me
            is CityBazaarEvent.CantAfford -> event.seat == me
            else -> false
        }
    }

    /** The panel [seat] / [tile] was just closed (a moment ago, by this same tap) or is open now. */
    private var closedSeat: Int? = null
    private var closedTile: Int? = null
    private var closedAt = kotlinx.datetime.Instant.DISTANT_PAST

    private fun isShowing(seat: Int? = null, tile: Int? = null): Boolean {
        val justClosed = clock.now() - closedAt < AppConstants.PANEL_RETAP_MS.milliseconds
        return (seat != null && (panelSeat == seat || (justClosed && closedSeat == seat))) ||
            (tile != null && (panelTile == tile || (justClosed && closedTile == tile)))
    }
}
