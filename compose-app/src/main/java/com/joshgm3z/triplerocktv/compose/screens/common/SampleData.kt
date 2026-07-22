package com.joshgm3z.triplerocktv.compose.screens.common

import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.core.repository.room.category.CategoryData
import com.joshgm3z.triplerocktv.core.repository.room.stream.MovieMetadata
import com.joshgm3z.triplerocktv.core.repository.room.stream.StreamData

val sampleLabelToCategoryMap = mapOf(
    "Malayalam" to listOf(
        CategoryData(
            categoryId = 1,
            categoryName = "English 4k",
            parentId = 0,
            count = 2,
            streamType = StreamType.VideoOnDemand,
            firstStreamIcon = "https://image.tmdb.org/t/p/w600_and_h900_bestv2/qmDpIHrmpJINaRKAfWQfftjCdyi.jpg"
        ),
        CategoryData(
            categoryId = 1,
            categoryName = "English 4k",
            parentId = 0,
            count = 2,
            streamType = StreamType.VideoOnDemand,
            firstStreamIcon = "https://image.tmdb.org/t/p/w600_and_h900_bestv2/qmDpIHrmpJINaRKAfWQfftjCdyi.jpg"
        ),
        CategoryData(
            categoryId = 1,
            categoryName = "English 4k",
            parentId = 0,
            count = 2,
            streamType = StreamType.VideoOnDemand,
            firstStreamIcon = "https://image.tmdb.org/t/p/w600_and_h900_bestv2/qmDpIHrmpJINaRKAfWQfftjCdyi.jpg"
        ),
        CategoryData(
            categoryId = 1,
            categoryName = "English 4k",
            parentId = 0,
            count = 2,
            streamType = StreamType.VideoOnDemand,
            firstStreamIcon = "https://image.tmdb.org/t/p/w600_and_h900_bestv2/qmDpIHrmpJINaRKAfWQfftjCdyi.jpg"
        ),
    ),
    "Hindi" to listOf(),
    "English" to listOf(
        CategoryData(
            categoryId = 1,
            categoryName = "English 4k",
            parentId = 0,
            count = 2,
            streamType = StreamType.VideoOnDemand,
            firstStreamIcon = "https://image.tmdb.org/t/p/w600_and_h900_bestv2/qmDpIHrmpJINaRKAfWQfftjCdyi.jpg"
        ),
        CategoryData(
            categoryId = 1,
            categoryName = "English 4k",
            parentId = 0,
            count = 2,
            streamType = StreamType.VideoOnDemand,
            firstStreamIcon = "https://image.tmdb.org/t/p/w600_and_h900_bestv2/qmDpIHrmpJINaRKAfWQfftjCdyi.jpg"
        ),
    )
)

