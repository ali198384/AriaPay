package ir.neobank.ariapay.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = AppRoute.AuthGraph.route,
        modifier = modifier,
    ) {
        navigation(
            route = AppRoute.AuthGraph.route,
            startDestination = AppRoute.MobileNumber.route,
        ) {
            mobileNumberNavHost(navController)
            otpNavHost(navController)
        }

        homeNavHost()
    }
}
