package com.mili.eclipsereads.data.local.db

import androidx.room.migration.Migration

/**
 * This object contains the database migrations.
 * Add new migrations to the MIGRATIONS list as the database schema evolves.
 * Example:
 * val MIGRATION_1_2 = object : Migration(1, 2) {
 *     override fun migrate(database: SupportSQLiteDatabase) {
 *         database.execSQL("ALTER TABLE users ADD COLUMN last_update INTEGER")
 *     }
 * }
 */
object Migrations {
    val ALL_MIGRATIONS = arrayOf<Migration>(
        // Add migrations here
    )
}
