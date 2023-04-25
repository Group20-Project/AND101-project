package com.example.music_search

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class SpotAdapter(private val songList: MutableList<MutableMap<String,String>>) : RecyclerView.Adapter<SpotAdapter.ViewHolder>(){
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val songImage: ImageView
        val nameTextView = itemView.findViewById<TextView>(R.id.track_name)
        val artistTextView = itemView.findViewById<TextView>(R.id.artist)

        init {
            songImage = view.findViewById(R.id.recyclerImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.song_item, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount() = songList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // open song on image click
        holder.songImage.setOnClickListener {
            holder.songImage.context.startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse(songList[position].getValue("trackURL"))))
        }

        holder.songImage.setOnLongClickListener{
            holder.songImage.context.startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse(songList[position].getValue("imageURL"))))
            return@setOnLongClickListener true
        }

        Log.d("Spotify Adapter position", songList[position].toString())
        holder.nameTextView.text = songList[position].getValue("trackName")
        holder.artistTextView.text = songList[position].getValue("artist")

        Glide.with(holder.itemView)
            .load(songList[position].getValue("imageURL"))
            .centerCrop()
            .into(holder.songImage)
    }
}