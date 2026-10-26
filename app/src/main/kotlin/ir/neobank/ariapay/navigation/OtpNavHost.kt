package ir.neobank.ariapay.navigation

import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import ir.neobank.ariapay.core.session.SessionManager
import ir.neobank.ariapay.feature.auth.AuthViewModel
import ir.neobank.ariapay.feature.auth.OtpRoute

fun NavGraphBuilder.otpNavHost(
    navController: NavController,
    sessionManager: SessionManager,
) {
    composable(AppRoute.Otp.route) { backStackEntry ->
        val authGraphEntry = remember(backStackEntry) {
            navController.getBackStackEntry(AppRoute.AuthGraph.route)
        }
        val authViewModel: AuthViewModel = hiltViewModel(authGraphEntry)
        OtpRoute(
            viewModel = authViewModel,
            onBack = { navController.popBackStack() },
            onNavigateToHome = { userId ->
                sessionManager.startSession(userId)
                navController.navigate(AppRoute.AppPin.route) {
                    popUpTo(AppRoute.AuthGraph.route) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        )
    }
}
