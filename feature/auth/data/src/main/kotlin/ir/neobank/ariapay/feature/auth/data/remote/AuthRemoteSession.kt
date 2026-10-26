package ir.neobank.ariapay.feature.auth.data.remote


data class AuthRemoteSession(
    val accessToken: String,
    val userId: String,
) {
    init {
        require(accessToken.isNotBlank())
        require(userId.isNotBlank())
    }

    override fun toString(): String = "AuthRemoteSession(accessToken=[REDACTED], userId=[REDACTED])"
}
