package com.joshgm3z.triplerocktv.core.repository.impl

import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.core.repository.data.Episode
import com.joshgm3z.triplerocktv.core.repository.data.EpisodeInfo
import com.joshgm3z.triplerocktv.core.repository.room.MovieMetadata
import com.joshgm3z.triplerocktv.core.repository.room.StreamData
import com.joshgm3z.triplerocktv.core.repository.room.series.Season
import com.joshgm3z.triplerocktv.core.repository.room.series.SeriesStream

fun sampleSeriesData() = SeriesStream(
    seasons = listOf(
        Season(
            episodes = listOf(
                Episode(
                    id = 1,
                    episode_num = 1,
                    title = "Pilot",
                    container_extension = "mp4",
                    season = 1,
                    added = "2024-01-01",
                    episodeInfo = EpisodeInfo(
                        releasedate = "2024-01-01",
                        plot = "",
                        duration_secs = 6000000,
                        duration = "60m",
                        movie_image = null,
                        rating = "8.0",
                        season = "1"
                    ),
                    lastPlayed = 10000,
                    playedDuration = 280000
                )
            ),
            number = 1,
            name = "Season 1",
            coverImageUrl = "https://example.com/season1.jpg",
            voteAverage = 8.0f,
            overview = ""
        ),
        Season(
            episodes = listOf(
                Episode(
                    id = 2,
                    episode_num = 1,
                    title = "New Beginnings",
                    container_extension = "mkv",
                    season = 2,
                    added = "2025-01-01",
                    episodeInfo = EpisodeInfo(
                        releasedate = "2025-01-01",
                        plot = "",
                        duration_secs = 3700,
                        duration = "62m",
                        movie_image = null,
                        rating = "8.5",
                        season = "2"
                    ),
                    lastPlayed = 0,
                    playedDuration = 0
                )
            ),
            number = 2,
            name = "Season 2",
            coverImageUrl = "https://example.com/season2.jpg",
            voteAverage = 0.5f,
            overview = ""
        )
    ),
    seriesId = 11,
    categoryId = 11,
    backdropUrl = null,
    cast = null,
    coverImageUrl = null,
    director = null,
    genre = null,
    lastModified = null,
    lastPlayed = 10000,
    name = "Sample Series",
    num = 1,
    plot = null,
    rating = null,
    releaseDate = null,
    lastPlayedEpisodeId = 1,
)

fun sampleStreamData() = listOf(
    StreamData(
        streamId = 10001,
        num = 1,
        name = "Inception (2010)",
        streamTypeText = "movie",
        streamIcon = "https://image.tmdb.org/t/p/w600_and_h900_bestv2/qmDpIHrmpJINaRKAfWQfftjCdyi.jpg",
        categoryId = 1,
        added = "1625012046",
        playedDuration = 42800,
        lastPlayed = 1000,
        streamType = StreamType.VideoOnDemand,
        rating = 8.3f,
        extension = "mp4",
        inMyList = true,
        movieMetadata = MovieMetadata(
            totalDurationMs = 4280000L,
            description = "A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a CEO.",
        )
    ),
    StreamData(
        streamId = 10001,
        num = 1,
        name = "Inception (2010)",
        streamTypeText = "movie",
        streamIcon = "https://image.tmdb.org/t/p/w600_and_h900_bestv2/qmDpIHrmpJINaRKAfWQfftjCdyi.jpg",
        categoryId = 1,
        added = "1625012046",
        playedDuration = 42800,
        lastPlayed = 242,
        streamType = StreamType.VideoOnDemand,
        rating = 8.3f,
        extension = "mp4",
        inMyList = true,
        movieMetadata = MovieMetadata(
            totalDurationMs = 4280000L,
            description = "A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a CEO.",
        )
    ),
)
