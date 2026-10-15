package com.kglabs28.yapnplay.appengine.features.about

import app.cash.turbine.test
import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.core.common.YapNPlayError
import com.kglabs28.yapnplay.appengine.core.testing.TestDispatcherProvider
import com.kglabs28.yapnplay.appengine.features.about.data.AboutRepositoryImpl
import com.kglabs28.yapnplay.appengine.features.about.data.StaticAppInfoSource
import com.kglabs28.yapnplay.appengine.features.about.domain.AboutRepository
import com.kglabs28.yapnplay.appengine.features.about.domain.AppInfo
import com.kglabs28.yapnplay.appengine.features.about.domain.GetAppInfo
import com.kglabs28.yapnplay.appengine.features.about.ui.AboutScreenEffect
import com.kglabs28.yapnplay.appengine.features.about.ui.AboutScreenIntent
import com.kglabs28.yapnplay.appengine.features.about.ui.AboutViewModel
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

private class FakeAboutRepository(private val result: AppResult<AppInfo>) : AboutRepository {
    override suspend fun appInfo(): AppResult<AppInfo> = result
}

/**
 * Sample-feature tests: repository maps data, use case passes through,
 * view model turns results into states and back-navigation into an effect.
 */
class AboutFeatureTest {

    private val info = AppInfo(appName = "YapNPlay", version = "1.0", contractVersion = "0.2.0")
    private val dispatchers = TestDispatcherProvider()

    @Test
    fun `repository returns app info`() = runTest {
        val repo = AboutRepositoryImpl(StaticAppInfoSource())
        val result = repo.appInfo()
        assertIs<AppResult.Success<AppInfo>>(result)
        assertEquals("YapNPlay", result.data.appName)
    }

    @Test
    fun `use case returns repository result`() = runTest {
        val useCase = GetAppInfo(FakeAboutRepository(AppResult.Success(info)))
        assertEquals(AppResult.Success(info), useCase())
    }

    @Test
    fun `load shows info`() = runTest {
        val vm = AboutViewModel(GetAppInfo(FakeAboutRepository(AppResult.Success(info))), dispatchers)
        vm.state.test {
            assertEquals(true, awaitItem().loading)
            vm.onIntent(AboutScreenIntent.Load)
            val loaded = awaitItem()
            assertEquals(false, loaded.loading)
            assertEquals(info, loaded.info)
            assertNull(loaded.error)
            cancelAndIgnoreRemainingEvents()
        }
        vm.clear()
    }

    @Test
    fun `failure shows error`() = runTest {
        val vm = AboutViewModel(
            GetAppInfo(FakeAboutRepository(AppResult.Failure(YapNPlayError.Unknown("boom")))),
            dispatchers,
        )
        vm.state.test {
            awaitItem()
            vm.onIntent(AboutScreenIntent.Load)
            awaitItem() // loading
            val failed = awaitItem()
            assertEquals(false, failed.loading)
            assertEquals(true, failed.error?.isNotEmpty())
            cancelAndIgnoreRemainingEvents()
        }
        vm.clear()
    }

    @Test
    fun `back emits effect once`() = runTest {
        val vm = AboutViewModel(GetAppInfo(FakeAboutRepository(AppResult.Success(info))), dispatchers)
        vm.effects.test {
            vm.onIntent(AboutScreenIntent.GoBack)
            assertEquals(AboutScreenEffect.Back, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        vm.clear()
    }
}
