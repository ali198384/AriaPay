package ir.neobank.ariapay

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import dagger.hilt.android.HiltAndroidApp
import ir.neobank.ariapay.core.session.SessionManager
import javax.inject.Inject

@HiltAndroidApp
class AriaPayApplication : Application() {

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(
            object : DefaultLifecycleObserver {
                override fun onStop(owner: LifecycleOwner) {
                    sessionManager.onAppBackgrounded()
                }
            },
        )
    }
}
