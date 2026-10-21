package com.kglabs28.yapnplay.appengine.core.designsystem

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import com.kglabs28.yapnplay.appengine.resources.Res
import com.kglabs28.yapnplay.appengine.resources.inter_variable
import com.kglabs28.yapnplay.appengine.resources.rajdhani_bold
import com.kglabs28.yapnplay.appengine.resources.rajdhani_medium
import com.kglabs28.yapnplay.appengine.resources.rajdhani_regular
import com.kglabs28.yapnplay.appengine.resources.rajdhani_semibold
import org.jetbrains.compose.resources.Font

/**
 * Font families, common to every platform (commonMain/composeResources/font):
 * Rajdhani for headings and buttons, Inter (variable) for body text.
 */
@Composable
fun headlineFontFamily(): FontFamily = FontFamily(
    Font(Res.font.rajdhani_regular, FontWeight.Normal),
    Font(Res.font.rajdhani_medium, FontWeight.Medium),
    Font(Res.font.rajdhani_semibold, FontWeight.SemiBold),
    Font(Res.font.rajdhani_bold, FontWeight.Bold),
)

@Composable
fun bodyFontFamily(): FontFamily = FontFamily(
    listOf(FontWeight.Normal, FontWeight.Medium, FontWeight.SemiBold, FontWeight.Bold).map { weight ->
        Font(Res.font.inter_variable, weight, variationSettings = FontVariation.Settings(FontVariation.weight(weight.weight)))
    },
)
