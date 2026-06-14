package com.joshgm3z.triplerocktv.core.repository.impl

import com.joshgm3z.triplerocktv.core.repository.LiveTvRepository
import com.joshgm3z.triplerocktv.core.repository.impl.helper.IptvServiceProvider
import com.joshgm3z.triplerocktv.core.repository.retrofit.XmlTvParser
import com.joshgm3z.triplerocktv.core.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class LiveTvRepositoryImpl
@Inject constructor(
    private val scope: CoroutineScope,
    private val iptvService: IptvServiceProvider,
) : LiveTvRepository {

    override fun fetchLiveTvGuide() {
        Logger.debug("entry")
        scope.launch {
            val response = (iptvService.get() ?: return@launch).getXmltv(
                iptvService.username,
                iptvService.password
            )
            if (response.isSuccessful) {
                val responseBody = response.body() ?: return@launch
                val programs = XmlTvParser.parse(responseBody.byteStream())

                Logger.debug("programs = [$programs]")
            }
        }
        Logger.debug("exit")
    }
}