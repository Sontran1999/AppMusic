package com.example.appmusic.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.VectorDrawable
import android.media.MediaMetadataRetriever
import com.example.appmusic.R
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.lang.Exception


object Utils {
    fun songArt(path: String): Bitmap? {
        val retriever = MediaMetadataRetriever()
        val inputStream: InputStream
        var bitmap: Bitmap? = null
        try {
            retriever.setDataSource(path)
            if (retriever.embeddedPicture != null) {
                inputStream = ByteArrayInputStream(retriever.embeddedPicture)
                bitmap = BitmapFactory.decodeStream(inputStream)
                retriever.release()
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
        return bitmap
    }
}
