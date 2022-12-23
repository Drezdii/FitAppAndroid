package com.bartoszdrozd.fitapp.data.auth

import com.bartoszdrozd.fitapp.domain.auth.RegisterUserParameters
import com.bartoszdrozd.fitapp.utils.ResultValue
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

    override suspend fun register(userData: RegisterUserParameters): ResultValue<RegisterUserResult> {
        val res = service.registerUser(userData)
        return if (res.isSuccessful) {
            ResultValue.Success(res.body()!!)
        } else {
            if (res.code() == 400) {
                // Request was successful but registration failed
                val errors = gson.fromJson(
                    res.errorBody()?.charStream(), RegisterValidationError::class.java
                )
                ResultValue.Success(RegisterUserResult(null, null, errors))
            } else {
                ResultValue.Error(Exception("Something went wrong processing the request."))
            }
        }
    }

    override suspend fun getUserId(): String? {
        return FirebaseAuth.getInstance().currentUser?.uid
    }

    override suspend fun getUserToken(): String {
        val user = FirebaseAuth.getInstance().currentUser ?: return ""
        return user.getIdToken(false).await().token ?: ""
    }
}