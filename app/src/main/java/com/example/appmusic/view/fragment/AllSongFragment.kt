package com.example.appmusic.view.fragment

import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appmusic.R
import com.example.appmusic.adapter.SongAdapter
import com.example.appmusic.model.Song


class AllSongFragment(val onClick: (Int, ArrayList<Song>) -> Unit): Fragment() {
    val songsList: ArrayList<Song> = ArrayList()

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


//    companion object {
//        fun newInstance(): AllSongFragment = AllSongFragment()
//    }

    private fun showView(view: View) {
        var recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(view.context)
//        songsList.add(Song("1","1","1"))
        getMusic()
        var songAdapter: SongAdapter = SongAdapter(songsList, onClick)
        recyclerView.adapter = songAdapter
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