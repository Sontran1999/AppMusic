package com.example.appmusic.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.os.Parcelable
import android.widget.Toast
import com.example.appmusic.model.Song

class MyService : Service(), MediaPlayer.OnPreparedListener{
    companion object {
        var onPreparedListener: (() -> Unit)? = null
    }

    var mBinder: IBinder = MyBinder()
    lateinit var mediaPlayer: MediaPlayer
    var index = 0
    var listSong: ArrayList<Song> = arrayListOf()
    var notification: CreateNotification? = null
    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        var bundle = intent?.getBundleExtra("data")
        if (bundle != null) {
            this.index = bundle.getInt("index")
            this.listSong = bundle.getParcelableArrayList<Parcelable>("listSong") as ArrayList<Song>
        }
        runMusic(index)
        return super.onStartCommand(intent, flags, startId)
    }

    fun runMusic(index: Int){
        notification = CreateNotification(this, listSong[index])
        startForeground(1, notification?.builder())
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
        mediaPlayer.reset()
        mediaPlayer.setDataSource(listSong[index].path)
        mediaPlayer.setOnPreparedListener(this)
        mediaPlayer.prepareAsync()

    }

    override fun onBind(intent: Intent?): IBinder? {

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

    fun stop(){
        mediaPlayer.stop()
    }
    fun play() {
        mediaPlayer.start()
    }

    fun getSumTime(): Int {
        return mediaPlayer.duration
    }

    fun getCurrentTime(): Int {
        return mediaPlayer.currentPosition
    }

    fun isPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }

    fun next():Boolean{
        if (index + 1 < listSong.size) {
            runMusic(index + 1)
            index++
            return true
        }
        else{
            return false
        }
    }

    fun previous():Boolean{
        if (index - 1 >= 0) {
           runMusic(index - 1)
            index--
            return true
        } else {
            return false
        }
    }
    fun completion():Boolean{
        mediaPlayer.setOnCompletionListener {
            return@setOnCompletionListener
        }
        return false
    }

    inner class MyBinder : Binder() {
        fun getService(): MyService {
            return this@MyService
        }
    }

    override fun onPrepared(p0: MediaPlayer?) {
        p0?.start()
        onPreparedListener?.let { it() }
    }

}
