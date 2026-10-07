package ir.neobank.ariapay.feature.auth

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import ir.neobank.ariapay.core.domain.auth.AuthError
import ir.neobank.ariapay.core.domain.auth.AuthRepository
import ir.neobank.ariapay.core.domain.auth.AuthResult
import ir.neobank.ariapay.core.domain.auth.AuthSession
import ir.neobank.ariapay.core.domain.auth.MobileNumber
import ir.neobank.ariapay.core.domain.auth.OtpCode
import ir.neobank.ariapay.core.domain.auth.RequestOtpUseCase
import ir.neobank.ariapay.core.domain.auth.VerifyOtpUseCase
import ir.neobank.ariapay.core.testing.MainDispatcherExtension
import ir.neobank.ariapay.feature.auth.contract.AuthEffect
import ir.neobank.ariapay.feature.auth.contract.AuthEvent
import ir.neobank.ariapay.feature.auth.contract.AuthStep
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MainDispatcherExtension::class)
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
            vm.onEvent(AuthEvent.VerifyClicked)
            runCurrent()

            assertThat(awaitItem()).isEqualTo(AuthEffect.NavigateToHome)
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
            assertThat(state.resendSecondsLeft).isEqualTo(0)
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun createVm(
        repo: FakeAuthRepository = FakeAuthRepository(),
    ) = AuthViewModel(
        requestOtpUseCase = RequestOtpUseCase(repo),
        verifyOtpUseCase = VerifyOtpUseCase(repo),
    )

    private class FakeAuthRepository(
        private val requestResult: () -> AuthResult<Unit> = {
            AuthResult.Success(Unit)
        },
        private val verifyResult: () -> AuthResult<AuthSession> = {
            AuthResult.Success(AuthSession("demo-access-token"))
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
