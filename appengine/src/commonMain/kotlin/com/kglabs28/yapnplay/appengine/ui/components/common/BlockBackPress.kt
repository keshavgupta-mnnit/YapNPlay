package com.kglabs28.yapnplay.appengine.ui.components.common

import androidx.compose.runtime.Composable

/**
 * While [enabled], the system Back gesture/button only calls [onBack]
 * (nothing by default). Used by the blocking overlay so a modal can only
 * be left through its own buttons, or dismissed when it allows that.
 * Other platforms have no system Back.
 */
@Composable
expect fun BlockBackPress(enabled: Boolean, onBack: () -> Unit = {})
