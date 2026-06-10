package com.joshgm3z.triplerocktv.core.selfupdate

import android.content.Context
import com.joshgm3z.triplerocktv.core.util.Logger
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class ApkInstaller
@Inject constructor(
    @param:ApplicationContext
    private val context: Context,
) {
    fun installApk(apkFile: File) {
        Logger.debug("apkFile = [${apkFile.path}]")
        if (apkFile.exists()) {
            val contentUri = androidx.core.content.FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                apkFile
            )

            val installIntent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                setDataAndType(contentUri, "application/vnd.android.package-archive")
            }
            context.startActivity(installIntent)
        }
    }
}