package com.kglabs28.yapnplay.appengine.ui.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.network.ConnectionStatus
import com.kglabs28.yapnplay.appengine.core.network.RealtimeConnection
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import kotlinx.coroutines.delay
import org.koin.compose.koinInject

/**
 * Shows the app only once the backend answers, so no screen ever opens
 * empty. Before the first connection: a "Connecting…" card, or "Can't
 * reach YapNPlay" with Retry when it fails. Later drops cover the app
 * with "Connection lost" (after a short grace, so a quick reconnect
 * shows nothing) and clear by themselves when it is back.
 */
@Composable
fun ConnectionGate(
    modifier: Modifier = Modifier,
    realtime: RealtimeConnection = koinInject(),
    content: @Composable () -> Unit,
) {
    LaunchedEffect(Unit) { realtime.connect() }
    val status by realtime.status.collectAsState()
    var everOnline by rememberSaveable { mutableStateOf(false) }
    var showLost by remember { mutableStateOf(false) }
    LaunchedEffect(status) {
        showLost = false
        if (status == ConnectionStatus.ONLINE) {
            everOnline = true
        } else {
            delay(LOST_GRACE_MS)
            showLost = true
        }
    }

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (!everOnline) {
            ConnectionCard(
                title = if (status == ConnectionStatus.OFFLINE) Strings.connectionOffline else Strings.connectionConnecting,
                body = if (status == ConnectionStatus.OFFLINE) Strings.connectionOfflineBody else Strings.connectionConnectingBody,
                onRetry = realtime::retryNow.takeIf { status == ConnectionStatus.OFFLINE },
            )
        } else {
            content()
            if (showLost) {
                val colors = YapNPlayTheme.colors
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(colors.Background.copy(alpha = SCRIM_ALPHA))
                        // Nothing behind can be used while the connection is down.
                        .pointerInput(Unit) { awaitEachGesture { awaitFirstDown(requireUnconsumed = false).consume() } },
                    contentAlignment = Alignment.Center,
                ) {
                    ConnectionCard(
                        title = Strings.connectionLost,
                        body = if (status == ConnectionStatus.OFFLINE) Strings.connectionOfflineBody else Strings.connectionLostBody,
                        onRetry = realtime::retryNow.takeIf { status == ConnectionStatus.OFFLINE },
                    )
                }
                BlockBackPress(enabled = true, onBack = {})
            }
        }
    }
}

@Composable
private fun ConnectionCard(title: String, body: String, onRetry: (() -> Unit)?) {
    ModalCard(
        title = title,
        subtitle = body,
        actions = {
            onRetry?.let {
                GlassPillButton(text = Strings.connectionRetry, onClick = it, leadingIcon = Icons.Rounded.Refresh)
            }
        },
    )
}

private const val LOST_GRACE_MS = 2_000L
private const val SCRIM_ALPHA = 0.8f
