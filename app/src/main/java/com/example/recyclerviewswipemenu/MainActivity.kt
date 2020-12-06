package com.example.recyclerviewswipemenu

import android.graphics.Canvas
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recyclerviewswipemenu.databinding.ActivityMainBinding
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

/**
 * https://codeburst.io/android-swipe-menu-with-recyclerview-8f28a235ff28
 */

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
        val swipeController = SwipeController(object : SwipeControllerActions() {
            override fun onLeftClicked(position: Int) {
                super.onLeftClicked(position)
            }

            override fun onRightClicked(position: Int) {
                playersAdapter.removeItem(position)
            }
        })
        val itemTouchHelper = ItemTouchHelper(swipeController)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)

        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
            adapter = playersAdapter

            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
                    swipeController.onDraw(c)
                }
            })
        }
    }
}