package noor.serry.rawaa.ui.screens.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import noor.serry.rawaa.BackStackProvider
import noor.serry.rawaa.R
import noor.serry.rawaa.ui.navigation.AppRoute
import noor.serry.rawaa.ui.screens.onboarding.component.AnimatedSkipText
import noor.serry.rawaa.ui.screens.onboarding.component.OnboardingIndicators
import noor.serry.rawaa.ui.screens.onboarding.component.OnboardingPage
import org.koin.androidx.compose.koinViewModel
import kotlin.math.max

@Composable
fun OnboardingScreen(viewModel: OnboardingViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()
    OnboardingContent(
        state = state,
        interactionListener = viewModel
    )

    HandleEffects(viewModel.effect)
}

@Composable
private fun OnboardingContent(
    state: OnboardingUiState,
    interactionListener: OnboardingInteractionListener
) {
    val pagerState = rememberPagerState() {
        OnboardingData.screens.size
    }
    var indicatorYOffset = remember { mutableIntStateOf(-1) }
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        AnimatedSkipText(
            isVisible = pagerState.currentPage != OnboardingData.screens.size - 1,
            textId = R.string.skip,
            onClick = interactionListener::onSkipClick,
            modifier = Modifier
                .padding(top = 20.dp, start = 40.dp)
        )

        AnimatedSkipText(
            isVisible = pagerState.currentPage == OnboardingData.screens.size - 1,
            textId = R.string.previous,
            onClick = interactionListener::onSkipClick,
            modifier = Modifier
                .padding(top = 20.dp, end = 32.dp),
            alignment = Alignment.TopEnd
        )

        HorizontalPager(
            pagerState,
            modifier = Modifier
                .fillMaxSize(),
        ) { page ->
            val screenData = OnboardingData.screens[page]
            OnboardingPage(
                screenData,
                titleCoordinates = {
                    if (indicatorYOffset.intValue != -1) return@OnboardingPage
                    with(density) {
                        indicatorYOffset.intValue = it.positionInWindow().y.toDp().value.toInt()
                    }
                },
                isLastScreen = page == OnboardingData.screens.size - 1,
                onClickNext = { pagerState.goToNextPage(scope) },
                onClickStartNow = interactionListener::onClickStartNow,
                onClickLogin = interactionListener::onClickLogin,
            )
        }

        AnimatedVisibility(
            indicatorYOffset.intValue != -1,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .padding(top = max(indicatorYOffset.intValue - 32, 0).dp)
                .fillMaxWidth()
        ) {
            OnboardingIndicators(
                currentIndicatorNumber = pagerState.currentPage,
                numberOfIndicators = 3,
            )
        }
    }
}

@Composable
private fun HandleEffects(effects: Flow<OnboardingEffect>) {
    val navigationBackStack = BackStackProvider.current
    LaunchedEffect(Unit) {
        effects.collectLatest { effect ->
            when (effect) {
                OnboardingEffect.NavigateToLogin -> navigationBackStack.add(AppRoute.Login)
                is OnboardingEffect.ShowError -> {}
            }
        }
    }
}

fun PagerState.goToNextPage(scope: CoroutineScope) {
    if (currentPage < pageCount - 1) {
        scope.launch {
            animateScrollToPage(currentPage + 1,
                animationSpec = tween(500))
        }
    }
}