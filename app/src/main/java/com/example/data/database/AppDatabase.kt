package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TabEntity::class, DebtEntity::class, PersonEntity::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun debtDao(): DebtDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "deyoni_database"
                )
// Removed fallbackToDestructiveMigration to protect user data from wiping
                // .fallbackToDestructiveMigration()
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                .build()
                INSTANCE = instance
                instance
            }
        }

        private val MIGRATION_1_2 = object : androidx.room.migration.Migration(1, 2) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                // If any schema changed from 1 to 2, write it here. Otherwise, leave empty if it was just additive or no-op needed
            }
        }
        
        private val MIGRATION_2_3 = object : androidx.room.migration.Migration(2, 3) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `persons` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `defaultType` TEXT NOT NULL, `iconName` TEXT NOT NULL, `createdAt` INTEGER NOT NULL)")
            }
        }
    }
}
