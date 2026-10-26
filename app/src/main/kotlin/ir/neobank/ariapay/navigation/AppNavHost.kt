package ir.neobank.ariapay.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import ir.neobank.ariapay.core.session.AppSessionState
import ir.neobank.ariapay.core.session.SessionManager

@Composable
fun AppNavHost(
    sessionManager: SessionManager,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    val sessionState by sessionManager.state.collectAsStateWithLifecycle()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    BackHandler(enabled = sessionState is AppSessionState.Locked) {
        // Keep the protected destination behind the PIN screen until authentication succeeds.
    }

    LaunchedEffect(sessionState, currentRoute) {
        if (sessionState is AppSessionState.Locked &&
            currentRoute != null &&
            currentRoute != AppRoute.AppPin.route
        ) {
            navController.navigate(AppRoute.AppPin.route) {
                launchSingleTop = true
            }
        }
    }

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
            otpNavHost(navController, sessionManager)
        }

        appPinNavHost(navController)
        homeNavHost()
    }
}
