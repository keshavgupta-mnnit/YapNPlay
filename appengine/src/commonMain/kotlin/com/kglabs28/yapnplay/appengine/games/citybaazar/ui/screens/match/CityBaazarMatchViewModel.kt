package com.kglabs28.yapnplay.appengine.games.citybaazar.ui.screens.match

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
import com.kglabs28.yapnplay.appengine.features.voice.usecase.SetLocallyMutedUseCase
import com.kglabs28.yapnplay.appengine.games.citybaazar.domain.CityBaazarColour
import com.kglabs28.yapnplay.appengine.games.citybaazar.domain.CityBaazarCommand
import com.kglabs28.yapnplay.appengine.games.citybaazar.domain.CityBaazarEvent
import com.kglabs28.yapnplay.appengine.games.citybaazar.domain.CityBaazarMatch
import com.kglabs28.yapnplay.appengine.games.citybaazar.domain.CityBaazarPresence
import com.kglabs28.yapnplay.appengine.games.citybaazar.domain.CityBaazarTurnPhase
import com.kglabs28.yapnplay.appengine.games.citybaazar.domain.CityBaazarUpdate
import com.kglabs28.yapnplay.appengine.games.citybaazar.domain.usecase.ObserveCityBaazarMatchUseCase
import com.kglabs28.yapnplay.appengine.games.citybaazar.domain.usecase.ObserveCityBaazarSpeakingUseCase
import com.kglabs28.yapnplay.appengine.games.citybaazar.domain.usecase.SendCityBaazarCommandUseCase
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.CITY_BAAZAR_TURN_SECONDS
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.CardDeck
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.CityBaazarBoard
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.CityBaazarCards
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.MoneyReason
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.TileKind
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.TILE_COUNT
import com.kglabs28.yapnplay.appengine.games.citybaazar.ui.mapper.CityBaazarModerationUiMapper
import com.kglabs28.yapnplay.appengine.games.citybaazar.ui.mapper.CityBaazarUiMapper
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * City Baazar table. Server updates are queued and played event by event:
 * the dice tumble in the roller's colour, the token hops tile by tile,
 * money moves show on the cards, the user's own money shows as a short
 * balance sheet. The user's decisions (buy, build, jail, debt) appear as
 * balance-sheet dialogs on their turn; any running auction shows to every
 * player (bid or pass; the seller watches). The user's cities sit below in
 * colour stacks; open cards light their tiles on the board.
 */
