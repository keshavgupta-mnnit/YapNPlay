package com.kglabs28.yapnplay

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.kglabs28.yapnplay.appengine.YapNPlayRoot
import com.kglabs28.yapnplay.appengine.di.appModules
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Renders the whole app at the three reference sizes without crashing.
 * Hosted in the app module so the manifest activity resolves.
 * Pixel comparison is manual (gallery on the three emulators) until a
 * screenshot library supports AGP 9 — see the catalog comment.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], packageName = "com.kglabs28.yapnplay")
class GalleryRenderTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Before
    fun setUp() {
        if (GlobalContext.getOrNull() == null) {
            startKoin { modules(appModules) }
        }
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    @Config(qualifiers = "+w640dp-h360dp")
    fun `renders at 640x360`() {
        composeRule.setContent { YapNPlayRoot() }
        composeRule.onNodeWithText("YapNPlay").assertExists()
    }

    @Test
    @Config(qualifiers = "+w800dp-h360dp")
    fun `renders at 800x360`() {
        composeRule.setContent { YapNPlayRoot() }
        composeRule.onNodeWithText("YapNPlay").assertExists()
    }

    @Test
    @Config(qualifiers = "+w1280dp-h800dp")
    fun `renders at 1280x800`() {
        composeRule.setContent { YapNPlayRoot() }
        composeRule.onNodeWithText("YapNPlay").assertExists()
    }
}
