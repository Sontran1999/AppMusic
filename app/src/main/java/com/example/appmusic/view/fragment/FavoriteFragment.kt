package com.example.appmusic.view.fragment

import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appmusic.R
import com.example.appmusic.adapter.SongAdapter
import com.example.appmusic.common.Utils
import com.example.appmusic.model.Song
import com.example.appmusic.view.activity.MainActivity
import com.example.appmusic.viewmodel.ViewModel
import kotlinx.android.synthetic.main.activity_main.*

class FavoriteFragment(
    var listSong: MutableList<Song>,
    var onClick: (Int, ArrayList<Song>) -> Unit,
    var onClickFavorite: (MutableList<Song>) -> Unit
) : Fragment() {
    var viewModel: ViewModel? = null
    var image: Bitmap? = null

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view: View = inflater.inflate(R.layout.fragment_tab, container, false)
        showView(view)
        return view
    }


    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun showView(view: View) {
        viewModel = ViewModel()
        var recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        var songAdapter = SongAdapter(view.context, onClick, onClickFavorite)
        recyclerView.adapter = songAdapter
        songAdapter.setList(listSong as ArrayList<Song>, 0)
        if (listSong.size != 0) {
            image = listSong[0].path?.let {
                Utils.songArt(it)?.let { viewModel!!.blur(view.context, it) }
            }
            recyclerView.background = BitmapDrawable(resources, image)
            toolbar?.background = BitmapDrawable(resources, image)
        } else {
            Toast.makeText(activity, "Don't have any favorite songs", Toast.LENGTH_SHORT).show()
        }

    }
}