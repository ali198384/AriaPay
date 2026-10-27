package ir.neobank.ariapay.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import ir.neobank.ariapay.BuildConfig
import ir.neobank.ariapay.feature.profile.ProfileRoute

fun NavGraphBuilder.profileNavHost(navController: NavController) {
    composable(AppRoute.Profile.route) {
        ProfileRoute(
            appVersionName = BuildConfig.VERSION_NAME,
            onBack = { navController.popBackStack() },
            onNavigateToAuthentication = {
                navController.navigate(AppRoute.AuthGraph.route) {
                    popUpTo(AppRoute.Home.route) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            },
        )
    }
}
