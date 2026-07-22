package com.joshgm3z.triplerocktv.compose

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import com.joshgm3z.triplerocktv.compose.theme.TripleRockTvTheme
import com.joshgm3z.triplerocktv.core.util.FirebaseConfig
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
                    TvNavHost()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        firebaseConfig.init()
    }
}