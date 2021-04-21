package com.example.appmusic.view.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appmusic.R
import com.example.appmusic.adapter.SongAdapter
import com.example.appmusic.common.Utils
import com.example.appmusic.model.Items
import com.example.appmusic.model.Song
import com.example.appmusic.viewmodel.ViewModel
import kotlinx.android.synthetic.main.activity_main.*


class AllSongFragment(
    var tyle: Int,
    var onClick: (Int, ArrayList<Song>) -> Unit,
    var onClickFavorite: (MutableList<Song>) -> Unit
) : Fragment() {
    var songsList: ArrayList<Song> = ArrayList()
    private val PERMISSION_REQUEST = 1
    var viewModel: ViewModel = ViewModel()
    private var mListEpisodes: ArrayList<Items> = ArrayList()
    var songAdapter: SongAdapter? = null
    var image: Bitmap? = null

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        var view: View = inflater.inflate(R.layout.fragment_tab, container, false)
        showView(view, tyle)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }


    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun showView(view: View, tyle: Int) {
        var recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        songAdapter = SongAdapter(view.context, onClick, onClickFavorite)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        recyclerView.adapter = songAdapter
        val arr = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

        if (ContextCompat.checkSelfPermission(
                view.context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(view.context as Activity, arr, PERMISSION_REQUEST)
        } else {
            if (tyle == 1) {
                registerSearch()
                Toast.makeText(activity, "Local", Toast.LENGTH_SHORT).show()
                getMusic()
                songAdapter!!.setList(songsList, 0)
                image = songsList[0].path?.let {
                        Utils.songArt(it)?.let { viewModel!!.blur(view.context, it) }
                    }
                recyclerView.background = BitmapDrawable(resources, image)
                toolbar?.background = BitmapDrawable(resources, image)
                constrain?.background = BitmapDrawable(resources, image)
            } else {
                Toast.makeText(activity, "Online", Toast.LENGTH_SHORT).show()
                loadAPI()
                recyclerView.setBackgroundResource(R.color.gray_color)
            }
        }


    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @SuppressLint("ResourceAsColor")
    fun loadAPI() {
        viewModel.music.observe(this, {
            if (it != null) {
                mListEpisodes = it as ArrayList<Items>
                Toast.makeText(view?.context, "loading", Toast.LENGTH_LONG).show()
                mListEpisodes.forEachIndexed { index, episodes ->
                    songsList.add(
                        Song(
                            mListEpisodes[index].name,
                            mListEpisodes[index].type,
                            mListEpisodes[index].preview_url,
                        )
                    )
                }
                songAdapter?.setList(songsList, 1)
            } else {
                Toast.makeText(view?.context, "error loading from API", Toast.LENGTH_LONG).show()
            }
        })
        viewModel?.getAll()

    }

    private fun getMusic() {
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

    private fun registerSearch() {
        viewModel.searchSong.observe(this) {
            Log.d("aaa",it.size.toString())
            if (it.size != 0) {
                songAdapter?.setList(it as ArrayList<Song>,0)
            } else {
                view?.context?.let { it1 ->
                    AlertDialog.Builder(it1).setTitle("No information")
                        .setMessage("The information you are looking for is not available")
                        .setNegativeButton(
                            "OK ",
                            DialogInterface.OnClickListener { dialogInterface, i ->
                                dialogInterface.cancel()
                            }).show()
                }
            }
        }
    }

}