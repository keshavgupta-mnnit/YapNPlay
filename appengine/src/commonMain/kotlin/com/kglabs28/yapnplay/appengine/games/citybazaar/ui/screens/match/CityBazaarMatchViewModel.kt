package com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match

import com.kglabs28.yapnplay.appengine.features.seathandover.presentation.SeatHandoverAction
import com.kglabs28.yapnplay.appengine.features.seathandover.presentation.SeatHandoverController
import com.kglabs28.yapnplay.appengine.features.voice.domain.MySpeech
import com.kglabs28.yapnplay.appengine.features.voice.domain.MicGate
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.CityBazaarAuctionInfo
import com.kglabs28.yapnplay.appengine.features.voice.domain.VoiceNeed
import com.kglabs28.yapnplay.appengine.features.voice.usecase.TableVoiceUseCases
import com.kglabs28.yapnplay.appengine.features.seathandover.presentation.LeaveDialogState
import com.kglabs28.yapnplay.appengine.ui.components.leaderboard.LeaderboardResult
import com.kglabs28.yapnplay.appengine.features.matchresult.domain.SeatResult
import com.kglabs28.yapnplay.appengine.features.matchresult.usecase.ObserveMatchCoinsUseCase
import com.kglabs28.yapnplay.appengine.core.common.TextUtils
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.mapper.CbFormat
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.mapper.CbNoticeMapper
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.mapper.CbPromptMapper
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.mapper.CbPropertyMapper
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.mapper.CbResultMapper
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.mapper.CbTableMapper
import com.kglabs28.yapnplay.appengine.core.ui.TableStrings
import com.kglabs28.yapnplay.appengine.core.ui.LudoStrings
import com.kglabs28.yapnplay.appengine.core.ui.CityBazaarStrings
import com.kglabs28.yapnplay.appengine.appmanager.helpers.GamesAppManagerHelper
import com.kglabs28.yapnplay.appengine.appmanager.helpers.CityBazaarAppManagerHelper
import kotlin.time.Duration.Companion.milliseconds
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbMoneyFlightUi
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
import com.kglabs28.yapnplay.appengine.features.moderation.domain.ModerationRules
import com.kglabs28.yapnplay.appengine.features.moderation.domain.Penalty
import com.kglabs28.yapnplay.appengine.features.moderation.domain.ReportVoteRequest
import com.kglabs28.yapnplay.appengine.features.moderation.usecase.ModerationUseCases
import com.kglabs28.yapnplay.appengine.features.voice.usecase.ObserveMySpeakingUseCase
import com.kglabs28.yapnplay.appengine.features.voice.usecase.SetMicOpenUseCase
import com.kglabs28.yapnplay.appengine.features.voice.usecase.ObserveMicOnUseCase
import com.kglabs28.yapnplay.appengine.features.voice.usecase.SetMicOnUseCase
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
    private val voice: TableVoiceUseCases,
    private val observeMySpeaking: ObserveMySpeakingUseCase,
    private val setMicOpen: SetMicOpenUseCase,
    private val observeMicOn: ObserveMicOnUseCase,
    private val setMicOn: SetMicOnUseCase,
    private val moderation: ModerationUseCases,
    private val playerFriends: PlayerFriendUseCases,
    private val audio: GameAudio,
    private val cbConfig: CityBazaarAppManagerHelper,
    private val gamesConfig: GamesAppManagerHelper,
    private val observeMatchCoins: ObserveMatchCoinsUseCase,
    private val clock: AppClock,
    private val historyCache: CbHistoryCache,
    private val seats: SeatHandoverController,
    dispatchers: DispatcherProvider,
) : MviViewModel<CityBazaarMatchState, CityBazaarMatchIntent, CityBazaarMatchEffect>(CityBazaarMatchState(), dispatchers) {

    private var matchId: String? = null
    /** Voice only while another person is here and someone's mic is on. */
    private val voiceNeed = VoiceNeed { wanted -> matchId?.let { voice.setVoiceWanted.match(it, wanted) } }
    private var match: CityBazaarMatch? = null
    private var speaking: Set<String> = emptySet()
    /** The user speaking into their mic, and whether that mic is open (on at the table, app in front). */
    private var mySpeech = MySpeech.SILENT
    /** Who at the table is talking with their mic off (from its snapshots). */
    private var mutedTalkers: Set<String> = emptySet()
    /** The app-wide mic switch the table follows. */
    private var micSwitch = false
    private val micGate = MicGate { open, listenMuted -> setMicOpen(open, listenMuted) }
    private var mutedForMe: Set<String> = emptySet()
    private var speakerOn = true
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
    /** The shared exit dialog (who takes the seat, its countdown). */
    private val leave = LeaveDialogState()
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
    /** The auction as it stood when the user passed: hidden until someone bids again. */
    private var passedAuction: CityBazaarAuctionInfo? = null
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
    /** The match was just ended by vote: its feed line shows a moment before the board. */
    private var endedByVote = false
    /** The coins and XP MatchEnded paid (it arrives just after the last snapshot). */
    private var seatResults: List<SeatResult> = emptyList()
    /** The deck of the last card drawn (its money event names it). */
    private var lastDeck: String? = null
    private var lastCardText: String? = null
    /** A roll not told yet (it joins the next line of the history). */
    private var pendingRoll: TableFeedLineUi? = null
    private var pendingDice: CityBazaarEvent.Rolled? = null
    private val turnAlarm = TurnAlarm(hurrySeconds = { gamesConfig.turnHurrySeconds() }) { sound(it) }
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
                val picked = saleSelection.sortedBy { if (CbPromptMapper.isBuilding(it)) 0 else 1 }
                saleSelection = emptySet()
                picked.forEach { id ->
                    val tile = CbPromptMapper.assetTile(id)
                    send(if (CbPromptMapper.isBuilding(id)) CityBazaarCommand.SellBuilding(tile) else CityBazaarCommand.SellTile(tile))
                }
            }
            CityBazaarMatchIntent.StartAuction -> saleSelection.singleOrNull()?.takeIf { !CbPromptMapper.isBuilding(it) }?.let {
                saleSelection = emptySet()
                send(CityBazaarCommand.StartAuction(CbPromptMapper.assetTile(it)))
            }
            CityBazaarMatchIntent.Bid -> send(CityBazaarCommand.Bid)
            CityBazaarMatchIntent.PassAuction -> {
                passedAuction = match?.auction
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
                    else -> if (CbPropertyMapper.tileInfo(m, intent.tile) != null) showPanel(tile = intent.tile, seat = null)
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
            CityBazaarMatchIntent.ToggleSpeaker -> voice.setSpeakerOn(!speakerOn)
            is CityBazaarMatchIntent.ToggleHearSpectator -> voice.setMuted(intent.userId, intent.userId !in mutedForMe)
            is CityBazaarMatchIntent.ToggleHear -> match?.player(intent.seat)?.let { voice.setMuted(it.userId, it.userId !in mutedForMe) }
            // Not the user, a CPU or a seat the CPU plays for someone who left.
            // A bot's seat: the leader may offer it to a spectator. Another person: add (or remove) as a friend.
            is CityBazaarMatchIntent.PlayerLongPressed -> match?.player(intent.seat)?.let { p ->
                when {
                    p.isCpu || p.presence == CityBazaarPresence.BOT -> seat(SeatHandoverAction.OpenBotSeatOffer(p.seat))
                    !p.isMe -> openFriendAction(p.userId, p.name, p.avatarId)
                }
            }
            CityBazaarMatchIntent.ConfirmFriendAction -> confirmFriendAction()
            CityBazaarMatchIntent.DismissFriendAction -> setState { copy(friendAction = null) }
            CityBazaarMatchIntent.OpenBank -> showPanel(bank = true)
            is CityBazaarMatchIntent.VoteExtension -> send(CityBazaarCommand.VoteExtension(intent.keepPlaying))
            // Asks first, as at every table; the vote starts on End game.
            CityBazaarMatchIntent.ProposeEnd -> showModal(CbModal.ConfirmEnd)
            CityBazaarMatchIntent.ConfirmEnd -> {
                showModal(null)
                send(CityBazaarCommand.ProposeEnd)
            }
            is CityBazaarMatchIntent.VoteEnd -> send(CityBazaarCommand.VoteEnd(intent.agree))
            CityBazaarMatchIntent.OpenHistory -> {
                historyOpen = true
                render()
            }
            CityBazaarMatchIntent.DismissHistory -> {
                historyOpen = false
                render()
            }
            CityBazaarMatchIntent.ExitClicked -> {
                leave.open(clock.now())
                render()
            }
            is CityBazaarMatchIntent.Seat -> seat(intent.action)
            is CityBazaarMatchIntent.PickSeatTaker -> {
                leave.pick(intent.index)
                render()
            }
            CityBazaarMatchIntent.ConfirmLeave -> {
                // Only a player has a seat to hand on (to the picked spectator, or a CPU).
                val giveTo = leave.giveTo(match?.spectators?.map { it.userId }.orEmpty())
                leave.close()
                showModal(null)
                if (match?.me != null) send(CityBazaarCommand.Leave(giveTo))
                emitEffect(CityBazaarMatchEffect.Close)
            }
            CityBazaarMatchIntent.DismissModal -> if (matchGone) {
                emitEffect(CityBazaarMatchEffect.Close)
            } else {
                leave.close()
                showModal(null)
            }
            CityBazaarMatchIntent.OpenSettings -> showSettings()
            CityBazaarMatchIntent.ToggleEffects -> {
                audio.setEffectsOn(!audio.settings.value.effectsOn)
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
            CityBazaarMatchIntent.ShareResult -> leaderboard?.let { emitEffect(CityBazaarMatchEffect.ShareText(TableStrings.leaderboardShareText(it.game, it.myPlace))) }
        }
    }

    /** The backend no longer has this match; closing the message closes the table. */
    private var matchGone = false

    private fun load(id: String) {
        if (matchId == id) return
        matchId = id
        // Back at this match (a rejoin): its history so far, not an empty list.
        history.clear()
        history.addAll(historyCache.get(id))
        feedId = history.maxOfOrNull { it.id } ?: feedId
        jobs.forEach { it.cancel() }
        jobs = listOf(
            scope.launch {
                try {
                    observeMatch(id).collect { queue.send(it) }
                } catch (e: MatchGoneException) {
                    // The server lost the match (e.g. it restarted): say so, then close the table.
                    matchGone = true
                    showModal(CbModal.ReportOutcome(TableStrings.matchGoneTitle, TableStrings.matchGoneBody))
                }
            },
            scope.launch { for (update in queue) play(update) },
            scope.launch { observeMatchCoins().collect { seatResults = it; leaderboard = leaderboard?.let { b -> LeaderboardResult.withResult(b, it) }; render() } },
            scope.launch { observeSpeaking(id).collect { speaking = it; render() } },
            scope.launch { voice.observeMutedTalkers(id).collect { mutedTalkers = it; render() } },
            scope.launch { observeMySpeaking().collect { mySpeech = it; render() } },
            scope.launch { observeMicOn().collect { micSwitch = it; syncMic() } },
            scope.launch { voice.observeMuted().collect { mutedForMe = it; render() } },
            scope.launch { voice.observeSpeakerOn().collect { speakerOn = it; render() } },
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
        if (update.events.any { it is CityBazaarEvent.EndVoted && it.ended }) endedByVote = true
        // First snapshot, or in the background: straight to the latest table.
        if (match == null || !foreground) {
            reset(update.match)
            return
        }
        playing = true
        val m = update.match
        var breathed = 0L
        for ((index, event) in update.events.withIndex()) {
            // Club / Rest House: the lander and every other player, told once as one move.
            if (event is CityBazaarEvent.Money && clubLander(event) != null) {
                if (index > 0 && sameClubRun(update.events[index - 1], event)) continue
                val run = update.events.drop(index).takeWhile { sameClubRun(it, event) }.filterIsInstance<CityBazaarEvent.Money>()
                if (run.size > 1) {
                    clubRun(run, m)
                    if (breathed < cbConfig.eventBreathMaxMs()) {
                        delay(cbConfig.eventBreathMs())
                        breathed += cbConfig.eventBreathMs()
                    }
                    continue
                }
            }
            if (event is CityBazaarEvent.Card) {
                lastDeck = CityBazaarFeedMapper.deckLabel(event.cardId)
                lastCardText = CityBazaarCards.find(event.cardId)?.text
            }
            var line = runCatching { CityBazaarFeedMapper.line(event, m, lastDeck) }
                .onFailure { CrashReporter.record(it, "CityBazaarFeed.line") }
                .getOrNull()
            // History: one line per move. A roll waits and is told at the end of what it led to
            // ("… rent for Pune (rolled 4 + 3)"); a roll that led to nothing gets its own line.
            when {
                event is CityBazaarEvent.Rolled -> {
                    flushRoll()
                    pendingRoll = line
                    pendingDice = event
                    line = null
                }
                event is CityBazaarEvent.TurnPassed -> flushRoll()
                line != null && pendingDice != null -> {
                    pendingDice?.let { dice -> line = line?.let { it.copy(text = it.text + CityBazaarStrings.cbFeedRollSuffix(dice.die1, dice.die2)) } }
                    pendingRoll = null
                    pendingDice = null
                }
            }
            // One message per event: the user's own as a popup (when on), everyone else's as the
            // bottom snackbar; all in the history. Then a short breath before the next one.
            val mine = involvesMe(event, m)
            val popup = mine && m.moneyPopups
            if (popup) CbNoticeMapper.notice(m, event)?.let { showTransaction(it) }
            line?.let { feed(if (popup) it.copy(quiet = true) else it) }
            val shown = popup && (event is CityBazaarEvent.Money || CbNoticeMapper.notice(m, event) != null) || (line != null && !line.quiet && !popup)
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
                        CityBazaarStrings.cbAuctionWon(m.player(w)?.name.orEmpty(), name, CbFormat.rupees(event.amount))
                    } ?: CityBazaarStrings.cbAuctionBank(name, CbFormat.rupees(event.amount))
                    passedAuction = null
                }
                else -> Unit
            }
            // Same as the server's pause: 2.5 s per message, at most 8 s per update.
            if (shown && breathed < cbConfig.eventBreathMaxMs()) {
                delay(cbConfig.eventBreathMs())
                breathed += cbConfig.eventBreathMs()
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
        if (mine && m.moneyPopups) {
            CbNoticeMapper.transaction(m, event, seat, before, line, lastDeck, lastCardText)?.let { showTransaction(it) }
        } else if (mine && event.reason != MoneyReason.BANKRUPTCY) {
            // Popups off: the amount flies from the payer to the receiver.
            transactionId++
            transactionJob?.cancel()
            transaction = null
            moneyFlight = CbMoneyFlightUi(transactionId, event.from, event.to, CbFormat.rupees(event.amount))
            transactionJob = scope.launch {
                delay(AppConstants.CB_MONEY_FLIGHT_MS)
                moneyFlight = null
                render()
            }
        }
        render()
    }

    /** Club: the lander pays everyone; Rest House: everyone pays the lander. Null for any other money. */
    private fun clubLander(event: CityBazaarEvent.Money): Int? = when (event.reason) {
        MoneyReason.CLUB -> event.from?.takeIf { event.to != null }
        MoneyReason.REST -> event.to?.takeIf { event.from != null }
        else -> null
    }

    /** [other] is part of the same Club / Rest House move as [first]. */
    private fun sameClubRun(other: CityBazaarEvent, first: CityBazaarEvent.Money): Boolean =
        other is CityBazaarEvent.Money && other.reason == first.reason && other.tile == first.tile && clubLander(other) == clubLander(first)

    /**
     * One Club / Rest House move ([run]: one payment per other player) as a
     * single message: the money moves on every card at once, the history and
     * snackbar say "X paid all N players" (or "all N paid X") once, and the
     * user gets one popup — the total when they landed, their own share
     * otherwise.
     */
    private fun clubRun(run: List<CityBazaarEvent.Money>, m: CityBazaarMatch) {
        val first = run.first()
        val club = first.reason == MoneyReason.CLUB
        val lander = clubLander(first) ?: return
        val mySeat = m.me?.seat
        val before = mySeat?.let { shownCash[it] } ?: 0
        run.forEach { e ->
            e.from?.let { shownCash[it] = (shownCash[it] ?: 0) - e.amount }
            e.to?.let { shownCash[it] = (shownCash[it] ?: 0) + e.amount }
        }
        val name = m.player(lander)?.name.orEmpty()
        val each = CbFormat.rupees(first.amount)
        val total = run.sumOf { it.amount }
        var text = if (club) CityBazaarStrings.cbFeedClubAll(name, each, run.size) else CityBazaarStrings.cbFeedRestAll(name, each, run.size)
        pendingDice?.let { dice -> text += CityBazaarStrings.cbFeedRollSuffix(dice.die1, dice.die2) }
        pendingRoll = null
        pendingDice = null
        val myShare = run.firstOrNull { it.from == mySeat || it.to == mySeat }
        val popup = myShare != null && m.moneyPopups
        if (popup && mySeat == lander) {
            val gain = !club
            showTransaction(
                CbTransactionUi(
                    id = 0,
                    title = if (club) CityBazaarStrings.cbTitleClub else CityBazaarStrings.cbTitleRestHouse,
                    line = if (club) CityBazaarStrings.cbYouClubAll(each, run.size, CbFormat.rupees(total)) else CityBazaarStrings.cbYouRestAll(each, run.size, CbFormat.rupees(total)),
                    status = if (gain) CityBazaarStrings.cbTxCrediting else CityBazaarStrings.cbTxDeducting,
                    fromAvatarId = if (gain) null else m.player(lander)?.avatarId,
                    toAvatarId = if (gain) m.player(lander)?.avatarId else null,
                    amount = CbFormat.rupees(total),
                    cash = CbFormat.rupees(before),
                    changeLabel = if (gain) CityBazaarStrings.cbTxReceivedLabel else CityBazaarStrings.cbTxPaidLabel,
                    change = if (gain) CbFormat.plus(total) else CbFormat.minus(total),
                    remaining = CbFormat.rupees(if (gain) before + total else before - total),
                    gain = gain,
                ),
            )
        } else if (popup && myShare != null) {
            CbNoticeMapper.transaction(m, myShare, mySeat, before, null, lastDeck, lastCardText)?.let { showTransaction(it) }
        }
        runCatching { CityBazaarFeedMapper.line(first, m, lastDeck) }.getOrNull()?.let { feed(CityBazaarFeedMapper.reworded(it, text, m).copy(quiet = popup)) }
        render()
    }

    /** Shows [tx] as the popup for a few seconds (a newer one replaces it). */
    private fun showTransaction(tx: CbTransactionUi) {
        transactionId++
        transaction = tx.copy(id = transactionId)
        transactionJob?.cancel()
        transactionJob = scope.launch {
            delay(cbConfig.popupMs())
            transaction = null
            render()
        }
        render()
    }

    private fun reasonLabel(reason: MoneyReason): String = when (reason) {
        MoneyReason.RENT -> CityBazaarStrings.cbRentLand
        MoneyReason.TAX -> CityBazaarStrings.cbTaxTitle
        MoneyReason.CLUB -> CityBazaarStrings.cbClubTitle
        MoneyReason.JAIL_FINE -> CityBazaarStrings.cbJailFineTitle
        MoneyReason.LATE_ROLL -> CityBazaarStrings.cbLateFineLabel
        else -> CityBazaarStrings.cbChange
    }

    private suspend fun card(event: CityBazaarEvent.Card, m: CityBazaarMatch) {
        val spec = CityBazaarCards.find(event.cardId) ?: return
        val deck = if (spec.deck == CardDeck.LUCK) CityBazaarStrings.cbLuckLabel else CityBazaarStrings.cbChestLabel
        if (event.seat == m.me?.seat) {
            cardTitle = CityBazaarStrings.cbCardTitle(deck, spec.text)
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
                feed(TableFeedLineUi(0, CityBazaarStrings.cbFeedStarted, TableFeedTone.INFO))
            } else if (!waitAnnounced) {
                waitAnnounced = true
                feed(TableFeedLineUi(0, CityBazaarStrings.cbFeedWaiting, TableFeedTone.INFO))
            }
        }
        if (justFinished) {
            sound(GameSound.WIN)
            // Ended by vote: the feed's "ended by vote" line shows first, then the board.
            val board = CbResultMapper.leaderboard(newMatch, CbFormat.elapsed(clock.now() - newMatch.startedAt)).let { LeaderboardResult.withResult(it, seatResults) }
            if (endedByVote) {
                scope.launch {
                    delay(END_NOTICE_MS)
                    leaderboard = board.let { LeaderboardResult.withResult(it, seatResults) }
                    render()
                }
            } else {
                leaderboard = board
            }
        }
        render()
    }

    private fun render() {
        val m = match ?: return
        val me = m.me
        if (autoplayWanted != null && autoplayWanted == (me?.let { it.autoplay || it.presence == CityBazaarPresence.BOT } == true)) autoplayWanted = null
        // The mic listens only while it's on at the table and the app is in front.
        micGate.update(micOn = me?.micOn == true, inFront = foreground)
        voiceNeed.update(micGate.open, speakerOn, m.players.filter { !it.isMe && !it.isCpu && !it.removed }.map { it.micOn } + m.spectators.filter { it.userId != m.viewerUserId }.map { it.micOn })
        // Others' talking comes from the server; the user's own from their mic.
        val talkers = if (mySpeech == MySpeech.SPEAKING) speaking + m.viewerUserId else speaking
        // Talking with the mic off: others' from the table, the user's own from their mic at once.
        val greyTalkers = if (mySpeech == MySpeech.SPEAKING_MUTED) mutedTalkers + m.viewerUserId else mutedTalkers
        val now = clock.now()
        val myTurn = me != null && !playing && !me.bankrupt && m.currentSeat == me.seat && !me.autoplay && me.presence == CityBazaarPresence.PLAYING
        val remainingMs = m.turnEndsAt?.let { (it - now).inWholeMilliseconds.coerceAtLeast(0) } ?: 0L
        val countdown = CbFormat.countdown(remainingMs / MILLIS)
        turnAlarm.update(myTurn, remainingMs)
        val cards = CbTableMapper.cards(
            m,
            CbTableMapper.CardInputs(talkers, mutedForMe, if (playing) null else remainingMs, CITY_BAZAAR_TURN_SECONDS, shownCash, greyTalkers),
        )
        val (myCard, left, right) = CbTableMapper.sides(m, cards)
        val colourOf: (Int) -> CityBazaarColour? = { seat -> m.player(seat)?.colour }
        val shownMe = me?.copy(cash = shownCash[me.seat] ?: me.cash)
        // The user's turn decision, as a balance-sheet dialog.
        val decision: CbModal? = if (myTurn && shownMe != null) {
            when (m.phase) {
                CityBazaarTurnPhase.BUY -> m.pendingTile?.let { CbPromptMapper.buy(shownMe, it, countdown) }
                CityBazaarTurnPhase.BUILD -> m.pendingTile?.let { CbPromptMapper.build(m, shownMe, it, countdown) }
                CityBazaarTurnPhase.JAIL_ROLL -> CbPromptMapper.jailRoll(m, shownMe, countdown)
                CityBazaarTurnPhase.JAIL_CHOICE -> CbPromptMapper.jailChoice(shownMe, countdown)
                CityBazaarTurnPhase.DEBT -> CbPromptMapper.debt(m, shownMe, saleSelection, countdown)
                else -> null
            }
        } else {
            null
        }
        // A running auction asks every player who can bid higher (not the owner, the highest bidder, anyone short
        // of the next bid, or watchers); a pass hides it only until the next bid.
        val auction = m.auction?.takeIf { !playing && shownMe != null && !shownMe.bankrupt && !passedOn(it) }?.let { a ->
            CbPromptMapper.auction(m, a, shownMe!!, CbFormat.countdown((a.endsAt - now).inWholeSeconds))
        }
        // Rounds are over: everyone sees the continue-or-end vote (only players still in can vote).
        val extension = m.extensionVote?.takeIf { m.phase == CityBazaarTurnPhase.EXTENSION_VOTE && !playing }?.let {
            CbPromptMapper.extensionVote(m, it, CbFormat.countdown((it.endsAt - now).inWholeSeconds.coerceAtLeast(0)))
        }
        // Someone asked to end the game: everyone sees it while play is paused.
        val endVote = m.endVote?.let { CbPromptMapper.endVote(m, it, now) }
        val leaving = leave.ui(now, m.spectators.map { it.name }, m.me != null, roomMatch = m.isRoomMatch)?.let { CbModal.LeaveConfirm(it) }
        val modal = vote?.let { CityBazaarModerationUiMapper.vote(it, now) } ?: leaving ?: endVote ?: auction ?: extension ?: decision
            ?: seats.modal(CbTableMapper.seatTable(m), now)?.let { CbModal.Seat(it) } ?: userModal
        val panel = panelTile?.let { CbPropertyMapper.tileInfo(m, it) }
            ?: panelSeat?.let { CbPropertyMapper.playerPanel(m, it, shownCash[it] ?: 0) }
            ?: panelDeck?.let { CbPropertyMapper.cardTable(it) }
            ?: (if (panelCities) CbPropertyMapper.properties(m, panelGroup) else null)
            ?: if (panelBank) CbPropertyMapper.bankPanel(m) else null
        setState {
            copy(
                loaded = true,
                title = CityBazaarStrings.cbTitle,
                subtitle = TableStrings.tableSubtitle(
                    listOfNotNull(
                        m.title,
                        m.roundLimit?.let { limit -> CityBazaarStrings.cbRoundOf(m.round, limit) },
                    ),
                ),
                audience = CbTableMapper.audience(m, talkers, mutedForMe, greyTalkers),
                roomMatch = m.isRoomMatch,
                spectatorCount = m.spectatorCount.toString(),
                elapsed = if (m.phase == CityBazaarTurnPhase.FINISHED && elapsed.isNotEmpty()) elapsed else CbFormat.elapsed(now - m.startedAt),
                tiles = CbTableMapper.tiles(shownOwners, shownLevels, colourOf),
                tokens = CbTableMapper.tokens(m, shownPositions),
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
                stacks = CbTableMapper.stacks(m),
                spectating = me == null,
                canRoll = myTurn && (m.phase == CityBazaarTurnPhase.ROLL || m.phase == CityBazaarTurnPhase.JAIL_ROLL),
                rollLabel = if (m.phase == CityBazaarTurnPhase.JAIL_ROLL && myTurn) CityBazaarStrings.cbRollDoubles else CityBazaarStrings.cbRollDice,
                // What the user just set wins until the server agrees (no waiting on the round trip).
                autoplay = autoplayWanted ?: (me?.autoplay == true || me?.presence == CityBazaarPresence.BOT),
                // The app-wide mic switch, at once (the server catches up through syncMic).
                // The app-wide mic switch, at once (the server catches up); a spectator's too.
                micOn = micSwitch,
                speakerOn = speakerOn,
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
        matchId?.let { historyCache.put(it, history.toList()) }
        if (!item.quiet) {
            feedSnack = item
            feedJob?.cancel()
            feedJob = scope.launch {
                delay(cbConfig.snackbarMs())
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
        // Ending by vote: a player still at the table, playing for themselves, while nothing else is being decided.
        val m = match
        val me = m?.me
        val canProposeEnd = m != null && me != null && !me.isCpu && !me.bankrupt && me.presence == CityBazaarPresence.PLAYING &&
            m.phase != CityBazaarTurnPhase.FINISHED && m.phase != CityBazaarTurnPhase.EXTENSION_VOTE && m.auction == null && m.endVote == null
        showModal(CbModal.Settings(effectsOn = s.effectsOn, musicOn = s.musicOn, canProposeEnd = canProposeEnd))
    }

    private fun showReport(selected: String?, error: String?) {
        val m = match ?: return
        val voters = CityBazaarModerationUiMapper.voters(m)
        showModal(
            CbModal.Report(
                people = CityBazaarModerationUiMapper.reportablePeople(m),
                selectedUserId = selected,
                warning = LudoStrings.ludoReportWarning(ModerationRules.requiredAgreements(voters)),
                error = error ?: if (ModerationRules.canReport(voters)) null else LudoStrings.ludoReportNeedPeople,
            ),
        )
    }

    private suspend fun submitReport() {
        val m = match ?: return
        val report = userModal as? CbModal.Report ?: return
        val person = report.people.firstOrNull { it.userId == report.selectedUserId } ?: return
        when (moderation.report(m.matchId, person.userId, person.name, CityBazaarModerationUiMapper.voters(m))) {
            is AppResult.Success -> showModal(null)
            is AppResult.Failure -> showReport(report.selectedUserId, LudoStrings.ludoReportNeedPeople)
        }
    }

    private fun showPenalty(watchingAd: Boolean) {
        val p = penalty ?: return
        showModal(CityBazaarModerationUiMapper.penalty(p, clock.now(), watchingAd, adOffered = watchingAd || moderation.isAdReady()))
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
        // A spectator's mic is on their spectator entry.
        val current = (m.me?.micOn ?: m.spectators.firstOrNull { it.userId == m.viewerUserId }?.micOn) ?: return
        val want = micSwitch && foreground
        if (current != want) scope.launch { send(CityBazaarCommand.SetMic(want)) }
    }

    override fun onCleared() {
        micGate.clear()
        voiceNeed.clear()
    }

    /** The user passed on [auction] and nobody has bid since (same city, same highest bid and bidder). */
    private fun passedOn(auction: CityBazaarAuctionInfo): Boolean =
        passedAuction?.let { it.tile == auction.tile && it.bid == auction.bid && it.leaderSeat == auction.leaderSeat } == true

    private fun sound(sound: GameSound) {
        if (foreground) audio.play(sound)
    }

    /** A seat dialog's answer (or a long press on a bot's seat), handled the same in every game. */
    private suspend fun seat(action: SeatHandoverAction) {
        val m = match ?: return
        val id = matchId ?: return
        if (seats.handle(id, CbTableMapper.seatTable(m), action)) render()
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
            is AppResult.Failure -> setState { copy(friendAction = action.copy(busy = false, error = TableStrings.playerFriendFailed)) }
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

    /** A roll that led to nothing to tell: its own history line. */
    private fun flushRoll() {
        pendingRoll?.let { feed(it) }
        pendingRoll = null
        pendingDice = null
    }
}

/** How long "ended by vote" shows before the final board. */
private const val END_NOTICE_MS = 2_500L
