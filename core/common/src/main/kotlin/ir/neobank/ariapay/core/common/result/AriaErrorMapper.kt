package ir.neobank.ariapay.core.common.result

import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException


object AriaErrorMapper {

    fun fromHttp(
        httpCode: Int,
        bodyMessage: String? = null,
        retryAfterSeconds: Long? = null,
    ): AriaException = when (httpCode) {
        401 -> AriaException.Unauthorized()
        403 -> AriaException.Forbidden()
        404 -> AriaException.NotFound()
        408 -> AriaException.Timeout()
        429 -> AriaException.RateLimited(retryAfterSeconds)
        in 400..499 -> AriaException.Validation(
            message = bodyMessage ?: "اطلاعات ارسال‌شده نامعتبر است.",
        )
        in 500..599 -> AriaException.Server(
            httpCode = httpCode,
            message = bodyMessage ?: "خطای سمت سرور ($httpCode).",
        )
        else -> AriaException.Unknown(message = bodyMessage ?: "خطای پیش‌بینی‌نشده رخ داد.")
    }

    fun fromThrowable(throwable: Throwable): AriaException = when (throwable) {
        is AriaException -> throwable
        is UnknownHostException,
        is ConnectException,
            -> AriaException.NoInternet(throwable)
        is SocketTimeoutException -> AriaException.Timeout(throwable)
        else -> AriaException.Unknown(cause = throwable)
    }
}