class CityBaazarMatchViewModel(
    private val observeMatch: ObserveCityBaazarMatchUseCase,
    private val observeSpeaking: ObserveCityBaazarSpeakingUseCase,
    private val sendCommand: SendCityBaazarCommandUseCase,
    private val observeMuted: ObserveLocallyMutedUseCase,
    private val setMuted: SetLocallyMutedUseCase,
    private val moderation: ModerationUseCases,
    private val audio: GameAudio,
    private val clock: AppClock,
    dispatchers: DispatcherProvider,
) : MviViewModel<CityBaazarMatchState, CityBaazarMatchIntent, CityBaazarMatchEffect>(CityBaazarMatchState(), dispatchers) {

    private var matchId: String? = null
    private var match: CityBaazarMatch? = null
    private var speaking: Set<String> = emptySet()
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
    private var vote: ReportVoteRequest? = null
    private var penalty: Penalty? = null
    private var saleSelection: Set<String> = emptySet()
    /** The auction (tile) the user passed on: no more prompts for it. */
    private var passedAuction: Int? = null
    private var toast: CbToastUi? = null
    private var toastId = 0L
    private var toastJob: Job? = null
    /** The card the user just drew, so the money that follows is titled with it. */
    private var cardTitle: String? = null
    private var foreground = true
    private var awayJob: Job? = null
    private var markedAway = false
    private val queue = Channel<CityBaazarUpdate>(Channel.UNLIMITED)
    private var jobs: List<Job> = emptyList()

    override suspend fun handleIntent(intent: CityBaazarMatchIntent) {
        when (intent) {
            is CityBaazarMatchIntent.Load -> load(intent.matchId)
            CityBaazarMatchIntent.Roll -> if (state.value.canRoll) send(CityBaazarCommand.Roll)
            CityBaazarMatchIntent.Buy -> send(CityBaazarCommand.Buy)
            CityBaazarMatchIntent.Decline -> send(CityBaazarCommand.Decline)
            CityBaazarMatchIntent.ConfirmBuild -> (state.value.modal as? CbModal.Build)?.let { send(CityBaazarCommand.Build(it.tile)) }
            CityBaazarMatchIntent.SkipBuild -> send(CityBaazarCommand.SkipBuild)
            CityBaazarMatchIntent.PayJailFine -> send(CityBaazarCommand.PayJailFine)
            CityBaazarMatchIntent.StayInJail -> send(CityBaazarCommand.StayInJail)
            is CityBaazarMatchIntent.ToggleSale -> {
                saleSelection = if (intent.assetId in saleSelection) saleSelection - intent.assetId else saleSelection + intent.assetId
                render()
            }
            CityBaazarMatchIntent.ConfirmSale -> {
                // Buildings first: a city can only be sold bare.
                val picked = saleSelection.sortedBy { if (CityBaazarUiMapper.isBuilding(it)) 0 else 1 }
                saleSelection = emptySet()
                picked.forEach { id ->
                    val tile = CityBaazarUiMapper.assetTile(id)
                    send(if (CityBaazarUiMapper.isBuilding(id)) CityBaazarCommand.SellBuilding(tile) else CityBaazarCommand.SellTile(tile))
                }
            }
            CityBaazarMatchIntent.StartAuction -> saleSelection.singleOrNull()?.takeIf { !CityBaazarUiMapper.isBuilding(it) }?.let {
                saleSelection = emptySet()
                send(CityBaazarCommand.StartAuction(CityBaazarUiMapper.assetTile(it)))
            }
            CityBaazarMatchIntent.Bid -> send(CityBaazarCommand.Bid)
            CityBaazarMatchIntent.PassAuction -> {
                passedAuction = match?.auction?.tile
                render()
            }
            CityBaazarMatchIntent.DeclareBankruptcy -> send(CityBaazarCommand.DeclareBankruptcy)
            is CityBaazarMatchIntent.TileClicked -> match?.let { m ->
                if (state.value.modal != null) return@let
                // Luck / Shagun Chest tiles show their 2–12 table; others their card.
                when (CityBaazarBoard.tile(intent.tile).kind) {
                    TileKind.LUCK -> showModal(CityBaazarUiMapper.cardTable(CardDeck.LUCK))
                    TileKind.CHEST -> showModal(CityBaazarUiMapper.cardTable(CardDeck.CHEST))
                    else -> CityBaazarUiMapper.tileInfo(m, intent.tile)?.let { showModal(it) }
                }
            }
            is CityBaazarMatchIntent.OpenStack -> match?.let { m -> CityBaazarUiMapper.properties(m, intent.group)?.let { showModal(it) } }
            is CityBaazarMatchIntent.OpenDeck -> showModal(CityBaazarUiMapper.cardTable(intent.deck))
            is CityBaazarMatchIntent.PlayerClicked -> match?.let { m ->
                CityBaazarUiMapper.playerInfo(m, intent.seat, shownCash[intent.seat] ?: 0)?.let { showModal(it) }
            }
            CityBaazarMatchIntent.ToggleView -> setState { copy(threeD = !threeD) }
            CityBaazarMatchIntent.OpenMyCities -> match?.let { m -> CityBaazarUiMapper.properties(m, null)?.let { showModal(it) } }
            CityBaazarMatchIntent.Trade -> showToast(CityBaazarUiMapper.noteToast(Strings.cbTrade, Strings.cbTradeHint))
            CityBaazarMatchIntent.ToggleAutoplay -> send(CityBaazarCommand.SetAutoplay(!state.value.autoplay))
            is CityBaazarMatchIntent.ToggleMic -> when {
                state.value.micOn -> send(CityBaazarCommand.SetMic(false))
                intent.permissionGranted -> send(CityBaazarCommand.SetMic(true))
                else -> emitEffect(CityBaazarMatchEffect.RequestMicPermission)
            }
            is CityBaazarMatchIntent.MicPermissionResult ->
                if (intent.granted) send(CityBaazarCommand.SetMic(true)) else showModal(CbModal.MicPermissionNeeded)
            CityBaazarMatchIntent.OpenAppSettings -> {
                showModal(null)
                emitEffect(CityBaazarMatchEffect.OpenAppSettings)
            }
            is CityBaazarMatchIntent.ToggleHear -> match?.player(intent.seat)?.let { setMuted(it.userId, it.userId !in mutedForMe) }
            CityBaazarMatchIntent.ExitClicked -> showModal(CbModal.LeaveConfirm)
            CityBaazarMatchIntent.ConfirmLeave -> {
                showModal(null)
                if (match?.me != null) send(CityBaazarCommand.Leave)
                emitEffect(CityBaazarMatchEffect.Close)
            }
            CityBaazarMatchIntent.DismissModal -> showModal(null)
            CityBaazarMatchIntent.OpenSettings -> showSettings()
            CityBaazarMatchIntent.ToggleEffects -> {
                audio.setEffectsOn(!audio.settings.value.effectsOn)
                showSettings()
            }
            CityBaazarMatchIntent.ToggleMusic -> {
                audio.setMusicOn(!audio.settings.value.musicOn)
                showSettings()
            }
            CityBaazarMatchIntent.OpenReport -> showReport(selected = null, error = null)
            is CityBaazarMatchIntent.SelectReportPerson -> showReport(selected = intent.userId, error = null)
            CityBaazarMatchIntent.SubmitReport -> submitReport()
            is CityBaazarMatchIntent.Vote -> vote?.let {
                vote = null
                render()
                moderation.castVote(it.reportId, intent.agree)
            }
            CityBaazarMatchIntent.WatchAd -> watchAd()
            is CityBaazarMatchIntent.ScreenVisible -> onScreenVisible(intent.visible)
            CityBaazarMatchIntent.Done -> emitEffect(CityBaazarMatchEffect.Close)
        }
    }

    private fun load(id: String) {
        if (matchId == id) return
        matchId = id
        jobs.forEach { it.cancel() }
        jobs = listOf(
            scope.launch { observeMatch(id).collect { queue.send(it) } },
            scope.launch { for (update in queue) play(update) },
            scope.launch { observeSpeaking(id).collect { speaking = it; render() } },
            scope.launch { observeMuted().collect { mutedForMe = it; render() } },
            scope.launch { moderation.voteRequests().collect { vote = it; render() } },
            scope.launch { moderation.outcomes().collect { showModal(CityBaazarModerationUiMapper.outcome(it)) } },
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
    private suspend fun play(update: CityBaazarUpdate) {
        // First snapshot, or in the background: straight to the latest table.
        if (match == null || !foreground) {
            reset(update.match)
            return
        }
        playing = true
        val m = update.match
        for (event in update.events) {
            when (event) {
                is CityBaazarEvent.Rolled -> {
                    die1 = event.die1
                    die2 = event.die2
                    diceSeat = event.seat
                    rollId++
                    sound(GameSound.DICE_ROLL)
                    render()
                    delay(AppConstants.DICE_ROLL_MS + AppConstants.LUDO_DICE_REST_MS)
                }
                is CityBaazarEvent.Moved -> walk(event)
                is CityBaazarEvent.Money -> money(event, m)
                is CityBaazarEvent.Card -> card(event, m)
                is CityBaazarEvent.Bought -> {
                    shownOwners[event.tile] = event.seat
                    render()
                }
                is CityBaazarEvent.Built -> {
                    shownLevels[event.tile] = event.level
                    render()
                }
                is CityBaazarEvent.Sold -> {
                    val level = shownLevels[event.tile] ?: 0
                    if (level > 0) shownLevels[event.tile] = level - 1 else shownOwners.remove(event.tile)
                    render()
                }
                is CityBaazarEvent.Jailed -> {
                    sound(GameSound.CAPTURE)
                    if (event.seat == m.me?.seat) showToast(CityBaazarUiMapper.noteToast(Strings.cbSentToJail, Strings.cbJailedBody, gain = false))
                }
                is CityBaazarEvent.Freed -> if (event.seat == m.me?.seat) showToast(CityBaazarUiMapper.noteToast(Strings.cbFreedTitle, Strings.cbFreedBody))
                is CityBaazarEvent.AuctionEnded -> {
                    val name = CityBaazarBoard.tile(event.tile).name
                    val line = event.winner?.let { w ->
                        shownOwners[event.tile] = w
                        Strings.cbAuctionWon(m.player(w)?.name.orEmpty(), name, CityBaazarUiMapper.rupees(event.amount))
                    } ?: Strings.cbAuctionBank(name, CityBaazarUiMapper.rupees(event.amount))
                    passedAuction = null
                    showToast(CityBaazarUiMapper.noteToast(Strings.cbAuctionEnded, line))
                }
                is CityBaazarEvent.Bankrupt -> m.player(event.seat)?.let {
                    showToast(CityBaazarUiMapper.noteToast(Strings.cbWentBankrupt(it.name), Strings.cbBankruptBody, gain = false))
                }
                else -> Unit
            }
        }
        playing = false
        reset(m)
    }

    /** The token hops tile by tile (or jumps straight to jail). */
    private suspend fun walk(event: CityBaazarEvent.Moved) {
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
    private suspend fun money(event: CityBaazarEvent.Money, m: CityBaazarMatch) {
        val mySeat = m.me?.seat
        val mine = mySeat != null && (event.from == mySeat || event.to == mySeat)
        val before = mySeat?.let { shownCash[it] } ?: 0
        event.from?.let { shownCash[it] = (shownCash[it] ?: 0) - event.amount }
        event.to?.let { shownCash[it] = (shownCash[it] ?: 0) + event.amount }
        if (event.reason in QUIET) {
            render()
            return
        }
        if (!mine) {
            // Everyone sees who paid whom (rent, taxes, fees, fines).
            val payer = event.from?.let { m.player(it)?.name }
            if (payer != null && event.reason in PAYMENTS) {
                val tileName = event.tile?.let { CityBaazarBoard.tile(it).name }
                val reason = tileName ?: reasonLabel(event.reason)
                showToast(CityBaazarUiMapper.paymentToast(payer, event.to?.let { m.player(it)?.name }, reason, event.amount))
            }
            render()
            return
        }
        val gain = event.to == mySeat
        val tileName = event.tile?.let { CityBaazarBoard.tile(it).name }.orEmpty()
        val other = (if (gain) event.from else event.to)?.let { m.player(it)?.name }.orEmpty()
        val (title, label) = when (event.reason) {
            MoneyReason.RENT -> if (gain) Strings.cbPaidYouRent(other, tileName) to Strings.cbRentFrom(other)
            else Strings.cbLandedOnOwned(tileName, other) to Strings.cbRentPaidTo(other)
            MoneyReason.TAX -> Strings.cbTaxTitle to tileName
            MoneyReason.CLUB -> Strings.cbClubTitle to Strings.cbCornerClub
            MoneyReason.START -> Strings.cbStartTitle to Strings.cbCornerStart
            MoneyReason.JAIL_FINE -> Strings.cbJailFineTitle to Strings.cbCornerJail
            MoneyReason.LATE_ROLL -> Strings.cbLateFineTitle to Strings.cbLateFineLabel
            MoneyReason.AUCTION -> Strings.cbAuctionEnded to tileName
            else -> (cardTitle ?: Strings.cbChange) to Strings.cbChange
        }
        showToast(CityBaazarUiMapper.moneyToast(title, before, label, event.amount, gain))
        delay(AppConstants.CB_MONEY_PAUSE_MS)
    }

    private fun reasonLabel(reason: MoneyReason): String = when (reason) {
        MoneyReason.RENT -> Strings.cbRentLand
        MoneyReason.TAX -> Strings.cbTaxTitle
        MoneyReason.CLUB -> Strings.cbClubTitle
        MoneyReason.JAIL_FINE -> Strings.cbJailFineTitle
        MoneyReason.LATE_ROLL -> Strings.cbLateFineLabel
        else -> Strings.cbChange
    }

    private suspend fun card(event: CityBaazarEvent.Card, m: CityBaazarMatch) {
        val spec = CityBaazarCards.find(event.cardId) ?: return
        val deck = if (spec.deck == CardDeck.LUCK) Strings.cbLuckLabel else Strings.cbChestLabel
        if (event.seat == m.me?.seat) {
            cardTitle = Strings.cbCardTitle(deck, spec.text)
            showToast(CityBaazarUiMapper.noteToast(deck, spec.text))
        } else {
            showToast(CityBaazarUiMapper.noteToast(Strings.cbSomeoneDrew(m.player(event.seat)?.name.orEmpty(), deck), spec.text))
        }
        delay(AppConstants.CB_CARD_PAUSE_MS)
    }

    /** Shows the server's table as it is (after any animation). */
    private fun reset(newMatch: CityBaazarMatch) {
        val justFinished = newMatch.phase == CityBaazarTurnPhase.FINISHED && match?.phase != CityBaazarTurnPhase.FINISHED
        match = newMatch
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
        if (newMatch.phase != CityBaazarTurnPhase.DEBT) saleSelection = emptySet()
        if (justFinished) {
            sound(GameSound.WIN)
            userModal = CityBaazarUiMapper.result(newMatch)
        }
        render()
    }

    private fun render() {
        val m = match ?: return
        val me = m.me
        val now = clock.now()
        val myTurn = me != null && !playing && !me.bankrupt && m.currentSeat == me.seat && !me.autoplay && me.presence == CityBaazarPresence.PLAYING
        val remainingMs = m.turnEndsAt?.let { (it - now).inWholeMilliseconds.coerceAtLeast(0) } ?: 0L
        val countdown = CityBaazarUiMapper.countdown(remainingMs / MILLIS)
        val cards = CityBaazarUiMapper.cards(
            m,
            CityBaazarUiMapper.CardInputs(speaking, mutedForMe, if (playing) null else remainingMs, CITY_BAAZAR_TURN_SECONDS, shownCash),
        )
        val (myCard, left, right) = CityBaazarUiMapper.sides(m, cards)
        val colourOf: (Int) -> CityBaazarColour? = { seat -> m.player(seat)?.colour }
        val shownMe = me?.copy(cash = shownCash[me.seat] ?: me.cash)
        // The user's turn decision, as a balance-sheet dialog.
        val decision: CbModal? = if (myTurn && shownMe != null) {
            when (m.phase) {
                CityBaazarTurnPhase.BUY -> m.pendingTile?.let { CityBaazarUiMapper.buy(shownMe, it, countdown) }
                CityBaazarTurnPhase.BUILD -> m.pendingTile?.let { CityBaazarUiMapper.build(m, shownMe, it, countdown) }
                CityBaazarTurnPhase.JAIL_ROLL -> CityBaazarUiMapper.jailRoll(m, countdown)
                CityBaazarTurnPhase.JAIL_CHOICE -> CityBaazarUiMapper.jailChoice(shownMe, countdown)
                CityBaazarTurnPhase.DEBT -> CityBaazarUiMapper.debt(m, shownMe, saleSelection, countdown)
                else -> null
            }
        } else {
            null
        }
        // A running auction shows to every player (not to watchers, nor after passing it).
        val auction = m.auction?.takeIf { !playing && shownMe != null && !shownMe.bankrupt && it.tile != passedAuction }?.let { a ->
            CityBaazarUiMapper.auction(m, a, shownMe!!, CityBaazarUiMapper.countdown((a.endsAt - now).inWholeSeconds))
        }
        val modal = vote?.let { CityBaazarModerationUiMapper.vote(it, now) } ?: auction ?: decision ?: userModal
        setState {
            copy(
                loaded = true,
                title = Strings.cbTitle,
                subtitle = m.roundLimit?.let { limit -> m.title + " · " + Strings.cbRoundOf(m.round, limit) } ?: m.title,
                audience = CityBaazarUiMapper.audience(m, speaking, mutedForMe),
                roomMatch = m.isRoomMatch,
                spectatorCount = m.spectatorCount.toString(),
                spectatorLabel = Strings.ludoSpectators,
                elapsed = if (m.phase == CityBaazarTurnPhase.FINISHED && elapsed.isNotEmpty()) elapsed else CityBaazarUiMapper.elapsed(now - m.startedAt),
                tiles = CityBaazarUiMapper.tiles(shownOwners, shownLevels, colourOf),
                tokens = CityBaazarUiMapper.tokens(m, shownPositions),
                highlight = when (modal) {
                    is CbModal.Properties -> modal.cards.map { it.tile }.toSet()
                    is CbModal.TileInfo -> setOf(modal.card.tile)
                    is CbModal.Buy -> setOf(modal.tile)
                    is CbModal.Build -> setOf(modal.tile)
                    is CbModal.Auction -> setOf(modal.tile)
                    is CbModal.PlayerInfo -> modal.cards.map { it.tile }.toSet()
                    else -> emptySet()
                },
                dice = CbDiceUi(die1, die2, rollId, diceSeat?.let(colourOf)),
                myCard = myCard,
                leftCards = left,
                rightCards = right,
                stacks = CityBaazarUiMapper.stacks(m),
                spectating = me == null,
                canRoll = myTurn && (m.phase == CityBaazarTurnPhase.ROLL || m.phase == CityBaazarTurnPhase.JAIL_ROLL),
                rollLabel = if (m.phase == CityBaazarTurnPhase.JAIL_ROLL && myTurn) Strings.cbRollDoubles else Strings.cbRollDice,
                autoplay = me?.autoplay == true || me?.presence == CityBaazarPresence.BOT,
                micOn = me?.micOn == true,
                toast = toast,
                modal = modal,
            )
        }
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

    private fun showModal(modal: CbModal?) {
        userModal = modal
        render()
    }

    private fun showSettings() {
        val s = audio.settings.value
        showModal(CbModal.Settings(effectsOn = s.effectsOn, musicOn = s.musicOn))
    }

    private fun showReport(selected: String?, error: String?) {
        val m = match ?: return
        val voters = CityBaazarModerationUiMapper.voters(m)
        showModal(
            CbModal.Report(
                people = CityBaazarModerationUiMapper.reportablePeople(m),
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
        when (moderation.report(m.matchId, person.userId, person.name, CityBaazarModerationUiMapper.voters(m))) {
            is AppResult.Success -> showModal(null)
            is AppResult.Failure -> showReport(report.selectedUserId, Strings.ludoReportNeedPeople)
        }
    }

    private fun showPenalty(watchingAd: Boolean) {
        val p = penalty ?: return
        showModal(CityBaazarModerationUiMapper.penalty(p, clock.now(), watchingAd))
    }

    private suspend fun watchAd() {
        showPenalty(watchingAd = true)
        if (moderation.clearPenaltyWithAd() is AppResult.Failure) showPenalty(watchingAd = false)
    }

    /** Background: sound stops, the mic goes off, and after a short grace the server plays for the user. */
    private suspend fun onScreenVisible(visible: Boolean) {
        foreground = visible
        awayJob?.cancel()
        if (visible) {
            audio.startMusic()
            if (markedAway) {
                markedAway = false
                send(CityBaazarCommand.SetAway(false))
            }
        } else {
            audio.stopMusic()
            if (state.value.micOn) send(CityBaazarCommand.SetMic(false))
            awayJob = scope.launch {
                delay(AppConstants.LUDO_AWAY_GRACE_MS)
                markedAway = true
                send(CityBaazarCommand.SetAway(true))
            }
        }
    }

    private fun sound(sound: GameSound) {
        if (foreground) audio.play(sound)
    }

    private suspend fun send(command: CityBaazarCommand) {
        val id = matchId ?: return
        sendCommand(id, command)
    }

    private companion object {
        const val MILLIS = 1_000L
        /** Money the dialogs already showed (buying, building, selling). */
        val QUIET = setOf(MoneyReason.BUY, MoneyReason.BUILD, MoneyReason.SELL, MoneyReason.BANKRUPTCY)
        /** Payments everyone is told about. */
        val PAYMENTS = setOf(MoneyReason.RENT, MoneyReason.TAX, MoneyReason.CLUB, MoneyReason.JAIL_FINE, MoneyReason.LATE_ROLL, MoneyReason.CARD, MoneyReason.AUCTION)
    }
}
