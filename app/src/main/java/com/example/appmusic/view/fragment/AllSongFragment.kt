package com.example.appmusic.view.fragment

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toolbar
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appmusic.R
import com.example.appmusic.adapter.SongAdapter
import com.example.appmusic.common.Utils
import com.example.appmusic.model.Song
import com.example.appmusic.viewmodel.ViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_playing.*
import kotlinx.android.synthetic.main.fragment_tab.*


class AllSongFragment(val onClick: (Int, ArrayList<Song>) -> Unit) : Fragment() {
    val songsList: ArrayList<Song> = ArrayList()
    private val PERMISSION_REQUEST = 1
    var viewModel: ViewModel? = null
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view: View = inflater.inflate(R.layout.fragment_tab, container, false)
        showView(view)
        Toast.makeText(activity, "All Songs", Toast.LENGTH_SHORT).show()
        return view
    }


    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun showView(view: View) {
        viewModel = ViewModel()
        var recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        val arr = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

        if (ContextCompat.checkSelfPermission(
                view.context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(view.context as Activity, arr, PERMISSION_REQUEST)
        } else {
            getMusic()
        }
        var songAdapter: SongAdapter = SongAdapter(view.context, songsList, onClick)
        recyclerView.adapter = songAdapter
        var image =
            songsList[1].path?.let { Utils.songArt(it)?.let { viewModel!!.blur(view.context, it) } }
        recyclerView.background =  BitmapDrawable(resources,image)
    }


    fun getMusic() {
        val songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val songCursor: Cursor? = context?.contentResolver?.query(songUri, null, null, null, null)
        if (songCursor != null && songCursor.moveToFirst()) {
            val songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val songPath = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            do {
                songsList.add(
                    Song(
                        songCursor.getString(songTitle),
                        songCursor.getString(songArtist),
                        songCursor.getString(songPath)
                    )
                )
            } while (songCursor.moveToNext())
            songCursor.close()
        }
    }

}