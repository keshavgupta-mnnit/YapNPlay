package com.kglabs28.yapnplay.appengine.ui.screens.main

import com.kglabs28.yapnplay.appengine.core.common.YapNPlayError
import com.kglabs28.yapnplay.appengine.core.ui.ProfileStrings
import com.kglabs28.yapnplay.appengine.features.profile.presentation.ProfileUiMapper
import com.kglabs28.yapnplay.appengine.features.profile.usecase.SendFriendRequestUseCase
import kotlinx.coroutines.flow.first
import com.kglabs28.yapnplay.appengine.core.common.StartupGate
import com.kglabs28.yapnplay.appengine.ui.components.moderation.AppSettingsController
import kotlin.time.Duration.Companion.seconds
import com.kglabs28.yapnplay.appengine.core.common.AppLog
import com.kglabs28.yapnplay.appengine.features.coins.usecase.GetAdLimitStatusUseCase
import com.kglabs28.yapnplay.appengine.core.ads.LaunchCoinsOffer
import com.kglabs28.yapnplay.appengine.appmanager.helpers.AdsAppManagerHelper
import com.kglabs28.yapnplay.appengine.features.profile.usecase.CollectDailyBonusUseCase
import com.kglabs28.yapnplay.appengine.features.profile.usecase.ObserveDailyBonusUseCase
import com.kglabs28.yapnplay.appengine.ui.components.coins.CoinsUiMapper
import com.kglabs28.yapnplay.appengine.ui.components.coins.CoinsOfferUi
import com.kglabs28.yapnplay.appengine.core.ui.CoinsStrings
import com.kglabs28.yapnplay.appengine.features.coins.usecase.IsRewardedAdReadyUseCase
import com.kglabs28.yapnplay.appengine.features.coins.usecase.WatchAdForCoinsUseCase
import kotlinx.coroutines.delay
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.common.AppClock
import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.features.matchmaking.usecase.GetActiveMatchUseCase
import com.kglabs28.yapnplay.appengine.core.common.DispatcherProvider
import com.kglabs28.yapnplay.appengine.core.common.TextUtils
import com.kglabs28.yapnplay.appengine.core.mvi.MviViewModel
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.features.notifications.domain.AppNotification
import com.kglabs28.yapnplay.appengine.features.notifications.usecase.MarkNotificationsReadUseCase
import com.kglabs28.yapnplay.appengine.features.notifications.usecase.ObserveNotificationsUseCase
import com.kglabs28.yapnplay.appengine.features.profile.usecase.GetHomeScreenDataUseCase
import com.kglabs28.yapnplay.appengine.features.profile.usecase.ObserveMyProfileUseCase
import kotlinx.coroutines.launch

/**
 * Dashboard scaffold: owns the shared top bar data and the selected
 * tab. Profile loads once from the use case; the home cards switch
 * tabs; the bell shows the notification history and a tap opens
 * that room.
 */
