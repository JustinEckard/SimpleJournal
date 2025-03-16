package com.example.simplejournal

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface JournalEntryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entry: JournalEntry)

    @Update
    suspend fun update(entry: JournalEntry)

    @Delete
    suspend fun delete(entry: JournalEntry)

    @Query("SELECT * FROM journal_entries ORDER BY createdAt DESC")
    fun getAllEntries(): Flow<List<JournalEntry>>

    @Query("SELECT * FROM journal_entries WHERE id = :id")
    suspend fun getEntryById(id: Long): JournalEntry?
}