val sampleCategoryToStreamDataMap = mapOf(
    CategoryData(
        categoryId = 1,
        categoryName = "English 4k",
        parentId = 0,
        count = 2,
        streamType = StreamType.VideoOnDemand,
        firstStreamIcon = "https://image.tmdb.org/t/p/w600_and_h900_bestv2/qmDpIHrmpJINaRKAfWQfftjCdyi.jpg"
    ) to listOf(
        StreamData(
            streamId = 652973,
            num = 1,
            name = "Inception (2010)",
            streamTypeText = "movie",
            streamIcon = "https://image.tmdb.org/t/p/w600_and_h900_bestv2/qmDpIHrmpJINaRKAfWQfftjCdyi.jpg",
            categoryId = 1,
            added = "1625012046",
            streamType = StreamType.VideoOnDemand,
            rating = 8.3f,
            extension = "mp4",
            movieMetadata = MovieMetadata(
                totalDurationMs = 4280000L,
                description = "A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a CEO.",
            )
        ),
        StreamData(
            streamId = 652973,
            num = 1,
            name = "Inception (2010)",
            streamTypeText = "movie",
            streamIcon = "https://image.tmdb.org/t/p/w600_and_h900_bestv2/qmDpIHrmpJINaRKAfWQfftjCdyi.jpg",
            categoryId = 1,
            added = "1625012046",
            streamType = StreamType.VideoOnDemand,
            rating = 8.3f,
            extension = "mp4",
            movieMetadata = MovieMetadata(
                totalDurationMs = 4280000L,
                description = "A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a CEO.",
            )
        ),
        StreamData(
            streamId = 652973,
            num = 1,
            name = "Inception (2010)",
            streamTypeText = "movie",
            streamIcon = "https://image.tmdb.org/t/p/w600_and_h900_bestv2/qmDpIHrmpJINaRKAfWQfftjCdyi.jpg",
            categoryId = 1,
            added = "1625012046",
            streamType = StreamType.VideoOnDemand,
            rating = 8.3f,
            extension = "mp4",
            movieMetadata = MovieMetadata(
                totalDurationMs = 4280000L,
                description = "A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a CEO.",
            )
        )
    ),
    CategoryData(
        categoryId = 1,
        categoryName = "OSCAR WINNING MOVIES",
        parentId = 0,
        count = 2,
        streamType = StreamType.VideoOnDemand,
        firstStreamIcon = "https://image.tmdb.org/t/p/w600_and_h900_bestv2/qmDpIHrmpJINaRKAfWQfftjCdyi.jpg"
    ) to listOf(
        StreamData(
            streamId = 652973,
            num = 1,
            name = "Inception (2010)",
            streamTypeText = "movie",
            streamIcon = "https://image.tmdb.org/t/p/w600_and_h900_bestv2/qmDpIHrmpJINaRKAfWQfftjCdyi.jpg",
            categoryId = 1,
            added = "1625012046",
            streamType = StreamType.VideoOnDemand,
            rating = 8.3f,
            extension = "mp4",
            movieMetadata = MovieMetadata(
                totalDurationMs = 4280000L,
                description = "A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a CEO.",
            )
        ),
        StreamData(
            streamId = 652973,
            num = 1,
            name = "Inception (2010)",
            streamTypeText = "movie",
            streamIcon = "https://image.tmdb.org/t/p/w600_and_h900_bestv2/qmDpIHrmpJINaRKAfWQfftjCdyi.jpg",
            categoryId = 1,
            added = "1625012046",
            streamType = StreamType.VideoOnDemand,
            rating = 8.3f,
            extension = "mp4",
            movieMetadata = MovieMetadata(
                totalDurationMs = 4280000L,
                description = "A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a CEO.",
            )
        ),
        StreamData(
            streamId = 652973,
            num = 1,
            name = "Inception (2010)",
            streamTypeText = "movie",
            streamIcon = "https://image.tmdb.org/t/p/w600_and_h900_bestv2/qmDpIHrmpJINaRKAfWQfftjCdyi.jpg",
            categoryId = 1,
            added = "1625012046",
            streamType = StreamType.VideoOnDemand,
            rating = 8.3f,
            extension = "mp4",
            movieMetadata = MovieMetadata(
                totalDurationMs = 4280000L,
                description = "A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a CEO.",
            )
        )
    )
)

