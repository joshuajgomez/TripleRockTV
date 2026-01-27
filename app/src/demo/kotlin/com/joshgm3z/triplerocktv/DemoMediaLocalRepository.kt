package com.joshgm3z.triplerocktv

import com.joshgm3z.triplerocktv.repository.MediaLocalRepository
import com.joshgm3z.triplerocktv.repository.room.CategoryEntity
import com.joshgm3z.triplerocktv.repository.room.StreamEntity
import com.joshgm3z.triplerocktv.ui.home.TopbarItem
import kotlinx.coroutines.delay
import javax.inject.Inject

class DemoMediaLocalRepository
@Inject
constructor() : MediaLocalRepository {

    override suspend fun fetchCategories(
        topbarItem: TopbarItem,
        onSuccess: (List<CategoryEntity>) -> Unit,
        onError: (String) -> Unit
    ) {
        delay(100)
        onSuccess(categoryEntityList)
    }

    override suspend fun fetchAllMediaData(
        categoryId: Int,
        onSuccess: (List<StreamEntity>) -> Unit,
        onError: (String) -> Unit
    ) {
        delay(100)
        when (categoryId) {
            122 -> onSuccess(streamsCategory122)
            56 -> onSuccess(streamEntityCategory56)
            43 -> onSuccess(streamEntityCategory43)
        }
    }

    override suspend fun fetchMediaDataById(
        streamId: Int,
        onSuccess: (StreamEntity) -> Unit,
        onError: (String) -> Unit
    ) {
        delay(100)
        var streamEntity = streamsCategory122.find { it.streamId == streamId }
        if (streamEntity == null)
            streamEntity = streamEntityCategory56.find { it.streamId == streamId }
        if (streamEntity == null)
            streamEntity = streamEntityCategory43.find { it.streamId == streamId }

        if (streamEntity == null)
            onError("Cannot find info")
        else
            onSuccess(streamEntity)

    }

    private val categoryEntityList = listOf(
        CategoryEntity(
            categoryId = 122,
            categoryName = "ENGLISH (4K)",
            parentId = 0,
            count = 3
        ),
        CategoryEntity(
            categoryId = 56,
            categoryName = "OSCAR WINNING MOVIES",
            parentId = 0,
            count = 2
        ),
        CategoryEntity(
            categoryId = 43,
            categoryName = "ENGLISH FHD (2026)",
            parentId = 0,
            count = 1
        ),
    )

    // List for Category ID 122
    private val streamsCategory122 = listOf(
        StreamEntity(
            streamId = 20642,
            num = 1,
            name = "Wonder Woman 1984 (2020)(4K)",
            streamType = "movie",
            streamIcon = "https://image.tmdb.org/t/p/w600_and_h900_bestv2/lNVHB85FUDZqLzvug3k6FA07RIr.jpg",
            categoryId = 122,
            added = "1609012046"
        ),
        StreamEntity(
            streamId = 20646,
            num = 2,
            name = "Soul (2020).(4K)",
            streamType = "movie",
            streamIcon = "https://image.tmdb.org/t/p/w600_and_h900_bestv2/hm58Jw4Lw8OIeECIq5qyPYhAeRJ.jpg",
            categoryId = 122,
            added = "1609059512"
        ),
        StreamEntity(
            streamId = 24585,
            num = 3,
            name = "The Equalizer 2 (2018)",
            streamType = "movie",
            streamIcon = "https://image.tmdb.org/t/p/w600_and_h900_bestv2/cQvc9N6JiMVKqol3wcYrGshsIdZ.jpg",
            categoryId = 122,
            added = "1619623164"
        ),
    )

    private val streamEntityCategory56: List<StreamEntity> = listOf(
        StreamEntity(
            streamId = 110926,
            num = 8,
            name = "Minions: The Rise of Gru (2022)",
            streamType = "movie",
            streamIcon = "https://image.tmdb.org/t/p/w600_and_h900_bestv2/wKiOkZTN9lUUUNZLmtnwubZYONg.jpg",
            categoryId = 56,
            added = "1660403258"
        ),
        StreamEntity(
            streamId = 110929,
            num = 9,
            name = "The Forgiven (2022)",
            streamType = "movie",
            streamIcon = "https://image.tmdb.org/t/p/w600_and_h900_bestv2/nlwyzhUWSVpRQjUHKFFECPyKPOA.jpg",
            categoryId = 56,
            added = "1660403815"
        ),
        StreamEntity(
            streamId = 110930,
            num = 10,
            name = "Thirteen Lives (2022)(4K)",
            streamType = "movie",
            streamIcon = "https://image.tmdb.org/t/p/w600_and_h900_bestv2/yi5KcJqFxy0D6yP8nCfcF8gJGg5.jpg",
            categoryId = 56,
            added = "1660403999"
        ),
        StreamEntity(
            streamId = 110939,
            num = 11,
            name = "A Farewell to Ozark (2022)",
            streamType = "movie",
            streamIcon = "https://image.tmdb.org/t/p/w600_and_h900_bestv2/h6HRRa9DKIyf743QD1xwKZjyVRb.jpg",
            categoryId = 56,
            added = "1660407904"
        )
    )

    private val streamEntityCategory43: List<StreamEntity> = listOf(
        StreamEntity(
            streamId = 94503,
            num = 4,
            name = "Downton Abbey: A New Era (2022)",
            streamType = "movie",
            streamIcon = "https://image.tmdb.org/t/p/w600_and_h900_bestv2/r5n4CLoIjUcnT3shWDi6MHdJ25a.jpg",
            categoryId = 43,
            added = "1655460997"
        ),
        StreamEntity(
            streamId = 95798,
            num = 5,
            name = "Doctor Strange in the Multiverse of Madness (2022)",
            streamType = "movie",
            streamIcon = "https://image.tmdb.org/t/p/w600_and_h900_bestv2/9Gtg2DzBhmYamXBS1hKAhiwbBKS.jpg",
            categoryId = 43,
            added = "1655934284"
        ),
        StreamEntity(
            streamId = 110922,
            num = 6,
            name = "Andrew Schulz: Infamous (2022)",
            streamType = "movie",
            streamIcon = "https://image.tmdb.org/t/p/w600_and_h900_bestv2/oKHJ4xsqi66hYU3fMiKbUd92i6U.jpg",
            categoryId = 43,
            added = "1660402806"
        ),
        StreamEntity(
            streamId = 110924,
            num = 7,
            name = "Elvis (2022)",
            streamType = "movie",
            streamIcon = "https://image.tmdb.org/t/p/w600_and_h900_bestv2/qBOKWqAFbveZ4ryjJJwbie6tXkQ.jpg",
            categoryId = 43,
            added = "1660403013"
        ),
    )

}