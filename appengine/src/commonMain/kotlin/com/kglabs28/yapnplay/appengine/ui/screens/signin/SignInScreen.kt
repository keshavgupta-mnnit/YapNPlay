package com.kglabs28.yapnplay.appengine.ui.screens.signin

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.ui.components.common.ModalCard
import com.kglabs28.yapnplay.appengine.ui.components.common.PillButton
import com.kglabs28.yapnplay.appengine.ui.components.common.ShrinkText
import org.koin.compose.getKoin

/** The web's first screen until the player signs in: one "Continue with Google" card. */
@Composable
fun SignInScreen() {
    val koin = getKoin()
    val viewModel = remember { koin.get<SignInViewModel>() }
    DisposableEffect(viewModel) { onDispose { viewModel.clear() } }
    LaunchedEffect(viewModel) { viewModel.onIntent(SignInIntent.Opened) }
    val state by viewModel.state.collectAsState()
    SignInContent(state = state, onGoogle = { viewModel.onIntent(SignInIntent.Google) })
}

@Composable
private fun SignInContent(state: SignInState, onGoogle: () -> Unit) {
    val colors = YapNPlayTheme.colors
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        ModalCard(
            title = Strings.signInTitle,
            subtitle = Strings.signInBody,
            actions = {
                PillButton(
                    text = if (state.busy) Strings.signInWorking else Strings.signInGoogle,
                    onClick = onGoogle,
                    enabled = !state.busy,
                    gradient = listOf(colors.CtaGlassGreen),
                    contentColor = colors.TextPrimary,
                    rimColor = colors.CtaGlassGreenRim,
                    glassFill = colors.CtaGlassGreen,
                    leadingIcon = Icons.Rounded.AccountCircle,
                    trailingIcon = null,
                    height = YapNPlayTheme.dimens.smallButtonHeight,
                    textStyle = YapNPlayTheme.typography.sectionTitle,
                )
            },
        ) {
            state.error?.let { ShrinkText(text = it, style = YapNPlayTheme.typography.body.copy(color = colors.Error), maxLines = ERROR_LINES) }
        }
    }
}

/** The failure message may wrap to a second line before it shrinks. */
private const val ERROR_LINES = 2
