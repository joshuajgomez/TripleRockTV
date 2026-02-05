package com.joshgm3z.triplerocktv

import com.joshgm3z.triplerocktv.repository.room.epg.IptvEpgListing
import com.joshgm3z.triplerocktv.repository.room.live.LiveTvCategory
import com.joshgm3z.triplerocktv.repository.room.live.LiveTvStream
import com.joshgm3z.triplerocktv.repository.room.series.SeriesCategory
import com.joshgm3z.triplerocktv.repository.room.series.SeriesStream
import com.joshgm3z.triplerocktv.repository.room.vod.VodCategory
import com.joshgm3z.triplerocktv.repository.room.vod.VodStream

class DemoData {
    companion object {

        val sampleVideoUrl1 =
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
        val sampleVideoUrl2 =
            "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/Zeitgeist/Zeitgeist%202010_%20Year%20in%20Review.mp4"

        val sampleVodStreams = listOf(
            VodStream(
                streamId = 10001,
                num = 1,
                name = "Inception (2010)",
                streamType = "movie",
                streamIcon = "https://image.tmdb.org/t/p/w600_and_h900_bestv2/qmDpIHrmpJINaRKAfWQfftjCdyi.jpg",
                categoryId = 101,
                added = "1625012046"
            ),
            VodStream(
                streamId = 10042,
                num = 2,
                name = "Breaking Bad S01E01",
                streamType = "series",
                streamIcon = "https://image.tmdb.org/t/p/w600_and_h900_bestv2/eSzpy96DwBujGFj0xMbXBcGcfxX.jpg",
                categoryId = 101,
                added = "1625112046"
            ),
            VodStream(
                streamId = 10002,
                num = 2,
                name = "Breaking Bad S01E01",
                streamType = "series",
                streamIcon = "https://image.tmdb.org/t/p/w600_and_h900_bestv2/eSzpy96DwBujGFj0xMbXBcGcfxX.jpg",
                categoryId = 102,
                added = "1625112046"
            ),
            VodStream(
                streamId = 10003,
                num = 3,
                name = "Planet Earth II",
                streamType = "documentary",
                streamIcon = "https://image.tmdb.org/t/p/w600_and_h900_bestv2/6v2b7YbExg1kQ9g6zQ6gA2Q2nLk.jpg",
                categoryId = 103,
                added = "1625212046"
            ),
            VodStream(
                streamId = 10004,
                num = 4,
                name = "No Icon Example",
                streamType = "movie",
                streamIcon = null,
                categoryId = 104,
                added = "1625312046"
            )
        )

        val sampleLiveStreams = listOf(
            LiveTvStream(
                streamId = 20001,
                num = 1,
                name = "BBC News",
                streamType = "news",
                streamIcon = "https://upload.wikimedia.org/wikipedia/commons/5/5f/BBC_News_2022_%28Alt%29.svg",
                categoryId = 201,
                added = "1626012046"
            ),
            LiveTvStream(
                streamId = 20002,
                num = 2,
                name = "ESPN",
                streamType = "sports",
                streamIcon = "https://upload.wikimedia.org/wikipedia/commons/2/2f/ESPN_wordmark.svg",
                categoryId = 202,
                added = "1626112046"
            ),
            LiveTvStream(
                streamId = 20003,
                num = 3,
                name = "Cartoon Network",
                streamType = "kids",
                streamIcon = "https://upload.wikimedia.org/wikipedia/commons/2/2a/Cartoon_Network_2010_logo.svg",
                categoryId = 203,
                added = "1626212046"
            ),
            LiveTvStream(
                streamId = 20004,
                num = 4,
                name = "Discovery Channel",
                streamType = "documentary",
                streamIcon = null,
                categoryId = 204,
                added = "1626312046"
            )
        )

        fun getSampleSeriesStreams(): List<SeriesStream> {
            return listOf(
                SeriesStream(
                    seriesId = 1,
                    num = 101,
                    name = "The Great Adventure",
                    cover = "cover1.jpg",
                    plot = "A thrilling journey across unknown lands.",
                    cast = "John Doe, Jane Smith",
                    director = "Alice Johnson",
                    genre = "Adventure",
                    releaseDate = "2022-01-15",
                    lastModified = "2024-06-01",
                    rating = "8.5",
                    categoryId = 2
                ),
                SeriesStream(
                    seriesId = 2,
                    num = 102,
                    name = "Mystery Manor",
                    cover = "cover2.jpg",
                    plot = "Unravel the secrets of the old manor.",
                    cast = "Emily Clark, Bob Brown",
                    director = "David Lee",
                    genre = "Mystery",
                    releaseDate = "2021-10-10",
                    lastModified = "2024-05-20",
                    rating = "7.9",
                    categoryId = 1
                ),
                SeriesStream(
                    seriesId = 3,
                    num = 101,
                    name = "The Great Adventure",
                    cover = "cover1.jpg",
                    plot = "A thrilling journey across unknown lands.",
                    cast = "John Doe, Jane Smith",
                    director = "Alice Johnson",
                    genre = "Adventure",
                    releaseDate = "2022-01-15",
                    lastModified = "2024-06-01",
                    rating = "8.5",
                    categoryId = 4
                ),
                SeriesStream(
                    seriesId = 4,
                    num = 102,
                    name = "Mystery Manor",
                    cover = "cover2.jpg",
                    plot = "Unravel the secrets of the old manor.",
                    cast = "Emily Clark, Bob Brown",
                    director = "David Lee",
                    genre = "Mystery",
                    releaseDate = "2021-10-10",
                    lastModified = "2024-05-20",
                    rating = "7.9",
                    categoryId = 3
                )
            )
        }

        fun sampleVodCategory(): List<VodCategory> = listOf(
            VodCategory(
                categoryId = 122,
                categoryName = "ENGLISH (4K)",
                parentId = 0,
                count = 3
            ),
            VodCategory(
                categoryId = 56,
                categoryName = "OSCAR WINNING MOVIES",
                parentId = 0,
                count = 2
            ),
            VodCategory(
                categoryId = 43,
                categoryName = "ENGLISH FHD (2026)",
                parentId = 0,
                count = 1
            ),
        )

        fun getSampleSeriesCategories(): List<SeriesCategory> {
            return listOf(
                SeriesCategory(
                    categoryId = 1,
                    categoryName = "Drama",
                    parentId = 0,
                    count = 10
                ),
                SeriesCategory(
                    categoryId = 2,
                    categoryName = "Comedy",
                    parentId = 0,
                    count = 8
                ),
                SeriesCategory(
                    categoryId = 3,
                    categoryName = "Documentary",
                    parentId = 0,
                    count = 5
                ),
                SeriesCategory(
                    categoryId = 4,
                    categoryName = "Sci-Fi",
                    parentId = 0,
                    count = 7
                )
            )
        }

        fun getSampleLiveTvCategories(): List<LiveTvCategory> {
            return listOf(
                LiveTvCategory(
                    categoryId = 1,
                    categoryName = "News",
                    parentId = 0,
                    count = 12
                ),
                LiveTvCategory(
                    categoryId = 2,
                    categoryName = "Sports",
                    parentId = 0,
                    count = 8
                ),
                LiveTvCategory(
                    categoryId = 3,
                    categoryName = "Movies",
                    parentId = 0,
                    count = 15
                ),
                LiveTvCategory(
                    categoryId = 4,
                    categoryName = "Kids",
                    parentId = 0,
                    count = 5
                )
            )
        }

        fun getSampleIptvEpgListings(): List<IptvEpgListing> {
            return listOf(
                IptvEpgListing(
                    id = 1,
                    epgId = "epg001",
                    title = "Morning News",
                    lang = "en",
                    start = "2024-06-10T06:00:00Z",
                    end = "2024-06-10T07:00:00Z",
                    description = "Latest news and updates from around the world.",
                    channelId = "ch001",
                    startTimestamp = "1718008800",
                    stopTimestamp = "1718012400"
                ),
                IptvEpgListing(
                    id = 2,
                    epgId = "epg002",
                    title = "Cartoon Hour",
                    lang = "en",
                    start = "2024-06-10T07:00:00Z",
                    end = "2024-06-10T08:00:00Z",
                    description = "Fun cartoons for kids.",
                    channelId = "ch002",
                    startTimestamp = "1718012400",
                    stopTimestamp = "1718016000"
                )
            )
        }

        val allStreams = sampleVodStreams + sampleLiveStreams + getSampleSeriesStreams()
    }
}