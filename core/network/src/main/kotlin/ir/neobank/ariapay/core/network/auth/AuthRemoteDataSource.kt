package ir.neobank.ariapay.core.network.auth

interface AuthRemoteDataSource {
    suspend fun requestOtp(mobile: String): AuthRemoteResponse

    suspend fun verifyOtp(mobile: String, code: String): AuthRemoteResponse
}
