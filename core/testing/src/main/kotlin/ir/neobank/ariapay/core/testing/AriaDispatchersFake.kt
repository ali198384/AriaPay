package ir.neobank.ariapay.core.testing

import ir.neobank.ariapay.core.common.dispatchers.AriaDispatchers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher

class AriaDispatchersFake(
    private val testDispatcher: TestDispatcher = StandardTestDispatcher(),
) : AriaDispatchers {
    override val io: CoroutineDispatcher = testDispatcher
    override val default: CoroutineDispatcher = testDispatcher
    override val main: CoroutineDispatcher = testDispatcher
}