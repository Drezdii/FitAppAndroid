package com.bartoszdrozd.fitapp.domain.auth

import com.bartoszdrozd.fitapp.data.auth.IUserRepository
import com.bartoszdrozd.fitapp.data.auth.RegisterUserResult
import com.bartoszdrozd.fitapp.di.IoDispatcher
import com.bartoszdrozd.fitapp.domain.UseCase
import com.bartoszdrozd.fitapp.utils.ResultValue
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class RegisterUserUseCase @Inject constructor(
    private val userRepo: IUserRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : UseCase<RegisterUserParameters, RegisterUserResult>(dispatcher) {
    override suspend fun execute(params: RegisterUserParameters): RegisterUserResult {
        return when (val res = userRepo.register(params)) {
            is ResultValue.Error -> throw res.exception
            is ResultValue.Success -> res.data
            is ResultValue.Loading -> TODO()
        }
    }
}

data class RegisterUserParameters(
    val email: String,
    val username: String,
    val password: String,
    val passwordConfirm: String
)

