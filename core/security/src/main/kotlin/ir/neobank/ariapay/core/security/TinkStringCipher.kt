package ir.neobank.ariapay.core.security

import android.content.Context
import android.util.AtomicFile
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeysetHandle
import com.google.crypto.tink.RegistryConfiguration
import com.google.crypto.tink.TinkProtoKeysetFormat
import com.google.crypto.tink.aead.PredefinedAeadParameters
import com.google.crypto.tink.config.TinkConfig
import com.google.crypto.tink.integration.android.AndroidKeystore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.security.GeneralSecurityException
import java.util.Base64

/** Encrypts small strings with Tink AEAD and protects the Tink keyset with Android Keystore. */
class TinkStringCipher(
    context: Context,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : SecureValueCipher {
    private val keysetFile = AtomicFile(
        File(context.applicationContext.noBackupFilesDir, KEYSET_FILE_NAME),
    )

    private val aead: Aead by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        createAead()
    }

    override suspend fun encrypt(
        plaintext: String,
        purpose: SecureValuePurpose,
    ): String = withContext(ioDispatcher) {
        require(plaintext.isNotEmpty()) { "Value to encrypt must not be empty" }

        val encrypted = aead.encrypt(
            plaintext.toByteArray(StandardCharsets.UTF_8),
            purpose.associatedData.toByteArray(StandardCharsets.UTF_8),
        )
        CIPHERTEXT_PREFIX + Base64.getEncoder().withoutPadding().encodeToString(encrypted)
    }

    override suspend fun decrypt(
        ciphertext: String,
        purpose: SecureValuePurpose,
    ): String = withContext(ioDispatcher) {
        if (!ciphertext.startsWith(CIPHERTEXT_PREFIX)) {
            throw GeneralSecurityException("Unsupported encrypted value format")
        }

        val encrypted = try {
            Base64.getDecoder().decode(ciphertext.removePrefix(CIPHERTEXT_PREFIX))
        } catch (exception: IllegalArgumentException) {
            throw GeneralSecurityException("Encrypted value is malformed", exception)
        }
        val plaintext = aead.decrypt(
            encrypted,
            purpose.associatedData.toByteArray(StandardCharsets.UTF_8),
        )
        String(plaintext, StandardCharsets.UTF_8)
    }

    private fun createAead(): Aead {
        TinkConfig.register()

        val keysetExists = keysetFile.baseFile.exists()
        val masterKeyExists = AndroidKeystore.hasKey(MASTER_KEY_ALIAS)
        if (keysetExists != masterKeyExists) {
            throw GeneralSecurityException(
                "Tink keyset and Android Keystore key are out of sync",
            )
        }

        val masterAead = if (masterKeyExists) {
            AndroidKeystore.getAead(MASTER_KEY_ALIAS)
        } else {
            AndroidKeystore.generateNewAes256GcmKey(MASTER_KEY_ALIAS)
            AndroidKeystore.getAead(MASTER_KEY_ALIAS)
        }

        val keysetHandle = if (keysetExists) {
            val encryptedKeyset = keysetFile.openRead().use { it.readBytes() }
            TinkProtoKeysetFormat.parseEncryptedKeyset(
                encryptedKeyset,
                masterAead,
                KEYSET_ASSOCIATED_DATA,
                RegistryConfiguration.get(),
            )
        } else {
            KeysetHandle.generateNew(
                PredefinedAeadParameters.AES256_GCM,
                RegistryConfiguration.get()
            ).also { handle ->
                val encryptedKeyset = TinkProtoKeysetFormat.serializeEncryptedKeyset(
                    handle,
                    masterAead,
                    KEYSET_ASSOCIATED_DATA,
                    RegistryConfiguration.get()
                )
                writeKeyset(encryptedKeyset)
            }
        }

        return keysetHandle.getPrimitive(RegistryConfiguration.get(), Aead::class.java)
    }

    private fun writeKeyset(encryptedKeyset: ByteArray) {
        val output = keysetFile.startWrite()
        try {
            output.write(encryptedKeyset)
            keysetFile.finishWrite(output)
        } catch (exception: IOException) {
            keysetFile.failWrite(output)
            throw exception
        }
    }

    private companion object {
        const val MASTER_KEY_ALIAS = "ariapay_tink_master_key_v1"
        const val KEYSET_FILE_NAME = "ariapay_tink_keyset_v1.bin"
        val KEYSET_ASSOCIATED_DATA = "ariapay/tink/keyset/v1".toByteArray(StandardCharsets.UTF_8)
        const val CIPHERTEXT_PREFIX = "tink:v1:"
    }
}
