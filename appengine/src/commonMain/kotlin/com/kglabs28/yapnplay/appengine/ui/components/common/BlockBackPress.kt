package com.kglabs28.yapnplay.appengine.ui.components.common

import androidx.compose.runtime.Composable

/**
 * While [enabled], the system Back gesture/button does nothing. Used by
 * the blocking overlay so a modal can only be left through its own
 * buttons. Android swallows Back; other platforms have no system Back.
 */
@Composable
expect fun BlockBackPress(enabled: Boolean)
