package ir.neobank.ariapay.core.domain.auth


sealed interface AuthError {
    data object InvalidMobile : AuthError
    data object InvalidOtp : AuthError
    data object WrongCode : AuthError
    data object Expired : AuthError
    data object Network : AuthError
}
