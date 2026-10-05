package ir.neobank.ariapay.core.network.auth


sealed interface AuthRemoteError {
    data object InvalidMobile : AuthRemoteError
    data object WrongCode : AuthRemoteError
    data object Expired : AuthRemoteError
}
