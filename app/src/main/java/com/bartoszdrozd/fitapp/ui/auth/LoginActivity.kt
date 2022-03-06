package com.bartoszdrozd.fitapp.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bartoszdrozd.fitapp.MainActivity
import com.bartoszdrozd.fitapp.ui.landingpage.OnboardingScreen
import com.bartoszdrozd.fitapp.ui.theme.FitAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FitAppTheme {
                val navController = rememberNavController()
                Surface(Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = "onboarding",
                    ) {
                        composable("onboarding") {
                            OnboardingScreen(navigateToSignIn = { navController.navigate("signin") },
                                navigateToRegister = { navController.navigate("register") })
                        }
                        composable("signin") {
                            SignInScreen(
                                navigateToRegister = {
                                    navController.navigate(
                                        "register"
                                    )
                                },
                                onSignInFinished = { finishAuth() },
                                viewModel = hiltViewModel()
                            )
                        }
                        composable("register") {
                            RegisterScreen(
                                onRegisterFinished = { finishAuth() },
                                viewModel = hiltViewModel()
                            )
                        }
                    }
                }
            }
        }
    }

    private fun finishAuth() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingPreview() {
    FitAppTheme {
        OnboardingScreen(navigateToRegister = {}, navigateToSignIn = {})
    }
}