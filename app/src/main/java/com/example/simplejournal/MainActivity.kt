package com.example.simplejournal
import android.widget.Button // Add this import at the top of MainActivity.kt
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    companion object {
        private const val NEW_ENTRY_REQUEST_CODE = 1
        private const val EDIT_ENTRY_REQUEST_CODE = 2
        const val EXTRA_ENTRY_ID = "ENTRY_ID"
        const val EXTRA_ENTRY_TITLE = "ENTRY_TITLE"
        const val EXTRA_ENTRY_CONTENT = "ENTRY_CONTENT"
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: JournalEntryAdapter
    private lateinit var journalDao: JournalEntryDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val database = JournalDatabase.getDatabase(application)
        journalDao = database.journalEntryDao()

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = JournalEntryAdapter(
            { entry: JournalEntry ->
                lifecycleScope.launch {
                    val intent = android.content.Intent(this@MainActivity, EditEntryActivity::class.java)
                    intent.putExtra(EXTRA_ENTRY_ID, entry.id)
                    intent.putExtra(EXTRA_ENTRY_TITLE, entry.title)
                    intent.putExtra(EXTRA_ENTRY_CONTENT, entry.content)
                    startActivityForResult(intent, EDIT_ENTRY_REQUEST_CODE)
                    println("Item clicked, Title: ${entry.title}")
                }
            },
            { entry: JournalEntry ->
                lifecycleScope.launch {
                    journalDao.delete(entry)
                }
                true
            }
        )
        recyclerView.adapter = adapter

        lifecycleScope.launch {
            journalDao.getAllEntries().collectLatest { entries ->
//                Log.d("MainActivity", "Number of entries received: ${entries.size}")
                adapter.submitList(entries)
            }
        }

        val addButton: Button = findViewById(R.id.addButton) // Find the new Button
        addButton.setOnClickListener {
            val intent = android.content.Intent(this, EditEntryActivity::class.java)
            startActivityForResult(intent, NEW_ENTRY_REQUEST_CODE)
        }

        // No need to find or setOnClickListener for fabAdd anymore
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: android.content.Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                NEW_ENTRY_REQUEST_CODE -> {
                    val title = data?.getStringExtra(EXTRA_ENTRY_TITLE)
                    val content = data?.getStringExtra(EXTRA_ENTRY_CONTENT)
                    if (!title.isNullOrEmpty() && !content.isNullOrEmpty()) {
                        val newEntry = JournalEntry(title = title, content = content)
                        lifecycleScope.launch {
                            journalDao.insert(newEntry)
                        }
                    }
                }
                EDIT_ENTRY_REQUEST_CODE -> {
                    val entryId = data?.getLongExtra(EXTRA_ENTRY_ID, -1L)
                    val title = data?.getStringExtra(EXTRA_ENTRY_TITLE)
                    val content = data?.getStringExtra(EXTRA_ENTRY_CONTENT)
                    if (entryId != null && entryId != -1L && !title.isNullOrEmpty() && !content.isNullOrEmpty()) {
                        lifecycleScope.launch {
                            val existingEntry = journalDao.getEntryById(entryId)
                            existingEntry?.let {
                                it.title = title
                                it.content = content
                                journalDao.update(it)
                            }
                        }
                    }
                }
            }
        }
    }
}