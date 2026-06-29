package com.joshgm3z.triplerocktv

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.joshgm3z.triplerocktv.core.util.FirebaseConfig
import com.joshgm3z.triplerocktv.core.util.Logger
import com.joshgm3z.triplerocktv.core.util.isDevBuild
import com.joshgm3z.triplerocktv.core.viewmodel.OnlineTyperViewModel
import com.joshgm3z.triplerocktv.databinding.ActivityMainBinding
import com.joshgm3z.triplerocktv.util.setVisible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    private val onlineTyperViewModel: OnlineTyperViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var firebaseConfig: FirebaseConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvDemoMarker.apply {
            if (isDevBuild) {
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

    override fun onResume() {
        super.onResume()
        firebaseConfig.init()
    }
}
