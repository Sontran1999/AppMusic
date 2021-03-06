package com.example.appmusic.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.appmusic.R
import com.example.appmusic.model.Song

class SongAdapter(var mSong: ArrayList<Song>, val onClick: (Int, ArrayList<Song>) -> Unit) :
    RecyclerView.Adapter<SongAdapter.ViewHoder>() {


    class ViewHoder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var musicName: TextView = itemView.findViewById(R.id.tv_music_name)
        var musicsubTitle: TextView = itemView.findViewById(R.id.tv_music_subtitle)
        var constrain: ConstraintLayout = itemView.findViewById(R.id.constrain)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHoder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view: View = layoutInflater.inflate(R.layout.playlist_iteams, parent, false)
        var viewHoder: ViewHoder = ViewHoder(view)
        return viewHoder
    }

    override fun onBindViewHolder(holder: ViewHoder, position: Int) {
        holder.musicName.text = mSong[position].title
        holder.musicsubTitle.text = mSong[position].subTitle
        holder.constrain.setOnClickListener {
            onClick(position, mSong)
        }
    }

    override fun getItemCount(): Int {
        return mSong.size
    }
}