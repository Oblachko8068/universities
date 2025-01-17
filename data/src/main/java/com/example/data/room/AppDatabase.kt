package com.example.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.data.model.CountryDbEntity
import com.example.data.model.UniversityDbEntity

@Database(
    version = 1,
    entities = [
        CountryDbEntity::class,
        UniversityDbEntity::class
    ]
)

abstract class AppDatabase: RoomDatabase() {

    abstract fun getCountryDao(): CountryDao

    abstract fun getUniversityDao(): UniversityDao
}