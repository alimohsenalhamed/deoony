package com.deoony.app.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [TabEntity::class, DebtEntity::class, PersonEntity::class, DebtPaymentEntity::class],
    version = 4,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun debtDao(): DebtDao
    abstract fun debtPaymentDao(): DebtPaymentDao

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
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
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

        private val MIGRATION_3_4 = object : androidx.room.migration.Migration(3, 4) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                // 1. Create debt_payments table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `debt_payments` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `debtId` INTEGER NOT NULL,
                        `amountMinor` INTEGER NOT NULL,
                        `currencyCode` TEXT NOT NULL DEFAULT 'YER',
                        `paidAtMillis` INTEGER NOT NULL,
                        `note` TEXT NOT NULL,
                        `createdAtMillis` INTEGER NOT NULL
                    )
                """.trimIndent())

                // 2. Create the temp table matching version 4 debts structure
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `debts_new` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `tabId` INTEGER NOT NULL,
                        `title` TEXT NOT NULL,
                        `personName` TEXT NOT NULL,
                        `amountMinor` INTEGER NOT NULL,
                        `currencyCode` TEXT NOT NULL,
                        `currencyScale` INTEGER NOT NULL,
                        `isLentByMe` INTEGER NOT NULL,
                        `dueDate` TEXT NOT NULL,
                        `dueDateMillis` INTEGER,
                        `reminderEnabled` INTEGER NOT NULL,
                        `reminderDateTime` INTEGER,
                        `isPaid` INTEGER NOT NULL,
                        `status` TEXT NOT NULL,
                        `notes` TEXT NOT NULL,
                        `createdAt` INTEGER NOT NULL
                    )
                """.trimIndent())

                // 3. Populate temporary table with converted amount and status mapping
                db.execSQL("""
                    INSERT INTO `debts_new` (
                        `id`, `tabId`, `title`, `personName`, `amountMinor`, `currencyCode`, `currencyScale`,
                        `isLentByMe`, `dueDate`, `dueDateMillis`, `reminderEnabled`, `reminderDateTime`,
                        `isPaid`, `status`, `notes`, `createdAt`
                    )
                    SELECT
                        `id`, `tabId`, `title`, `personName`,
                        CAST(ROUND(`amount` * 100) AS INTEGER), 'YER', 2,
                        `isLentByMe`, `dueDate`,
                        CASE WHEN strftime('%s', `dueDate`) IS NOT NULL 
                             THEN CAST(strftime('%s', `dueDate`) AS INTEGER) * 1000 
                             ELSE NULL 
                        END,
                        `reminderEnabled`, `reminderDateTime`,
                        `isPaid`, CASE WHEN `isPaid` = 1 THEN 'PAID' ELSE 'ACTIVE' END, `notes`, `createdAt`
                    FROM `debts`
                """.trimIndent())

                // 4. Drop the old debts table
                db.execSQL("DROP TABLE IF EXISTS `debts`")

                // 5. Rename debts_new to debts
                db.execSQL("ALTER TABLE `debts_new` RENAME TO `debts`")
            }
        }
    }
}
