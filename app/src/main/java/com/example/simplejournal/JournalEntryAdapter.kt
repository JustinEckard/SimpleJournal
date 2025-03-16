package com.example.simplejournal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale

class JournalEntryAdapter(
    private val onItemClickListener: (JournalEntry) -> Unit,
    private val onItemLongClickListener: (JournalEntry) -> Boolean
) : ListAdapter<JournalEntry, JournalEntryAdapter.JournalEntryViewHolder>(JournalEntryDiffCallback()) {

    inner class JournalEntryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.textViewTitle)
        val dateTextView: TextView = itemView.findViewById(R.id.textViewDate)
        val contentTextView: TextView = itemView.findViewById(R.id.textViewContent)

        fun bind(entry: JournalEntry) {
            titleTextView.text = entry.title
            val dateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
            dateTextView.text = dateFormat.format(entry.createdAt)
            contentTextView.text = entry.content

            itemView.setOnClickListener {
                onItemClickListener(entry)
            }
            itemView.setOnLongClickListener {
                onItemLongClickListener(entry)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JournalEntryViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_journal_entry, parent, false)
        return JournalEntryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: JournalEntryViewHolder, position: Int) {
        val currentEntry = getItem(position)
        holder.bind(currentEntry)
    }

    class JournalEntryDiffCallback : DiffUtil.ItemCallback<JournalEntry>() {
        override fun areItemsTheSame(oldItem: JournalEntry, newItem: JournalEntry): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: JournalEntry, newItem: JournalEntry): Boolean {
            return oldItem == newItem
        }
    }
}