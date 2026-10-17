package com.kglabs28.yapnplay.appengine.ui.components.common

import androidx.compose.runtime.Composable

/** JVM (tests/desktop) has no runtime permissions: everything is granted. */
@Composable
actual fun rememberPermissionRequester(permission: AppPermission): PermissionRequester =
    PermissionRequester(isGranted = { true }, request = { it(true) }, openSettings = {})

@Composable
actual fun RequestStartupPermissions() = Unit
