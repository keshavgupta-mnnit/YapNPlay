package com.kglabs28.yapnplay.appengine.ui.components.playhub

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.glassPanel
import com.kglabs28.yapnplay.appengine.ui.components.common.IconCircle
import com.kglabs28.yapnplay.appengine.ui.components.common.PillButton
import com.kglabs28.yapnplay.appengine.ui.components.common.ShrinkText

/**
 * One way to play on the Quick Play hub: a frosted glass card lit in its
 * [accent] — icon and two-tone title ([title] in [titleColor], [titleAccent]
 * in the accent), a [status] pill, the [art], a two-line [description] and
 * the button. The whole card is tappable, like the button.
 */
@Composable
fun PlayModeCard(
    icon: ImageVector,
    title: String,
    titleAccent: String,
    titleColor: Color,
    accent: Color,
    description: String,
    buttonText: String,
    buttonColor: Color,
    buttonRim: Color,
    onClick: () -> Unit,
    status: @Composable () -> Unit,
    art: @Composable (Modifier) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    val shape = RoundedCornerShape(dimens.cardRadius)

    Box(modifier = modifier.fillMaxSize()) {
        // The glass takes the taps, clipped to the card, so a press lights the card's own outline.
        Box(
            Modifier
                .matchParentSize()
                .glassPanel(
                    shape = shape,
                    glowColor = accent,
                    surfaceBrush = Brush.verticalGradient(listOf(colors.HomeCardFrostTop, colors.HomeCardFrostBottom)),
                )
                .clip(shape)
                .clickable(enabled = enabled, onClick = onClick),
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Spacing.Md, vertical = Spacing.Sm),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.Xs),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.Xs, Alignment.CenterHorizontally),
            ) {
                IconCircle(
                    imageVector = icon,
                    fill = colors.HomeIconFrost,
                    size = dimens.playHubHeaderIcon,
                    iconSize = dimens.homeBarIcon,
                    rimColor = accent,
                )
                ShrinkText(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = titleColor)) { append(title) }
                        withStyle(SpanStyle(color = accent)) { append(titleAccent) }
                    },
                    style = typography.zoneTitle,
                )
            }
            status()
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                art(Modifier.fillMaxSize())
            }
            ShrinkText(
                text = description,
                style = typography.playHubDescription.copy(color = colors.HomeTextSoft, textAlign = TextAlign.Center),
                maxLines = 2,
                modifier = Modifier.fillMaxWidth(),
            )
            PillButton(
                text = buttonText,
                onClick = onClick,
                gradient = listOf(buttonColor),
                contentColor = colors.TextPrimary,
                rimColor = buttonRim,
                glassFill = buttonColor,
                trailingIcon = Icons.Rounded.ChevronRight,
                height = dimens.playHubCtaHeight,
                enabled = enabled,
                modifier = Modifier.fillMaxWidth(AppConstants.PLAY_HUB_CTA_WIDTH_FRACTION),
            )
        }
    }
}
