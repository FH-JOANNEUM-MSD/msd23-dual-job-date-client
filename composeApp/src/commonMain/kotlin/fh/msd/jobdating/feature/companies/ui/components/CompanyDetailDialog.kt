package fh.msd.jobdating.feature.companies.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.RemoveCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import coil3.compose.LocalPlatformContext
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Scale
import fh.msd.jobdating.core.ui.components.dialogs.PlatformDialog
import fh.msd.jobdating.core.ui.theme.DislikeRed
import fh.msd.jobdating.core.ui.theme.LikeGreen
import fh.msd.jobdating.feature.companies.domain.model.Company
import fh.msd.jobdating.feature.companies.domain.model.VoteType
import fh.msd.jobdating.feature.companies.ui.NeutralOrange
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import dualjobdating.composeapp.generated.resources.Res
import dualjobdating.composeapp.generated.resources.company_detail_close
import dualjobdating.composeapp.generated.resources.company_detail_dislike
import dualjobdating.composeapp.generated.resources.company_detail_like
import dualjobdating.composeapp.generated.resources.company_detail_neutral

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CompanyDetailDialog(
    company: Company,
    onDismiss: () -> Unit,
    onVote: (VoteType) -> Unit
) {
    val allImages = buildList {
        if (company.logoUrl.isNotBlank()) add(company.logoUrl)
        addAll(company.imageUrls)
    }

    val shouldUseFallback = allImages.isEmpty()
    val pagerState = rememberPagerState(pageCount = {
        if (shouldUseFallback) 3 else allImages.size
    })

    PlatformDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .systemBarsPadding()
        ) {
            val isLandscape = maxWidth > maxHeight

            if (isLandscape) {
                // ── Landscape: image left, content + actions right ──
                Row(modifier = Modifier.fillMaxSize()) {

                    // Left: image pager
                    Box(
                        modifier = Modifier
                            .weight(0.5f)
                            .fillMaxHeight()
                    ) {
                        ImagePager(
                            allImages = allImages,
                            shouldUseFallback = shouldUseFallback,
                            pagerState = pagerState,
                            company = company,
                            modifier = Modifier.fillMaxSize()
                        )

                        // bottom gradient
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(0.4f)
                                .align(Alignment.BottomCenter)
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, MaterialTheme.colorScheme.surface)
                                    )
                                )
                        )

                        // close button
                        Surface(
                            onClick = onDismiss,
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surface,
                            shadowElevation = 8.dp,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 16.dp, end = 16.dp)
                                .size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = stringResource(Res.string.company_detail_close),
                                    modifier = Modifier.size(20.dp),
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        // dot indicators
                        if (pagerState.pageCount > 1) {
                            Row(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                repeat(pagerState.pageCount) { index ->
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .background(
                                                color = if (pagerState.currentPage == index)
                                                    MaterialTheme.colorScheme.primary
                                                else
                                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                                                shape = CircleShape
                                            )
                                    )
                                }
                            }
                        }
                    }

                    // Right: scrollable content + action buttons
                    Column(
                        modifier = Modifier
                            .weight(0.5f)
                            .fillMaxHeight()
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .verticalScroll(rememberScrollState())
                                .padding(24.dp)
                        ) {
                            Text(
                                text = company.name,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            if (company.website.isNotBlank()) {
                                val uriHandler = LocalUriHandler.current
                                Row(
                                    modifier = Modifier.clickable { uriHandler.openUri(company.website) },
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = company.website,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                            }

                            if (company.description.isNotBlank()) {
                                Text(
                                    text = company.description,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        // Action buttons
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface)
                                .navigationBarsPadding()
                                .padding(vertical = 20.dp, horizontal = 24.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            DetailActionButton(
                                icon = Icons.Outlined.Cancel,
                                contentDescription = stringResource(Res.string.company_detail_dislike),
                                color = DislikeRed,
                                onClick = { onVote(VoteType.DISLIKE) }
                            )
                            DetailActionButton(
                                icon = Icons.Outlined.RemoveCircleOutline,
                                contentDescription = stringResource(Res.string.company_detail_neutral),
                                color = NeutralOrange,
                                onClick = { onVote(VoteType.NEUTRAL) }
                            )
                            DetailActionButton(
                                icon = Icons.Outlined.CheckCircle,
                                contentDescription = stringResource(Res.string.company_detail_like),
                                color = LikeGreen,
                                onClick = { onVote(VoteType.LIKE) }
                            )
                        }
                    }
                }
            } else {
                // ── Portrait: unchanged ──
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(0.5f)
                        ) {
                            ImagePager(
                                allImages = allImages,
                                shouldUseFallback = shouldUseFallback,
                                pagerState = pagerState,
                                company = company,
                                modifier = Modifier.fillMaxSize()
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(0.5f)
                                    .align(Alignment.BottomCenter)
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(Color.Transparent, MaterialTheme.colorScheme.surface)
                                        )
                                    )
                            )

                            Surface(
                                onClick = onDismiss,
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.surface,
                                shadowElevation = 8.dp,
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(top = 24.dp, end = 24.dp)
                                    .size(56.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = stringResource(Res.string.company_detail_close),
                                        modifier = Modifier.size(24.dp),
                                        tint = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            if (pagerState.pageCount > 1) {
                                Row(
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .padding(bottom = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    repeat(pagerState.pageCount) { index ->
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .background(
                                                    color = if (pagerState.currentPage == index)
                                                        MaterialTheme.colorScheme.primary
                                                    else
                                                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                                                    shape = CircleShape
                                                )
                                        )
                                    }
                                }
                            }
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .verticalScroll(rememberScrollState())
                                .padding(24.dp)
                        ) {
                            Text(
                                text = company.name,
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            if (company.website.isNotBlank()) {
                                val uriHandler = LocalUriHandler.current
                                Row(
                                    modifier = Modifier.clickable { uriHandler.openUri(company.website) },
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = company.website,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                            }

                            if (company.description.isNotBlank()) {
                                Text(
                                    text = company.description,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Spacer(modifier = Modifier.height(120.dp))
                        }
                    }

                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
                            .navigationBarsPadding()
                            .padding(vertical = 32.dp, horizontal = 24.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DetailActionButton(
                            icon = Icons.Outlined.Cancel,
                            contentDescription = stringResource(Res.string.company_detail_dislike),
                            color = DislikeRed,
                            onClick = { onVote(VoteType.DISLIKE) }
                        )
                        DetailActionButton(
                            icon = Icons.Outlined.RemoveCircleOutline,
                            contentDescription = stringResource(Res.string.company_detail_neutral),
                            color = NeutralOrange,
                            onClick = { onVote(VoteType.NEUTRAL) }
                        )
                        DetailActionButton(
                            icon = Icons.Outlined.CheckCircle,
                            contentDescription = stringResource(Res.string.company_detail_like),
                            color = LikeGreen,
                            onClick = { onVote(VoteType.LIKE) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ImagePager(
    allImages: List<String>,
    shouldUseFallback: Boolean,
    pagerState: androidx.compose.foundation.pager.PagerState,
    company: Company,
    modifier: Modifier = Modifier
) {
    if (shouldUseFallback) {
        val fallbackImages = CompanyImageProvider.getFallbackImages(company.id)
        HorizontalPager(state = pagerState, modifier = modifier) { page ->
            Image(
                painter = painterResource(fallbackImages[page]),
                contentDescription = "Company Image ${page + 1}",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    } else {
        HorizontalPager(state = pagerState, modifier = modifier) { page ->
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalPlatformContext.current)
                    .data(allImages[page])
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .diskCachePolicy(CachePolicy.DISABLED)
                    .scale(Scale.FIT)
                    .crossfade(true)
                    .build(),
                contentDescription = "Company Image ${page + 1}",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                success = { SubcomposeAsyncImageContent() },
                error = {
                    val fallbackImages = CompanyImageProvider.getFallbackImages(company.id)
                    Image(
                        painter = painterResource(fallbackImages[0]),
                        contentDescription = "Company Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            )
        }
    }
}

@Composable
private fun DetailActionButton(
    icon: ImageVector,
    contentDescription: String,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.size(72.dp),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = color,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}
