package ir.neobank.ariapay.core.common.result

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class AriaResultTest {

    @Test
    fun `map transforms success data and keeps error`() {
        val success: AriaResult<Int> = AriaResult.Success(10)
        val error: AriaResult<Int> = AriaResult.Error(AriaException.NoInternet())

        assertThat((success.map { it * 2 } as AriaResult.Success).data).isEqualTo(20)
        assertThat(error.map { it * 2 }).isInstanceOf(AriaResult.Error::class.java)
    }

    @Test
    fun `getOrElse returns data or fallback`() {
        val success: AriaResult<String> = AriaResult.Success("ok")
        val error: AriaResult<String> = AriaResult.Error(AriaException.Timeout())

        assertThat(success.getOrElse { "fallback" }).isEqualTo("ok")
        assertThat(error.getOrElse { it.message.orEmpty() })
            .isEqualTo("زمان پاسخ‌گویی سرور به پایان رسید.")
    }
}
