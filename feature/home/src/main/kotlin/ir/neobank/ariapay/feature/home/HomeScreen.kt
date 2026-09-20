package ir.neobank.ariapay.feature.home


import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ir.neobank.ariapay.core.common.util.MoneyFormatter.formatRials
import ir.neobank.ariapay.core.common.util.MoneyFormatter.formatTomans

@Composable
fun HomeRoute() {
    HomeScreen()
}

@Composable
fun HomeScreen() {
    Log.d("TAG1", "A:"+formatRials(345325357L))
    Log.d("TAG1", "B:"+formatRials(345325357L, false))
    Log.d("TAG1", "C:"+formatTomans(345325357L))
    Log.d("TAG1", "D:"+formatTomans(345325357L, false))
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("AriaPay")
    }
}

@Preview(showBackground = true)
@Composable
private fun HomePreview() {
    HomeScreen()
}
