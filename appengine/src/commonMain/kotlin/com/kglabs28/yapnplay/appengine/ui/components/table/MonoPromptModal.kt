package com.kglabs28.yapnplay.appengine.ui.components.table

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import com.kglabs28.yapnplay.appengine.ui.components.common.BlockingOverlay
import com.kglabs28.yapnplay.appengine.ui.components.common.ModalCard

/**
 * A choice the table is waiting on (a colour, the Roulette colour, a swap
 * target), shown as a modal over the table so it never covers the top bar.
 */
@Composable
fun MonoPromptModal(title: String, content: @Composable ColumnScope.() -> Unit) {
    BlockingOverlay(visible = true) {
        ModalCard(title = title, content = content)
    }
}
