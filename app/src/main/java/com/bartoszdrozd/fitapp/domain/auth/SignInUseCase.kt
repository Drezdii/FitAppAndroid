package com.bartoszdrozd.fitapp.domain.auth

import com.bartoszdrozd.fitapp.data.auth.IUserRepository
import com.bartoszdrozd.fitapp.data.auth.InvalidCredentialsException
import com.bartoszdrozd.fitapp.di.IoDispatcher
import com.bartoszdrozd.fitapp.domain.UseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class SignInUseCase @Inject constructor(
    private val userRepo: IUserRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : UseCase<SignInParameters, Unit>(dispatcher) {
    override suspend fun execute(params: SignInParameters) {
        if (params.email.isEmpty() || params.password.isEmpty()) {
            throw InvalidCredentialsException()
        }
        userRepo.signIn(params.email, params.password)
    }
}

data class SignInParameters(
    val email: String,
    val password: String
)

