package com.joshgm3z.triplerocktv.core.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joshgm3z.triplerocktv.core.repository.LoadingStatus
import com.joshgm3z.triplerocktv.core.repository.MediaLocalRepository
import com.joshgm3z.triplerocktv.core.repository.MediaOnlineRepository
import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.core.repository.impl.LocalDatastore
import com.joshgm3z.triplerocktv.core.util.relativeTime
import com.joshgm3z.triplerocktv.core.util.withComma
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DownloadedItemState(
    val status: String,
    val filesCount: String? = null,
    val loadingStatus: LoadingStatus? = null,
)

data class DownloaderUiState(
    val stateMap: Map<StreamType, DownloadedItemState?> = mapOf(
        StreamType.VideoOnDemand to null,
        StreamType.Series to null,
        StreamType.LiveTV to null,
    ),
    val enableButtons: Boolean = false,
    val overallUpdateStatus: LoadingStatus? = null,
)

@HiltViewModel
class UpdaterViewModel
@Inject
constructor(
    savedStateHandle: SavedStateHandle,
    private val onlineRepository: MediaOnlineRepository,
    private val localRepository: MediaLocalRepository,
    private val localDatastore: LocalDatastore,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DownloaderUiState())
    val uiState = _uiState.asStateFlow()

    private val queue = ArrayDeque<StreamType>()

    val autoUpdateAndExit = savedStateHandle.get<Boolean>("autoUpdateAndExit") ?: false

    init {
        viewModelScope.launch(Dispatchers.IO) {
            if (autoUpdateAndExit) {
                startUpdate(*_uiState.value.stateMap.keys.toTypedArray())
            } else {
                _uiState.update { currentState ->
                    val updatedMap = currentState.stateMap.toMutableMap()
                    currentState.stateMap.keys.forEach { type ->
                        val countText = localDatastore.getTotalCount(type)
                        val lastContentUpdate = localDatastore.getLastContentUpdate(type)
                        updatedMap[type] =
                            if (lastContentUpdate == 0L || countText.isEmpty() || countText == "0")
                                DownloadedItemState(status = "Tap update to fetch videos")
                            else DownloadedItemState(
                                filesCount = "$countText videos",
                                status = "Last updated ${lastContentUpdate.relativeTime()}"
                            )

                    }
                    currentState.copy(
                        stateMap = updatedMap,
                        enableButtons = true
                    )
                }
            }
        }
    }

    fun startUpdate(vararg streamTypes: StreamType) {
        _uiState.update { currentState ->
            val updatedMap = currentState.stateMap.toMutableMap()
            streamTypes.forEach { type ->
                updatedMap[type] = DownloadedItemState(
                    status = "Waiting update",
                    loadingStatus = LoadingStatus.Initial
                )
            }
            currentState.copy(
                stateMap = updatedMap,
                enableButtons = false
            )
        }

        queue.clear()
        queue.addAll(streamTypes.toList())
        resumeQueue()
    }

    private fun DownloaderUiState.getOverallUpdateStatus(): LoadingStatus {
        return when {
            stateMap.values.any {
                it?.loadingStatus == LoadingStatus.Complete
            } -> LoadingStatus.Complete

            else -> LoadingStatus.Error
        }
    }

    private fun resumeQueue() {
        if (queue.isEmpty()) {
            viewModelScope.launch {
                _uiState.update { currentState ->
                    localDatastore.setLastContentUpdate(System.currentTimeMillis())
                    currentState.copy(
                        enableButtons = true,
                        overallUpdateStatus = currentState.getOverallUpdateStatus()
                    )
                }
            }
            return
        }

        val type = queue.removeFirst()
        viewModelScope.launch {
            onlineRepository.startUpdate(
                type,
                onFetch = {
                    viewModelScope.launch(Dispatchers.IO) {
                        _uiState.update { currentState ->
                            val updatedMap = currentState.stateMap.toMutableMap()
                            updatedMap[type] = DownloadedItemState(
                                filesCount = if (it.status == LoadingStatus.Complete)
                                    "${localDatastore.getTotalCount(type)} videos" else null,
                                status = when (it.status) {
                                    LoadingStatus.Ongoing -> "Updating ${it.percent}%"
                                    LoadingStatus.Complete -> "Last updated just now"
                                    LoadingStatus.Initial -> "Initialising"
                                    LoadingStatus.Error -> it.error ?: "Couldn't complete"
                                },
                                loadingStatus = it.status
                            )
                            currentState.copy(stateMap = updatedMap)
                        }
                        if (it.status == LoadingStatus.Complete || it.status == LoadingStatus.Error) {
                            localDatastore.setLastContentUpdate(
                                type, System.currentTimeMillis(),
                                localRepository.numberOfFiles(type).withComma()
                            )
                            resumeQueue()
                        }
                    }
                },
                onError = { error, summary ->
                    _uiState.update {
                        it.copy(enableButtons = true)
                    }
                },
            )
        }
    }
}
