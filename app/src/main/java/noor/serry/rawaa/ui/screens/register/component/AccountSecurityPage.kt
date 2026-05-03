package noor.serry.rawaa.ui.screens.register.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

@Composable
fun AccountSecurityPage(
    password: String,
    confirmPassword: String,
    passwordsMatch: Boolean,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onRegister: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth(),horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(R.string.account_security),
            color = AppTheme.color.text,
            style = AppTheme.textStyle.headline.small.copy(fontSize = 20.sp)
        )
        Text(
            text = stringResource(R.string.step_3_of_3),
            color = AppTheme.color.textSecondary,
            style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Normal),
            modifier = Modifier.padding(top = 4.dp)
        )

        LabelInputField(
            text = password,
            onValueChange = onPasswordChange,
            hintText = stringResource(R.string.strong_password),
            label = stringResource(R.string.password),
            icon = painterResource(R.drawable.lock),
            isPassword = true,
            modifier = Modifier.padding(top = 24.dp)
        )

        LabelInputField(
            text = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            hintText = stringResource(R.string.re_enter_password),
            label = stringResource(R.string.confirm_password),
            icon = painterResource(R.drawable.lock),
            isPassword = true,
            modifier = Modifier.padding(top = 16.dp),
            borderColor = if (!passwordsMatch && confirmPassword.isNotEmpty())
                AppTheme.color.error else AppTheme.color.border,
            borderFocusedColor = if (!passwordsMatch && confirmPassword.isNotEmpty())
                AppTheme.color.error else AppTheme.color.borderFocus
        )

        AnimatedVisibility(visible = !passwordsMatch && confirmPassword.isNotEmpty()) {
            Text(
                text = stringResource(R.string.passwords_do_not_match),
                color = AppTheme.color.error,
                style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Normal),
                modifier = Modifier.padding(top = 6.dp)
            )
        }

        // Password requirements hint
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .background(Color(0xFFF8FAFC), RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.password_requirements_title),
                color = AppTheme.color.textSecondary,
                style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Normal),
                modifier = Modifier.padding(bottom = 4.dp)
            )
            listOf(
                R.string.password_req_length,
                R.string.password_req_uppercase,
                R.string.password_req_number,
            ).forEach { req ->
                Row(modifier = Modifier.padding(top = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "• ",
                        color = AppTheme.color.textSecondary,
                        style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Normal),
                    )
                    Text(
                        text = stringResource(req),
                        color = AppTheme.color.textSecondary,
                        style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Normal),
                    )
                }
            }
        }

        BaseButton(
            text = stringResource(R.string.create_account),
            onClick = onRegister,
            modifier = Modifier.padding(top = 32.dp),
            roundedCornerSize = 8.dp
        )
    }
}
