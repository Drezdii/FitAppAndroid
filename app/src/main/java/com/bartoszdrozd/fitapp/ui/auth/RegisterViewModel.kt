package com.bartoszdrozd.fitapp.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bartoszdrozd.fitapp.data.auth.RegisterUserResponseErrorCode.*
import com.bartoszdrozd.fitapp.domain.auth.RegisterUserParameters
import com.bartoszdrozd.fitapp.domain.auth.RegisterUserUseCase
import com.bartoszdrozd.fitapp.domain.auth.SignInWithCustomTokenUseCase
import com.bartoszdrozd.fitapp.utils.ResultValue
import com.bartoszdrozd.fitapp.utils.ResultValue.Error
import com.bartoszdrozd.fitapp.utils.ResultValue.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUserUseCase: RegisterUserUseCase,
    private val signInWithCustomTokenUseCase: SignInWithCustomTokenUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(RegisterUserUiState())
    val uiState: StateFlow<RegisterUserUiState> = _uiState

    private val _generalErrorEvent = Channel<Int>()
    private val _registerFinishedEvent = Channel<Int>()
    val generalErrorEvent: Flow<Int> = _generalErrorEvent.receiveAsFlow()
    val registerFinishedEvent: Flow<Int> = _registerFinishedEvent.receiveAsFlow()

    fun register(userData: RegisterUserParameters) {
        // Debounce if already waiting for a result
        if (_uiState.value.isLoading) {
            return
        }

        // Validate all fields before proceeding
        validateForm(userData)

        // Check if current state is equal to the default state of no errors
        if (_uiState.value != RegisterUserUiState()) {
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            when (val res = registerUserUseCase(userData)) {
                is Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    _generalErrorEvent.send(1)
                }
                // Register user use case returns Success when registration fails
                // so we must check if there are errors
                is Success -> {
                    if (res.data.errorsObject == null) {
                        // Registration succeeded
                        // If that's the case, the token is guaranteed to be not null
                        if (signInWithCustomTokenUseCase(res.data.signInToken!!) is Success) {
                            _registerFinishedEvent.send(1)
                        } else {
                            _generalErrorEvent.send(1)
                        }
                    }
                    if (res.data.errorsObject != null) {
                        var currentState = _uiState.value.copy()
                        res.data.errorsObject.errors.forEach { error ->
                            when (error.fieldName) {
                                "email" -> {
                                    when (error.errorCode) {
                                        EMAIL_ALREADY_EXISTS -> currentState =
                                            currentState.copy(emailError = RegisterUserErrorCode.ALREADY_IN_USE)
                                        INVALID_EMAIL -> currentState =
                                            currentState.copy(emailError = RegisterUserErrorCode.FIELD_ERROR)
                                        else -> {}
                                    }
                                }
                                "username" -> {
                                    when (error.errorCode) {
                                        USERNAME_TOO_SHORT -> currentState =
                                            currentState.copy(usernameError = RegisterUserErrorCode.FIELD_ERROR)
                                        USERNAME_ALREADY_EXISTS -> currentState =
                                            currentState.copy(usernameError = RegisterUserErrorCode.ALREADY_IN_USE)
                                        else -> {}
                                    }
                                }
                            }
                        }
                        currentState = currentState.copy(isLoading = false)
                        _uiState.value = currentState
                    }
                }
                is ResultValue.Loading -> TODO()
            }
        }
    }

    private fun validateForm(userData: RegisterUserParameters) {
        validateEmail(userData.email)
        validateUsername(userData.username)
        validatePassword(userData.password)
        validatePasswordConfirm(userData.password, userData.passwordConfirm)
    }

    fun validateEmail(email: String) {
        // TODO: Add proper email validation
        _uiState.value = when {
            email.isEmpty() -> {
                _uiState.value.copy(emailError = RegisterUserErrorCode.REQUIRED)
            }
            email.length < 4 -> {
                _uiState.value.copy(emailError = RegisterUserErrorCode.FIELD_ERROR)
            }
            else -> {
                _uiState.value.copy(emailError = RegisterUserErrorCode.NONE)
            }
        }
    }

    fun validateUsername(nickname: String) {
        _uiState.value = when {
            nickname.isEmpty() -> {
                _uiState.value.copy(usernameError = RegisterUserErrorCode.REQUIRED)
            }
            nickname.length < 3 -> {
                _uiState.value.copy(usernameError = RegisterUserErrorCode.FIELD_ERROR)
            }
            else -> {
                _uiState.value.copy(usernameError = RegisterUserErrorCode.NONE)
            }
        }
    }

    fun validatePassword(password: String) {
        _uiState.value = when {
            password.isEmpty() -> {
                _uiState.value.copy(passwordError = RegisterUserErrorCode.REQUIRED)
            }
            password.length < 6 -> {
                _uiState.value.copy(passwordError = RegisterUserErrorCode.FIELD_ERROR)
            }
            else -> {
                _uiState.value.copy(passwordError = RegisterUserErrorCode.NONE)
            }
        }
    }

    fun validatePasswordConfirm(password: String, passwordConfirm: String) {
        _uiState.value = when {
            passwordConfirm.isEmpty() -> {
                _uiState.value.copy(passwordConfirmError = RegisterUserErrorCode.REQUIRED)
            }
            password != passwordConfirm -> {
                _uiState.value.copy(passwordConfirmError = RegisterUserErrorCode.FIELD_ERROR)
            }
            else -> {
                _uiState.value.copy(passwordConfirmError = RegisterUserErrorCode.NONE)
            }
        }
    }
}

data class RegisterUserUiState(
    val emailError: RegisterUserErrorCode = RegisterUserErrorCode.NONE,
    val usernameError: RegisterUserErrorCode = RegisterUserErrorCode.NONE,
    val passwordError: RegisterUserErrorCode = RegisterUserErrorCode.NONE,
    val passwordConfirmError: RegisterUserErrorCode = RegisterUserErrorCode.NONE,
    val isLoading: Boolean = false
)

enum class RegisterUserErrorCode {
    NONE,
    REQUIRED,

    // Error unique to that field (e.g Username is too short)
    FIELD_ERROR,
    ALREADY_IN_USE
}