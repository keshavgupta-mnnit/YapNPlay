package com.kglabs28.yapnplay.appengine.ui.components.voice

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import com.kglabs28.yapnplay.appengine.features.voice.domain.VoiceAvailability
import org.koin.compose.koinInject

/**
 * Whether voice works right now (see [VoiceAvailability]): false in a
 * Nearby party without internet. Every mic and speaker switch reads it and
 * shows greyed out, doing nothing, while it is false.
 */
val LocalVoiceEnabled = staticCompositionLocalOf { true }

/** Provides [LocalVoiceEnabled] to every screen, from the app's one voice-availability state. */
@Composable
fun VoiceAvailabilityHost(content: @Composable () -> Unit) {
    val availability: VoiceAvailability = koinInject()
    val available by availability.available.collectAsState()
    CompositionLocalProvider(LocalVoiceEnabled provides available, content = content)
}

/** A voice switch's look: greyed out while voice isn't available. */
fun Modifier.voiceSwitch(enabled: Boolean): Modifier = if (enabled) this else alpha(VOICE_OFF_ALPHA)

/** A voice switch's tap: does nothing while voice isn't available. */
fun voiceTap(enabled: Boolean, onTap: () -> Unit): () -> Unit = { if (enabled) onTap() }

private const val VOICE_OFF_ALPHA = 0.4f
