package ir.neobank.ariapay.core.common.result

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import java.net.UnknownHostException

class AriaExceptionTest {

    @Test
    fun `http 401 becomes Unauthorized`() {
        val error = AriaErrorMapper.fromHttp(401)
        assertThat(error).isInstanceOf(AriaException.Unauthorized::class.java)
    }

    @Test
    fun `http 429 keeps retryAfter`() {
        val error = AriaErrorMapper.fromHttp(429, retryAfterSeconds = 30)
        assertThat(error).isInstanceOf(AriaException.RateLimited::class.java)
        assertThat((error as AriaException.RateLimited).retryAfterSeconds).isEqualTo(30)
    }

    @Test
    fun `unknown host becomes NoInternet`() {
        val error = AriaErrorMapper.fromThrowable(UnknownHostException("dns"))
        assertThat(error).isInstanceOf(AriaException.NoInternet::class.java)
    }

    @Test
    fun `existing AriaException is not wrapped again`() {
        val original = AriaException.Validation("شماره موبایل نامعتبر است.", field = "phone")
        val mapped = AriaErrorMapper.fromThrowable(original)
        assertThat(mapped).isSameInstanceAs(original)
    }
}
