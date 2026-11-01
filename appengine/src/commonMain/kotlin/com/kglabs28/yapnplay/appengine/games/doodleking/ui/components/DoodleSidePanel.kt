package com.kglabs28.yapnplay.appengine.games.doodleking.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.common.ShrinkText
import com.kglabs28.yapnplay.appengine.ui.components.table.arenaGlass

/** A right-column panel: the table's dark glass with a title, then its list. */
@Composable
fun DoodleSidePanel(title: String, modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    val colors = YapNPlayTheme.colors
    Column(
        modifier = modifier
            .arenaGlass(RoundedCornerShape(YapNPlayTheme.dimens.ludoGlossRadius))
            .padding(horizontal = Spacing.Sm, vertical = Spacing.Xs),
        verticalArrangement = Arrangement.spacedBy(Spacing.Xxs),
    ) {
        ShrinkText(text = title, style = YapNPlayTheme.typography.label.copy(color = colors.ArenaCrown), modifier = Modifier.fillMaxWidth())
        content()
    }
}
