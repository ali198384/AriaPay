package ir.neobank.ariapay.core.datastore


import kotlinx.coroutines.flow.Flow

interface AriaPreferencesDataSource {
    val isLoggedIn: Flow<Boolean>
    val accessToken: Flow<String?>

    suspend fun saveSession(accessToken: String)
    suspend fun setLoggedIn(value: Boolean)
    suspend fun setAccessToken(token: String?)
    suspend fun clearSession()
}
