package com.kglabs28.yapnplay

import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.AppIconGlyph
import com.kglabs28.yapnplay.appengine.ui.components.PrimaryButton
import com.kglabs28.yapnplay.appengine.ui.components.SecondaryButton
import com.kglabs28.yapnplay.appengine.ui.components.ToggleIconButton
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Interaction tests on the JVM via Robolectric, hosted in the app module
 * so the manifest activity resolves. Callbacks fire, disabled components
 * ignore clicks, touch targets stay reachable.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], packageName = "com.kglabs28.yapnplay")
class ComponentInteractionTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `primary button click fires`() {
        var clicked = 0
        composeRule.setContent {
            YapNPlayTheme { PrimaryButton(text = "Go", onClick = { clicked++ }) }
        }
        composeRule.onNodeWithText("GO").performClick()
        assert(clicked == 1) { "Click did not fire" }
    }

    @Test
    fun `disabled primary button ignores clicks`() {
        var clicked = 0
        composeRule.setContent {
            YapNPlayTheme { PrimaryButton(text = "Go", onClick = { clicked++ }, enabled = false) }
        }
        composeRule.onNodeWithText("GO").assertIsNotEnabled()
        composeRule.onNodeWithText("GO").performClick()
        assert(clicked == 0) { "Disabled button fired" }
    }

    @Test
    fun `toggle flips state`() {
        var selected = false
        composeRule.setContent {
            YapNPlayTheme {
                ToggleIconButton(selected = selected, onToggle = { selected = it }) {
                    AppIconGlyph(glyph = "M")
                }
            }
        }
        composeRule.onNodeWithText("M").performClick()
        assert(selected) { "Toggle did not flip" }
    }

    @Test
    fun `buttons meet the 48dp touch minimum`() {
        composeRule.setContent {
            YapNPlayTheme {
                PrimaryButton(text = "Go", onClick = {})
            }
        }
        composeRule.onNodeWithText("GO").assertHeightIsAtLeast(48.dp)
    }

    @Test
    fun `enabled button is enabled`() {
        composeRule.setContent {
            YapNPlayTheme { SecondaryButton(text = "Back", onClick = {}) }
        }
        composeRule.onNodeWithText("BACK").assertIsEnabled()
    }
}
