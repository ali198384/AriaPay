package ir.neobank.ariapay.feature.auth.data.remote


sealed interface AuthRemoteError {
    data object InvalidMobile : AuthRemoteError
    data object WrongCode : AuthRemoteError
    data object Expired : AuthRemoteError
}
