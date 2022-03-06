package com.bartoszdrozd.fitapp.data.auth

import com.bartoszdrozd.fitapp.domain.auth.RegisterUserParameters
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface IAuthService {
    @Headers("Content-Type: application/json")
    @POST("users")
    suspend fun registerUser(@Body user: RegisterUserParameters): Response<RegisterUserResult>
}