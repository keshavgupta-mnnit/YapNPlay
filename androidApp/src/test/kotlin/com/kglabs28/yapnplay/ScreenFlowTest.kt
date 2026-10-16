package com.kglabs28.yapnplay

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.kglabs28.yapnplay.appengine.core.common.DispatcherProvider
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.di.aboutModule
import com.kglabs28.yapnplay.appengine.features.about.ui.AboutScreen
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

private class ImmediateDispatchers : DispatcherProvider {
    override val main: CoroutineDispatcher = Dispatchers.Unconfined
    override val io: CoroutineDispatcher = Dispatchers.Unconfined
    override val default: CoroutineDispatcher = Dispatchers.Unconfined
    override val unconfined: CoroutineDispatcher = Dispatchers.Unconfined
}

/**
 * Screen-level integration: real Koin graph, real view models, use cases
 * and repositories — only the data source is static. Eager dispatchers
 * keep every emission deterministic (see STATUS.md).
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], packageName = "com.kglabs28.yapnplay")
class ScreenFlowTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<TestActivity>()

    private val testDispatchers = module {
        single<DispatcherProvider> { ImmediateDispatchers() }
    }

    @Before
    fun setUp() {
        // The Application may already have started the real graph in this
        // process; each test takes full ownership for determinism.
        GlobalContext.getOrNull()?.let { stopKoin() }
        startKoin { modules(listOf(testDispatchers, aboutModule)) }
    }

    @After
    fun tearDown() {
        GlobalContext.getOrNull()?.let { stopKoin() }
    }

    // Routing tests paused on user request — they return when tests do.

    @Test
    fun `about shows real app info end to end`() {
        composeRule.setContent {
            YapNPlayTheme { AboutScreen(onBack = {}) }
        }
        composeRule.onNodeWithText("YapNPlay 1.0.0-phase7 · contract 0.2.0").assertExists()
    }

}
