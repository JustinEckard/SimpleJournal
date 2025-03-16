package com.example.simplejournal // Make sure this matches your package name

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "journal_entries")
data class JournalEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    var title: String,
    var content: String,
    val createdAt: Date = Date()
)