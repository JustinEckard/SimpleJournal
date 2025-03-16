package com.example.simplejournal // Make sure this matches your package name

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [JournalEntry::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class JournalDatabase : RoomDatabase() {

    abstract fun journalEntryDao(): JournalEntryDao

    companion object {
        @Volatile
        private var INSTANCE: JournalDatabase? = null

        fun getDatabase(context: Context): JournalDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    JournalDatabase::class.java,
                    "journal_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}