package ir.neobank.ariapay.feature.auth.domain


sealed interface AuthError {
    data object InvalidMobile : AuthError
    data object InvalidOtp : AuthError
    data object WrongCode : AuthError
    data object Expired : AuthError
    data object Network : AuthError
    data object Unknown : AuthError
}
