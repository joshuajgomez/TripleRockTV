package com.joshgm3z.triplerocktv.repository.retrofit

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface OpenSubtitlesService {
    @GET("subtitles")
    suspend fun searchSubtitles(
        @Query("query") query: String,
        @Header("Api-Key") apiKey: String = Secrets.OPEN_SUBTITLES_API_KEY,
        @Header("User-Agent") userAgent: String = "TripleRockTV v1.0",
        @Header("Content-Type") contentType: String = "application/json",
        @Header("Accept") accept: String = "application/json",
    ): OpenSubtitlesSearchResponse

    @POST("login")
    suspend fun login(
        @Header("Api-Key") apiKey: String = Secrets.OPEN_SUBTITLES_API_KEY,
        @Header("User-Agent") userAgent: String = "TripleRockTV v1.0",
        @Header("Content-Type") contentType: String = "application/json",
        @Header("Accept") accept: String = "application/json",
        @Body body: OpenSubtitlesLoginRequest = OpenSubtitlesLoginRequest(),
    ): OpenSubtitlesLoginResponse

    @POST("download")
    suspend fun download(
        @Header("Authorization") token: String,
        @Header("Api-Key") apiKey: String = Secrets.OPEN_SUBTITLES_API_KEY,
        @Header("User-Agent") userAgent: String = "TripleRockTV v1.0",
        @Header("Content-Type") contentType: String = "application/json",
        @Header("Accept") accept: String = "application/json",
        @Body body: OpenSubtitlesDownloadRequest,
    ): OpenSubtitlesDownloadResponse
}

data class OpenSubtitlesDownloadRequest(
    @SerializedName("file_id")
    val fileId: Int
)

data class OpenSubtitlesDownloadResponse(
    @SerializedName("link")
    val link: String,
    @SerializedName("file_name")
    val fileName: String,
    @SerializedName("requests")
    val requests: Int,
    @SerializedName("remaining")
    val remaining: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("reset_time")
    val resetTime: String,
    @SerializedName("reset_time_utc")
    val resetTimeUtc: String,
)

data class OpenSubtitlesLoginRequest(
    val username: String = Secrets.OPEN_SUBTITLES_USERNAME,
    val password: String = Secrets.OPEN_SUBTITLES_PASSWORD
)

data class OpenSubtitlesLoginResponse(
    @SerializedName("user")
    val user: OpenSubtitlesUser,
    @SerializedName("token")
    val token: String,
    @SerializedName("base_url")
    val baseUrl: String,
    @SerializedName("status")
    val status: Int,
)

data class OpenSubtitlesUser(
    @SerializedName("allowed_translations")
    val allowedTranslations: Int,
    @SerializedName("level")
    val level: String,
    @SerializedName("allowed_downloads")
    val allowedDownloads: Int,
    @SerializedName("user_id")
    val userId: Int,
)

// OpenSubtitles API response data classes
data class OpenSubtitlesSearchResponse(
    @SerializedName("data")
    val data: List<OpenSubtitlesSubtitle>
)

data class OpenSubtitlesSubtitle(
    @SerializedName("id")
    val id: String,
    @SerializedName("attributes")
    val attributes: OpenSubtitlesAttributes
)

data class OpenSubtitlesAttributes(
    @SerializedName("subtitle_id")
    val subtitleId: String,
    @SerializedName("language")
    val language: String,
    @SerializedName("download_count")
    val downloadCount: Int,
    @SerializedName("hearing_impaired")
    val hearingImpaired: Boolean,
    @SerializedName("hd")
    val hd: Boolean,
    @SerializedName("ratings")
    val ratings: Double,
    @SerializedName("release_name")
    val releaseName: String?,
    @SerializedName("upload_date")
    val uploadDate: String,
    @SerializedName("votes")
    val votes: Int,
    @SerializedName("url")
    val url: String?,
    @SerializedName("feature_details")
    val featureDetails: FeatureDetails?,
    @SerializedName("files")
    val files: List<OpenSubtitleFile>?
)

data class FeatureDetails(
    @SerializedName("title")
    val title: String,
)

data class OpenSubtitleFile(
    @SerializedName("file_id")
    val fileId: Int,
    @SerializedName("cd_number")
    val cdNumber: Int,
    @SerializedName("file_name")
    val fileName: String,
)
