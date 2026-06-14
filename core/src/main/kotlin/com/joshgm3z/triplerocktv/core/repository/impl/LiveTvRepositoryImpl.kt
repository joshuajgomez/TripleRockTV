package com.joshgm3z.triplerocktv.core.repository.impl

import com.joshgm3z.triplerocktv.core.repository.LiveTvRepository
import com.joshgm3z.triplerocktv.core.repository.data.XmlTvProgram
import com.joshgm3z.triplerocktv.core.repository.impl.helper.IptvServiceProvider
import com.joshgm3z.triplerocktv.core.repository.retrofit.XmlTvParser
import com.joshgm3z.triplerocktv.core.repository.room.stream.StreamData
import com.joshgm3z.triplerocktv.core.repository.room.stream.StreamDataDao
import com.joshgm3z.triplerocktv.core.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class LiveTvRepositoryImpl
@Inject constructor(
    private val scope: CoroutineScope,
    private val iptvService: IptvServiceProvider,
    private val streamDataDao: StreamDataDao,
) : LiveTvRepository {

    override fun fetchLiveTvGuide(
        onFetch: (List<XmlTvProgram>) -> Unit,
        onError: (String) -> Unit
    ) {
        val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss Z")
        val now = ZonedDateTime.now().format(formatter)
        Logger.debug("entry. now its $now")
        scope.launch {
            val response = (iptvService.get() ?: return@launch).getXmltv(
                iptvService.username,
                iptvService.password
            )
            Logger.debug("response = [$response]")
            if (response.isSuccessful) {
                val responseBody = response.body() ?: return@launch
                val programs = XmlTvParser.parse(responseBody.byteStream())

                if (programs.isEmpty()) onError("No programs found")
                else onFetch(programs)
            } else {
                onError(response.message())
            }
        }
    }

    override suspend fun fetchLiveStream(epgChannelId: String): StreamData {
        return streamDataDao.fetchLiveStreamByEpgChannelId(epgChannelId)
    }
}

fun String.parseXmlTvDate(): ZonedDateTime {
    // The pattern matches: Year(4) Month(2) Day(2) Hour(2) Min(2) Sec(2) Offset(Zone)
    val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss Z")
    val parsedDate = ZonedDateTime.parse(this, formatter)
    return parsedDate.withZoneSameInstant(ZoneId.systemDefault())
}

fun ZonedDateTime.toHHmm(): String {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    return this.format(formatter)
}