package com.emanu.nosemueran.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val Cream = Color(0xFFF6F4EC)
val Paper = Color(0xFFFCFBF6)
val LeafGreen = Color(0xFF2F5D3E)
val LeafDark = Color(0xFF1C3B27)
val Sage = Color(0xFF78967C)
val SoftMint = Color(0xFFE4EDDF)
val Mist = Color(0xFFEDF1E8)
val Bark = Color(0xFF33372F)
val Muted = Color(0xFF6C736A)
val Clay = Color(0xFFC07B4C)
val Sunny = Color(0xFFD9A93A)

private val AppColors = lightColorScheme(
    primary = LeafGreen,
    onPrimary = Color.White,
    primaryContainer = SoftMint,
    onPrimaryContainer = LeafDark,
    secondary = Sage,
    onSecondary = Color.White,
    secondaryContainer = Mist,
    onSecondaryContainer = LeafDark,
    tertiary = Clay,
    onTertiary = Color.White,
    background = Cream,
    onBackground = Bark,
    surface = Paper,
    onSurface = Bark,
    surfaceVariant = Mist,
    onSurfaceVariant = Muted,
    outline = Color(0xFFB7C1AF),
    outlineVariant = Color(0xFFDCE1D5),
    error = Color(0xFFB3452F),
    onError = Color.White,
    errorContainer = Color(0xFFF6DED6),
    onErrorContainer = Color(0xFF5C2318)
)

private val AppTypography = Typography(
    displaySmall = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 34.sp,
        lineHeight = 40.sp,
        letterSpacing = (-0.5).sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 34.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 23.sp,
        lineHeight = 29.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 21.sp,
        lineHeight = 27.sp
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 21.sp
    ),
    titleSmall = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 19.sp
    ),
    bodyLarge = TextStyle(fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontSize = 14.sp, lineHeight = 21.sp),
    bodySmall = TextStyle(fontSize = 12.5.sp, lineHeight = 18.sp),
    labelLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 14.sp),
    labelMedium = TextStyle(fontWeight = FontWeight.Medium, fontSize = 12.sp),
    labelSmall = TextStyle(fontWeight = FontWeight.Medium, fontSize = 11.sp, letterSpacing = 1.1.sp)
)

private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(26.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun NoSeMueranTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AppColors,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}
