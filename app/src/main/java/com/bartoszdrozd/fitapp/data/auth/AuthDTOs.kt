package com.bartoszdrozd.fitapp.data.auth

class InvalidCredentialsException : Exception()

data class RegisterValidationError(val errors: List<ValidationError>)

data class ValidationError(
    val fieldName: String,
    val errorMessage: String?,
    val errorCode: RegisterUserResponseErrorCode,
)

enum class RegisterUserResponseErrorCode {
    GENERIC_ERROR,
    EMAIL_ALREADY_EXISTS,
    PASSWORDS_NOT_EQUAL,
    INVALID_EMAIL,
    USERNAME_TOO_SHORT,
    PASSWORD_TOO_SHORT,
    USERNAME_ALREADY_EXISTS,
    NONE
}

data class RegisterUserResult(
    val id: String?,
    val signInToken: String?,
    val errorsObject: RegisterValidationError?
)