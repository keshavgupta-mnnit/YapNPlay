package com.kglabs28.yapnplay.appengine.ui.components.common

import com.kglabs28.yapnplay.appengine.core.ui.RoomStrings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.Brush
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.glassPanel

/**
 * Dark glass card for modals shown through [BlockingOverlay]: title,
 * optional subtitle, any body, then the action buttons on the right.
 * With [onClose], a round cross sits at the top right.
 */
@Composable
fun ModalCard(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    onClose: (() -> Unit)? = null,
    /** The card's glass (default near-opaque dark; the profile uses a see-through glass). */
    surface: Color = YapNPlayTheme.colors.Background.copy(alpha = DEFAULT_SURFACE_ALPHA),
    /** A gradient glass instead of [surface] (the profile's frosted purple). */
    surfaceBrush: Brush? = null,
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable ColumnScope.() -> Unit = {},
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography

    Column(
        modifier = modifier
            .width(dimens.modalWidth)
            .glassPanel(
                glowColor = colors.SocialZoneAccent,
                surface = surface,
                surfaceBrush = surfaceBrush ?: SolidColor(surface),
            )
            .padding(Spacing.Lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.Sm),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.Xxxs)) {
            // Title at the start, cross at the end, on one line.
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.Xs),
            ) {
                BasicText(
                    text = title,
                    style = typography.title.copy(color = colors.TextPrimary),
                    modifier = Modifier.weight(1f),
                )
                onClose?.let {
                    GlassIconButton(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = RoomStrings.lobbyClose,
                        onClick = it,
                        size = dimens.modalCloseButton,
                        iconSize = dimens.modalCloseIcon,
                        tint = colors.TextPrimary,
                    )
                }
            }
            subtitle?.let {
                BasicText(text = it, style = typography.label.copy(color = colors.TextSecondary))
            }
        }
        content()
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.Xs, Alignment.End),
            verticalAlignment = Alignment.CenterVertically,
            content = actions,
        )
    }
}

private const val DEFAULT_SURFACE_ALPHA = 0.94f
