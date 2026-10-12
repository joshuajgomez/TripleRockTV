package com.joshgm3z.triplerocktv.core.repository.m3u8

import com.joshgm3z.triplerocktv.core.repository.impl.getName
import com.joshgm3z.triplerocktv.core.repository.room.playlist.Playlist
import com.joshgm3z.triplerocktv.core.repository.room.playlist.PlaylistDao
import com.joshgm3z.triplerocktv.core.repository.room.playlist.PlaylistType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.InputStream
import javax.inject.Inject

data class M3uChannel(
    val name: String,
    val url: String,
    val logoUrl: String?,
    val group: String?
) {
    override fun toString(): String {
        return "\nM3uChannel(name='$name', url='$url', logoUrl=$logoUrl, group=$group)"
    }
}

class M3u8PlaylistRepository
@Inject constructor(
    private val playlistDao: PlaylistDao
) {

    private val propertyRegex = """([a-zA-Z0-9_-]+)="([^"]+)"""".toRegex()

    suspend fun isUrlValid(playlistUrl: String): Boolean {
        val m3uService = getM3u8Service(playlistUrl)
        val response = m3uService.downloadPlaylist(playlistUrl)
        return response.isSuccessful.apply {
            if (this) {
                withContext(Dispatchers.IO) {
                    playlistDao.insert(
                        Playlist(
                            playlistName = playlistUrl.getName(),
                            url = playlistUrl,
                            type = PlaylistType.M3u8,
                            added = System.currentTimeMillis(),
                        )
                    )
                }
            }
        }
    }

    suspend fun getChannels(playlistUrl: String): List<M3uChannel> = withContext(Dispatchers.IO) {
        val m3uService = getM3u8Service(playlistUrl)
        val response = m3uService.downloadPlaylist(playlistUrl)
        val body = response.body() ?: return@withContext emptyList()

        parseM3u(body.byteStream())
    }

    private fun getM3u8Service(baseUrl: String): M3uService {
        val formattedUrl = if (!baseUrl.endsWith("/")) "$baseUrl/" else baseUrl
        return Retrofit.Builder()
            .baseUrl(formattedUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(M3uService::class.java)
    }

    private fun parseM3u(inputStream: InputStream): List<M3uChannel> {
        val channels = mutableListOf<M3uChannel>()
        var currentMetadata: Map<String, String>? = null
        var currentName: String? = null

        inputStream.bufferedReader().useLines { lines ->
            lines.forEach { line ->
                val trimmedLine = line.trim()
                when {
                    trimmedLine.startsWith("#EXTINF") -> {
                        // Extract attributes: tvg-logo="...", group-title="..."
                        currentMetadata = propertyRegex.findAll(trimmedLine)
                            .associate { it.groupValues[1] to it.groupValues[2] }

                        // Extract channel name (text after the last comma)
                        currentName = trimmedLine.substringAfterLast(",").trim()
                    }
                    // If it's a URL line (not a comment and not empty)
                    trimmedLine.isNotEmpty() && !trimmedLine.startsWith("#") -> {
                        if (currentName != null) {
                            channels.add(
                                M3uChannel(
                                    name = currentName!!,
                                    url = trimmedLine,
                                    logoUrl = currentMetadata?.get("tvg-logo"),
                                    group = currentMetadata?.get("group-title")
                                )
                            )
                        }
                        currentMetadata = null
                        currentName = null
                    }
                }
            }
        }
        return channels
    }

}
