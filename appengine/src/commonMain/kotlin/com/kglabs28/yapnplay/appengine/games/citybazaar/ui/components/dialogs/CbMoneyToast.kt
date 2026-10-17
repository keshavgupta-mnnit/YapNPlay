package com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.table.tableFrostedGlass
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbRowKind
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbToastUi

/** A balance-sheet note over the board (rent, tax, cards); informational, no buttons. */
@Composable
fun CbMoneyToast(toast: CbToastUi, modifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val accent = if (toast.gain) colors.CbGain else colors.CbLoss
    Column(
        modifier = modifier
            .width(dimens.cbToastWidth)
            .tableFrostedGlass(tint = accent, shape = RoundedCornerShape(dimens.ludoCardRadius), radius = dimens.ludoCardRadius)
            .padding(Spacing.Sm),
        verticalArrangement = Arrangement.spacedBy(Spacing.Xxs),
    ) {
        BasicText(text = toast.title, style = YapNPlayTheme.typography.label.copy(color = accent, fontWeight = FontWeight.Bold), maxLines = 2)
        if (toast.rows.size == 1 && toast.rows.first().kind == CbRowKind.NORMAL && toast.rows.first().amount.isEmpty()) {
            BasicText(text = toast.rows.first().label, style = YapNPlayTheme.typography.label.copy(color = colors.TextPrimary))
        } else {
            CbSheetTable(rows = toast.rows)
        }
    }
}
