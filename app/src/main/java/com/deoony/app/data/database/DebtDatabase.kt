package com.deoony.app.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [TabEntity::class, PersonEntity::class, DebtEntity::class, PaymentEntity::class],
    version = 1,
    exportSchema = true
)
abstract class DebtDatabase : RoomDatabase() {
    abstract fun debtDao(): DebtDao

    companion object {
        @Volatile
        private var INSTANCE: DebtDatabase? = null

        fun getDatabase(context: Context): DebtDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DebtDatabase::class.java,
                    "debt_database.db"
                )
                // NO fallbackToDestructiveMigration as requested by user!
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
