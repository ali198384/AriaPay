package ir.neobank.ariapay.core.domain.auth


data class AuthSession(val accessToken: String) {
    init {
        require(accessToken.isNotBlank())
    }

    override fun toString(): String = "AuthSession(accessToken=[REDACTED])"
}