class MainViewModel(
    private val getHomeScreenData: GetHomeScreenDataUseCase,
    observeMyProfile: ObserveMyProfileUseCase,
    observeNotifications: ObserveNotificationsUseCase,
    private val markNotificationsRead: MarkNotificationsReadUseCase,
    private val getActiveMatch: GetActiveMatchUseCase,
    private val watchAdForCoins: WatchAdForCoinsUseCase,
    private val observeDailyBonus: ObserveDailyBonusUseCase,
    private val adsConfig: AdsAppManagerHelper,
    private val launchCoinsOffer: LaunchCoinsOffer,
    private val getAdLimitStatus: GetAdLimitStatusUseCase,
    private val collectDailyBonus: CollectDailyBonusUseCase,
    private val isAdReady: IsRewardedAdReadyUseCase,
    private val clock: AppClock,
    private val settingsController: AppSettingsController,
    private val startupGate: StartupGate,
    private val sendFriendRequest: SendFriendRequestUseCase,
    dispatchers: DispatcherProvider,
) : MviViewModel<MainState, MainIntent, MainEffect>(MainState(), dispatchers) {

    private var history: List<AppNotification> = emptyList()

    init {
        // Name and avatar follow edits made on the Profile screen.
        scope.launch {
            observeMyProfile().collect { profile ->
                if (profile != null) setState { copy(userName = profile.name, avatarId = profile.avatarId) }
            }
        }
        // The daily bonus, whichever screen's /me call claimed it first.
        scope.launch {
            // Not over the Play Games sign-in dialog: the bonus waits until it is done.
            startupGate.ready.first { it }
            offerCoinsOnLaunch()
            observeDailyBonus().collect { coins ->
                setState { copy(dailyBonus = if (coins > 0) Strings.dailyBonus(TextUtils.groupedThousands(coins)) else null) }
            }
        }
        // Pushes land in the history from the push service; the bell
        // follows it live.
        scope.launch {
            observeNotifications().collect {
                history = it
                showNotifications()
            }
        }
    }

    override suspend fun handleIntent(intent: MainIntent) {
        when (intent) {
            is MainIntent.SelectTab -> setState { copy(selectedTab = intent.tab) }
            MainIntent.CollectDailyBonus -> {
                collectDailyBonus()
                offerCoinsOnLaunch()
            }
            MainIntent.LoadProfile -> {
                load()
                offerCoinsOnLaunch()
                checkActiveMatch()
            }
            MainIntent.Rejoin -> state.value.rejoin?.let {
                setState { copy(rejoin = null) }
                emitEffect(MainEffect.OpenMatch(it.gameId, it.matchId))
            }
            MainIntent.DismissRejoin -> setState { copy(rejoin = null) }
            is MainIntent.AddFriendFromLink -> addFriendFromLink(intent.userCode)
            MainIntent.DismissFriendLink -> setState { copy(friendLink = null) }
            MainIntent.AskExit -> setState { copy(exitAsked = true) }
            MainIntent.DismissExit -> setState { copy(exitAsked = false) }
            // Only when an ad can play now: an offer that ends in "no ad" is worse than none.
            MainIntent.OpenGetCoins -> if (isAdReady().also { AppLog.d(AppConstants.LOG_TAG_ADS, "home: coins + tapped, ad ready=$it") }) {
                setState { copy(coinsOffer = CoinsOfferUi(CoinsStrings.getCoinsTitle, CoinsStrings.getCoinsBody)) }
                refreshAdLimits()
            }
            MainIntent.CloseGetCoins -> {
                AppLog.d(AppConstants.LOG_TAG_ADS, "home: coins dialog closed")
                setState { copy(coinsOffer = null) }
            }
            MainIntent.WatchAdForCoins -> watchAd()
            MainIntent.OpenSettings -> showSettings()
            MainIntent.CloseSettings -> setState { copy(settings = null) }
            MainIntent.ToggleEffects -> setState { copy(settings = settingsController.toggleEffects()) }
            MainIntent.ToggleMusic -> setState { copy(settings = settingsController.toggleMusic()) }
            MainIntent.ToggleKeyboard -> setState { copy(settings = settingsController.toggleKeyboard()) }
            MainIntent.OpenHistory -> {
                setState { copy(settings = null) }
                emitEffect(MainEffect.OpenHistory)
            }
            is MainIntent.OpenFeedback -> {
                setState { copy(settings = null) }
                emitEffect(MainEffect.OpenFeedback(intent.compose))
            }
            MainIntent.ToggleNotifications ->
                if (state.value.notificationsOpen) closeNotifications() else openNotifications()
            MainIntent.DismissNotifications -> closeNotifications()
            is MainIntent.NotificationClicked -> {
                closeNotifications()
                emitEffect(MainEffect.OpenRoom(intent.roomId))
            }
        }
    }

    private fun openNotifications() {
        showNotifications() // fresh "5m ago" labels
        setState { copy(notificationsOpen = true) }
    }

    /** Closing counts as seen: unread highlights and the badge clear. */
    private fun closeNotifications() {
        if (!state.value.notificationsOpen) return
        setState { copy(notificationsOpen = false) }
        markNotificationsRead()
    }

    private fun showNotifications() {
        val now = clock.now()
        setState {
            copy(
                notifications = history.map {
                    NotificationUi(
                        id = it.id,
                        roomId = it.roomId,
                        title = it.title,
                        body = it.body,
                        timeLabel = Strings.timeAgo((now - it.receivedAt).inWholeMinutes),
                        unread = !it.read,
                    )
                },
                unreadNotifications = history.count { !it.read },
            )
        }
    }

    /** Back on the dashboard with a match still running for the user: offer to rejoin. */
    private suspend fun checkActiveMatch() {
        val match = (getActiveMatch() as? AppResult.Success)?.data
        setState { copy(rejoin = match?.let { RejoinUi(it.matchId, it.gameId, it.title) }) }
    }

    /**
     * Once per app launch, after the daily bonus (if any) is collected: offer
     * free coins for a rewarded ad, only if one is ready (or comes in within
     * [LAUNCH_AD_WAIT]). Missed then, it is not offered later in this launch;
     * the coins "+" offers the same any time an ad is ready.
     */
    private suspend fun offerCoinsOnLaunch() {
        if (!startupGate.ready.value || observeDailyBonus().value > 0 || state.value.coinsOffer != null || !state.value.profileLoaded) return
        if (!adsConfig.isLaunchOfferEnabled() || !launchCoinsOffer.takeIfDue()) return
        if (!isAdReady.await(LAUNCH_AD_WAIT) || state.value.coinsOffer != null) {
            return AppLog.d(AppConstants.LOG_TAG_ADS, "home: launch offer skipped, no ad ready")
        }
        AppLog.d(AppConstants.LOG_TAG_ADS, "home: launch offer shown")
        // Only when an ad can pay now (no cooldown, ads left today); an older server without limits: always.
        val status = (getAdLimitStatus() as? AppResult.Success)?.data
        if (status != null && !status.canWatch(clock.now())) return
        setState { copy(coinsOffer = CoinsUiMapper.withLimits(CoinsOfferUi(CoinsStrings.getCoinsTitle, CoinsStrings.getCoinsBody), status, clock.now())) }
    }

    /** Puts today's ad limits on the open offer (and greys the button out when none can pay). */
    private suspend fun refreshAdLimits() {
        val status = (getAdLimitStatus() as? AppResult.Success)?.data ?: return
        setState { copy(coinsOffer = coinsOffer?.let { CoinsUiMapper.withLimits(it, status, clock.now()) }) }
    }

    private suspend fun load() {
        val data = (getHomeScreenData() as? AppResult.Success)?.data ?: return
        setState {
            copy(
                userName = data.profile.name,
                level = data.profile.level.toString(),
                // Reaching the server means online; the WebSocket connection
                // manager (Phase 12) will drive this live later.
                online = true,
                profileLoaded = true,
                coinBalance = TextUtils.groupedThousands(data.profile.coins),
                levelProgress = levelProgress(data.profile.xp, data.profile.levelXp, data.profile.nextLevelXp),
                xpLabel = Strings.topBarXp(TextUtils.groupedThousands(data.profile.xp), TextUtils.groupedThousands(data.profile.nextLevelXp)),
            )
        }
    }

    /** Ad watched to the end → the server adds coins; the balance updates and the dialog says how many. */
    private suspend fun watchAd() {
        val offer = state.value.coinsOffer
        AppLog.d(AppConstants.LOG_TAG_ADS, "home: Watch Ad tapped (canWatch=${offer?.canWatch}, watching=${offer?.watchingAd})")
        // Extra taps while one ad is loading or playing do nothing (one ad per tap).
        if (offer?.watchingAd == true) return
        // A cooldown or today's limit: the button is off and its note says why.
        if (offer != null && !offer.canWatch) return
        setState { copy(coinsOffer = coinsOffer?.copy(watchingAd = true, message = null)) }
        val result = watchAdForCoins.invoke()
        val message = CoinsUiMapper.adMessage(result)
        setState {
            copy(
                coinsOffer = coinsOffer?.copy(watchingAd = false, message = message),
                coinBalance = (result as? AppResult.Success)?.let { TextUtils.groupedThousands(it.data.coins) } ?: coinBalance,
            )
        }
        // Then: "next in 2h · 4 left today", or the daily limit, with the button off.
        refreshAdLimits()
    }

    /** After sign-in (the account must exist first), sends the request and says how it went. */
    private suspend fun addFriendFromLink(userCode: String) {
        startupGate.ready.first { it }
        val ui = when (val result = sendFriendRequest(userCode)) {
            is AppResult.Success -> ProfileUiMapper.outcome(result.data, userCode.trim().uppercase()).let { (message, failed) -> FriendLinkUi(message, failed) }
            is AppResult.Failure -> FriendLinkUi(
                if (result.error is YapNPlayError.Validation) ProfileStrings.profileCodeNotFound else ProfileStrings.profileNetworkError,
                failed = true,
            )
        }
        setState { copy(friendLink = ui) }
    }

    private fun showSettings() {
        setState { copy(settings = settingsController.current()) }
    }

    private fun levelProgress(xp: Int, levelXp: Int, nextLevelXp: Int): Float =
        if (nextLevelXp > levelXp) ((xp - levelXp).toFloat() / (nextLevelXp - levelXp)).coerceIn(0f, 1f) else 0f
}

/** How long the launch offer waits for the first ad to come in before giving up for this launch. */
private val LAUNCH_AD_WAIT = 8.seconds
