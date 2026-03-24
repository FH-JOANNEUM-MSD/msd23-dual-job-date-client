package fh.msd.jobdating.core.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import dualjobdating.composeapp.generated.resources.Res
import dualjobdating.composeapp.generated.resources.background
import dualjobdating.composeapp.generated.resources.background_light
import org.jetbrains.compose.resources.painterResource

@Composable
fun AppBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val bgPainter = if (isSystemInDarkTheme()) {
        painterResource(Res.drawable.background)
    } else {
        painterResource(Res.drawable.background_light)
    }

    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = bgPainter,
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )
        content()
    }
}