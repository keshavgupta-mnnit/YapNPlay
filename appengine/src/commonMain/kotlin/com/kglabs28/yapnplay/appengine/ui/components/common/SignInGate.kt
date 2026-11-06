package com.kglabs28.yapnplay.appengine.ui.components.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.kglabs28.yapnplay.appengine.features.profile.usecase.ObserveSignInRequiredUseCase
import com.kglabs28.yapnplay.appengine.ui.screens.signin.SignInScreen
import org.koin.compose.koinInject

/**
 * The web's entry check, on every page load: no account in this browser →
 * the login screen; otherwise [content] (the app). Signing out elsewhere
 * (the account was signed in to in another browser) brings the login back.
 * The app itself never shows it.
 */
@Composable
fun SignInGate(content: @Composable () -> Unit) {
    val observe: ObserveSignInRequiredUseCase = koinInject()
    val required by remember(observe) { observe() }.collectAsState(initial = observe.now())
    if (required) SignInScreen() else content()
}
