package com.example.appmusic.viewmodel

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
import com.example.app_retrofit.data.remote.APIService
import com.example.app_retrofit.data.remote.ApiUtils
import com.example.appmusic.Episodes
import com.example.appmusic.FileJson
import retrofit2.Call
import retrofit2.Response


class ViewModel : ViewModel() {
    private val BITMAP_SCALE = 0.01f
    private val BLUR_RADIUS = 25f
    var mAPI: APIService? = ApiUtils().getAPIService()
    var music: MutableLiveData<List<Episodes>> = MutableLiveData()


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

    fun getAll(){
        mAPI?.getAll()?.enqueue(object : retrofit2.Callback<FileJson>{
            override fun onResponse(
                call: Call<FileJson>,
                response: Response<FileJson>
            ) {
                if (response.isSuccessful) {
                    Log.d("response", "employees loaded from API: ${response.body()?.episodes?.let {
                        it[0]
                    }}")
                    music?.postValue(response.body()?.episodes)
                } else {
                    Log.d("MainActivity", response.message() + response.code())
                    music?.postValue(null)
                }
            }

            override fun onFailure(call: Call<FileJson>, t: Throwable) {
                Log.d("MainActivity", "error loading from API")
                music.postValue(null)
            }

        })
    }

}