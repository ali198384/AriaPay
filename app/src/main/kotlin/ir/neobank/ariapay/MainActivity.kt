package ir.neobank.ariapay

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.AndroidEntryPoint
import ir.neobank.ariapay.core.session.SessionManager
import ir.neobank.ariapay.core.designsystem.theme.AriaTheme
import ir.neobank.ariapay.navigation.AppNavHost
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AriaTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavHost(
                        sessionManager = sessionManager,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}
