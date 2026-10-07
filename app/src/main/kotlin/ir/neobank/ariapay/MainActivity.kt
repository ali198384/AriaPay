package ir.neobank.ariapay

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import dagger.hilt.android.AndroidEntryPoint
import ir.neobank.ariapay.core.designsystem.theme.AriaTheme
import ir.neobank.ariapay.feature.auth.MobileNumberRoute

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AriaTheme {
                Surface {
                    MobileNumberRoute()
                }
            }
        }
    }
}
