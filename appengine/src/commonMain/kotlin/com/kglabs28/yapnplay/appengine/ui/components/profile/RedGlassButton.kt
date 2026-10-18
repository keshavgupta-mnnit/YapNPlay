package com.kglabs28.yapnplay.appengine.ui.components.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon

/** A pill filled with glassy red (a light sheen on top), white icon and text: Log out, in the top bar and its dialog. */
@Composable
fun RedGlassButton(text: String, icon: ImageVector, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val shape = YapNPlayTheme.shapes.pill
    Row(
        modifier = modifier
            .height(dimens.topBarControlHeight)
            .clip(shape)
            .background(Brush.verticalGradient(listOf(colors.Error.copy(alpha = FILL_ALPHA), colors.ErrorDeep.copy(alpha = FILL_ALPHA))))
            .drawWithContent {
                drawRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = SHINE_ALPHA), Color.Transparent), 0f, size.height / 2), size = Size(size.width, size.height / 2))
                drawContent()
            }
            .border(dimens.hairline, Color.White.copy(alpha = RIM_ALPHA), shape)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = Spacing.Md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Xs, Alignment.CenterHorizontally),
    ) {
        AppIcon(imageVector = icon, tint = colors.TextPrimary, size = dimens.iconSmall)
        BasicText(text = text, style = YapNPlayTheme.typography.sectionTitle.copy(color = colors.TextPrimary))
    }
}

private const val FILL_ALPHA = 0.85f
private const val SHINE_ALPHA = 0.3f
private const val RIM_ALPHA = 0.35f
