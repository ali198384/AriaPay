package ir.neobank.ariapay.feature.auth.domain


data class AuthSession(
    val accessToken: String,
    val userId: String,
) {
    init {
        require(accessToken.isNotBlank())
        require(userId.isNotBlank())
    }

    override fun toString(): String = "AuthSession(accessToken=[REDACTED], userId=[REDACTED])"
}
