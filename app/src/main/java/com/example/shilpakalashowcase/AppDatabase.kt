package com.example.shilpakalashowcase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * The Room Database for the app.
 * Follows the Singleton pattern to ensure only one instance exists.
 */
@Database(entities = [Artwork::class, ArtisanProfile::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun artworkDao(): ArtworkDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // If the INSTANCE is not null, return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "shilpa_kala_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
