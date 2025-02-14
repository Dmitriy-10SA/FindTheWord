package com.andef.findtheword.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.andef.findtheword.R


class AnagramAdapter: ListAdapter<String, AnagramAdapter.AnagramViewHolder>(AnagramCallback()) {
    private var _anagrams = mutableListOf<String>()
    var anagrams = _anagrams.toList()
        get() = _anagrams.toList()
        set(value) {
            _anagrams = value.toMutableList()
            field = _anagrams.toList()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnagramViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.anagram_item,
            parent,
            false
        )
        return AnagramViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnagramViewHolder, position: Int) {
        val word = getItem(position)
        holder.textViewAnagram.text = word
    }

    class AnagramViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewAnagram: TextView = itemView.findViewById(R.id.textViewAnagram)
    }
}