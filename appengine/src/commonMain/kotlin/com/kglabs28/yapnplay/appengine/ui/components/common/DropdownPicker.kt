package com.kglabs28.yapnplay.appengine.ui.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/**
 * A tap-to-open list in a rounded box: shows the chosen entry of
 * [options] (or [placeholder]); picking one closes it.
 */
@Composable
fun DropdownPicker(
    options: List<String>,
    selectedIndex: Int?,
    placeholder: String,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    val shape = RoundedCornerShape(dimens.rowRadius)
    var open by remember { mutableStateOf(false) }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.RoomsSmallButton)
            .border(dimens.hairline, colors.IconMuted, shape),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { open = !open }
                .padding(Spacing.Sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BasicText(
                text = selectedIndex?.let { options.getOrNull(it) } ?: placeholder,
                style = typography.sectionTitle.copy(color = if (selectedIndex == null) colors.TextSecondary else colors.TextPrimary),
                modifier = Modifier.weight(1f),
            )
            AppIcon(imageVector = if (open) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore, tint = colors.IconMuted)
        }
        if (open) {
            Column(
                modifier = Modifier
                    .heightIn(max = dimens.reportListMaxHeight)
                    .verticalScrollWithBar(),
            ) {
                options.forEachIndexed { index, label ->
                    BasicText(
                        text = label,
                        style = typography.label.copy(color = if (index == selectedIndex) colors.Gold else colors.TextPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSelect(index)
                                open = false
                            }
                            .padding(horizontal = Spacing.Sm, vertical = Spacing.Xs),
                    )
                }
            }
        }
    }
}
