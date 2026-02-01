package com.joshgm3z.triplerocktv

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.joshgm3z.triplerocktv.ui.loading.MediaLoadingFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LegacyMainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_legacy_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_browse_fragment, MainBrowseFragment())
                .commit()
        }
    }
}
