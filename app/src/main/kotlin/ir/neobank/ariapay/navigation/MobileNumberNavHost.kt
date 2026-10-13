package ir.neobank.ariapay.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import ir.neobank.ariapay.feature.auth.AuthViewModel
import ir.neobank.ariapay.feature.auth.MobileNumberRoute

fun NavGraphBuilder.mobileNumberNavHost(navController: NavController) {
    composable(AppRoute.MobileNumber.route) { backStackEntry ->
        val authGraphEntry = remember(backStackEntry, navController) {
            navController.getBackStackEntry(AppRoute.AuthGraph.route)
        }
        val authViewModel: AuthViewModel = hiltViewModel(authGraphEntry)
        val navigateToOtp: () -> Unit = remember(navController) {
            {
                navController.navigate(AppRoute.Otp.route) {
                    launchSingleTop = true
                }
            }
        }

        MobileNumberRoute(
            viewModel = authViewModel,
            onNavigateToOtp = navigateToOtp,
        )
    }
}
