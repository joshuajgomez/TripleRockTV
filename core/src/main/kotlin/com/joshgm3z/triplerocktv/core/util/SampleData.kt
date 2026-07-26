package com.joshgm3z.triplerocktv.core.util

import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.core.repository.room.category.CategoryData
import com.joshgm3z.triplerocktv.core.repository.room.series.SeriesStream
import com.joshgm3z.triplerocktv.core.repository.room.stream.StreamData

val sampleSeriesList = mutableListOf<SeriesStream>().apply {
    repeat(15) {
        add(SeriesStream(seriesId = it, name = "Stranger Things"))
    }
}

val sampleVodList = mutableListOf<StreamData>().apply {
    repeat(15) {
        add(
            StreamData(
                streamId = it,
                name = "Inception (2010)",
                streamType = StreamType.VideoOnDemand
            )
        )
    }
}

val sampleLiveTvList = mutableListOf<StreamData>().apply {
    repeat(15) {
        add(
            StreamData(
                streamId = it,
                name = "Inception (2010)",
                streamType = StreamType.LiveTV
            )
        )
    }
}

val sampleVodCategoryList = mutableListOf<CategoryData>().apply {
    repeat(15) {
        add(
            CategoryData(
                categoryId = it,
                categoryName = "English 4k",
                streamType = StreamType.VideoOnDemand
            )
        )
    }
}

val sampleLiveTvCategoryList = mutableListOf<CategoryData>().apply {
    repeat(15) {
        add(
            CategoryData(
                categoryId = it,
                categoryName = "English 4k",
                streamType = StreamType.LiveTV
            )
        )
    }
}

val sampleSeriesCategoryList = mutableListOf<CategoryData>().apply {
    repeat(15) {
        add(
            CategoryData(
                categoryId = it,
                categoryName = "English 4k",
                streamType = StreamType.Series
            )
        )
    }
}

val sampleLabelToCategoryMap = mutableMapOf<String, List<CategoryData>>().apply {
    put("English", sampleVodCategoryList)
    put("Malayalam", sampleVodCategoryList)
    put("Hindi", sampleVodCategoryList)
}