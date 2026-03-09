package noor.serry.rawaa.ui.screens.onboarding.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import noor.serry.designsystem.design.AppTheme
import noor.serry.designsystem.components.BaseButton
import noor.serry.designsystem.components.Text
import noor.serry.rawaa.R
import noor.serry.rawaa.ui.screens.onboarding.OnboardingScreenData

@Composable
fun OnboardingPage(
    onboardingScreenData: OnboardingScreenData,
    titleCoordinates: (LayoutCoordinates) -> Unit,
    isLastScreen: Boolean,
    onClickNext: () -> Unit,
    onClickStartNow: () -> Unit,
    onClickLogin: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(onboardingScreenData.imageResId),
            modifier = Modifier
                .padding(horizontal = 40.dp)
                .padding(top = 134.dp)
                .fillMaxWidth()
                .height(230.dp),
            contentDescription = null,
            contentScale = ContentScale.FillBounds
        )

        Text(
            text = stringResource(onboardingScreenData.titleResId),
            color = AppTheme.color.text,
            style = AppTheme.textStyle.headline.small,
            modifier = Modifier
                .padding(top = 99.dp, start = 32.dp, end = 32.dp)
                .fillMaxWidth()
                .onGloballyPositioned(titleCoordinates),
            textAlign = TextAlign.Center,
        )

        Text(
            text = stringResource(onboardingScreenData.descriptionResId),
            color = AppTheme.color.textSecondary,
            style = AppTheme.textStyle.body.small,
            modifier = Modifier
                .padding(top = 8.dp, start = 32.dp, end = 32.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center,
        )

        Column(modifier = Modifier.padding(top = 40.dp)) {
            if (isLastScreen) {
                BaseButton(
                    text = stringResource(R.string.start_now),
                    onClick = onClickStartNow,
                    modifier = Modifier
                        .padding(horizontal = 32.dp)
                        .height(56.dp)
                        .fillMaxWidth(),
                    roundedCornerSize = 10.dp
                )
                BaseButton(
                    text = stringResource(R.string.already_have_an_account),
                    onClick = onClickLogin,
                    modifier = Modifier
                        .padding(horizontal = 32.dp)
                        .padding(top = 8.dp)
                        .height(56.dp)
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = AppTheme.color.primary,
                            shape = RoundedCornerShape(10.dp)
                        ),
                    backgroundColor = AppTheme.color.bg,
                    textColor = AppTheme.color.primary,
                    roundedCornerSize = 10.dp
                )
            } else {
                BaseButton(
                    text = stringResource(R.string.next),
                    onClick = onClickNext,
                    modifier = Modifier
                        .padding(horizontal = 32.dp)
                        .height(56.dp)
                        .fillMaxWidth(),
                    icon = painterResource(R.drawable.ic_arrow_forward),
                    roundedCornerSize = 10.dp
                )
            }
        }
    }
}