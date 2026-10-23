package com.kglabs28.yapnplay.appengine.ui.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/** Pill tabs ("Online (6)", "Offline (12)", "Most wins"…): the picked one glows green. */
@Composable
fun <T> PillTabs(
    tabs: List<Pair<T, String>>,
    selected: T,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val shape = YapNPlayTheme.shapes.pill
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(Spacing.Xs)) {
        tabs.forEach { (tab, label) ->
            val on = tab == selected
            Box(
                modifier = Modifier
                    .height(YapNPlayTheme.dimens.profileTabHeight)
                    .clip(shape)
                    .background(if (on) colors.LiveGreen.copy(alpha = FILL_ALPHA) else Color.Transparent)
                    .border(YapNPlayTheme.dimens.hairline, if (on) colors.LiveGreen else colors.IconMuted.copy(alpha = RIM_ALPHA), shape)
                    .clickable { onSelect(tab) }
                    .padding(horizontal = Spacing.Sm),
                contentAlignment = Alignment.Center,
            ) {
                ShrinkText(text = label, style = YapNPlayTheme.typography.micro.copy(color = if (on) colors.LiveGreen else colors.TextSecondary))
            }
        }
    }
}

private const val FILL_ALPHA = 0.18f
private const val RIM_ALPHA = 0.5f
