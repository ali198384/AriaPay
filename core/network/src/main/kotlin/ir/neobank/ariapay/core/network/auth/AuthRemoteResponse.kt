package ir.neobank.ariapay.core.network.auth


data class AuthRemoteResponse(
    val session: AuthRemoteSession? = null,
    val error: AuthRemoteError? = null,
) {
    val isSuccess: Boolean get() = error == null

    init {
        require(error == null || session == null) {
            "An authentication response cannot contain both a session and an error."
        }
    }
}
