package com.kglabs28.yapnplay.appengine.features.about.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.ui.components.AppSnackbar
import com.kglabs28.yapnplay.appengine.ui.components.PrimaryButton
import com.kglabs28.yapnplay.appengine.ui.components.SecondaryButton
import org.koin.compose.koinInject

/**
 * Sample feature screen proving the whole pattern: MVI view model,
 * use case, repository, Koin wiring, tests.
 */
@Composable
fun AboutScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AboutViewModel = koinInject(),
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(viewModel) {
        viewModel.onIntent(AboutScreenIntent.Load)
        viewModel.effects.collect { effect ->
            if (effect is AboutScreenEffect.Back) onBack()
        }
    }
    val info = state.info
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(YapNPlayTheme.colors.Background)
            .padding(Spacing.Lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.Lg, Alignment.CenterVertically),
    ) {
        BasicText(
            text = Strings.aboutTitle,
            style = YapNPlayTheme.typography.headline.copy(color = YapNPlayTheme.colors.TextPrimary),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        when {
            state.loading -> {
                BasicText(
                    text = Strings.aboutLoading,
                    style = YapNPlayTheme.typography.body.copy(color = YapNPlayTheme.colors.TextSecondary),
                )
            }
            info != null -> {
                BasicText(
                    text = Strings.aboutInfo(info.appName, info.version, info.contractVersion),
                    style = YapNPlayTheme.typography.body.copy(color = YapNPlayTheme.colors.TextSecondary),
                )
            }
            state.error != null -> {
                AppSnackbar(
                    message = state.error ?: "",
                    actionLabel = Strings.aboutRetry,
                    onAction = { viewModel.onIntent(AboutScreenIntent.Retry) },
                )
            }
        }
        SecondaryButton(text = Strings.aboutTitle, onClick = { viewModel.onIntent(AboutScreenIntent.GoBack) })
        PrimaryButton(text = Strings.aboutRetry, onClick = { viewModel.onIntent(AboutScreenIntent.Retry) })
    }
}
