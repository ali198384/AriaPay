package ir.neobank.ariapay.core.common.result


sealed class AriaException(
    message: String,
    cause: Throwable? = null,
) : Exception(message, cause) {

    class NoInternet(
        cause: Throwable? = null,
    ) : AriaException("اتصال اینترنت در دسترس نیست.", cause)

    class Timeout(
        cause: Throwable? = null,
    ) : AriaException("زمان پاسخ‌گویی سرور به پایان رسید.", cause)

    class Unauthorized(
        override val message: String = "نشست شما منقضی شده است. دوباره وارد شوید.",
    ) : AriaException(message)

    class Forbidden(
        override val message: String = "دسترسی به این عملیات مجاز نیست.",
    ) : AriaException(message)

    class NotFound(
        override val message: String = "مورد درخواستی پیدا نشد.",
    ) : AriaException(message)

    class RateLimited(
        val retryAfterSeconds: Long? = null,
    ) : AriaException("تعداد درخواست‌ها بیش از حد مجاز است.")

    class Server(
        val httpCode: Int,
        override val message: String = "خطای سمت سرور ($httpCode).",
    ) : AriaException(message)

    class Validation(
        override val message: String,
        val field: String? = null,
    ) : AriaException(message)

    class Business(
        val code: String,
        override val message: String,
    ) : AriaException(message)

    class Unknown(
        cause: Throwable? = null,
        override val message: String = "خطای پیش‌بینی‌نشده رخ داد.",
    ) : AriaException(message, cause)
}
