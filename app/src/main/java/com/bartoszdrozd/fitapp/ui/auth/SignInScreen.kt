package com.bartoszdrozd.fitapp.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bartoszdrozd.fitapp.R
import com.bartoszdrozd.fitapp.ui.components.OutlinedTextFieldError
import com.bartoszdrozd.fitapp.ui.theme.FitAppTheme

@Composable
fun SignInScreen(
    navigateToRegister: () -> Unit,
    onSignInFinished: () -> Unit,
    viewModel: SignInViewModel
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    val signInError by viewModel.signInError.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.signInFinishedEvent.collect {
            onSignInFinished()
        }
    }

    // TODO: Show Toast on general error
//    val context = LocalContext.current
//    LaunchedEffect(key1 = 1) {
//        viewModel.signInError.collect {
//            Toast.makeText(context, context.getText(R.string.general_error), Toast.LENGTH_LONG)
//                .show()
//        }
//    }

    val passwordError =
        if (signInError == SignInErrorCode.INVALID_CREDENTIALS) stringResource(R.string.signin_invalid_creds) else ""

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            stringResource(R.string.sign_in),
            fontSize = dimensionResource(R.dimen.title_size).value.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth(0.8f),
            singleLine = true,
            label = { Text(stringResource(R.string.email)) },
            placeholder = { Text(stringResource(R.string.email)) }
        )

        OutlinedTextFieldError(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth(0.8f),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            error = passwordError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            label = { Text(stringResource(R.string.password)) },
            placeholder = { Text(stringResource(R.string.password)) }
        )

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.vertical_padding)))

        Button(
            modifier = Modifier.fillMaxWidth(0.5f),
            onClick = { viewModel.signIn(email, password) }
        ) {
            Text(stringResource(R.string.sign_in))
        }

        TextButton(onClick = { navigateToRegister() }) {
            Text(stringResource(R.string.register))
        }
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 600)
@Composable
fun SignInScreenPreview() {
    FitAppTheme {
        SignInScreen(navigateToRegister = {}, onSignInFinished = {}, viewModel())
    }
}