package com.joshgm3z.triplerocktv.repository.room.epg

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface EpgListingDao {
    @Query("SELECT * FROM epg_listing")
    fun getAllEpgListings(): List<IptvEpgListing>

    @Query("SELECT * FROM epg_listing WHERE id = :id")
    fun getEpgListingById(id: Int): IptvEpgListing?

    @Query("DELETE FROM epg_listing")
    fun deleteAllEpgListings()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(epgListings: List<IptvEpgListing>)
}