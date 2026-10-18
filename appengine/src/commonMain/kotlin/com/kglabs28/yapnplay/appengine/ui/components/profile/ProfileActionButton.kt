package com.kglabs28.yapnplay.appengine.ui.components.profile

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.common.GlassPillButton
import com.kglabs28.yapnplay.appengine.ui.components.common.PillButton

/** A profile button as in the design: dark glass (Cancel, Decline), green (Save, Accept) or red (Remove, Block, Log Out). */
@Composable
fun ProfileActionButton(
    text: String,
    kind: ProfileButtonKind,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    height: Dp = YapNPlayTheme.dimens.ctaHeight,
) {
    val colors = YapNPlayTheme.colors
    val typography = YapNPlayTheme.typography
    when (kind) {
        ProfileButtonKind.NEUTRAL -> GlassPillButton(
            text = text,
            onClick = onClick,
            modifier = modifier,
            leadingIcon = leadingIcon,
            accent = colors.IconMuted,
            enabled = enabled,
            height = height,
        )
        ProfileButtonKind.CONFIRM -> PillButton(
            text = text,
            onClick = onClick,
            gradient = listOf(colors.CtaGreenStart, colors.CtaGreenEnd),
            contentColor = colors.CtaGreenContent,
            modifier = modifier,
            enabled = enabled,
            height = height,
            leadingIcon = leadingIcon,
            trailingIcon = null,
            textStyle = typography.sectionTitle,
        )
        ProfileButtonKind.DANGER -> PillButton(
            text = text,
            onClick = onClick,
            gradient = listOf(colors.Error, colors.ErrorDeep),
            contentColor = colors.TextPrimary,
            modifier = modifier,
            enabled = enabled,
            height = height,
            rimColor = colors.Error.copy(alpha = RIM_ALPHA),
            leadingIcon = leadingIcon,
            trailingIcon = null,
            textStyle = typography.sectionTitle,
        )
    }
}

private const val RIM_ALPHA = 0.6f
