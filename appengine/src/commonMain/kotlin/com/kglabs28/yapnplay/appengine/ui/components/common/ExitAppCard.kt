package com.kglabs28.yapnplay.appengine.ui.components.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ExitToApp
import androidx.compose.runtime.Composable
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings

/** Back on the main screen: "Exit YapNPlay?" Stay, or exit. */
@Composable
fun ExitAppCard(
    onExit: () -> Unit,
    onStay: () -> Unit,
) {
    val colors = YapNPlayTheme.colors
    ModalCard(
        title = Strings.exitTitle,
        subtitle = Strings.exitBody,
        onClose = onStay,
        actions = {
            GlassPillButton(text = Strings.exitStay, onClick = onStay)
            PillButton(
                text = Strings.exitAction,
                onClick = onExit,
                gradient = listOf(colors.CtaGlassRed),
                contentColor = colors.TextPrimary,
                rimColor = colors.CtaGlassRedRim,
                glassFill = colors.CtaGlassRed,
                leadingIcon = Icons.AutoMirrored.Rounded.ExitToApp,
                trailingIcon = null,
                height = YapNPlayTheme.dimens.smallButtonHeight,
                textStyle = YapNPlayTheme.typography.sectionTitle,
            )
        },
    )
}
