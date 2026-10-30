package com.group5.track.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.group5.track.data.local.dao.PropertyDao
import com.group5.track.data.local.entity.PropertyEntity

@Database(
    entities = [PropertyEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun propertyDao(): PropertyDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "track_database.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                val timestamp = System.currentTimeMillis()
                db.execSQL("""
                    INSERT INTO properties (title, address, property_type, monthly_price, bedrooms, is_available, agent_phone, description, created_at)
                    VALUES 
                    ('Modern 2BR Apartment', 'A-204, Nairobi', 'Apartment', 25000.0, 2, 1, '+254712345678', 'Spacious apartment with parking and security', $timestamp),
                    ('Cozy 1BR Studio', 'B-101, Nairobi', 'Apartment', 18000.0, 1, 1, '+254723456789', 'Affordable studio near matatu stage', ${timestamp - 86400000}),
                    ('Luxury 3BR House', 'C-305, Nairobi', 'House', 65000.0, 3, 1, '+254734567890', 'Premium house with garden and DSQ', ${timestamp - 172800000})
                """.trimIndent())
            }
        }
    }
}
