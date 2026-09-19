package red.line.tamirkar.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColors = lightColorScheme(
    primary = RedLinePrimary,
    onPrimary = Color.White,
    secondary = RedLineSecondary,
    background = RedLineBackground,
    surface = Color.White,
    tertiary = VipGold
)

private val DarkColors = darkColorScheme(
    primary = RedLinePrimaryDark,
    onPrimary = Color.White,
    secondary = RedLineSecondary,
    background = RedLineSurfaceDark,
    surface = Color(0xFF1E1E1E),
    tertiary = VipGold
)

@Composable
fun TamirkarTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // با توجه به لوگوی برند، رنگ ثابت پیشنهاد می‌شود
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = TamirkarTypography,
        content = content
    )
}
