package com.example.appmusic.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.os.Parcelable
import com.example.appmusic.model.Song

class MyService : Service(){
    var mBinder: IBinder =  MyBinder()
    lateinit var mediaPlayer: MediaPlayer
    var index = 0
    var listSong: ArrayList<Song> = arrayListOf()
    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
    }

    override fun onBind(intent: Intent?): IBinder? {

        var bundle = intent?.getBundleExtra("data")
        if (bundle != null) {
            this.index = bundle.getInt("index")
            this.listSong = bundle.getParcelableArrayList<Parcelable>("listSong") as ArrayList<Song>
        }
        mediaPlayer.reset()
        mediaPlayer.setDataSource(listSong[index].path)
        mediaPlayer.prepare()
        play()
        return mBinder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    fun pause() {
        mediaPlayer.pause()
    }

    fun play() {
        mediaPlayer.start()
    }

    fun isPlaying(): Boolean{
        return mediaPlayer.isPlaying
    }

    class MyBinder : Binder() {
        fun getService(): MyService {
            return MyService()
        }
    }
}
