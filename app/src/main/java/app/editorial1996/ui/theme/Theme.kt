package app.editorial1996.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object EditorialColors {
    val CanvasBg = Color(0xFFF0F0F0)
    val PrimaryInk = Color(0xFF1A1A1A)
    val SecondaryInk = Color(0xFF737373)
    val CoverPlaceholder = Color(0xFFE5E5E5)
    val ErrorInk = Color(0xFFBA1A1A)
}

private val EditorialColorScheme = lightColorScheme(
    primary = EditorialColors.PrimaryInk,
    onPrimary = Color.White,
    secondary = EditorialColors.SecondaryInk,
    onSecondary = Color.White,
    background = EditorialColors.CanvasBg,
    onBackground = EditorialColors.PrimaryInk,
    surface = EditorialColors.CanvasBg,
    onSurface = EditorialColors.PrimaryInk,
    surfaceVariant = EditorialColors.CoverPlaceholder,
    onSurfaceVariant = EditorialColors.SecondaryInk,
    error = EditorialColors.ErrorInk,
    onError = Color.White,
)

private val EditorialShapes = Shapes(
    extraSmall = RoundedCornerShape(0.dp),
    small = RoundedCornerShape(0.dp),
    medium = RoundedCornerShape(0.dp),
    large = RoundedCornerShape(0.dp),
    extraLarge = RoundedCornerShape(0.dp),
)

@Composable
fun Editorial1996Theme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = EditorialColorScheme,
        typography = EditorialTypography,
        shapes = EditorialShapes,
        content = content,
    )
}
