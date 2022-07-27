package com.bartoszdrozd.fitapp.ui.auth

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bartoszdrozd.fitapp.R
import com.bartoszdrozd.fitapp.domain.auth.RegisterUserParameters
import com.bartoszdrozd.fitapp.ui.auth.RegisterUserErrorCode.*
import com.bartoszdrozd.fitapp.ui.components.OutlinedTextFieldError
import com.bartoszdrozd.fitapp.ui.theme.FitAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(onRegisterFinished: () -> Unit, viewModel: RegisterViewModel) {
    var email by rememberSaveable { mutableStateOf("") }
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordConfirm by rememberSaveable { mutableStateOf("") }

    val context = LocalContext.current

    LaunchedEffect(key1 = 1) {
        viewModel.generalErrorEvent.collect {
            Toast.makeText(context, context.getText(R.string.general_error), Toast.LENGTH_LONG)
                .show()
        }

        viewModel.registerFinishedEvent.collect {
            onRegisterFinished()
        }
    }

    val state by viewModel.uiState.collectAsState()

    val emailError = when (state.emailError) {
        NONE -> ""
        REQUIRED -> stringResource(R.string.required_field)
        FIELD_ERROR -> stringResource(R.string.invalid_email)
        ALREADY_IN_USE -> stringResource(R.string.email_in_use)
    }

    val usernameError = when (state.usernameError) {
        NONE -> ""
        REQUIRED -> stringResource(R.string.required_field)
        FIELD_ERROR -> stringResource(R.string.username_too_short)
        ALREADY_IN_USE -> stringResource(R.string.nickname_in_use)
    }

    val passwordError = when (state.passwordError) {
        NONE -> ""
        REQUIRED -> stringResource(R.string.required_field)
        FIELD_ERROR -> stringResource(R.string.password_too_short)
        else -> ""
    }

    val passwordConfirmError = when (state.passwordConfirmError) {
        NONE -> ""
        REQUIRED -> stringResource(R.string.required_field)
        FIELD_ERROR -> stringResource(R.string.passwords_must_match)
        else -> ""
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            stringResource(R.string.register),
            fontSize = dimensionResource(R.dimen.title_size).value.sp
        )

        Spacer(Modifier.height(20.dp))

        OutlinedTextFieldError(
            value = email,
            onValueChange = {
                email = it
                viewModel.validateEmail(it)
            },
            modifier = Modifier
                .fillMaxWidth(0.8f),
            error = emailError,
            singleLine = true,
            label = { Text(stringResource(R.string.email)) },
            placeholder = { Text(stringResource(R.string.email)) }
        )

        OutlinedTextFieldError(
            value = username,
            onValueChange = {
                username = it
                viewModel.validateUsername(it)
            },
            modifier = Modifier.fillMaxWidth(0.8f),
            singleLine = true,
            error = usernameError,
            label = { Text(stringResource(R.string.username)) },
            placeholder = { Text(stringResource(R.string.username)) }
        )

        OutlinedTextFieldError(
            value = password,
            onValueChange = {
                password = it
                viewModel.validatePassword(it)
            },
            modifier = Modifier.fillMaxWidth(0.8f),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            error = passwordError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            label = { Text(stringResource(R.string.password)) },
            placeholder = { Text(stringResource(R.string.password)) }
        )


        OutlinedTextFieldError(
            value = passwordConfirm,
            onValueChange = {
                passwordConfirm = it
                viewModel.validatePasswordConfirm(password = password, passwordConfirm = it)
            },
            modifier = Modifier.fillMaxWidth(0.8f),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            error = passwordConfirmError,
            label = { Text(stringResource(R.string.password_confirm)) },
            placeholder = { Text(stringResource(R.string.password_confirm)) }
        )

        Spacer(Modifier.height(dimensionResource(R.dimen.vertical_padding)))

        Button(
            onClick = {
                val userData = RegisterUserParameters(
                    email,
                    username,
                    password,
                    passwordConfirm
                )
                viewModel.register(userData)
            },
            Modifier.fillMaxWidth(0.5f),
        ) {
            if (state.isLoading) {
                // Change to an icon
                Text("Loading...")
            } else {
                Text(stringResource(R.string.register))
            }
        }

        TextButton(onClick = { }) {
            Text(stringResource(R.string.sign_in))
        }
    }
}


@Preview(showBackground = true, widthDp = 320, heightDp = 600)
@Composable
fun RegisterScreenPreview() {
    FitAppTheme {
        RegisterScreen(onRegisterFinished = {}, viewModel())
    }
}