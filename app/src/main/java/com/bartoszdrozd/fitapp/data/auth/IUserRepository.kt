package com.bartoszdrozd.fitapp.data.auth

import com.bartoszdrozd.fitapp.domain.auth.RegisterUserParameters
import com.bartoszdrozd.fitapp.utils.ResultValue

interface IUserRepository {
    suspend fun signIn(email: String, password: String)
    suspend fun signIn(customToken: String)
    suspend fun register(userData: RegisterUserParameters): ResultValue<RegisterUserResult>
    suspend fun getUserId(): String?
    suspend fun getUserToken(): String
}