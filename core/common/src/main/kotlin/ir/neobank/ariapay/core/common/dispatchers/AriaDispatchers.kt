package ir.neobank.ariapay.core.common.dispatchers

import kotlinx.coroutines.CoroutineDispatcher

interface AriaDispatchers {
    val io: CoroutineDispatcher
    val default: CoroutineDispatcher
    val main: CoroutineDispatcher
}