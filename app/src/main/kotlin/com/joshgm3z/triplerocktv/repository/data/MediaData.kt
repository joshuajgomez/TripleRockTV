package com.joshgm3z.triplerocktv.repository.data

class MediaData(
    val id: String,
    val title: String,
    val year: String,
    val description: String,
    val thumbNail: Int,
) {
    companion object {
        fun sample() = MediaData(
            id = "1",
            title = "Avatar: The Way of Water",
            year = "2022",
            description = "Second installment to Avatar, this is a blockbuster from James Cameron. Second installment to Avatar, this is a blockbuster from James Cameron. Second installment to Avatar, this is a blockbuster from James Cameron.",
            thumbNail = com.joshgm3z.triplerocktv.R.drawable.avatar_movie
        )
    }
}
