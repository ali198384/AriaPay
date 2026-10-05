package ir.neobank.ariapay.core.network.auth


data class AuthRemoteSession(
    val accessToken: String,
) {
    init {
        require(accessToken.isNotBlank())
    }

    override fun toString(): String = "AuthRemoteSession(accessToken=[REDACTED])"
}
