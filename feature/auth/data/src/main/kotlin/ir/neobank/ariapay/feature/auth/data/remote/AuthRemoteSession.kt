package ir.neobank.ariapay.feature.auth.data.remote


data class AuthRemoteSession(
    val accessToken: String,
) {
    init {
        require(accessToken.isNotBlank())
    }

    override fun toString(): String = "AuthRemoteSession(accessToken=[REDACTED])"
}
