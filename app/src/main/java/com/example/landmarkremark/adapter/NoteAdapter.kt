package com.example.landmarkremark.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.landmarkremark.data.models.Note
import com.example.landmarkremark.databinding.ItemNoteBinding

class NoteAdapter(var noteList: List<Note>) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    var onItemClick: ((Note) -> Unit)? = null
    var onItemDetailClick: ((Note) -> Unit)? = null

    inner class NoteViewHolder(private val binding: ItemNoteBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(note: Note) {
            binding.note = note
            binding.executePendingBindings()
        }

        fun setOnClick(note: Note) {
            itemView.setOnClickListener { onItemClick?.invoke(note) }

            binding.ivDetail.setOnClickListener {
                onItemDetailClick?.invoke(note)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemNoteBinding.inflate(inflater, parent, false)
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val currentNote = noteList[position]
        holder.bind(currentNote)
        holder.setOnClick(currentNote)
    }

    override fun getItemCount(): Int {
        return noteList.size
    }
}