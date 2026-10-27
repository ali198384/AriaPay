package ir.neobank.ariapay.feature.auth

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import ir.neobank.ariapay.feature.auth.domain.AuthError
import ir.neobank.ariapay.feature.auth.domain.repository.AuthRepository
import ir.neobank.ariapay.feature.auth.domain.AuthResult
import ir.neobank.ariapay.feature.auth.domain.AuthSession
import ir.neobank.ariapay.feature.auth.domain.MobileNumber
import ir.neobank.ariapay.feature.auth.domain.OtpCode
import ir.neobank.ariapay.feature.auth.domain.usecase.RequestOtpUseCase
import ir.neobank.ariapay.feature.auth.domain.usecase.VerifyOtpUseCase
import ir.neobank.ariapay.core.testing.MainDispatcherExtension
import ir.neobank.ariapay.feature.auth.contract.AuthEffect
import ir.neobank.ariapay.feature.auth.contract.AuthEvent
import ir.neobank.ariapay.feature.auth.contract.AuthStep
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.time.Duration.Companion.milliseconds

@ExtendWith(MainDispatcherExtension::class)
@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    @Test
    fun `initial state is phone step`() = runTest {
        val vm = createVm()

        vm.uiState.test {
            val s = awaitItem()
            assertThat(s.step).isEqualTo(AuthStep.MOBILE_NUMBER)
            assertThat(s.isLoading).isFalse()
            assertThat(s.mobileNumber).isEmpty()
            assertThat(s.otpCode).isEmpty()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `invalid mobile shows an error without calling repository`() = runTest {
        val repo = FakeAuthRepository()
        val vm = createVm(repo)

        vm.onEvent(AuthEvent.MobileNumberChanged("09123"))
        vm.onEvent(AuthEvent.ContinueClicked)
        runCurrent()

        vm.uiState.test {
            val s = expectMostRecentItem()
            assertThat(s.step).isEqualTo(AuthStep.MOBILE_NUMBER)
            assertThat(s.error).isEqualTo(AuthError.InvalidMobile)
            assertThat(s.isLoading).isFalse()
            assertThat(repo.requestCount).isEqualTo(0)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `continue requests otp with normalized mobile and emits navigation effect`() = runTest {
        val repo = FakeAuthRepository()
        val vm = createVm(repo)

        vm.onEvent(AuthEvent.MobileNumberChanged("۰۹۱۲۳۴۵۶۷۸۹"))
        vm.onEvent(AuthEvent.ContinueClicked)

        vm.effect.test {
            runCurrent()
            assertThat(awaitItem()).isEqualTo(AuthEffect.NavigateToOtp)
            cancelAndIgnoreRemainingEvents()
        }
        vm.uiState.test {
            val s = expectMostRecentItem()
            assertThat(s.step).isEqualTo(AuthStep.OTP)
            assertThat(s.isLoading).isFalse()
            assertThat(repo.requestCount).isEqualTo(1)
            assertThat(repo.lastMobile).isEqualTo("09123456789")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `request failure keeps mobile step and exposes repository error`() = runTest {
        val repo = FakeAuthRepository(
            requestResult = { AuthResult.Failure(AuthError.Network) },
        )
        val vm = createVm(repo)

        vm.onEvent(AuthEvent.MobileNumberChanged("09123456789"))
        vm.onEvent(AuthEvent.ContinueClicked)
        runCurrent()

        vm.uiState.test {
            val state = expectMostRecentItem()
            assertThat(state.step).isEqualTo(AuthStep.MOBILE_NUMBER)
            assertThat(state.error).isEqualTo(AuthError.Network)
            assertThat(state.isLoading).isFalse()
            assertThat(repo.requestCount).isEqualTo(1)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `invalid otp is rejected without calling repository`() = runTest {
        val repo = FakeAuthRepository()
        val vm = createVm(repo)
        vm.onEvent(AuthEvent.MobileNumberChanged("09123456789"))
        vm.onEvent(AuthEvent.ContinueClicked)
        runCurrent()
        vm.onEvent(AuthEvent.OtpCodeChanged("123"))

        vm.onEvent(AuthEvent.VerifyClicked)
        runCurrent()

        vm.uiState.test {
            val state = expectMostRecentItem()
            assertThat(state.step).isEqualTo(AuthStep.OTP)
            assertThat(state.error).isEqualTo(AuthError.InvalidOtp)
            assertThat(state.isLoading).isFalse()
            assertThat(repo.verifyCount).isEqualTo(0)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `wrong otp response keeps user on otp step`() = runTest {
        val repo = FakeAuthRepository(
            verifyResult = { AuthResult.Failure(AuthError.WrongCode) },
        )
        val vm = createVm(repo)
        vm.onEvent(AuthEvent.MobileNumberChanged("۰۹۱۲۳۴۵۶۷۸۹"))
        vm.onEvent(AuthEvent.ContinueClicked)
        runCurrent()
        vm.onEvent(AuthEvent.OtpCodeChanged("۱۲۳۴۵۶"))

        vm.onEvent(AuthEvent.VerifyClicked)
        runCurrent()

        vm.uiState.test {
            val state = expectMostRecentItem()
            assertThat(state.step).isEqualTo(AuthStep.OTP)
            assertThat(state.error).isEqualTo(AuthError.WrongCode)
            assertThat(state.isLoading).isFalse()
            assertThat(repo.verifyCount).isEqualTo(1)
            assertThat(repo.lastMobile).isEqualTo("09123456789")
            assertThat(repo.lastOtp).isEqualTo("123456")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `successful verification authenticates and emits home navigation`() = runTest {
        val repo = FakeAuthRepository()
        val vm = createVm(repo)
        vm.onEvent(AuthEvent.MobileNumberChanged("۰۹۱۲۳۴۵۶۷۸۹"))
        vm.onEvent(AuthEvent.ContinueClicked)
        runCurrent()
        vm.onEvent(AuthEvent.OtpCodeChanged("۱۲۳۴۵۶"))

        vm.effect.test {
            vm.onEvent(AuthEvent.OtpCodeCompleted("۱۲۳۴۵۶"))
            runCurrent()

            assertThat(awaitItem()).isEqualTo(AuthEffect.NavigateToHome("demo-user"))
            cancelAndIgnoreRemainingEvents()
        }

        vm.uiState.test {
            val state = expectMostRecentItem()
            assertThat(state.step).isEqualTo(AuthStep.AUTHENTICATED)
            assertThat(state.otpCode).isEmpty()
            assertThat(state.isLoading).isFalse()
            assertThat(repo.verifyCount).isEqualTo(1)
            assertThat(repo.lastMobile).isEqualTo("09123456789")
            assertThat(repo.lastOtp).isEqualTo("123456")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `back from otp clears otp and returns to mobile step`() = runTest {
        val vm = createVm()
        vm.onEvent(AuthEvent.MobileNumberChanged("09123456789"))
        vm.onEvent(AuthEvent.ContinueClicked)
        runCurrent()
        vm.onEvent(AuthEvent.OtpCodeChanged("123456"))

        vm.onEvent(AuthEvent.BackFromOtp)

        vm.uiState.test {
            val state = expectMostRecentItem()
            assertThat(state.step).isEqualTo(AuthStep.MOBILE_NUMBER)
            assertThat(state.otpCode).isEmpty()
            assertThat(state.isLoading).isFalse()
            assertThat(state.error).isNull()
            assertThat(vm.resendSecondsLeft.value).isEqualTo(30)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `continuing with the same phone reuses otp and keeps countdown running`() = runTest {
        val repo = FakeAuthRepository()
        val vm = createVm(repo)
        vm.onEvent(AuthEvent.MobileNumberChanged("09123456789"))
        vm.onEvent(AuthEvent.ContinueClicked)
        runCurrent()
        advanceTimeBy(5_000.milliseconds)
        runCurrent()
        val secondsBeforeBack = vm.resendSecondsLeft.value

        vm.onEvent(AuthEvent.BackFromOtp)
        vm.onEvent(AuthEvent.ContinueClicked)
        runCurrent()

        assertThat(repo.requestCount).isEqualTo(1)
        assertThat(vm.uiState.value.step).isEqualTo(AuthStep.OTP)
        assertThat(vm.resendSecondsLeft.value).isEqualTo(secondsBeforeBack)
    }

    @Test
    fun `changing phone resets old countdown and requests a new otp`() = runTest {
        val repo = FakeAuthRepository()
        val vm = createVm(repo)
        vm.onEvent(AuthEvent.MobileNumberChanged("09123456789"))
        vm.onEvent(AuthEvent.ContinueClicked)
        runCurrent()

        vm.onEvent(AuthEvent.BackFromOtp)
        vm.onEvent(AuthEvent.MobileNumberChanged("09123456780"))
        assertThat(vm.resendSecondsLeft.value).isEqualTo(0)
        vm.onEvent(AuthEvent.ContinueClicked)
        runCurrent()

        assertThat(repo.requestCount).isEqualTo(2)
        assertThat(repo.lastMobile).isEqualTo("09123456780")
        assertThat(vm.resendSecondsLeft.value).isEqualTo(30)
    }

    @Test
    fun `expired otp is cleared and disabled until resend succeeds`() = runTest {
        val repo = FakeAuthRepository()
        val vm = createVm(repo)

        vm.otpUiState.test {
            awaitItem()
            vm.onEvent(AuthEvent.MobileNumberChanged("09123456789"))
            vm.onEvent(AuthEvent.ContinueClicked)
            runCurrent()
            expectMostRecentItem()

            vm.onEvent(AuthEvent.OtpCodeChanged("123456"))
            advanceTimeBy(RESEND_TEST_TIMEOUT_MILLIS.milliseconds)
            runCurrent()
            val expiredState = expectMostRecentItem()
            assertThat(expiredState.isOtpInputEnabled).isFalse()
            assertThat(vm.uiState.value.otpCode).isEmpty()

            vm.onEvent(AuthEvent.OtpCodeChanged("654321"))
            assertThat(vm.uiState.value.otpCode).isEmpty()

            vm.onEvent(AuthEvent.ResendCodeClicked)
            runCurrent()
            val resentState = expectMostRecentItem()
            assertThat(repo.requestCount).isEqualTo(2)
            assertThat(resentState.isOtpInputEnabled).isTrue()
            assertThat(vm.resendSecondsLeft.value).isEqualTo(30)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `resend countdown ticks do not emit a new otp form state`() = runTest {
        val vm = createVm()

        vm.otpUiState.test {
            assertThat(awaitItem().canVerify).isFalse()
            vm.onEvent(AuthEvent.MobileNumberChanged("09123456789"))
            vm.onEvent(AuthEvent.ContinueClicked)
            runCurrent()

            val otpState = expectMostRecentItem()
            assertThat(otpState.infoMessage).isEqualTo("کد تأیید ارسال شد")

            vm.resendSecondsLeft.test {
                assertThat(awaitItem()).isEqualTo(30)
                advanceTimeBy(1_000.milliseconds)
                runCurrent()
                assertThat(awaitItem()).isEqualTo(29)
            }

            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `resend is blocked during cooldown and clears old code when allowed`() = runTest {
        val repo = FakeAuthRepository()
        val vm = createVm(repo)
        vm.onEvent(AuthEvent.MobileNumberChanged("09123456789"))
        vm.onEvent(AuthEvent.ContinueClicked)
        runCurrent()
        vm.onEvent(AuthEvent.OtpCodeChanged("123456"))

        vm.onEvent(AuthEvent.ResendCodeClicked)
        runCurrent()
        assertThat(repo.requestCount).isEqualTo(1)
        assertThat(vm.uiState.value.otpCode).isEqualTo("123456")

        advanceTimeBy(30_000.milliseconds)
        runCurrent()
        vm.onEvent(AuthEvent.ResendCodeClicked)
        runCurrent()

        assertThat(repo.requestCount).isEqualTo(2)
        assertThat(vm.uiState.value.otpCode).isEmpty()
        assertThat(vm.resendSecondsLeft.value).isEqualTo(30)
    }

    private fun createVm(
        repo: FakeAuthRepository = FakeAuthRepository()
    ) = AuthViewModel(
        requestOtpUseCase = RequestOtpUseCase(repo),
        verifyOtpUseCase = VerifyOtpUseCase(repo)
    )

    private companion object {
        const val RESEND_TEST_TIMEOUT_MILLIS = 30_000L
    }

    private class FakeAuthRepository(
        private val requestResult: () -> AuthResult<Unit> = {
            AuthResult.Success(Unit)
        },
        private val verifyResult: () -> AuthResult<AuthSession> = {
            AuthResult.Success(AuthSession("demo-access-token", "demo-user"))
        }
    ) : AuthRepository {
        var requestCount = 0
            private set
        var verifyCount = 0
            private set
        var lastMobile: String? = null
            private set
        var lastOtp: String? = null
            private set

        override suspend fun requestOtp(mobile: MobileNumber): AuthResult<Unit> {
            requestCount++
            lastMobile = mobile.value
            return requestResult()
        }

        override suspend fun verifyOtp(
            mobile: MobileNumber,
            code: OtpCode
        ): AuthResult<AuthSession> {
            verifyCount++
            lastMobile = mobile.value
            lastOtp = code.value
            return verifyResult()
        }
    }
}
