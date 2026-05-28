package fh.msd.jobdating.core.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import dualjobdating.composeapp.generated.resources.Res
import dualjobdating.composeapp.generated.resources.background
import dualjobdating.composeapp.generated.resources.background_light
import dualjobdating.composeapp.generated.resources.logo_light_txt
import dualjobdating.composeapp.generated.resources.logo_txt
import org.jetbrains.compose.resources.painterResource

@Composable
fun AppBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val bgPainter = if (isDark) {
        painterResource(Res.drawable.background)
    } else {
        painterResource(Res.drawable.background_light)
    }
    val logoPainter = if (isDark) {
        painterResource(Res.drawable.logo_txt)
    } else {
        painterResource(Res.drawable.logo_light_txt)
    }

    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = bgPainter,
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )
        Image(
            painter = logoPainter,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 16.dp)
                .windowInsetsPadding(WindowInsets.statusBars)
                .width(160.dp)
        )
        content()
    }
}