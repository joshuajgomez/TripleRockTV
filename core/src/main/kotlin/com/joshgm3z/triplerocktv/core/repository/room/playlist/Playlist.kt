package com.joshgm3z.triplerocktv.core.repository.room.playlist

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

enum class PlaylistType {
    Xtream,
    M3u8,
}

@Entity(tableName = "playlists")
class Playlist(
    var playlistName: String,
    var url: String,
    var type: PlaylistType,
    val added: Long,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var username: String? = null
    var password: String? = null
    var expiryDate: Long? = null
    var used: Long? = null
}

@Dao
interface PlaylistDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(playlist: Playlist)

    @Delete
    fun delete(playlist: Playlist)

    @Query("DELETE FROM playlists")
    fun deleteAll()

    @Query("SELECT * FROM playlists ORDER BY used DESC")
    fun playlistsFlow(): Flow<List<Playlist>>
}