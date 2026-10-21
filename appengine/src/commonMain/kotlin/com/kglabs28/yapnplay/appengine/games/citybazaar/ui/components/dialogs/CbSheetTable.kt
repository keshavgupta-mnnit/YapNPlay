package com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.dialogs

import com.kglabs28.yapnplay.appengine.ui.components.common.ShrinkText
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbRowKind
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbSheetRowUi

/** The balance sheet: current balance, the change (red out / green in), and the remaining balance in bold gold. */
@Composable
fun CbSheetTable(rows: List<CbSheetRowUi>, modifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.RoomsSmallButton, RoundedCornerShape(dimens.cbRowRadius))
            .padding(Spacing.Xs),
        verticalArrangement = Arrangement.spacedBy(Spacing.Xxxs),
    ) {
        rows.forEach { row ->
            if (row.kind == CbRowKind.TOTAL) Box(Modifier.fillMaxWidth().height(dimens.hairline).background(colors.IconMuted))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.Xs)) {
                val bold = row.kind == CbRowKind.TOTAL
                ShrinkText(
                    text = row.label,
                    style = typography.label.copy(color = if (bold) colors.TextPrimary else colors.TextSecondary, fontWeight = if (bold) FontWeight.Bold else null),
                    modifier = Modifier.weight(1f),
                )
                ShrinkText(
                    text = row.amount,
                    style = typography.label.copy(
                        fontWeight = FontWeight.Bold,
                        color = when (row.kind) {
                            CbRowKind.MINUS -> colors.CbLoss
                            CbRowKind.PLUS -> colors.CbGain
                            CbRowKind.TOTAL -> colors.CbCash
                            CbRowKind.NORMAL -> colors.TextPrimary
                        },
                    ),
                )
            }
        }
    }
}
