package com.example.shilpakalashowcase

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity class representing an Artwork in the Room database.
 */
@Entity(tableName = "artworks")
data class Artwork(
    @PrimaryKey val id: String, // Artwork ID
    val title: String,          // Title of the artwork
    val description: String,    // Detailed description
    val material: String,       // Material used (e.g., Stone, Wood)
    val price: String,          // Price (e.g., ₹25,000)
    val image: String,          // Image URL or local URI
    val status: String,         // Status (Available / In Progress / Sold)
    val artisan: String,        // Artisan Name
    val category: String = "Stone Idols", // Category (Stone Idols, Wood Carving, Temple Art)
    val isFavorite: Boolean = false       // Favorite status
)

@Entity(tableName = "artisan_profile")
data class ArtisanProfile(
    @PrimaryKey val id: Int = 1,
    val name: String,
    val craftType: String,
    val location: String,
    val experience: String,
    val speciality: String,
    val about: String
)
