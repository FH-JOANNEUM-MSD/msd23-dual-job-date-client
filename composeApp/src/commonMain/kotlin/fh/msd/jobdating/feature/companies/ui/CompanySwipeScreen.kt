package fh.msd.jobdating.feature.companies.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.RemoveCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fh.msd.jobdating.feature.companies.domain.model.Company
import fh.msd.jobdating.feature.companies.domain.model.VoteType
import fh.msd.jobdating.feature.companies.ui.components.CompanyCard
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.abs

enum class SwipeHint { NONE, LIKE, DISLIKE, NEUTRAL }

val NeutralOrange = Color(0xFFF97316)

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
            state.isDone -> Text("All companies voted!", style = MaterialTheme.typography.headlineMedium)
            state.companies.isEmpty() -> Text("No companies available!", style = MaterialTheme.typography.headlineMedium)
            else -> SwipeContent(state, viewModel)
        }
    }
}

@Composable
private fun SwipeContent(
    state: CompanyListState,
    viewModel: CompanyListViewModel
) {
    val company = state.companies[state.currentIndex]
    val nextCompany = state.companies.getOrNull(state.currentIndex + 1)

    var swipeHint by remember(state.currentIndex) { mutableStateOf(SwipeHint.NONE) }
    var dragProgress by remember(state.currentIndex) { mutableStateOf(0f) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.75f),
            contentAlignment = Alignment.Center
        ) {
            if (nextCompany != null) {
                val nextScale = (0.92f + 0.08f * dragProgress).coerceIn(0.92f, 1f)
                Box(modifier = Modifier.fillMaxSize().scale(nextScale)) {
                    CompanyCard(company = nextCompany, modifier = Modifier.fillMaxSize())
                }
            }

            key(state.currentIndex) {
                SwipeableCompanyCard(
                    company = company,
                    swipeHint = swipeHint,
                    dragProgress = dragProgress,
                    onSwipe = { viewModel.onEvent(CompanyListEvent.Vote(company.id, it)) },
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
                onDislike = { viewModel.onEvent(CompanyListEvent.Vote(company.id, VoteType.DISLIKE)) },
                onNeutral = { viewModel.onEvent(CompanyListEvent.Vote(company.id, VoteType.NEUTRAL)) },
                onLike = { viewModel.onEvent(CompanyListEvent.Vote(company.id, VoteType.LIKE)) }
            )
        }
    }
}

@Composable
private fun SwipeableCompanyCard(
    company: Company,
    swipeHint: SwipeHint,
    dragProgress: Float,
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
                                    onHintChanged(SwipeHint.NONE, 0f)
                                    onSwipe(VoteType.NEUTRAL)
                                }
                                absX > swipeThreshold -> {
                                    val vote = if (offsetX.value > 0) VoteType.LIKE else VoteType.DISLIKE
                                    val targetX = if (offsetX.value > 0) 1500f else -1500f
                                    val targetR = if (offsetX.value > 0) 30f else -30f
                                    launch { offsetX.animateTo(targetX, animationSpec = tween(300)) }
                                    launch { rotation.animateTo(targetR, animationSpec = tween(300)) }
                                    onHintChanged(SwipeHint.NONE, 0f)
                                    onSwipe(vote)
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
                            val progress = maxOf(absX / swipeThreshold, absY / verticalSwipeThreshold).coerceIn(0f, 1f)
                            val hint = when {
                                offsetY.value < -verticalSwipeThreshold * 0.5f && absY > absX -> SwipeHint.NEUTRAL
                                offsetX.value > swipeThreshold * 0.5f && absX >= absY -> SwipeHint.LIKE
                                offsetX.value < -swipeThreshold * 0.5f && absX >= absY -> SwipeHint.DISLIKE
                                else -> SwipeHint.NONE
                            }
                            onHintChanged(hint, progress)
                        }
                    }
                )
            }
    ) {
        CompanyCard(
            company = company,
            modifier = Modifier.fillMaxSize(),
            swipeHint = SwipeHint.NONE,
            dragProgress = dragProgress
        )

        SwipeOverlay(swipeHint = swipeHint, dragProgress = dragProgress)
    }
}

@Composable
private fun SwipeActionButtons(
    swipeHint: SwipeHint,
    onDislike: () -> Unit,
    onNeutral: () -> Unit,
    onLike: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SwipeActionButton(
            icon = Icons.Outlined.Cancel,
            contentDescription = "Dislike",
            highlighted = swipeHint == SwipeHint.DISLIKE,
            highlightColor = MaterialTheme.colorScheme.error,
            onClick = onDislike
        )
        SwipeActionButton(
            icon = Icons.Outlined.RemoveCircle,
            contentDescription = "Neutral",
            highlighted = swipeHint == SwipeHint.NEUTRAL,
            highlightColor = NeutralOrange,
            onClick = onNeutral
        )
        SwipeActionButton(
            icon = Icons.Outlined.CheckCircle,
            contentDescription = "Like",
            highlighted = swipeHint == SwipeHint.LIKE,
            highlightColor = MaterialTheme.colorScheme.primary,
            onClick = onLike
        )
    }
}

@Composable
private fun SwipeActionButton(
    icon: ImageVector,
    contentDescription: String,
    highlighted: Boolean,
    highlightColor: Color,
    onClick: () -> Unit,
    size: Dp = 72.dp
) {
    val scale by animateFloatAsState(
        targetValue = if (highlighted) 1.2f else 1f,
        animationSpec = tween(150)
    )

    val iconColor = if (highlighted) highlightColor else MaterialTheme.colorScheme.onSurface

    Surface(
        onClick = onClick,
        modifier = Modifier.size(size).scale(scale),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = iconColor,
                modifier = Modifier.size(size * 0.6f)
            )
        }
    }
}

@Composable
private fun SwipeOverlay(swipeHint: SwipeHint, dragProgress: Float) {
    if (swipeHint == SwipeHint.NONE) return

    val icon = when (swipeHint) {
        SwipeHint.LIKE -> Icons.Outlined.CheckCircle
        SwipeHint.DISLIKE -> Icons.Outlined.Cancel
        SwipeHint.NEUTRAL -> Icons.Outlined.RemoveCircle
        SwipeHint.NONE -> null
    }
    val color = when (swipeHint) {
        SwipeHint.LIKE -> Color(0xFF639922)
        SwipeHint.DISLIKE -> Color(0xFFE24B4A)
        SwipeHint.NEUTRAL -> Color(0xFFF97316)
        SwipeHint.NONE -> Color.Transparent
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .border(
                width = (6f * dragProgress).dp,
                color = color.copy(alpha = dragProgress),
                shape = RoundedCornerShape(16.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        icon?.let {
            Icon(
                imageVector = it,
                contentDescription = null,
                tint = color.copy(alpha = dragProgress),
                modifier = Modifier.size(120.dp)
            )
        }
    }
}