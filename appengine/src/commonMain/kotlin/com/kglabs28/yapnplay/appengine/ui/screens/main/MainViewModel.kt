package com.kglabs28.yapnplay.appengine.ui.screens.main

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
    private val clock: AppClock,
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
            MainIntent.CollectDailyBonus -> setState { copy(dailyBonus = null) }
            MainIntent.LoadProfile -> {
                load()
                checkActiveMatch()
            }
            MainIntent.Rejoin -> state.value.rejoin?.let {
                setState { copy(rejoin = null) }
                emitEffect(MainEffect.OpenMatch(it.gameId, it.matchId))
            }
            MainIntent.DismissRejoin -> setState { copy(rejoin = null) }
            MainIntent.OpenSettings -> emitEffect(MainEffect.OpenSettingsDialog)
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
        // The daily bonus the server just added: say so for a moment.
        if (data.profile.dailyBonus > 0) {
            setState { copy(dailyBonus = Strings.dailyBonus(TextUtils.groupedThousands(data.profile.dailyBonus))) }
        }
    }

    private fun levelProgress(xp: Int, levelXp: Int, nextLevelXp: Int): Float =
        if (nextLevelXp > levelXp) ((xp - levelXp).toFloat() / (nextLevelXp - levelXp)).coerceIn(0f, 1f) else 0f
}
