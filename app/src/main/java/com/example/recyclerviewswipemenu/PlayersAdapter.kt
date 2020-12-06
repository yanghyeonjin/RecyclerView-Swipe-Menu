package com.example.recyclerviewswipemenu

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.recyclerviewswipemenu.databinding.RecyclerviewItemPlayerBinding

class PlayersAdapter : RecyclerView.Adapter<PlayerViewHolder>() {

    private var players = ArrayList<Player>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        return PlayerViewHolder(
            RecyclerviewItemPlayerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return this.players.size
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        holder.bindWithData(players[position])
    }

    fun setData(list: ArrayList<Player>) {
        this.players = list
        notifyDataSetChanged()
    }
}