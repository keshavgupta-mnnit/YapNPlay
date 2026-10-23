package com.kglabs28.yapnplay.appengine.ui.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/** A thin upright line between the top bar's groups. */
@Composable
fun TopBarDivider(modifier: Modifier = Modifier) {
    val dimens = YapNPlayTheme.dimens
    Box(modifier.width(dimens.hairline).height(dimens.topBarDividerHeight).background(YapNPlayTheme.colors.TextDisabled.copy(alpha = DIVIDER_ALPHA)))
}

private const val DIVIDER_ALPHA = 0.4f
