package com.bartoszdrozd.fitapp.data.auth

import com.bartoszdrozd.fitapp.domain.auth.RegisterUserParameters
import com.bartoszdrozd.fitapp.model.user.User
import com.bartoszdrozd.fitapp.utils.Result

interface IUserRepository {
    suspend fun signIn(email: String, password: String)
    suspend fun signIn(customToken: String)
    suspend fun register(userData: RegisterUserParameters): Result<RegisterUserResult>
    suspend fun getUser(): User?
    suspend fun getUserToken(): String
}