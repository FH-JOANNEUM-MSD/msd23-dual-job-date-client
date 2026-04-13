package fh.msd.jobdating.feature.companies.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ConfettiAnimation() {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val centerX = maxWidth / 2
        val centerY = maxHeight / 2

        val confettiCount = 100

        val confettiPieces = remember {
            List(confettiCount) { index ->
                val angle = (360f / confettiCount) * index
                val angleRad = angle * (kotlin.math.PI / 180.0).toFloat()
                val distance = (600..1200).random()
                ConfettiPiece(
                    angle = angleRad,
                    distance = distance.dp,
                    color = listOf(
                        Color(0xFFF97316),
                        Color(0xFF10B981),
                        Color(0xFFEF4444),
                        Color(0xFF3B82F6),
                        Color(0xFFFBBF24),
                        Color(0xFFEC4899),
                        Color(0xFF8B5CF6),
                        Color(0xFFF59E0B),
                        Color(0xFF14B8A6)
                    ).random(),
                    size = (12..24).random().dp
                )
            }
        }

        confettiPieces.forEach { piece ->
            val progress = remember { Animatable(0f) }

            LaunchedEffect(Unit) {
                progress.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 5000, easing = FastOutSlowInEasing)
                )
            }

            val currentDistance = piece.distance * progress.value
            val offsetX = centerX + (currentDistance * cos(piece.angle))
            val offsetY = centerY + (currentDistance * sin(piece.angle)) + (piece.distance * progress.value * progress.value * 0.3f)

            val alpha = 1f - progress.value

            Box(
                modifier = Modifier
                    .offset(x = offsetX, y = offsetY)
                    .size(piece.size)
                    .graphicsLayer {
                        this.alpha = alpha
                        rotationZ = progress.value * 1080f
                    }
                    .background(piece.color, shape = CircleShape)
            )
        }
    }
}

private data class ConfettiPiece(
    val angle: Float,
    val distance: Dp,
    val color: Color,
    val size: Dp
)