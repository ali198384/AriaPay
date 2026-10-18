package ir.neobank.ariapay.core.security

/** Distinguishes encrypted values and binds each ciphertext to its intended use. */
enum class SecureValuePurpose(val associatedData: String) {
    ACCESS_TOKEN("ariapay/access-token/v1"),
    APP_PIN("ariapay/app-pin/v1"),
}

interface SecureValueCipher {
    suspend fun encrypt(plaintext: String, purpose: SecureValuePurpose): String

    suspend fun decrypt(ciphertext: String, purpose: SecureValuePurpose): String
}
