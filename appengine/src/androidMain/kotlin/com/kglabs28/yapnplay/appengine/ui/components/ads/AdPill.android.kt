package com.kglabs28.yapnplay.appengine.ui.components.ads

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.nativead.NativeAd
import com.kglabs28.yapnplay.appengine.appmanager.helpers.AdsAppManagerHelper
import com.kglabs28.yapnplay.appengine.core.ads.AdPillLook
import com.kglabs28.yapnplay.appengine.core.ads.AdPillView
import com.kglabs28.yapnplay.appengine.core.ads.NativeAdLoader
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import org.koin.compose.koinInject

@Composable
actual fun AdPill(modifier: Modifier, reserveSpace: Boolean) {
    val config: AdsAppManagerHelper = koinInject()
    if (!config.isNativeEnabled()) return
    val context = LocalContext.current
    var ad by remember { mutableStateOf<NativeAd?>(null) }
    DisposableEffect(Unit) {
        val loader = NativeAdLoader(context.applicationContext, config.nativeUnitId()) { ad = it }
        loader.load()
        onDispose {
            loader.cancel()
            ad?.destroy()
        }
    }
    val shown = ad
    if (shown == null && !reserveSpace) return
    val dimens = YapNPlayTheme.dimens
    Box(modifier = modifier.width(dimens.adPillWidth).height(dimens.adPillHeight)) {
        if (shown != null) {
            val look = rememberAdPillLook()
            AndroidView(
                factory = { AdPillView(it, look) },
                update = { it.bind(shown) },
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

/** The pill's look from the theme, in the colours and pixels the Android views take. */
@Composable
private fun rememberAdPillLook(): AdPillLook {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    val density = LocalDensity.current
    return remember(colors, dimens, typography, density) {
        with(density) {
            AdPillLook(
                fillTop = colors.ArenaGlassTop.toArgb(),
                fillBottom = colors.ArenaGlassBottom.toArgb(),
                rim = colors.ArenaGlassRim.toArgb(),
                text = colors.TextPrimary.toArgb(),
                badgeFill = colors.Background.toArgb(),
                actionFill = colors.Gold.toArgb(),
                actionText = colors.Background.toArgb(),
                height = dimens.adPillHeight.roundToPx(),
                icon = dimens.adPillIcon.roundToPx(),
                inset = dimens.adPillInset.roundToPx(),
                rimWidth = dimens.hairline.roundToPx().coerceAtLeast(1),
                textSize = typography.label.fontSize.toPx(),
                smallTextSize = typography.micro.fontSize.toPx(),
            )
        }
    }
}
