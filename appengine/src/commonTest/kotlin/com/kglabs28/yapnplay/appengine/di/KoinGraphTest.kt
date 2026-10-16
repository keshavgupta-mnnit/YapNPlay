package com.kglabs28.yapnplay.appengine.di

import com.kglabs28.yapnplay.appengine.appmanager.AppConfigStore
import org.koin.dsl.module
import org.koin.test.check.checkModules
import kotlin.test.Test

/**
 * Verifies the whole dependency graph resolves. Add each new feature
 * module to [appModules] — this test fails if anything is missing.
 * The [AppConfigStore] comes from a platform module (same as production),
 * so a fake stands in here.
 */
class KoinGraphTest {

    private val fakeStoreModule = module {
        single<AppConfigStore> {
            object : AppConfigStore {
                override fun getAll(): Map<String, String> = emptyMap()
                override fun saveAll(map: Map<String, String>) = Unit
            }
        }
    }

    @Test
    fun `dependency graph resolves`() {
        checkModules {
            modules(appModules + fakeStoreModule)
        }
    }
}
