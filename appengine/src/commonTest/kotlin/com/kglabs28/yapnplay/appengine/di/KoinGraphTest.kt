package com.kglabs28.yapnplay.appengine.di

import org.koin.test.check.checkModules
import kotlin.test.Test

/**
 * Verifies the whole dependency graph resolves. Add each new feature
 * module to [appModules] — this test fails if anything is missing.
 */
class KoinGraphTest {

    @Test
    fun `dependency graph resolves`() {
        checkModules {
            modules(appModules)
        }
    }
}
