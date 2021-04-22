package com.example.appmusic.adapter


import android.content.Context
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.appmusic.R
import com.example.appmusic.common.Utils
import com.example.appmusic.model.Song
import com.squareup.picasso.Picasso


class SongAdapter(
    var context: Context,
    val onClick: (Int, ArrayList<Song>) -> Unit,
    var onClickFavorite: (MutableList<Song>) -> Unit
) :
    RecyclerView.Adapter<SongAdapter.ViewHoder>() {
    var mSong: ArrayList<Song> = ArrayList()
    var check: Int? = null
    var listSongFavorite: MutableList<Song> = mutableListOf()
    class ViewHoder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var musicName: TextView = itemView.findViewById(R.id.tv_music_name)
        var musicSubTitle: TextView = itemView.findViewById(R.id.tv_music_subtitle)
        var constrain: ConstraintLayout = itemView.findViewById(R.id.constrain)
        var imgMusic: ImageView = itemView.findViewById(R.id.img_music)
        var popupMenu: ImageView = itemView.findViewById(R.id.popupMenuBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHoder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view: View = layoutInflater.inflate(R.layout.playlist_iteams, parent, false)
        return ViewHoder(view)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: ViewHoder, position: Int) {
        holder.musicName.text = mSong[position].title
        holder.musicSubTitle.text = mSong[position].subTitle
        holder.constrain.setOnClickListener {
            onClick(position, mSong)
        }
        if(check == 0){
            holder.imgMusic.setImageBitmap(mSong[position].path?.let { Utils.songArt(it) })
        }
        else{
            Picasso.with(context).load(mSong[position].image).into(holder.imgMusic)
        }
        holder.popupMenu.setOnClickListener {
            var popupMenu = PopupMenu(context, holder.popupMenu)
            popupMenu.menuInflater.inflate(R.menu.menu_option, popupMenu.menu);
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.favorite ->{
                        Toast.makeText(context,"successfully added", Toast.LENGTH_SHORT).show()
//                        listSongFavorite.forEachIndexed { index, song ->
//                            if(song.title == mSong[position].title){
//                                Toast.makeText(context,"delete favorite songs successfully", Toast.LENGTH_SHORT).show()
//                                listSongFavorite.removeAt(index)
//                            }else{
//
//                            }
//                        }
                        listSongFavorite.add(mSong[position])
                        Toast.makeText(context,listSongFavorite.size.toString(), Toast.LENGTH_SHORT).show()
                        onClickFavorite(listSongFavorite)
                        true
                    }
                    R.id.share ->
                        true
                    else -> false
                }
            })
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                popupMenu.gravity = Gravity.END
            }
            popupMenu.setForceShowIcon(true)
            popupMenu.show()
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