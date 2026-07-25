package com.joshgm3z.triplerocktv.compose

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.joshgm3z.triplerocktv.compose.theme.TripleRockTvTheme
import com.joshgm3z.triplerocktv.core.util.FirebaseConfig
import com.joshgm3z.triplerocktv.core.util.isDevBuild
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ComposeActivity : AppCompatActivity() {

    @Inject
    lateinit var firebaseConfig: FirebaseConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TripleRockTvTheme {
                Surface(color = colorScheme.background) {
                    Box {
                        TvNavHost()
                        EnvironmentMarker()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        firebaseConfig.init()
    }
}

@Composable
fun EnvironmentMarker() {
    if (isDevBuild) Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 80.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = BuildConfig.FLAVOR,
            color = colorScheme.error,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .background(
                    color = colorScheme.errorContainer,
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(horizontal = 5.dp)
        )
    }
}