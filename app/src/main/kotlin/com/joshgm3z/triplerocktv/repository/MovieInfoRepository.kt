package com.joshgm3z.triplerocktv.repository

data class MovieData(
    val description: String,
    val backPoster: String? = null,
) {
    val backPosterUrl: String = "https://image.tmdb.org/t/p/w1280/$backPoster"
}

interface MovieInfoRepository {
    suspend fun searchMovieData(query: String): MovieData?
}