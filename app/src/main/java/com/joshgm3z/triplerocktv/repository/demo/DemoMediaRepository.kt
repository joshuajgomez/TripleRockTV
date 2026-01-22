package com.joshgm3z.triplerocktv.repository.demo

import com.joshgm3z.triplerocktv.repository.MediaRepository
import com.joshgm3z.triplerocktv.repository.data.MediaData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class DemoMediaRepository
@Inject
constructor(
    private val scope: CoroutineScope
) : MediaRepository {
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