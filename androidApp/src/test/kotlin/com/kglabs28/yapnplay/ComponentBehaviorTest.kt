package com.kglabs28.yapnplay

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.AppDialog
import com.kglabs28.yapnplay.appengine.ui.components.AppSnackbar
import com.kglabs28.yapnplay.appengine.ui.components.AppTextField
import com.kglabs28.yapnplay.appengine.ui.components.CountdownRing
import com.kglabs28.yapnplay.appengine.ui.components.DiceView
import com.kglabs28.yapnplay.appengine.ui.components.MemberChip
import com.kglabs28.yapnplay.appengine.ui.components.RoomCodeInput
import com.kglabs28.yapnplay.appengine.ui.components.SeatCard
import com.kglabs28.yapnplay.appengine.ui.components.ToggleIconButton
import com.kglabs28.yapnplay.appengine.ui.components.AppIconGlyph
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Behaviour coverage for every interactive component: inputs enforce
 * limits, dialogs route both buttons, dice/countdown render every state.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], packageName = "com.kglabs28.yapnplay")
class ComponentBehaviorTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<TestActivity>()

    @Test
    fun `text field enforces max length`() {
        composeRule.setContent {
            YapNPlayTheme {
                var text by remember { mutableStateOf("") }
                AppTextField(value = text, onValueChange = { text = it }, maxLength = 6)
            }
        }
        composeRule.onNode(hasSetTextAction()).performTextInput("abc")
        composeRule.onNodeWithText("abc").assertExists()
        composeRule.onNode(hasSetTextAction()).performTextInput("defghi")
        composeRule.onNodeWithText("abcdef").assertExists()
    }

    @Test
    fun `room code input uppercases and caps at six`() {
        composeRule.setContent {
            YapNPlayTheme {
                var code by remember { mutableStateOf("") }
                RoomCodeInput(code = code, onCodeChange = { code = it })
            }
        }
        composeRule.onNode(hasSetTextAction()).performTextInput("abcdef12")
        for (char in listOf("A", "B", "C", "D", "E", "F")) {
            composeRule.onNodeWithText(char).assertExists()
        }
    }

    @Test
    fun `dialog routes confirm and dismiss`() {
        var confirmed = 0
        var dismissed = 0
        composeRule.setContent {
            YapNPlayTheme {
                AppDialog(
                    title = "T",
                    body = "B",
                    onDismiss = { dismissed++ },
                    confirmLabel = "Yes",
                    onConfirm = { confirmed++ },
                    dismissLabel = "No",
                )
            }
        }
        composeRule.onNodeWithText("YES").performClick()
        composeRule.onNodeWithText("NO").performClick()
        assert(confirmed == 1 && dismissed == 1) { "confirm=$confirmed dismiss=$dismissed" }
    }

    @Test
    fun `snackbar action fires`() {
        var acted = 0
        composeRule.setContent {
            YapNPlayTheme {
                AppSnackbar(message = "Hi", actionLabel = "Go", onAction = { acted++ })
            }
        }
        composeRule.onNodeWithText("GO").performClick()
        assert(acted == 1) { "Snackbar action did not fire" }
    }

    @Test
    fun `seat card click fires when set`() {
        var clicked = 0
        composeRule.setContent {
            YapNPlayTheme {
                SeatCard(onClick = { clicked++ }) {}
            }
        }
        composeRule.onRoot().performClick()
        assert(clicked == 1) { "SeatCard click did not fire" }
    }

    @Test
    fun `toggle selected unselects`() {
        var selected = true
        composeRule.setContent {
            YapNPlayTheme {
                ToggleIconButton(selected = selected, onToggle = { selected = it }) {
                    AppIconGlyph(glyph = "M")
                }
            }
        }
        composeRule.onNodeWithText("M").performClick()
        assert(!selected) { "Toggle did not unselect" }
    }

    @Test
    fun `member chip click fires`() {
        var clicked = 0
        composeRule.setContent {
            YapNPlayTheme {
                MemberChip(text = "NEO", onClick = { clicked++ })
            }
        }
        composeRule.onNodeWithText("NEO").performClick()
        assert(clicked == 1) { "Chip click did not fire" }
    }

    @Test
    fun `dice renders all six faces`() {
        composeRule.setContent {
            YapNPlayTheme {
                Row {
                    for (face in 1..6) {
                        DiceView(value = face)
                    }
                }
            }
        }
        composeRule.onRoot().assertExists()
    }

    @Test
    fun `countdown renders empty and full`() {
        composeRule.setContent {
            YapNPlayTheme {
                Column {
                    CountdownRing(progress = 0f)
                    CountdownRing(progress = 1f)
                }
            }
        }
        composeRule.onRoot().assertExists()
    }
}
