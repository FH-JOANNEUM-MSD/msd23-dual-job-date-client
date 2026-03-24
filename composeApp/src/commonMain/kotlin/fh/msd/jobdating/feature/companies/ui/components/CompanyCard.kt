package fh.msd.jobdating.feature.companies.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.RemoveCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.LocalPlatformContext
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Scale
import fh.msd.jobdating.feature.companies.domain.model.Company
import fh.msd.jobdating.feature.companies.ui.SwipeHint
import androidx.compose.foundation.background
import androidx.compose.material.icons.outlined.DoNotDisturb
import androidx.compose.material.icons.outlined.RemoveCircleOutline

@Composable
fun CompanyCard(
    company: Company,
    modifier: Modifier = Modifier,
    swipeHint: SwipeHint = SwipeHint.NONE,
    dragProgress: Float = 0f
) {
    val borderColor = when (swipeHint) {
        SwipeHint.LIKE -> Color(0xFF639922)
        SwipeHint.DISLIKE -> Color(0xFFE24B4A)
        SwipeHint.NEUTRAL -> Color(0xFFF97316)
        SwipeHint.NONE -> Color.Transparent
    }

    val animatedBorderColor by animateColorAsState(
        targetValue = borderColor,
        animationSpec = tween(150)
    )

    val animatedBorderWidth by animateFloatAsState(
        targetValue = if (swipeHint != SwipeHint.NONE) 6f * dragProgress else 0f,
        animationSpec = tween(150)
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.75f)
            .border(
                width = animatedBorderWidth.dp,
                color = animatedBorderColor,
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        val blurRadius = dragProgress * 8f
                        if (blurRadius > 0f) {
                            renderEffect = BlurEffect(blurRadius, blurRadius, TileMode.Decal)
                        }
                    }
            ) {
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(LocalPlatformContext.current)
                        .data(company.logoUrl)
                        .memoryCachePolicy(CachePolicy.ENABLED)
                        .diskCachePolicy(CachePolicy.DISABLED)
                        .scale(Scale.FIT)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Company Logo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    success = {
                        SubcomposeAsyncImageContent()
                    },
                    error = {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Business,
                                contentDescription = "Logo not available",
                                modifier = Modifier.size(104.dp),
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxSize(0.5f)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.7f)
                                )
                            )
                        )
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    text = company.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = company.description,
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = company.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }

            if (swipeHint != SwipeHint.NONE) {
                val icon = when (swipeHint) {
                    SwipeHint.LIKE -> Icons.Outlined.CheckCircle
                    SwipeHint.DISLIKE -> Icons.Outlined.Cancel
                    SwipeHint.NEUTRAL -> Icons.Outlined.RemoveCircleOutline
                    SwipeHint.NONE -> null
                }
                val iconColor = when (swipeHint) {
                    SwipeHint.LIKE -> Color(0xFF639922)
                    SwipeHint.DISLIKE -> Color(0xFFE24B4A)
                    SwipeHint.NEUTRAL -> Color(0xFFF97316)
                    SwipeHint.NONE -> Color.Transparent
                }
                icon?.let {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        tint = iconColor.copy(alpha = dragProgress),
                        modifier = Modifier
                            .size(120.dp)
                            .align(Alignment.Center)
                    )
                }
            }
        }
    }
}