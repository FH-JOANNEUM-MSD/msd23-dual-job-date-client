package fh.msd.jobdating.feature.companies.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.RemoveCircleOutline
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fh.msd.jobdating.core.ui.theme.DislikeRed
import fh.msd.jobdating.core.ui.theme.LikeGreen
import fh.msd.jobdating.feature.companies.domain.model.Company
import fh.msd.jobdating.feature.companies.domain.model.VoteType
import fh.msd.jobdating.feature.companies.ui.components.CompanyCard
import fh.msd.jobdating.feature.companies.ui.components.CompanyDetailDialog
import fh.msd.jobdating.feature.companies.ui.components.ConfettiAnimation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.abs

enum class SwipeHint { NONE, LIKE, DISLIKE, NEUTRAL }

val NeutralOrange = Color(0xFFF97316)

@Composable
fun CompanySwipeScreen(
    onNavigateToAppointments: () -> Unit,
    viewModel: CompanySwipeViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        when {
            state.isLoading -> CircularProgressIndicator()
            state.error != null -> Text("Error: ${state.error}")
            state.isDone -> DoneCard(onNavigateToAppointments)
            state.companies.isEmpty() -> Text("No companies available!", style = MaterialTheme.typography.headlineMedium)
            else -> SwipeContent(state, viewModel)
        }
    }
}


@Composable
private fun DoneCard(onNavigateToAppointments: () -> Unit) {
    var showConfetti by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(3000)
        showConfetti = false
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (showConfetti) {
            ConfettiAnimation()
        }

        Card(
            onClick = onNavigateToAppointments,
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp)
                .align(Alignment.Center),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "All companies voted!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "Check your appointments",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Go to appointments",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}




@Composable
private fun SwipeContent(
    state: CompanyListState,
    viewModel: CompanySwipeViewModel
) {
    val company = state.companies[state.currentIndex]
    val nextCompany = state.companies.getOrNull(state.currentIndex + 1)
    var showDetailDialog by remember { mutableStateOf(false) }

    var swipeHint by remember(state.currentIndex) { mutableStateOf(SwipeHint.NONE) }
    var dragProgress by remember(state.currentIndex) { mutableStateOf(0f) }
    var dragOnlyProgress by remember(state.currentIndex) { mutableStateOf(0f) }

    val offsetX = remember(state.currentIndex) { Animatable(0f) }
    val offsetY = remember(state.currentIndex) { Animatable(0f) }
    val rotation = remember(state.currentIndex) { Animatable(0f) }
    val scope = rememberCoroutineScope()

    val triggerSwipe: (VoteType) -> Unit = { voteType ->
        scope.launch {
            dragOnlyProgress = 0f
            when (voteType) {
                VoteType.LIKE -> {
                    swipeHint = SwipeHint.LIKE
                    dragProgress = 1f
                    launch { offsetX.animateTo(1500f, animationSpec = tween(400)) }
                    launch { rotation.animateTo(30f, animationSpec = tween(400)) }
                }
                VoteType.DISLIKE -> {
                    swipeHint = SwipeHint.DISLIKE
                    dragProgress = 1f
                    launch { offsetX.animateTo(-1500f, animationSpec = tween(400)) }
                    launch { rotation.animateTo(-30f, animationSpec = tween(400)) }
                }
                VoteType.NEUTRAL -> {
                    swipeHint = SwipeHint.NEUTRAL
                    dragProgress = 1f
                    launch { offsetY.animateTo(-1500f, animationSpec = tween(400)) }
                }
            }
            kotlinx.coroutines.delay(400)
            swipeHint = SwipeHint.NONE
            dragProgress = 0f
            viewModel.onEvent(CompanyListEvent.Vote(company.id, voteType))
        }
    }

    if (showDetailDialog) {
        CompanyDetailDialog(
            company = company,
            onDismiss = { showDetailDialog = false },
            onVote = { voteType ->
                showDetailDialog = false
                triggerSwipe(voteType)
            }
        )
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.75f)
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (nextCompany != null) {
                CompanyCard(
                    company = nextCompany,
                    modifier = Modifier.fillMaxSize(),
                    isBackground = true
                )
            }

            key(state.currentIndex) {
                SwipeableCompanyCard(
                    company = company,
                    swipeHint = swipeHint,
                    dragProgress = dragProgress,
                    offsetX = offsetX,
                    offsetY = offsetY,
                    rotation = rotation,
                    onSwipe = { voteType ->
                        viewModel.onEvent(CompanyListEvent.Vote(company.id, voteType))
                    },
                    onHintChanged = { hint, progress ->
                        swipeHint = hint
                        dragProgress = progress
                        dragOnlyProgress = progress
                    },
                    onCardClick = { showDetailDialog = true }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        key(state.currentIndex) {
            SwipeActionButtons(
                swipeHint = swipeHint,
                onDislike = { triggerSwipe(VoteType.DISLIKE) },
                onNeutral = { triggerSwipe(VoteType.NEUTRAL) },
                onLike = { triggerSwipe(VoteType.LIKE) }
            )
        }
    }
}

@Composable
private fun SwipeableCompanyCard(
    company: Company,
    swipeHint: SwipeHint,
    dragProgress: Float,
    offsetX: Animatable<Float, *>,
    offsetY: Animatable<Float, *>,
    rotation: Animatable<Float, *>,
    onSwipe: (VoteType) -> Unit,
    onHintChanged: (SwipeHint, Float) -> Unit,
    onCardClick: () -> Unit
) {
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
            .clickable { onCardClick() }
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
            swipeHint = swipeHint,
            dragProgress = dragProgress
        )
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
            highlightColor = DislikeRed,
            onClick = onDislike
        )
        SwipeActionButton(
            icon = Icons.Outlined.RemoveCircleOutline,
            contentDescription = "Neutral",
            highlighted = swipeHint == SwipeHint.NEUTRAL,
            highlightColor = NeutralOrange,
            onClick = onNeutral
        )
        SwipeActionButton(
            icon = Icons.Outlined.CheckCircle,
            contentDescription = "Like",
            highlighted = swipeHint == SwipeHint.LIKE,
            highlightColor = LikeGreen,
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

    Surface(
        onClick = onClick,
        modifier = Modifier.size(size).scale(scale),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp,
        border = if (highlighted) {
            androidx.compose.foundation.BorderStroke(3.dp, highlightColor)
        } else {
            null
        }
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = highlightColor,
                modifier = Modifier.size(size * 0.6f)
            )
        }
    }
}