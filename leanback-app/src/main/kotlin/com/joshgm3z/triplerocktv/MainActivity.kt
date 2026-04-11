package com.joshgm3z.triplerocktv

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.lifecycleScope
import com.joshgm3z.triplerocktv.core.util.Logger
import com.joshgm3z.triplerocktv.core.util.getQrCode
import com.joshgm3z.triplerocktv.core.viewmodel.OnlineTyperViewModel
import com.joshgm3z.triplerocktv.util.setVisible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    private val onlineTyperViewModel: OnlineTyperViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<TextView>(R.id.tv_demo_marker).apply {
            if (listOf("demo", "dev").contains(BuildConfig.FLAVOR)) {
                text = BuildConfig.FLAVOR
                visibility = View.VISIBLE
            }
        }
        lifecycleScope.launch {
            onlineTyperViewModel.qrCodeBitmapState.collect {
                Logger.debug("qrCodeBitmapState = [${it}]")
                val iv = findViewById<ImageView>(R.id.iv_qrcode)
                iv.setVisible(it != null)
                iv.setImageBitmap(it)
            }
        }
    }
}
