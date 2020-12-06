package com.example.recyclerviewswipemenu

import androidx.recyclerview.widget.RecyclerView
import com.example.recyclerviewswipemenu.databinding.RecyclerviewItemPlayerBinding

class PlayerViewHolder(binding: RecyclerviewItemPlayerBinding) :
    RecyclerView.ViewHolder(binding.root) {

    private val tvName = binding.tvName
    private val tvNationality = binding.tvNationality
    private val tvClub = binding.tvClub
    private val tvOverall = binding.tvOverall
    private val tvAge = binding.tvAge

    fun bindWithData(item: Player) {
        this.tvName.text = item.name
        this.tvNationality.text = item.nationality
        this.tvClub.text = item.club
        this.tvOverall.text = item.overall.toString()
        this.tvAge.text = item.age.toString()
    }
}