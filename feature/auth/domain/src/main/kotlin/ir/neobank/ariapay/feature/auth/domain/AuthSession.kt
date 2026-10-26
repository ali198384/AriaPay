package ir.neobank.ariapay.feature.auth.domain


data class AuthSession(val accessToken: String) {
    init {
        require(accessToken.isNotBlank())
    }

    override fun toString(): String = "AuthSession(accessToken=[REDACTED])"
}
