package com.kglabs28.yapnplay.appengine.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import com.kglabs28.yapnplay.appengine.core.designsystem.HeadlineFontFamily
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/**
 * Player avatar: colored circle with initials, optional presence dot and
 * speaking ring. Real photos replace the initials later; the layout stays.
 */
@Composable
fun Avatar(
    initials: String,
    color: Color,
    modifier: Modifier = Modifier,
    size: Dp? = null,
    online: Boolean? = null,
    speaking: Boolean = false,
) {
    val dimens = YapNPlayTheme.dimens
    val resolvedSize = size ?: dimens.avatarCard
    SpeakingRing(
        speaking = speaking,
        modifier = modifier.size(resolvedSize),
    ) {
        Box(
            modifier = Modifier
                .size(resolvedSize)
                .clip(YapNPlayTheme.shapes.avatar)
                .background(color.copy(alpha = 0.35f)),
            contentAlignment = Alignment.Center,
        ) {
            BasicText(
                text = initials.take(2).uppercase(),
                style = YapNPlayTheme.typography.title.copy(
                    fontFamily = HeadlineFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    color = YapNPlayTheme.colors.TextPrimary,
                    textAlign = TextAlign.Center,
                ),
            )
        }
    }
    if (online != null) {
        Box(modifier = Modifier.size(resolvedSize)) {
            PresenceDot(
                online = online,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = dimens.hairline, y = dimens.hairline),
            )
        }
    }
}
