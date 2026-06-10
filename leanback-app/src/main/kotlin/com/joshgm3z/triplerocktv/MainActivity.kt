package com.joshgm3z.triplerocktv

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.joshgm3z.triplerocktv.core.util.Logger
import com.joshgm3z.triplerocktv.core.viewmodel.OnlineTyperViewModel
import com.joshgm3z.triplerocktv.databinding.ActivityMainBinding
import com.joshgm3z.triplerocktv.util.setVisible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    private val onlineTyperViewModel: OnlineTyperViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvDemoMarker.apply {
            if (listOf("demo", "dev").contains(BuildConfig.FLAVOR)) {
                text = BuildConfig.FLAVOR
                visibility = View.VISIBLE
            }
        }
        lifecycleScope.launch {
            onlineTyperViewModel.qrCodeBitmapState.collect {
                Logger.debug("qrCodeBitmapState = [${it}]")
                binding.llQrCode.setVisible(it != null)
                if (it != null) binding.ivQrcode.setImageBitmap(it)
            }
        }
    }
}
