package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        DonorEntity::class,
        BloodRequestEntity::class,
        DonorNotificationEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class LifeLinkDatabase : RoomDatabase() {
    abstract fun lifeLinkDao(): LifeLinkDao

    companion object {
        @Volatile
        private var INSTANCE: LifeLinkDatabase? = null

        fun getInstance(context: Context): LifeLinkDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LifeLinkDatabase::class.java,
                    "lifelink_production.db"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
