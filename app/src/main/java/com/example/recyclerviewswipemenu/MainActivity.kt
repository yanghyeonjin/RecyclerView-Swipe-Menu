package com.example.recyclerviewswipemenu

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recyclerviewswipemenu.databinding.ActivityMainBinding
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val TAG: String = "TAG"

    private lateinit var playersAdapter: PlayersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setPlayersAdapter()
        setupRecyclerView()
    }

    private fun setPlayersAdapter() {
        val players = ArrayList<Player>()

        try {
            val inputStreamReader = InputStreamReader(assets.open("players.csv"))

            val reader = BufferedReader(inputStreamReader)
            reader.readLine()
            var line: String?
            var st: List<String>


            line = reader.readLine()
            while (line != null) {
                st = line.split(",")

                val player = Player(
                    name = st[2],
                    nationality = st[5],
                    club = st[9],
                    overall = st[7].toInt(),
                    age = st[3].toInt()
                )

                players.add(player)

                // next line
                line = reader.readLine()
            }
        } catch (e: IOException) {
            Log.e(TAG, "$e")
        }

        playersAdapter = PlayersAdapter()
        playersAdapter.setData(players)
    }

    private fun setupRecyclerView() {
        val swipeController = SwipeController()
        val itemTouchHelper = ItemTouchHelper(swipeController)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)

        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
            adapter = playersAdapter
        }
    }
}