package ir.neobank.ariapay.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import ir.neobank.ariapay.feature.home.HomeRoute

fun NavGraphBuilder.homeNavHost(onNavigateToProfile: () -> Unit) {
    composable(AppRoute.Home.route) {
        HomeRoute(onNavigateToProfile = onNavigateToProfile)
    }
}
