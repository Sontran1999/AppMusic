package com.example.appmusic.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.appmusic.api.APIService
import com.example.appmusic.api.ApiUtils
import com.example.appmusic.model.Items
import com.example.appmusic.model.Json4Kotlin_Base
import com.example.appmusic.model.Song
import com.example.appmusic.model.Tracks
import retrofit2.Call
import retrofit2.Response


class ViewModel : ViewModel() {
    private val BITMAP_SCALE = 0.015f
    private val BLUR_RADIUS = 10f
    var mAPI: APIService? = ApiUtils().getAPIService()
    var music: MutableLiveData<ArrayList<Items>> = MutableLiveData()
    var searchSong: MutableLiveData<MutableList<Song>> = MutableLiveData()
    var timer: MutableLiveData<Boolean> = MutableLiveData()

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun blur(context: Context, image: Bitmap): Bitmap? {
        val width = Math.round(image.width * BITMAP_SCALE)
        val height = Math.round(image.height * BITMAP_SCALE)
        val inputBitmap = Bitmap.createScaledBitmap(image, width, height, false)
        val outputBitmap = Bitmap.createBitmap(inputBitmap)
        val rs = RenderScript.create(context)
        val theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
        val tmpIn = Allocation.createFromBitmap(rs, inputBitmap)
        val tmpOut = Allocation.createFromBitmap(rs, outputBitmap)
        theIntrinsic.setRadius(BLUR_RADIUS)
        theIntrinsic.setInput(tmpIn)
        theIntrinsic.forEach(tmpOut)
        tmpOut.copyTo(outputBitmap)
        return outputBitmap
    }

    fun getAll() {
        mAPI?.getAll()?.enqueue(object : retrofit2.Callback<Json4Kotlin_Base> {
            override fun onResponse(
                call: Call<Json4Kotlin_Base>,
                response: Response<Json4Kotlin_Base>
            ) {
                if (response.isSuccessful) {
                    Log.d(
                        "response", "employees loaded from API: ${
                            response.body()?.albums?.get(1)?.tracks?.items.let {
                                it?.get(0)
                            }
                        }"
                    )
                    var list: ArrayList<Items> = arrayListOf()
                    response.body()?.albums?.forEachIndexed { index, albums ->
                        albums.tracks.items.forEachIndexed { index, items ->
                            if (items.preview_url != null){
                                list.add(items)
                            }
                        }
                    }
                    music.postValue(list)
                } else {
                    Log.d("MainActivity", response.message() + response.code())
                    music.postValue(null)
                }
            }

            override fun onFailure(call: Call<Json4Kotlin_Base>, t: Throwable) {
                Log.d("MainActivity", "error loading from API")
                music.postValue(null)
            }

        })
    }

    fun search(query: String, listSong: ArrayList<Song>) {
        var listSearch: MutableList<Song> = mutableListOf()
        listSong.forEachIndexed { index, song ->
            var name = song.title
            var nameSinger =song.subTitle
            if (name != null) {
                if (name.toUpperCase().contains(query.toUpperCase())) {
                    listSearch.add(song)
                } else if (nameSinger != null) {
                    if (nameSinger.toUpperCase().contains(query.toUpperCase())) {
                        listSearch.add(song)
                    }
                }
            }
        }
        searchSong.postValue(listSearch)
    }

    fun checkTimer(check: Boolean){
        timer.postValue(check)
        Log.d("son","vo 2")
    }
}