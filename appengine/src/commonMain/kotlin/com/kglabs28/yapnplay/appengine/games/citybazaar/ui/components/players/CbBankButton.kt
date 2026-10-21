package com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.players

import com.kglabs28.yapnplay.appengine.core.ui.CityBazaarStrings
import com.kglabs28.yapnplay.appengine.core.designsystem.AppBrushes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon

/** The bank, as a black button with a glowing gold rim, a bank icon and gold "BANK". */
@Composable
fun CbBankButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val shape = RoundedCornerShape(dimens.ludoGlossRadius)
    val gold = colors.Gold
    Row(
        modifier = modifier
            // Gold light around the black button.
            .shadow(elevation = dimens.bankButtonGlow, shape = shape, ambientColor = gold, spotColor = gold)
            .clip(shape)
            .background(AppBrushes.bankButton)
            .border(dimens.borderThin, Brush.verticalGradient(listOf(gold, gold.copy(alpha = RIM_BOTTOM_ALPHA))), shape)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = Spacing.Sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Xs, Alignment.CenterHorizontally),
    ) {
        AppIcon(imageVector = Icons.Rounded.AccountBalance, tint = gold, size = dimens.iconMedium)
        BasicText(
            text = CityBazaarStrings.cbBank,
            style = YapNPlayTheme.typography.title.copy(color = gold, fontWeight = FontWeight.Bold, shadow = Shadow(color = gold, blurRadius = TEXT_GLOW)),
        )
    }
}

private const val RIM_BOTTOM_ALPHA = 0.55f
private const val TEXT_GLOW = 14f
