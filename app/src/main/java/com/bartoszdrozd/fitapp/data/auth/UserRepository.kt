package com.bartoszdrozd.fitapp.data.auth

import com.bartoszdrozd.fitapp.domain.auth.RegisterUserParameters
import com.bartoszdrozd.fitapp.utils.Result
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.gson.Gson
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val service: IAuthService,
    private val gson: Gson
) : IUserRepository {
    override suspend fun signIn(email: String, password: String) {
        try {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).await()
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            // Rethrow custom exception to not depend on Firebase classes other than in this repository
            throw InvalidCredentialsException()
        }
    }

    override suspend fun signIn(customToken: String) {
        FirebaseAuth.getInstance().signInWithCustomToken(customToken).await()
    }

    override suspend fun register(userData: RegisterUserParameters): Result<RegisterUserResult> {
        val res = service.registerUser(userData)
        return if (res.isSuccessful) {
            Result.Success(res.body()!!)
        } else {
            if (res.code() == 400) {
                // Request was successful but registration failed
                val errors = gson.fromJson(
                    res.errorBody()?.charStream(), RegisterValidationError::class.java
                )
                Result.Success(RegisterUserResult(null, null, errors))
            } else {
                Result.Error(Exception("Something went wrong processing the request."))
            }
        }
    }
}