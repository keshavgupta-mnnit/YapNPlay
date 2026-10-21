package com.kglabs28.yapnplay.appengine.ui.components.moderation

import com.kglabs28.yapnplay.appengine.core.ui.LudoStrings
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.common.GlassPillButton
import com.kglabs28.yapnplay.appengine.ui.components.common.ModalCard
import com.kglabs28.yapnplay.appengine.ui.components.common.PillButton

/** "Did they do it?" for someone else's voice report; it closes by itself when time runs out. */
@Composable
fun ReportVoteCard(
    title: String,
    body: String,
    secondsLeft: String,
    onAgree: () -> Unit,
    onDisagree: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val typography = YapNPlayTheme.typography
    ModalCard(
        title = title,
        subtitle = body,
        modifier = modifier,
        actions = {
            GlassPillButton(
                text = LudoStrings.ludoVoteDisagree,
                onClick = onDisagree,
                leadingIcon = Icons.Rounded.Close,
                height = YapNPlayTheme.dimens.ctaHeight,
            )
            PillButton(
                text = LudoStrings.ludoVoteAgree,
                onClick = onAgree,
                gradient = listOf(colors.Error, colors.Error.copy(alpha = GRADIENT_END)),
                contentColor = colors.TextPrimary,
                leadingIcon = Icons.Rounded.Check,
                trailingIcon = null,
                textStyle = typography.sectionTitle,
            )
        },
    ) {
        BasicText(text = secondsLeft, style = typography.label.copy(color = colors.Gold))
    }
}

private const val GRADIENT_END = 0.8f
