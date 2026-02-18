package com.joshgm3z.triplerocktv.repository.impl.helper

import com.joshgm3z.triplerocktv.repository.LoadingState
import com.joshgm3z.triplerocktv.repository.LoadingStatus
import com.joshgm3z.triplerocktv.repository.StreamType
import com.joshgm3z.triplerocktv.repository.impl.MediaOnlineRepositoryImpl.Companion.password
import com.joshgm3z.triplerocktv.repository.impl.MediaOnlineRepositoryImpl.Companion.username
import com.joshgm3z.triplerocktv.repository.retrofit.IptvService
import com.joshgm3z.triplerocktv.repository.room.epg.EpgListingDao
import com.joshgm3z.triplerocktv.util.Logger
import javax.inject.Inject

class EPGFetcher
@Inject
constructor(
    private val epgListingDao: EpgListingDao,
) {
    lateinit var iptvService: IptvService

    /*suspend fun fetchContent(
        onFetch: (MediaLoadingType, LoadingState) -> Unit,
        onError: (String, String) -> Unit
    ) {
        Logger.entry()
        val epgListings = iptvService.getShortEpg(
            username = username,
            password = password,
            streamId = 20642
        ).epgListings
        Logger.info("getShortEpg = $epgListings")
        if (epgListings.isNotEmpty()) {
            epgListingDao.deleteAllEpgListings()
            epgListingDao.insertAll(epgListings)
        } else {
            onFetch(
                MediaLoadingType.EPG,
                LoadingState(0, LoadingStatus.Error)
            )
            return
        }
        onFetch(
            StreamType.EPG,
            LoadingState(100, LoadingStatus.Complete)
        )
    }*/
}