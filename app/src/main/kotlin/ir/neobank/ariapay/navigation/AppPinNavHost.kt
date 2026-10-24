package ir.neobank.ariapay.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import ir.neobank.ariapay.feature.applock.AppPinRoute

fun NavGraphBuilder.appPinNavHost(navController: NavController) {
    composable(AppRoute.AppPin.route) {
        AppPinRoute(
            onPinAccepted = {
                val previousRoute = navController.previousBackStackEntry
                    ?.destination
                    ?.route

                if (previousRoute == AppRoute.Home.route) {
                    navController.popBackStack()
                } else {
                    navController.navigate(AppRoute.Home.route) {
                        popUpTo(AppRoute.AppPin.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            },
        )
    }
}