fun sampleStreamDataList(streamType: StreamType = StreamType.VideoOnDemand) = listOf(
    StreamData(
        streamId = 1,
        num = 1,
        name = "Inception (2010)",
        streamTypeText = "movie",
        streamIcon = "https://image.tmdb.org/t/p/w600_and_h900_bestv2/qmDpIHrmpJINaRKAfWQfftjCdyi.jpg",
        categoryId = 1,
        added = "1625012046",
        streamType = streamType,
        rating = 8.3f,
        extension = "mp4",
        movieMetadata = MovieMetadata(
            totalDurationMs = 4280000L,
            description = "A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a CEO.",
        )
    ).apply {
        favorite = true
    },
    StreamData(
        streamId = 2,
        num = 1,
        name = "Wonder Women (2024) Full HD",
        streamTypeText = "movie",
        streamIcon = "https://image.tmdb.org/t/p/w600_and_h900_bestv2/qmDpIHrmpJINaRKAfWQfftjCdyi.jpg",
        categoryId = 1,
        added = "1625012046",
        streamType = streamType,
        rating = 8.3f,
        extension = "mp4",
        movieMetadata = MovieMetadata(
            totalDurationMs = 4280000L,
            description = "A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a CEO.",
        )
    ),
    StreamData(
        streamId = 3,
        num = 1,
        name = "Inception (2010)",
        streamTypeText = "movie",
        streamIcon = "https://image.tmdb.org/t/p/w600_and_h900_bestv2/qmDpIHrmpJINaRKAfWQfftjCdyi.jpg",
        categoryId = 1,
        added = "1625012046",
        streamType = streamType,
        rating = 8.3f,
        extension = "mp4",
        movieMetadata = MovieMetadata(
            totalDurationMs = 4280000L,
            description = "A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a CEO.",
        )
    ).apply {
        favorite = true
    },
    StreamData(
        streamId = 4,
        num = 1,
        name = "Inception (2010)",
        streamTypeText = "movie",
        streamIcon = "https://image.tmdb.org/t/p/w600_and_h900_bestv2/qmDpIHrmpJINaRKAfWQfftjCdyi.jpg",
        categoryId = 1,
        added = "1625012046",
        streamType = streamType,
        rating = 8.3f,
        extension = "mp4",
        movieMetadata = MovieMetadata(
            totalDurationMs = 4280000L,
            description = "A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a CEO.",
        )
    ),
    StreamData(
        streamId = 5,
        num = 1,
        name = "Inception (2010)",
        streamTypeText = "movie",
        streamIcon = "https://image.tmdb.org/t/p/w600_and_h900_bestv2/qmDpIHrmpJINaRKAfWQfftjCdyi.jpg",
        categoryId = 1,
        added = "1625012046",
        streamType = streamType,
        rating = 8.3f,
        extension = "mp4",
        movieMetadata = MovieMetadata(
            totalDurationMs = 4280000L,
            description = "A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a CEO.",
        )
    ),
    StreamData(
        streamId = 6,
        num = 1,
        name = "Inception (2010)",
        streamTypeText = "movie",
        streamIcon = "https://image.tmdb.org/t/p/w600_and_h900_bestv2/qmDpIHrmpJINaRKAfWQfftjCdyi.jpg",
        categoryId = 1,
        added = "1625012046",
        streamType = streamType,
        rating = 8.3f,
        extension = "mp4",
        movieMetadata = MovieMetadata(
            totalDurationMs = 4280000L,
            description = "A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a CEO.",
        )
    ),
    StreamData(
        streamId = 7,
        num = 1,
        name = "Inception (2010)",
        streamTypeText = "movie",
        streamIcon = "https://image.tmdb.org/t/p/w600_and_h900_bestv2/qmDpIHrmpJINaRKAfWQfftjCdyi.jpg",
        categoryId = 1,
        added = "1625012046",
        streamType = streamType,
        rating = 8.3f,
        extension = "mp4",
        movieMetadata = MovieMetadata(
            totalDurationMs = 4280000L,
            description = "A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a CEO.",
        )
    ),
    StreamData(
        streamId = 8,
        num = 1,
        name = "Inception (2010)",
        streamTypeText = "movie",
        streamIcon = "https://image.tmdb.org/t/p/w600_and_h900_bestv2/qmDpIHrmpJINaRKAfWQfftjCdyi.jpg",
        categoryId = 1,
        added = "1625012046",
        streamType = streamType,
        rating = 8.3f,
        extension = "mp4",
        movieMetadata = MovieMetadata(
            totalDurationMs = 4280000L,
            description = "A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a CEO.",
        )
    )
)

val sampleCategoryList = listOf(
    CategoryData(
        categoryId = 1,
        categoryName = "English 4k",
        parentId = 0,
        count = 2,
        streamType = StreamType.VideoOnDemand,
        firstStreamIcon = "https://image.tmdb.org/t/p/w600_and_h900_bestv2/qmDpIHrmpJINaRKAfWQfftjCdyi.jpg"
    ),
    CategoryData(
        categoryId = 2,
        categoryName = "English 4k",
        parentId = 0,
        count = 2,
        streamType = StreamType.VideoOnDemand,
        firstStreamIcon = "https://image.tmdb.org/t/p/w600_and_h900_bestv2/qmDpIHrmpJINaRKAfWQfftjCdyi.jpg"
    ),
    CategoryData(
        categoryId = 3,
        categoryName = "English 4k",
        parentId = 0,
        count = 2,
        streamType = StreamType.VideoOnDemand,
        firstStreamIcon = "https://image.tmdb.org/t/p/w600_and_h900_bestv2/qmDpIHrmpJINaRKAfWQfftjCdyi.jpg"
    ),
    CategoryData(
        categoryId = 4,
        categoryName = "English 4k",
        parentId = 0,
        count = 2,
        streamType = StreamType.VideoOnDemand,
        firstStreamIcon = "https://image.tmdb.org/t/p/w600_and_h900_bestv2/qmDpIHrmpJINaRKAfWQfftjCdyi.jpg"
    ),
)