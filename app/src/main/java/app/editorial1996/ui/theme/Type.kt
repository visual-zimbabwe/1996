package app.editorial1996.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import app.editorial1996.R

@OptIn(ExperimentalTextApi::class)
val PlayfairDisplayFamily = FontFamily(
    Font(
        resId = R.font.playfair_display,
        weight = FontWeight.Normal,
        style = FontStyle.Normal,
        variationSettings = FontVariation.Settings(FontVariation.weight(400)),
    ),
    Font(
        resId = R.font.playfair_display_italic,
        weight = FontWeight.Normal,
        style = FontStyle.Italic,
        variationSettings = FontVariation.Settings(FontVariation.weight(400)),
    ),
)

@OptIn(ExperimentalTextApi::class)
val InterFamily = FontFamily(
    Font(
        resId = R.font.inter,
        weight = FontWeight.Light,
        variationSettings = FontVariation.Settings(FontVariation.weight(300)),
    ),
    Font(
        resId = R.font.inter,
        weight = FontWeight.Normal,
        variationSettings = FontVariation.Settings(FontVariation.weight(400)),
    ),
    Font(
        resId = R.font.inter,
        weight = FontWeight.Medium,
        variationSettings = FontVariation.Settings(FontVariation.weight(500)),
    ),
)

object EditorialTextStyles {
    val DisplayBrand = TextStyle(
        fontFamily = PlayfairDisplayFamily,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Italic,
        fontSize = 48.sp,
        lineHeight = 52.sp,
        letterSpacing = (-0.96).sp,
    )

    val HeadlineLarge = TextStyle(
        fontFamily = PlayfairDisplayFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
        lineHeight = 34.sp,
        letterSpacing = (-0.28).sp,
    )

    val HeadlineMedium = TextStyle(
        fontFamily = PlayfairDisplayFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
    )

    val HeadlineSmall = TextStyle(
        fontFamily = PlayfairDisplayFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 17.sp,
        lineHeight = 22.sp,
    )

    val BodyLarge = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp,
        letterSpacing = (-0.08).sp,
    )

    val BodyMedium = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 18.sp,
    )

    val LabelCaps = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        lineHeight = 14.sp,
        letterSpacing = 1.2.sp,
    )

    val DateStamp = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.66.sp,
    )
}

val EditorialTypography = Typography(
    displayLarge = EditorialTextStyles.DisplayBrand,
    headlineLarge = EditorialTextStyles.HeadlineLarge,
    headlineMedium = EditorialTextStyles.HeadlineMedium,
    headlineSmall = EditorialTextStyles.HeadlineSmall,
    bodyLarge = EditorialTextStyles.BodyLarge,
    bodyMedium = EditorialTextStyles.BodyMedium,
    labelSmall = EditorialTextStyles.LabelCaps,
)
