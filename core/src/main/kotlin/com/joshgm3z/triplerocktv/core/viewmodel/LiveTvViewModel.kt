package com.joshgm3z.triplerocktv.core.viewmodel

import androidx.lifecycle.ViewModel
import com.joshgm3z.triplerocktv.core.repository.LiveTvRepository
import com.joshgm3z.triplerocktv.core.util.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LiveTvViewModel
@Inject constructor(
    private val repository: LiveTvRepository
) : ViewModel() {

    init {
        Logger.debug("entry")
        repository.fetchLiveTvGuide()
    }
}