package com.bartoszdrozd.fitapp.domain.auth

import com.bartoszdrozd.fitapp.data.auth.IUserRepository
import com.bartoszdrozd.fitapp.di.IoDispatcher
import com.bartoszdrozd.fitapp.domain.UseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
class SignInWithCustomTokenUseCase @Inject constructor(
    private val userRepo: IUserRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : UseCase<String, Unit>(dispatcher) {
    override suspend fun execute(token: String) {
        userRepo.signIn(token)
    }
}