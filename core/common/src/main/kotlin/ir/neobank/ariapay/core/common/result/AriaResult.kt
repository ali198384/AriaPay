package ir.neobank.ariapay.core.common.result


sealed interface AriaResult<out T> {
    data class Success<out T>(val data: T) : AriaResult<T>
    data class Error(val exception: AriaException) : AriaResult<Nothing>
}

inline fun <T, R> AriaResult<T>.map(transform: (T) -> R): AriaResult<R> = when (this) {
    is AriaResult.Success -> AriaResult.Success(transform(data))
    is AriaResult.Error -> this
}


inline fun <T> AriaResult<T>.getOrElse(default: (AriaException) -> T): T = when (this) {
    is AriaResult.Success -> data
    is AriaResult.Error -> default(exception)
}

inline fun <T> AriaResult<T>.onSuccess(block: (T) -> Unit): AriaResult<T> {
    if (this is AriaResult.Success) block(data)
    return this
}

inline fun <T> AriaResult<T>.onError(block: (AriaException) -> Unit): AriaResult<T> {
    if (this is AriaResult.Error) block(exception)
    return this
}

