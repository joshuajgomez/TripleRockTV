package com.joshgm3z.triplerocktv

import com.joshgm3z.triplerocktv.repository.MediaLocalRepository
import com.joshgm3z.triplerocktv.repository.data.MediaData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class DemoMediaLocalRepository
@Inject
constructor(
    private val scope: CoroutineScope
) : MediaLocalRepository {
    override fun fetchAllMediaData(
        onSuccess: (List<MediaData>) -> Unit,
        onError: (String) -> Unit
    ) {
        scope.launch {
            delay(2000)
            onSuccess(
                listOf(
                    MediaData.sample(),
                    MediaData.sample(),
                    MediaData.sample(),
                    MediaData.sample(),
                    MediaData.sample(),
                    MediaData.sample(),
                )
            )
        }
    }

    override fun fetchMediaDataById(
        id: String,
        onSuccess: (MediaData) -> Unit,
        onError: (String) -> Unit
    ) {
        scope.launch {
            delay(2000)
            onSuccess(MediaData.sample())
        }
    }
}