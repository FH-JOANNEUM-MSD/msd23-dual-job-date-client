package fh.msd.jobdating.feature.companies.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fh.msd.jobdating.feature.companies.domain.model.VoteType
import fh.msd.jobdating.feature.companies.ui.components.CompanyCard
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.abs

enum class SwipeHint {
    NONE, LIKE, DISLIKE, NEUTRAL
}

@Composable
fun CompanySwipeScreen(
    viewModel: CompanyListViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        when {
            state.isLoading -> CircularProgressIndicator()

            state.error != null -> Text("Error: ${state.error}")

            state.isDone -> Text(
                text = "All companies voted!",
                style = MaterialTheme.typography.headlineMedium
            )

            state.companies.isEmpty() -> Text(
                text = "No Companies available!",
                style = MaterialTheme.typography.headlineMedium
            )

            else -> _CompanySwipeScreen(state, viewModel)
        }
    }
}

@Suppress("ComposableNaming")
@Composable
private fun _CompanySwipeScreen(
    state: CompanyListState,
    viewModel: CompanyListViewModel
) {
    val company = state.companies[state.currentIndex]
    val nextCompany = state.companies.getOrNull(state.currentIndex + 1)

    var swipeHint by remember(state.currentIndex) { mutableStateOf(SwipeHint.NONE) }
    var dragProgress by remember(state.currentIndex) { mutableStateOf(0f) }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.75f),
                contentAlignment = Alignment.Center
            ) {
                if (nextCompany != null) {
                    val nextCardScale = (0.92f + 0.08f * dragProgress).coerceIn(0.92f, 1f)

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .scale(nextCardScale)
                    ) {
                        CompanyCard(
                            company = nextCompany,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                key(state.currentIndex) {
                    SwipeableCompanyCard(
                        company = company,
                        onSwipe = { voteType ->
                            viewModel.onEvent(CompanyListEvent.Vote(company.id, voteType))
                        },
                        onHintChanged = { hint, progress ->
                            swipeHint = hint
                            dragProgress = progress
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            key(state.currentIndex) {
                SwipeActionButtons(
                    swipeHint = swipeHint,
                    onDislike = {
                        viewModel.onEvent(CompanyListEvent.Vote(company.id, VoteType.DISLIKE))
                    },
                    onNeutral = {
                        viewModel.onEvent(CompanyListEvent.Vote(company.id, VoteType.NEUTRAL))
                    },
                    onLike = {
                        viewModel.onEvent(CompanyListEvent.Vote(company.id, VoteType.LIKE))
                    }
                )
            }
        }
    }
}

@Composable
private fun SwipeableCompanyCard(
    company: fh.msd.jobdating.feature.companies.domain.model.Company,
    onSwipe: (VoteType) -> Unit,
    onHintChanged: (SwipeHint, Float) -> Unit
) {
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }
    val rotation = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    val swipeThreshold = 300f
    val verticalSwipeThreshold = 400f

    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                translationX = offsetX.value
                translationY = offsetY.value
                rotationZ = rotation.value
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        scope.launch {
                            val absX = abs(offsetX.value)
                            val absY = abs(offsetY.value)

                            when {
                                offsetY.value < -verticalSwipeThreshold && absY > absX -> {
                                    launch { offsetY.animateTo(-1500f, animationSpec = tween(300)) }
                                    launch {
                                        offsetX.animateTo(
                                            offsetX.value,
                                            animationSpec = tween(300)
                                        )
                                    }
                                    onHintChanged(SwipeHint.NONE, 0f)
                                    onSwipe(VoteType.NEUTRAL)
                                }

                                absX > swipeThreshold -> {
                                    val voteType =
                                        if (offsetX.value > 0) VoteType.LIKE else VoteType.DISLIKE
                                    val targetX = if (offsetX.value > 0) 1500f else -1500f
                                    val targetRotation = if (offsetX.value > 0) 30f else -30f
                                    launch {
                                        offsetX.animateTo(
                                            targetX,
                                            animationSpec = tween(300)
                                        )
                                    }
                                    launch {
                                        rotation.animateTo(
                                            targetRotation,
                                            animationSpec = tween(300)
                                        )
                                    }
                                    onHintChanged(SwipeHint.NONE, 0f)
                                    onSwipe(voteType)
                                }

                                else -> {
                                    launch { offsetX.animateTo(0f, animationSpec = tween(300)) }
                                    launch { offsetY.animateTo(0f, animationSpec = tween(300)) }
                                    launch { rotation.animateTo(0f, animationSpec = tween(300)) }
                                    onHintChanged(SwipeHint.NONE, 0f)
                                }
                            }
                        }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        scope.launch {
                            offsetX.snapTo(offsetX.value + dragAmount.x)
                            offsetY.snapTo(offsetY.value + dragAmount.y)
                            rotation.snapTo(offsetX.value / 20f)

                            val absX = abs(offsetX.value)
                            val absY = abs(offsetY.value)
                            val maxProgress = maxOf(
                                absX / swipeThreshold,
                                absY / verticalSwipeThreshold
                            ).coerceIn(0f, 1f)

                            val hint = when {
                                offsetY.value < -verticalSwipeThreshold * 0.5f && absY > absX -> SwipeHint.NEUTRAL
                                offsetX.value > swipeThreshold * 0.5f && absX >= absY -> SwipeHint.LIKE
                                offsetX.value < -swipeThreshold * 0.5f && absX >= absY -> SwipeHint.DISLIKE
                                else -> SwipeHint.NONE
                            }
                            onHintChanged(hint, maxProgress)
                        }
                    }
                )
            }
    ) {
        CompanyCard(
            company = company,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun SwipeActionButtons(
    swipeHint: SwipeHint,
    onDislike: () -> Unit,
    onNeutral: () -> Unit,
    onLike: () -> Unit
) {
    val dislikeHighlighted = swipeHint == SwipeHint.DISLIKE
    val neutralHighlighted = swipeHint == SwipeHint.NEUTRAL
    val likeHighlighted = swipeHint == SwipeHint.LIKE

    Row(
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SwipeActionButton(
            icon = Icons.Filled.Close,
            contentDescription = "Dislike",
            highlighted = dislikeHighlighted,
            highlightColor = MaterialTheme.colorScheme.error,
            defaultColor = MaterialTheme.colorScheme.onSurfaceVariant,
            onClick = onDislike
        )

        SwipeActionButton(
            icon = Icons.Filled.Star,
            contentDescription = "Neutral",
            highlighted = neutralHighlighted,
            highlightColor = MaterialTheme.colorScheme.tertiary,
            defaultColor = MaterialTheme.colorScheme.onSurfaceVariant,
            onClick = onNeutral,
            size = 48.dp
        )

        SwipeActionButton(
            icon = Icons.Filled.Favorite,
            contentDescription = "Like",
            highlighted = likeHighlighted,
            highlightColor = MaterialTheme.colorScheme.primary,
            defaultColor = MaterialTheme.colorScheme.onSurfaceVariant,
            onClick = onLike
        )
    }
}

@Composable
private fun SwipeActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    highlighted: Boolean,
    highlightColor: androidx.compose.ui.graphics.Color,
    defaultColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
    size: androidx.compose.ui.unit.Dp = 56.dp
) {
    val scale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (highlighted) 1.2f else 1f,
        animationSpec = tween(150)
    )

    val containerColor =
        if (highlighted) highlightColor.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
    val iconColor = if (highlighted) highlightColor else defaultColor

    Surface(
        onClick = onClick,
        modifier = Modifier.size(size).scale(scale),
        shape = CircleShape,
        color = containerColor,
        tonalElevation = if (highlighted) 4.dp else 0.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = iconColor,
                modifier = Modifier.size(size * 0.5f)
            )
        }
    }
}