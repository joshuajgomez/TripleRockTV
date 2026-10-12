package com.joshgm3z.triplerocktv.core.repository

import com.joshgm3z.triplerocktv.core.repository.room.playlist.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {

    fun playlistsFlow(): Flow<List<Playlist>>

    suspend fun addPlaylist(playlist: Playlist)

    suspend fun deletePlaylist(playlist: Playlist)

    suspend fun deleteAllPlaylists()
}