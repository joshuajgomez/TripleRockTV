package com.joshgm3z.triplerocktv.core.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joshgm3z.triplerocktv.core.repository.LiveTvRepository
import com.joshgm3z.triplerocktv.core.repository.data.XmlTvProgram
import com.joshgm3z.triplerocktv.core.util.Logger
import com.joshgm3z.triplerocktv.core.util.getTimeFrames
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import javax.inject.Inject

data class Channel(
    val streamId: Int,
    val id: String,
    val name: String,
    val logo: String?,
    val programs: List<XmlTvProgram> = emptyList()
)

data class LiveTvUiState(
    val activeProgram: XmlTvProgram? = null,
    val channels: List<Channel> = emptyList(),
    val timeFrames: List<ZonedDateTime>,
    val currentTime: ZonedDateTime,
) {
    override fun toString(): String {
        return "LiveTvUiState(" +
                "\n\tactiveProgram=$activeProgram, " +
                "\n\tchannels=$channels, " +
                "\n\ttimeFrames=$timeFrames)"
    }
}

@HiltViewModel
class LiveTvViewModel
@Inject constructor(
    private val repository: LiveTvRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        LiveTvUiState(
            currentTime = ZonedDateTime.now(),
            timeFrames = getTimeFrames()
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        fetchGuide()
    }

    fun fetchGuide() {
        repository.fetchLiveTvGuide(
            onFetch = {
                Logger.debug("onSuccess")
                buildChannels(it)
            },
            onError = {
                Logger.debug("onError")
            }
        )
    }

    private fun buildChannels(programs: List<XmlTvProgram>) {
        viewModelScope.launch(Dispatchers.IO) {
            val firstTimeFrame = _uiState.value.timeFrames.first()
            val lastTimeFrame = _uiState.value.timeFrames.last()

            val channels = programs
                .groupBy { it.id }
                .mapNotNull { (channelId, channelPrograms) ->
                    val filteredPrograms = channelPrograms.filter {
                        it.stop.isAfter(firstTimeFrame)
                                && it.start.isBefore(lastTimeFrame)
                    }

                    val streamData = repository.fetchLiveStream(channelId)
                    Channel(
                        id = channelId,
                        name = streamData?.name ?: return@mapNotNull null,
                        logo = streamData.streamIcon,
                        programs = filteredPrograms,
                        streamId = streamData.streamId
                    )
                }
            _uiState.update {
                it.copy(
                    channels = channels,
                    activeProgram = null
                )
            }
        }
    }
}