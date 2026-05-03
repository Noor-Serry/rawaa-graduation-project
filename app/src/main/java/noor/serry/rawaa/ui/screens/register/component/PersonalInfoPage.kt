package noor.serry.rawaa.ui.screens.register.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import noor.serry.designsystem.components.BaseButton
import noor.serry.designsystem.components.LabelInputField
import noor.serry.designsystem.components.Text
import noor.serry.designsystem.design.AppTheme
import noor.serry.rawaa.R
import noor.serry.rawaa.ui.screens.login.component.ActionBanner

@Composable
fun PersonalInfoPage(
    fullName: String,
    email: String,
    phone: String,
    onFullNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onNext: () -> Unit,
    onGoogleSignUp: () -> Unit,
    onNavigateToLogin: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth(),

        horizontalAlignment = Alignment.CenterHorizontally) {
        // Step label
        Text(
            text = stringResource(R.string.personal_info),
            color = AppTheme.color.primaryDark,
            style = AppTheme.textStyle.headline.small.copy(fontSize = 20.sp)
        )
        Text(
            text = stringResource(R.string.step_1_of_3),
            color = AppTheme.color.textSecondary,
            style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Normal),
            modifier = Modifier.padding(top = 4.dp)
        )

        LabelInputField(
            text = fullName,
            onValueChange = onFullNameChange,
            hintText = stringResource(R.string.enter_full_name),
            label = stringResource(R.string.full_name),
            icon = painterResource(R.drawable.person),
            modifier = Modifier.padding(top = 24.dp)
        )

        LabelInputField(
            text = email,
            onValueChange = onEmailChange,
            hintText = stringResource(R.string.unversity_email),
            label = stringResource(R.string.email),
            icon = painterResource(noor.serry.designsystem.R.drawable.mail),
            modifier = Modifier.padding(top = 16.dp)
        )

        LabelInputField(
            text = phone,
            onValueChange = onPhoneChange,
            hintText = "+20 123 456 7890",
            label = stringResource(R.string.phone),
            icon = painterResource(R.drawable.phone),
            modifier = Modifier.padding(top = 16.dp)
        )

        BaseButton(
            text = stringResource(R.string.next),
            onClick = onNext,
            modifier = Modifier.padding(top = 32.dp),
            roundedCornerSize = 8.dp
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(
                Modifier
                    .height(1.dp)
                    .weight(1f)
                    .align(Alignment.CenterVertically)
                    .background(AppTheme.color.border)
            )
            Text(
                text = stringResource(R.string.or),
                color = AppTheme.color.textSecondary,
                style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Normal),
            )
            Spacer(
                Modifier
                    .height(1.dp)
                    .weight(1f)
                    .align(Alignment.CenterVertically)
                    .background(AppTheme.color.border)
            )
        }

        BaseButton(
            text = stringResource(R.string.sign_up_with_google),
            onClick = onGoogleSignUp,
            modifier = Modifier.padding(top = 12.dp),
            roundedCornerSize = 12.dp,
            icon = painterResource(R.drawable.google),
            backgroundColor = AppTheme.color.bg,
            borderColor = AppTheme.color.border,
            borderWidth = 1.17.dp,
            textStyle = AppTheme.textStyle.body.medium,
            textColor = AppTheme.color.primaryDark,
            isMirror = false
        )

        ActionBanner(
            text = stringResource(R.string.already_have_account),
            actionText = stringResource(R.string.login),
            onActionClick = onNavigateToLogin,
            modifier = Modifier.padding(top = 24.dp)
        )
    }
}
