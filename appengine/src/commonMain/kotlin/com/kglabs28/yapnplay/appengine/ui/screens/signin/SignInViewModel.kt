package com.kglabs28.yapnplay.appengine.ui.screens.signin

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.core.common.DispatcherProvider
import com.kglabs28.yapnplay.appengine.core.mvi.MviViewModel
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.features.profile.usecase.SignInWithGoogleUseCase

/** The web's login screen: sends the player to Google, and signs in with what Google sends back. */
class SignInViewModel(
    private val signIn: SignInWithGoogleUseCase,
    dispatchers: DispatcherProvider,
) : MviViewModel<SignInState, SignInIntent, SignInEffect>(SignInState(), dispatchers) {

    override suspend fun handleIntent(intent: SignInIntent) {
        when (intent) {
            SignInIntent.Opened -> finishReturned()
            SignInIntent.Google -> {
                setState { copy(busy = true, error = null) }
                signIn.start()
            }
        }
    }

    private suspend fun finishReturned() {
        setState { copy(busy = true, error = null) }
        when (signIn.finishReturned()) {
            is AppResult.Success -> setState { copy(busy = false) }
            is AppResult.Failure -> setState { copy(busy = false, error = Strings.signInFailed) }
        }
    }
}
