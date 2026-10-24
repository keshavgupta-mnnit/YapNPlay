package com.kglabs28.yapnplay.appengine.ui.screens.welcome

import com.kglabs28.yapnplay.appengine.features.profile.usecase.GetPlayGamesNameUseCase
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.core.common.DispatcherProvider
import com.kglabs28.yapnplay.appengine.core.common.StartupGate
import com.kglabs28.yapnplay.appengine.core.mvi.MviViewModel
import com.kglabs28.yapnplay.appengine.core.ui.ProfileStrings
import com.kglabs28.yapnplay.appengine.features.profile.usecase.FinishWelcomeUseCase
import com.kglabs28.yapnplay.appengine.features.profile.usecase.ObserveMyProfileUseCase
import com.kglabs28.yapnplay.appengine.features.profile.usecase.ObserveWelcomeNeededUseCase
import com.kglabs28.yapnplay.appengine.features.profile.usecase.UpdateProfileUseCase
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * The first-launch welcome for a brand-new account: once the account and
 * its profile are there (after the Play Games sign-in), it asks for a name
 * (their Play Games name suggested, else the one they got) and an avatar. Saving or skipping
 * ends it for good.
 */
class WelcomeViewModel(
    observeWelcomeNeeded: ObserveWelcomeNeededUseCase,
    observeMyProfile: ObserveMyProfileUseCase,
    private val updateProfile: UpdateProfileUseCase,
    private val finishWelcome: FinishWelcomeUseCase,
    private val playGamesName: GetPlayGamesNameUseCase,
    startupGate: StartupGate,
    dispatchers: DispatcherProvider,
) : MviViewModel<WelcomeState, WelcomeIntent, WelcomeEffect>(WelcomeState(), dispatchers) {

    init {
        scope.launch {
            startupGate.ready.first { it }
            val profile = combine(observeWelcomeNeeded(), observeMyProfile()) { needed, profile -> profile?.takeIf { needed } }
                .first { it != null } ?: return@launch
            // Their Play Games name when there is one (better than the made-up "Player-1234").
            val suggested = playGamesName()?.take(AppConstants.MAX_NAME_LENGTH) ?: profile.name
            setState { copy(visible = true, name = suggested, avatarId = profile.avatarId) }
        }
    }

    override suspend fun handleIntent(intent: WelcomeIntent) {
        when (intent) {
            is WelcomeIntent.NameChanged -> setState { copy(name = intent.name.take(AppConstants.MAX_NAME_LENGTH), error = null) }
            is WelcomeIntent.PickAvatar -> setState { copy(avatarId = intent.avatarId) }
            WelcomeIntent.Save -> save()
            WelcomeIntent.Skip -> close()
        }
    }

    private suspend fun save() {
        val now = state.value
        if (now.saving) return
        if (now.name.isBlank()) {
            setState { copy(error = ProfileStrings.welcomeNameNeeded) }
            return
        }
        setState { copy(saving = true, error = null) }
        when (updateProfile(name = now.name, avatarId = now.avatarId.ifBlank { null })) {
            is AppResult.Success -> close()
            is AppResult.Failure -> setState { copy(saving = false, error = ProfileStrings.welcomeSaveFailed) }
        }
    }

    private fun close() {
        finishWelcome()
        setState { copy(visible = false, saving = false) }
    }
}
