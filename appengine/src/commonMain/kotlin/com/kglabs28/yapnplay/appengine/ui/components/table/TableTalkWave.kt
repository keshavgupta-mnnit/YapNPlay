package com.kglabs28.yapnplay.appengine.ui.components.table

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.common.VoiceWave

/**
 * How a player card shows talking, on every table: the voice wave under
 * the name, moving in the voice colours while they talk, still and grey
 * while they're quiet.
 */
@Composable
fun TableTalkWave(talking: Boolean, modifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    VoiceWave(
        modifier = modifier.fillMaxWidth().height(YapNPlayTheme.dimens.tableTalkWaveHeight),
        animated = talking,
        colors = if (talking) listOf(colors.VoiceWaveStart, colors.VoiceWaveEnd) else listOf(colors.VoiceWaveMuted, colors.VoiceWaveMuted),
    )
}
