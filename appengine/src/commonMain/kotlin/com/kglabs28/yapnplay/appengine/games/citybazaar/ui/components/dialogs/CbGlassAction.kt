package com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.table.TableGlassButton

/** A dialog button in the Royal Ludo glass: silver for the main choice, smoked for the other. */
@Composable
fun CbGlassAction(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    primary: Boolean = true,
    enabled: Boolean = true,
) {
    val colors = YapNPlayTheme.colors
    TableGlassButton(
        text = text,
        onClick = onClick,
        fill = if (primary) listOf(colors.LudoSilverTop, colors.LudoSilverMid, colors.LudoSilverBottom) else listOf(colors.LudoSmokeTop, colors.LudoSmokeBottom),
        textColor = if (primary) colors.LudoSilverText else colors.TextPrimary,
        enabled = enabled,
        modifier = modifier,
    )
}
