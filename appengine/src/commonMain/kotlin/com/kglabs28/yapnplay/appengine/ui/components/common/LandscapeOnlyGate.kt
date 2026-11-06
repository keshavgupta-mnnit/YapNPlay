package com.kglabs28.yapnplay.appengine.ui.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Fullscreen
import androidx.compose.material.icons.rounded.ScreenRotation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.common.FullScreen
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import org.koin.compose.koinInject

/**
 * The web on a phone held upright: the app is landscape-only and a browser
 * can't lock the screen, so a "turn your phone" card covers it until the
 * phone is turned. [content] stays running underneath (turning mid-game
 * loses nothing) and gets no taps while covered. Where the browser allows
 * it, "Full screen" also turns the screen sideways by itself; on iPhone
 * Safari (which doesn't) the card says how to add the app to the Home Screen.
 */
@Composable
fun LandscapeOnlyGate(content: @Composable () -> Unit) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        content()
        if (maxWidth < maxHeight) RotatePhoneCard()
    }
}

@Composable
private fun RotatePhoneCard() {
    val fullScreen: FullScreen = koinInject()
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.Background)
            // Swallows every tap so nothing underneath reacts.
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = {})
            .padding(Spacing.Lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.Md, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AppIcon(imageVector = Icons.Rounded.ScreenRotation, tint = colors.Cyan, size = dimens.profileAvatar)
        ShrinkText(text = Strings.rotateTitle, style = YapNPlayTheme.typography.sectionTitle.copy(color = colors.TextPrimary))
        ShrinkText(text = Strings.rotateBody, style = YapNPlayTheme.typography.body.copy(color = colors.TextSecondary), maxLines = BODY_LINES)
        if (fullScreen.canEnter()) {
            PillButton(
                text = Strings.rotateFullScreen,
                onClick = fullScreen::enter,
                gradient = listOf(colors.CtaGlassGreen),
                contentColor = colors.TextPrimary,
                rimColor = colors.CtaGlassGreenRim,
                glassFill = colors.CtaGlassGreen,
                leadingIcon = Icons.Rounded.Fullscreen,
                trailingIcon = null,
                height = dimens.smallButtonHeight,
                textStyle = YapNPlayTheme.typography.sectionTitle,
            )
        } else if (fullScreen.needsHomeScreen()) {
            ShrinkText(text = Strings.rotateHomeScreenHint, style = YapNPlayTheme.typography.body.copy(color = colors.Cyan), maxLines = BODY_LINES)
        }
    }
}

/** The explanation may wrap before it shrinks (a narrow upright phone). */
private const val BODY_LINES = 3
