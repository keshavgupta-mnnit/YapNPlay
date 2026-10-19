package com.kglabs28.yapnplay.appengine.harness

import com.kglabs28.yapnplay.appengine.appmanager.AppConfigStore
import com.kglabs28.yapnplay.appengine.core.network.BackendSession
import com.kglabs28.yapnplay.appengine.core.network.RealtimeGateway
import com.kglabs28.yapnplay.appengine.core.network.SessionProvider
import com.kglabs28.yapnplay.appengine.di.appModules
import com.kglabs28.yapnplay.appengine.games.mono.data.MonoMatchRepositoryImpl
import com.kglabs28.yapnplay.appengine.games.mono.data.remote.MonoMatchClient
import com.kglabs28.yapnplay.appengine.games.mono.domain.MonoMatchRepository
import com.kglabs28.yapnplay.appengine.games.monochaos.data.MonoChaosMatchRepositoryImpl
import com.kglabs28.yapnplay.appengine.games.monochaos.data.remote.MonoChaosMatchClient
import com.kglabs28.yapnplay.appengine.games.monochaos.domain.MonoChaosMatchRepository
import com.kglabs28.yapnplay.appengine.ui.screens.mono.MatchMonoChaosViewModel
import com.kglabs28.yapnplay.appengine.ui.screens.mono.MatchMonoViewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

/**
 * Builds the real Koin graph and resolves the two match screens' whole
 * dependency chain. A crash here is what a user hits on opening a MONO
 * table, so it is checked from the console rather than left to chance.
 * Run: `./gradlew :appengine:diCheck`
 */
object DiCheckHarness {

    @JvmStatic
    fun main(args: Array<String>) {
        val storeModule = module {
            single<AppConfigStore> {
                object : AppConfigStore {
                    override fun getAll(): Map<String, String> = emptyMap()
                    override fun saveAll(map: Map<String, String>) = Unit
                }
            }
        }
        val koin = startKoin {
            modules(appModules + storeModule)
        }.koin

        val checks = listOf<Pair<String, () -> Any?>>(
            "SessionProvider" to { koin.get<SessionProvider>() },
            "BackendSession" to { koin.get<SessionProvider>() is BackendSession },
            "RealtimeGateway" to { koin.get<RealtimeGateway>() },
            "MonoMatchClient" to { koin.get<MonoMatchClient>() },
            "MonoChaosMatchClient" to { koin.get<MonoChaosMatchClient>() },
            "MonoMatchRepository" to { koin.get<MonoMatchRepository>() },
            "MonoChaosMatchRepository" to { koin.get<MonoChaosMatchRepository>() },
            "MatchMonoViewModel" to { koin.get<MatchMonoViewModel>() },
            "MatchMonoChaosViewModel" to { koin.get<MatchMonoChaosViewModel>() },
        )

        var failures = 0
        checks.forEach { (name, resolve) ->
            try {
                resolve()
                println("OK    $name")
            } catch (e: Exception) {
                failures++
                println("FAIL  $name -> ${rootCause(e)}")
            }
        }
        koin.close()
        if (failures > 0) {
            println("\n$failures binding(s) failed to resolve")
            kotlin.system.exitProcess(1)
        }
        println("\nAll ${checks.size} bindings resolved.")
    }

    private fun rootCause(e: Throwable): String {
        var cause = e
        while (cause.cause != null && cause.cause !== cause) cause = cause.cause!!
        return "${cause::class.simpleName}: ${cause.message?.take(140)}"
    }
}