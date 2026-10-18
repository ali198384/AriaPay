package ir.neobank.ariapay.core.security

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.security.GeneralSecurityException

@RunWith(AndroidJUnit4::class)
class TinkStringCipherTest {

    private fun cipher(): TinkStringCipher = TinkStringCipher(
        InstrumentationRegistry.getInstrumentation().targetContext.applicationContext
    )

    @Test
    fun encryptedValueCanBeDecryptedWithTheSamePurpose() = runBlocking {
        val cipher = cipher()
        val token = "test-access-token"

        val encrypted = cipher.encrypt(token, SecureValuePurpose.ACCESS_TOKEN)

        assertTrue(encrypted.startsWith("tink:v1:"))
        assertNotEquals(token, encrypted)
        assertEquals(token, cipher.decrypt(encrypted, SecureValuePurpose.ACCESS_TOKEN))
    }

    @Test
    fun encryptedValueCannotBeDecryptedForAnotherPurpose() {
        val cipher = cipher()
        val encrypted = runBlocking {
            cipher.encrypt("123456", SecureValuePurpose.APP_PIN)
        }

        assertThrows(GeneralSecurityException::class.java) {
            runBlocking {
                cipher.decrypt(encrypted, SecureValuePurpose.ACCESS_TOKEN)
            }
        }
    }

    @Test
    fun encryptedKeysetCanBeReopenedByAnotherCipherInstance() = runBlocking {
        val token = "test-token-after-recreation"
        val encrypted = cipher().encrypt(token, SecureValuePurpose.ACCESS_TOKEN)

        assertEquals(
            token,
            cipher().decrypt(encrypted, SecureValuePurpose.ACCESS_TOKEN),
        )
    }
}
