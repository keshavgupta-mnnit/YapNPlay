package com.kglabs28.yapnplay.appengine.ui.components.nearby

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.glassPill
import com.kglabs28.yapnplay.appengine.features.nearby.presentation.NearbyReadyItemUi
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon
import com.kglabs28.yapnplay.appengine.ui.components.common.ShrinkText

/**
 * One slim checklist pill: a round green tick or red cross badge, the
 * name (and why, when Nearby said), and for anything not ready an orange
 * ">" bubble; tapping a not-ready row fixes it.
 */
@Composable
fun NearbyReadyRow(
    item: NearbyReadyItemUi,
    onFix: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    val tint = if (item.ok) colors.NearbyReadyGreen else colors.Error
    val shape = YapNPlayTheme.shapes.pill
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = dimens.nearbyCheckRow)
            .glassPill(glowColor = tint, surface = colors.RoomsPanel)
            .clip(shape)
            .clickable(enabled = !item.ok, role = Role.Button, onClick = onFix)
            .padding(horizontal = Spacing.Xs, vertical = Spacing.Xxxs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Xs),
    ) {
        Box(
            modifier = Modifier.size(dimens.nearbyCheckBadge).clip(CircleShape).background(tint),
            contentAlignment = Alignment.Center,
        ) {
            AppIcon(imageVector = if (item.ok) Icons.Rounded.Check else Icons.Rounded.Close, tint = colors.TextPrimary, size = dimens.iconTiny)
        }
        Column(modifier = Modifier.weight(1f)) {
            ShrinkText(text = item.label, style = typography.label.copy(color = colors.TextPrimary))
            item.detail?.let { ShrinkText(text = it, style = typography.caption.copy(color = colors.HomeTextSoft), maxLines = 2) }
        }
        if (!item.ok) {
            Box(
                modifier = Modifier.size(dimens.nearbyCheckBadge).clip(CircleShape).background(colors.PlayNearbyCta),
                contentAlignment = Alignment.Center,
            ) {
                AppIcon(imageVector = Icons.Rounded.ChevronRight, tint = colors.TextPrimary, size = dimens.iconSmall)
            }
        }
    }
}
