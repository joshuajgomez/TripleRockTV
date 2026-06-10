package com.joshgm3z.triplerocktv.ui.selfupdate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joshgm3z.triplerocktv.BuildConfig
import com.joshgm3z.triplerocktv.core.selfupdate.ApkInstaller
import com.joshgm3z.triplerocktv.core.selfupdate.DownloadState
import com.joshgm3z.triplerocktv.core.selfupdate.FileDownloader
import com.joshgm3z.triplerocktv.core.util.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SelfUpdateUiState(
    val title: String = "",
    val subtitle: String? = null,
    val enableButtons: Boolean = false,
    val buttonText: String = "Update",
)

private val devAppUrl =
    "https://github.com/joshuajgomez/TripleRockTV/releases/download/dev-release/3RockTV-leanback-app-dev.apk"
private val devAppTagUrl =
    "https://api.github.com/repos/joshuajgomez/TripleRockTV/releases/tags/dev-release"

private val onlineAppUrl =
    "https://github.com/joshuajgomez/TripleRockTV/releases/latest/download/3RockTV-leanback-app.apk"
private val onlineAppTagUrl =
    "https://api.github.com/repos/joshuajgomez/TripleRockTV/releases/latest"

@HiltViewModel
class SelfUpdateViewModel
@Inject constructor(
    private val fileDownloader: FileDownloader,
    private val apkInstaller: ApkInstaller
) : ViewModel() {

    private val _uiState = MutableStateFlow(SelfUpdateUiState())
    val uiState = _uiState.asStateFlow()

    private val apkUrl = if (BuildConfig.FLAVOR == "online") onlineAppUrl else devAppUrl
    private val apkTagUrl = if (BuildConfig.FLAVOR == "online") onlineAppTagUrl else devAppTagUrl

    init {
        checkUpdates()
    }

    fun onButtonClick() {
        if (_uiState.value.buttonText == "Update now") {
            downloadUpdate()
        } else if (_uiState.value.buttonText == "Check again") {
            checkUpdates()
        }
    }

    private fun checkUpdates() {
        _uiState.update {
            it.copy(
                title = "Checking app updates",
                enableButtons = false
            )
        }

        viewModelScope.launch(Dispatchers.IO) {
            delay(2000)
            val releaseName = fileDownloader.getLatestApkReleaseName(apkTagUrl)
            Logger.debug("releaseName = [$releaseName]")
            _uiState.update {
                if (releaseName != null && releaseName.getRunNumber() > BuildConfig.VERSION_CODE) {
                    it.copy(
                        title = "Update available",
                        subtitle = "New version  $releaseName is available for download",
                        enableButtons = true,
                        buttonText = "Update now"
                    )
                } else {
                    it.copy(
                        title = "App is up to date",
                        enableButtons = true,
                        buttonText = "Check again"
                    )
                }
            }
        }
    }

    private fun downloadUpdate() {
        _uiState.update {
            it.copy(
                title = "Downloading update",
                subtitle = "Please wait while the update is downloaded",
                enableButtons = false,
            )
        }
        fileDownloader.startDownload(
            fileUrl = apkUrl,
            onUpdate = {
                Logger.debug("Download state: $it")
                _uiState.update { uiState ->
                    when (it) {
                        DownloadState.Completed -> uiState.copy(
                            title = "Installing",
                            subtitle = "Please grant permissions, when asked",
                        )

                        DownloadState.Error -> uiState.copy(
                            title = "Error downloading update",
                            subtitle = "Cannot download update right now. Try again later",
                            enableButtons = true,
                            buttonText = "Try again"
                        )

                        else -> uiState
                    }
                }
            },
            onDownloadComplete = { apkInstaller.installApk(it) }
        )
    }
}