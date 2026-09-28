package ir.neobank.ariapay.core.designsystem.component


import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import ir.neobank.ariapay.core.designsystem.theme.AriaTheme
import org.junit.Rule
import org.junit.Test

class AriaButtonTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun loading_disablesClick() {
        composeRule.setContent {
            AriaTheme {
                AriaButton(text = "واریز", onClick = {}, loading = true)
            }
        }

        composeRule.onNodeWithText("واریز").assertIsNotEnabled()
    }
}
