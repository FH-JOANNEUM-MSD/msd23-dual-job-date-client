package fh.msd.jobdating.feature.companies.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import fh.msd.jobdating.feature.companies.domain.model.VoteType
import fh.msd.jobdating.feature.companies.ui.components.CompanyCard
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.abs

@Composable
fun CompanySwipeScreen(
    viewModel: CompanyListViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

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

            else -> {
                val company = state.companies[state.currentIndex]
                SwipeableCompanyCard(
                    company = company,
                    onSwipe = { voteType ->
                        viewModel.onEvent(CompanyListEvent.Vote(company.id, voteType))
                    }
                )
            }
        }
    }
}

@Composable
fun SwipeableCompanyCard(
    company: fh.msd.jobdating.feature.companies.domain.model.Company,
    onSwipe: (VoteType) -> Unit
) {
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }
    val rotation = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    val swipeThreshold = 300f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.75f)
            .graphicsLayer {
                translationX = offsetX.value
                translationY = offsetY.value
                rotationZ = rotation.value
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        scope.launch {
                            if (abs(offsetX.value) > swipeThreshold) {
                                val voteType = if (offsetX.value > 0) VoteType.LIKE else VoteType.DISLIKE

                                val targetX = if (offsetX.value > 0) 1000f else -1000f
                                launch { offsetX.animateTo(targetX, animationSpec = tween(200)) }
                                launch { rotation.animateTo(if (offsetX.value > 0) 30f else -30f, animationSpec = tween(200)) }

                                onSwipe(voteType)

                                offsetX.snapTo(0f)
                                offsetY.snapTo(0f)
                                rotation.snapTo(0f)
                            } else {
                                launch { offsetX.animateTo(0f, animationSpec = tween(200)) }
                                launch { offsetY.animateTo(0f, animationSpec = tween(200)) }
                                launch { rotation.animateTo(0f, animationSpec = tween(200)) }
                            }
                        }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        scope.launch {
                            offsetX.snapTo(offsetX.value + dragAmount.x)
                            offsetY.snapTo(offsetY.value + dragAmount.y)
                            rotation.snapTo(offsetX.value / 20f)
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