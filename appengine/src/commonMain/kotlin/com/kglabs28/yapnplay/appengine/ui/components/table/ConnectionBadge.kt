package com.kglabs28.yapnplay.appengine.ui.components.table

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SignalCellularAlt
import androidx.compose.material.icons.rounded.SignalCellularAlt1Bar
import androidx.compose.material.icons.rounded.SignalCellularAlt2Bar
import androidx.compose.material.icons.rounded.SignalCellularConnectedNoInternet0Bar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.TableStrings
import com.kglabs28.yapnplay.appengine.features.connection.domain.ConnectionQuality
import com.kglabs28.yapnplay.appengine.features.connection.usecase.ObserveConnectionQualityUseCase
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon
import org.koin.compose.koinInject

/**
 * The connection to the server, at every table: signal bars coloured by
 * the last round trip (green, amber, red) and its milliseconds, so a lag
 * reads as the network's, not the app's.
 */
@Composable
fun ConnectionBadge(modifier: Modifier = Modifier, observe: ObserveConnectionQualityUseCase = koinInject()) {
    val quality by remember(observe) { observe() }.collectAsState(initial = null)
    val reading = quality ?: return
    val colors = YapNPlayTheme.colors
    val (icon, tint) = when (reading.level) {
        ConnectionQuality.Level.GOOD -> Icons.Rounded.SignalCellularAlt to colors.LiveGreen
        ConnectionQuality.Level.FAIR -> Icons.Rounded.SignalCellularAlt2Bar to colors.Warning
        ConnectionQuality.Level.POOR -> Icons.Rounded.SignalCellularAlt1Bar to colors.Error
        ConnectionQuality.Level.OFFLINE -> Icons.Rounded.SignalCellularConnectedNoInternet0Bar to colors.Error
    }
    val label = reading.pingMs?.let { TableStrings.connectionPing(it) } ?: TableStrings.connectionOffline
    Row(
        modifier = modifier.semantics { contentDescription = TableStrings.connectionDescription(label) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Xxxs),
    ) {
        AppIcon(imageVector = icon, tint = tint, size = YapNPlayTheme.dimens.iconSmall)
        BasicText(text = label, style = YapNPlayTheme.typography.micro.copy(color = tint, fontWeight = FontWeight.Bold), maxLines = 1)
    }
}
