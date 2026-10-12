package com.joshgm3z.triplerocktv.core.repository.impl

import com.joshgm3z.triplerocktv.core.repository.PlaylistRepository
import com.joshgm3z.triplerocktv.core.repository.room.playlist.Playlist
import com.joshgm3z.triplerocktv.core.repository.room.playlist.PlaylistDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PlaylistRepositoryImpl
@Inject constructor(
    private val playlistDao: PlaylistDao
) : PlaylistRepository {

    override fun playlistsFlow(): Flow<List<Playlist>> = playlistDao.playlistsFlow()

    override suspend fun addPlaylist(playlist: Playlist) {
        playlistDao.insert(playlist)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        playlistDao.delete(playlist)
    }

    override suspend fun deleteAllPlaylists() {
        playlistDao.deleteAll()
    }
}