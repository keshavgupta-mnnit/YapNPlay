package com.kglabs28.yapnplay.web

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.kglabs28.yapnplay.appengine.YapNPlayRoot
import com.kglabs28.yapnplay.appengine.core.common.AppLog
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.di.appModules
import com.kglabs28.yapnplay.appengine.di.platformAppManagerModule
import com.kglabs28.yapnplay.appengine.ui.components.common.LandscapeOnlyGate
import kotlinx.browser.document
import org.koin.core.context.startKoin

/**
 * The web app's entry point. One address for everything: the page always
 * opens the same way (login screen until this browser has an account, then
 * the main screen); screens change inside the app, never the address.
 */
@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    AppLog.enabled = false
    startKoin { modules(appModules + platformAppManagerModule) }
    ComposeViewport(document.body!!) {
        // The theme here too: the rotate card sits over the app, outside its root.
        YapNPlayTheme { LandscapeOnlyGate { YapNPlayRoot() } }
    }
}
