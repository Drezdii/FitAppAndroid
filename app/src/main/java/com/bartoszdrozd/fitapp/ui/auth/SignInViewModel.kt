package com.bartoszdrozd.fitapp.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bartoszdrozd.fitapp.data.auth.InvalidCredentialsException
import com.bartoszdrozd.fitapp.domain.auth.SignInParameters
import com.bartoszdrozd.fitapp.domain.auth.SignInUseCase
import com.bartoszdrozd.fitapp.utils.ResultValue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase
) : ViewModel() {
    private val _signInError = MutableStateFlow(SignInErrorCode.NONE)
    private val _isLoading = MutableStateFlow(false)
    private val _signInFinishedEvent = Channel<Int>()

    val signInError: StateFlow<SignInErrorCode> = _signInError
    val isLoading: StateFlow<Boolean> = _isLoading
    val signInFinishedEvent: Flow<Int> = _signInFinishedEvent.receiveAsFlow()

    fun signIn(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _signInError.value = SignInErrorCode.INVALID_CREDENTIALS
            return
        }

        // Reset errors from the previous attempt
        _signInError.value = SignInErrorCode.NONE
        _isLoading.value = true

        viewModelScope.launch {
            val res = signInUseCase(
                SignInParameters(
                    email,
                    password
                )
            )

            if (res is ResultValue.Error) {
                _signInError.value = if (res.exception is InvalidCredentialsException) {
                    SignInErrorCode.INVALID_CREDENTIALS
                } else {
                    SignInErrorCode.GENERAL_ERROR
                }
                _isLoading.value = false
            } else {
                _signInFinishedEvent.send(1)
            }
        }
    }
}

enum class SignInErrorCode {
    NONE,
    INVALID_CREDENTIALS,
    GENERAL_ERROR
}