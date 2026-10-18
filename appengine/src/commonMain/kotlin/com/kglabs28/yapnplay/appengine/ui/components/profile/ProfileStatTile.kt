package com.kglabs28.yapnplay.appengine.ui.components.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.screens.profile.ProfileStatUi

/** One stat on the profile: the number in gold, its name under it. */
@Composable
fun ProfileStatTile(stat: ProfileStatUi, modifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    val typography = YapNPlayTheme.typography
    Column(
        modifier = modifier
            .background(colors.RoomsSmallButton, RoundedCornerShape(YapNPlayTheme.dimens.cbRowRadius))
            .padding(vertical = Spacing.Xs, horizontal = Spacing.Xxs),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        BasicText(text = stat.value, style = typography.title.copy(color = colors.Gold, fontWeight = FontWeight.Bold))
        BasicText(text = stat.label, style = typography.micro.copy(color = colors.TextSecondary, textAlign = TextAlign.Center), maxLines = 1)
    }
}
