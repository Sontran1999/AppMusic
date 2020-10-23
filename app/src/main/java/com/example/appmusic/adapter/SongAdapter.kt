package com.example.appmusic.adapter


import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.appmusic.R
import com.example.appmusic.common.Utils
import com.example.appmusic.model.Song
import com.squareup.picasso.Picasso
import java.net.URL


class SongAdapter(
    var context: Context,
    val onClick: (Int, ArrayList<Song>) -> Unit
) :
    RecyclerView.Adapter<SongAdapter.ViewHoder>() {
    var mSong: ArrayList<Song> = ArrayList()
    var check: Int? = null
    class ViewHoder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var musicName: TextView = itemView.findViewById(R.id.tv_music_name)
        var musicsubTitle: TextView = itemView.findViewById(R.id.tv_music_subtitle)
        var constrain: ConstraintLayout = itemView.findViewById(R.id.constrain)
        var img_music: ImageView = itemView.findViewById(R.id.img_music)
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
        if(check == 0){
            holder.img_music.setImageBitmap(mSong[position].path?.let { Utils.songArt(it) })
        }
        else{
            Picasso.with(context).load(mSong[position].image).into(holder.img_music)
        }
    }

    override fun getItemCount(): Int {
        return mSong.size
    }

    fun setList(list: ArrayList<Song>, check: Int) {
        this.mSong = list
        this.check = check
        notifyDataSetChanged()
    }
}