package com.example.simplejournal // Make sure this matches your package name

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class EditEntryActivity : AppCompatActivity() {

    private lateinit var editTextTitle: EditText
    private lateinit var editTextContent: EditText
    private lateinit var fabSave: FloatingActionButton
    private var entryId: Long = -1L
    private lateinit var journalDao: JournalEntryDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_entry)

        val database = JournalDatabase.getDatabase(application)
        journalDao = database.journalEntryDao()

        editTextTitle = findViewById(R.id.editTextTitle)
        editTextContent = findViewById(R.id.editTextContent)
        fabSave = findViewById(R.id.fabSave)


        if (intent.hasExtra(MainActivity.EXTRA_ENTRY_ID)) {
            entryId = intent.getLongExtra(MainActivity.EXTRA_ENTRY_ID, -1L)
            val title = intent.getStringExtra(MainActivity.EXTRA_ENTRY_TITLE)
            val content = intent.getStringExtra(MainActivity.EXTRA_ENTRY_CONTENT)

            editTextTitle.setText(title)
            editTextContent.setText(content)
        }

        fabSave.setOnClickListener {
            val title = editTextTitle.text.toString()
            val content = editTextContent.text.toString()

            lifecycleScope.launch {
                if (entryId != -1L) {
                    val existingEntry = journalDao.getEntryById(entryId)
                    existingEntry?.let {
                        it.title = title
                        it.content = content
                        journalDao.update(it)
                    }
                } else {

                    val newEntry = JournalEntry(title = title, content = content)
                    journalDao.insert(newEntry)
                }
                finish()
            }
        }
    }
}