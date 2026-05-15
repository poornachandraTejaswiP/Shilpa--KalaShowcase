package com.example.shilpakalashowcase

import kotlinx.coroutines.flow.Flow

/**
 * Repository class that abstracts access to the data source.
 */
class ArtworkRepository(private val artworkDao: ArtworkDao) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allArtworks: Flow<List<Artwork>> = artworkDao.getAllArtworks()
    
    val favoriteArtworks: Flow<List<Artwork>> = artworkDao.getFavoriteArtworks()

    fun getArtworksByCategory(category: String): Flow<List<Artwork>> = artworkDao.getArtworksByCategory(category)

    suspend fun toggleFavorite(id: String, isFavorite: Boolean) {
        artworkDao.updateFavoriteStatus(id, isFavorite)
    }

    // Suspend function to insert artworks
    suspend fun insertArtworks(artworks: List<Artwork>) {
        artworkDao.insertArtworks(artworks)
    }

    // Suspend function to insert a single artwork
    suspend fun insertArtwork(artwork: Artwork) {
        artworkDao.insertArtwork(artwork)
    }

    // Delete all artworks
    suspend fun deleteAll() {
        artworkDao.deleteAll()
    }

    // Profile Methods
    val profile: Flow<ArtisanProfile?> = artworkDao.getProfile()

    suspend fun updateProfile(profile: ArtisanProfile) {
        artworkDao.updateProfile(profile)
    }
}
