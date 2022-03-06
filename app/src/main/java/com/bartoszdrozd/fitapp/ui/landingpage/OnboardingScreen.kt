package com.bartoszdrozd.fitapp.ui.landingpage

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bartoszdrozd.fitapp.R
import com.bartoszdrozd.fitapp.ui.theme.FitAppTheme

@Composable
fun OnboardingScreen(navigateToSignIn: () -> Unit, navigateToRegister: () -> Unit) {
    Surface {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                stringResource(R.string.fitapp_name),
                fontSize = dimensionResource(R.dimen.title_size).value.sp
            )

            Spacer(modifier = Modifier.height(120.dp))

            Button(
                modifier = Modifier.fillMaxWidth(0.5f),
                onClick = { navigateToSignIn() }
            ) {
                Text(stringResource(R.string.sign_in))
            }

            Text(stringResource(R.string.or_divider))

            Button(
                modifier = Modifier.fillMaxWidth(0.5f),
                onClick = { navigateToRegister() }
            ) {
                Text(stringResource(R.string.register))
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 600)
@Composable
fun OnboardingPreview() {
    FitAppTheme {
//        OnboardingScreen()
    }
}