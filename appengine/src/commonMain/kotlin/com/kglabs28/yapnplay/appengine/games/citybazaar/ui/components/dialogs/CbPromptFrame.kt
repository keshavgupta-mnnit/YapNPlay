package com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.dialogs

import com.kglabs28.yapnplay.appengine.ui.components.common.ShrinkText
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.seat.SeatTimerChip

/**
 * A City Bazaar prompt as in the designs: frosted white glass with a soft
 * glow in [accent]; a darker header band with the neon [title] (time left
 * at the end) and a glowing rule under it; then the body and the buttons.
 */
@Composable
fun CbPromptFrame(
    title: AnnotatedString,
    accent: Color,
    modifier: Modifier = Modifier,
    width: Dp = YapNPlayTheme.dimens.cbDialogWidth,
    countdown: String? = null,
    trailing: @Composable (() -> Unit)? = null,
    buttons: (@Composable RowScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val shape = RoundedCornerShape(dimens.cardRadius)
    Column(
        modifier = modifier
            .width(width)
            .shadow(elevation = dimens.promptGlow, shape = shape, ambientColor = accent, spotColor = accent)
            .clip(shape)
            // Solid dark glass (the blurred table mustn't show through), frosted on top.
            .background(colors.Background.copy(alpha = BASE_ALPHA))
            .background(Brush.verticalGradient(listOf(Color.White.copy(alpha = FROST_TOP), Color.White.copy(alpha = FROST_BOTTOM))))
            // Neon rim: the accent running into pink and cyan.
            .border(dimens.promptRim, Brush.linearGradient(listOf(accent, colors.SocialZoneAccent, colors.Cyan, accent)), shape),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(modifier = Modifier.fillMaxWidth().background(Color.Black.copy(alpha = HEADER_ALPHA)).padding(horizontal = Spacing.Sm, vertical = Spacing.Xs)) {
            ShrinkText(
                text = title,
                style = YapNPlayTheme.typography.title.copy(
                    color = accent,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    shadow = Shadow(color = accent, blurRadius = GLOW_BLUR),
                ),
                modifier = Modifier.align(Alignment.Center).padding(horizontal = dimens.cutButtonMinWidth / 2),
            )
            Row(modifier = Modifier.align(Alignment.CenterEnd), verticalAlignment = Alignment.CenterVertically) {
                trailing?.invoke()
                countdown?.let { SeatTimerChip(text = it) }
            }
        }
        Box(Modifier.fillMaxWidth().height(dimens.hairline * 2).background(Brush.horizontalGradient(listOf(Color.Transparent, accent, Color.Transparent))))
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.Sm, vertical = Spacing.Xs),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.Xxs),
        ) {
            content()
            if (buttons != null) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Spacing.Md, Alignment.CenterHorizontally), verticalAlignment = Alignment.CenterVertically) {
                    buttons()
                }
            }
        }
    }
}

private const val GLOW_BLUR = 14f
private const val BASE_ALPHA = 0.92f
private const val FROST_TOP = 0.14f
private const val FROST_BOTTOM = 0.04f
private const val RIM_TOP = 0.55f
private const val RIM_BOTTOM = 0.7f
private const val HEADER_ALPHA = 0.22f
