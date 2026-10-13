package ir.neobank.ariapay.navigation

import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import ir.neobank.ariapay.feature.auth.AuthViewModel
import ir.neobank.ariapay.feature.auth.OtpRoute

fun NavGraphBuilder.otpNavHost(navController: NavController) {
    composable(AppRoute.Otp.route) { backStackEntry ->
        val authGraphEntry = remember(backStackEntry, navController) {
            navController.getBackStackEntry(AppRoute.AuthGraph.route)
        }
        val authViewModel: AuthViewModel = hiltViewModel(authGraphEntry)
        val navigateBack: () -> Unit = remember(navController) {
            { navController.popBackStack() }
        }
        val navigateToHome: () -> Unit = remember(navController) {
            {
                navController.navigate(AppRoute.Home.route) {
                    popUpTo(AppRoute.AuthGraph.route) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        }

        OtpRoute(
            viewModel = authViewModel,
            onBack = navigateBack,
            onNavigateToHome = navigateToHome,
        )
    }
}
