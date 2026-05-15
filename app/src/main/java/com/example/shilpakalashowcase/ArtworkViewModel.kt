package com.example.shilpakalashowcase

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * ViewModel to provide data to the UI and survive configuration changes.
 */
class ArtworkViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ArtworkRepository
    val allArtworks: Flow<List<Artwork>>
    val favoriteArtworks: Flow<List<Artwork>>
    val artisanProfile: Flow<ArtisanProfile?>

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    val filteredArtworks: Flow<List<Artwork>>

    val totalSales: Flow<String>
    val pendingOrders: Flow<Int>

    init {
        val artworkDao = AppDatabase.getDatabase(application).artworkDao()
        repository = ArtworkRepository(artworkDao)
        allArtworks = repository.allArtworks
        favoriteArtworks = repository.favoriteArtworks
        artisanProfile = repository.profile

        filteredArtworks = combine(allArtworks, _selectedCategory) { artworks, category ->
            if (category == "All") artworks else artworks.filter { it.category == category }
        }

        totalSales = allArtworks.map { artworks ->
            val sum = artworks.filter { it.status == "Sold" }
                .sumOf { it.price.replace("₹", "").replace(",", "").toIntOrNull() ?: 0 }
            "₹${String.format(Locale.getDefault(), "%,d", sum)}"
        }

        pendingOrders = allArtworks.map { artworks ->
            artworks.count { it.status == "In Progress" }
        }
        
        // Seed database with initial data
        seedDatabase()
    }

    private fun seedDatabase() = viewModelScope.launch {
        val currentArtworks = allArtworks.first()
        if (currentArtworks.isEmpty()) {
            val sampleArtworks = listOf(
                Artwork("SK001", "Hoysala Stone Idol", "A detailed hand-carved idol inspired by Hoysala temple sculpture style.", "Black Stone", "₹25,000", "https://images.unsplash.com/photo-1605640840605-14ac1855827b?w=900", "Available", "Ramesh Shilpi", "Stone Idols"),
                Artwork("SK002", "Temple Pillar Carving", "Traditional pillar design with floral and mythological patterns.", "Granite", "₹40,000", "https://images.unsplash.com/photo-1590050752117-238cb0fb12b1?w=900", "In Progress", "Mahesh Acharya", "Stone Idols"),
                Artwork("SK003", "Wooden Ganesha", "Premium wooden sculpture crafted with smooth finishing.", "Sandal Wood", "₹18,000", "https://images.unsplash.com/photo-1577083552431-6e5fd01aa342?w=900", "Available", "Kiran Crafts", "Wood Carving"),
                Artwork("SK004", "Nandi Sculpture", "A classic Nandi statue suitable for home and temple decoration.", "Soap Stone", "₹32,000", "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?w=900", "Sold", "Shivaraj Shilpi", "Stone Idols"),
                Artwork("SK005", "Heritage Door Frame", "Wooden door frame with heritage motifs and detailed border carving.", "Teak Wood", "₹55,000", "https://images.unsplash.com/photo-1518005020951-eccb494ad742?w=900", "Available", "Manjunath Art Works", "Wood Carving"),
                Artwork("SK006", "Krishna Idol", "Elegant Krishna idol with fine facial detailing and polish.", "Marble", "₹22,000", "Available", "Nagaraj Shilpi", "Stone Idols")
            )
            repository.insertArtworks(sampleArtworks)
        }

        if (artisanProfile.first() == null) {
            repository.updateProfile(ArtisanProfile(
                name = "Ramesh Shilpi",
                craftType = "Stone Carving",
                location = "Shivarapatna, Karnataka",
                experience = "18 years",
                speciality = "Hoysala and temple idols",
                about = "A master craftsman preserving traditional stone carving techniques and creating premium handmade sculptures."
            ))
        }
    }

    fun setCategory(category: String) {
        _selectedCategory.value = category
    }

    fun toggleFavorite(artwork: Artwork) = viewModelScope.launch {
        repository.toggleFavorite(artwork.id, !artwork.isFavorite)
    }

    fun updateProfile(profile: ArtisanProfile) = viewModelScope.launch {
        repository.updateProfile(profile)
    }

    fun insert(artwork: Artwork) = viewModelScope.launch {
        repository.insertArtwork(artwork)
    }
}
