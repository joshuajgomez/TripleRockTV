package com.joshgm3z.triplerocktv.core.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joshgm3z.triplerocktv.core.BuildConfig
import com.joshgm3z.triplerocktv.core.repository.impl.isOlderThan
import com.joshgm3z.triplerocktv.core.selfupdate.ApkInstaller
import com.joshgm3z.triplerocktv.core.selfupdate.DownloadState
import com.joshgm3z.triplerocktv.core.selfupdate.FileDownloader
import com.joshgm3z.triplerocktv.core.util.Logger
import com.joshgm3z.triplerocktv.core.util.isDevBuild
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class SelfUpdateUiState(
    val title: String = "",
    val subtitle: String? = null,
    val enableButtons: Boolean = false,
    val buttonAction: ButtonAction = ButtonAction.UpdateNow,
)

val tag = when {
    isDevBuild -> "dev-release"
    else -> "master-release"
}

private val leanbackAppUrl: String
    get() = "https://github.com/joshuajgomez/TripleRockTV/releases/download/$tag/3RockTV-leanback-app.apk"
private val composeAppUrl: String
    get() = "https://github.com/joshuajgomez/TripleRockTV/releases/download/$tag/3RockTV-compose-app.apk"
private val appTagUrl: String
    get() = "https://api.github.com/repos/joshuajgomez/TripleRockTV/releases/tags/$tag"

enum class ButtonAction(val text: String) {
    UpdateNow("Update now"),
    CheckAgain("Check again"),
    Install("Install"),
    TryAgain("Try again")
}

@HiltViewModel
class SelfUpdateViewModel
@Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val fileDownloader: FileDownloader,
    private val apkInstaller: ApkInstaller
) : ViewModel() {

    private val _uiState = MutableStateFlow(SelfUpdateUiState())
    val uiState = _uiState.asStateFlow()

    private val isComposeApp = savedStateHandle.get<Boolean>("isComposeApp") ?: false

    private val apkUrl = if (isComposeApp) composeAppUrl else leanbackAppUrl
    private val apkTagUrl = appTagUrl

    private var downloadedFile: File? = null

    init {
        checkUpdates()
    }

    fun onButtonClick() {
        when (_uiState.value.buttonAction) {
            ButtonAction.CheckAgain -> checkUpdates()
            ButtonAction.Install -> downloadedFile?.let { apkInstaller.installApk(it) }
            else -> downloadUpdate()
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
            delay(1000)
            val releaseInfo = fileDownloader.getLatestApkReleaseName(apkTagUrl)
            Logger.debug("releaseInfo = [$releaseInfo]")
            _uiState.update {
                if (BuildConfig.VERSION_NAME.isOlderThan(releaseInfo?.name)) {
                    it.copy(
                        title = "New version ${releaseInfo?.name} available",
                        subtitle = releaseInfo?.description,
                        enableButtons = true,
                        buttonAction = ButtonAction.UpdateNow
                    )
                } else {
                    it.copy(
                        title = "App is up to date",
                        enableButtons = true,
                        buttonAction = ButtonAction.CheckAgain
                    )
                }
            }
        }
    }

    private fun downloadUpdate() {
        downloadedFile = null
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
                        DownloadState.Completed -> {
                            informInstallErrorAfterDelay()
                            uiState.copy(
                                title = "Update file downloaded",
                                subtitle = "Trying to install automatically",
                            )
                        }

                        DownloadState.Error -> uiState.copy(
                            title = "Error downloading update",
                            subtitle = "Cannot download update right now. Try again later",
                            enableButtons = true,
                            buttonAction = ButtonAction.TryAgain
                        )

                        else -> uiState
                    }
                }
            },
            onDownloadComplete = {
                downloadedFile = it
                apkInstaller.installApk(it)
            }
        )
    }

    private fun informInstallErrorAfterDelay() {
        viewModelScope.launch {
            delay(3000)
            _uiState.update {
                it.copy(
                    subtitle = "Tap install to complete update",
                    enableButtons = true,
                    buttonAction = ButtonAction.Install
                )
            }
        }
    }
}