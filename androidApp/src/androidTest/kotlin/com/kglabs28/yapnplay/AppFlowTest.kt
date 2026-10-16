package com.kglabs28.yapnplay

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso.pressBack
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented flow tests — run on a real device or emulator, where
 * downloadable fonts, real window tiers and real navigation apply:
 *
 *   .\gradlew.bat :androidApp:connectedDebugAndroidTest
 *
 * Written here, executed there: this environment has no emulator,
 * so these are compile-verified only until run on hardware.
 */
@RunWith(AndroidJUnit4::class)
class AppFlowTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun homeShowsBothZones() {
        composeRule.onNodeWithText("ENTER GAME ZONE").assertExists()
        composeRule.onNodeWithText("PLAY WITH FRIENDS").assertExists()
    }

    @Test
    fun navigateToGalleryAndBack() {
        composeRule.onNodeWithText("Component gallery").performClick()
        composeRule.onNodeWithText("All").assertExists()
        pressBack()
        composeRule.onNodeWithText("ENTER GAME ZONE").assertExists()
    }

    @Test
    fun navigateToAboutShowsInfo() {
        composeRule.onNodeWithText("About").performClick()
        composeRule.onNodeWithText("YapNPlay 1.0.0-phase7 · contract 0.2.0").assertExists()
        pressBack()
        composeRule.onNodeWithText("PLAY WITH FRIENDS").assertExists()
    }
}
