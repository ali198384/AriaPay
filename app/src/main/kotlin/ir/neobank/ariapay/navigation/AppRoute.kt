package ir.neobank.ariapay.navigation

/** Central definitions for destinations and nested navigation graphs. */
sealed class AppRoute(val route: String) {
    data object AuthGraph : AppRoute("auth")
    data object MobileNumber : AppRoute("auth/mobile-number")
    data object Otp : AppRoute("auth/otp")
    data object AppPin : AppRoute("app-pin")
    data object Home : AppRoute("home")
}
