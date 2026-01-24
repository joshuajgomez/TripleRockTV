package com.joshgm3z.triplerocktv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.joshgm3z.triplerocktv.ui.MainNavigation
import com.joshgm3z.triplerocktv.ui.theme.Orange10
import com.joshgm3z.triplerocktv.ui.theme.TripleRockTVTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalTvMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TripleRockTVTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RectangleShape
                ) {
                    Box(contentAlignment = Alignment.BottomEnd) {
                        MainNavigation()
                        EnvironmentMarker()
                    }
                }
            }
        }
    }
}

@Composable
fun EnvironmentMarker() {
    if (BuildConfig.FLAVOR != "demo") return

    Row(
        modifier = Modifier.padding(50.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val color = Orange10
        Icon(Icons.Default.Info, contentDescription = null, tint = color)
        Spacer(Modifier.size(5.dp))
        Text("Demo mode", color = color)
    }
}
