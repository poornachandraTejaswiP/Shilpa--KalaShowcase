package com.example.shilpakalashowcase

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for the artworks table.
 */
@Dao
interface ArtworkDao {
    // Fetch all artworks as a Flow (reactive updates)
    @Query("SELECT * FROM artworks")
    fun getAllArtworks(): Flow<List<Artwork>>

    // Fetch favorite artworks
    @Query("SELECT * FROM artworks WHERE isFavorite = 1")
    fun getFavoriteArtworks(): Flow<List<Artwork>>

    // Fetch artworks by category
    @Query("SELECT * FROM artworks WHERE category = :category")
    fun getArtworksByCategory(category: String): Flow<List<Artwork>>

    // Toggle favorite status
    @Query("UPDATE artworks SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: String, isFavorite: Boolean)

    // Insert a list of artworks (replaces existing ones on conflict)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArtworks(artworks: List<Artwork>)

    // Insert a single artwork
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArtwork(artwork: Artwork)

    // Delete all artworks
    @Query("DELETE FROM artworks")
    suspend fun deleteAll()

    // Profile Methods
    @Query("SELECT * FROM artisan_profile WHERE id = 1")
    fun getProfile(): Flow<ArtisanProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateProfile(profile: ArtisanProfile)
}
