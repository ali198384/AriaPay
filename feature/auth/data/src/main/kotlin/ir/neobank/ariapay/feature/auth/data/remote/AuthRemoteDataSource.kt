package ir.neobank.ariapay.feature.auth.data.remote

interface AuthRemoteDataSource {
    suspend fun requestOtp(mobile: String): AuthRemoteResponse

    suspend fun verifyOtp(mobile: String, code: String): AuthRemoteResponse
}
