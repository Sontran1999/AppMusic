package com.example.appmusic.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.os.Parcelable
import android.util.Log
import android.widget.Toast
import com.example.appmusic.R
import com.example.appmusic.model.Song
import com.example.appmusic.view.activity.MainActivity
import com.example.appmusic.view.activity.PlayingActivity
import kotlinx.android.synthetic.main.fragment_playback_controls.view.*

class MyService : Service(), MediaPlayer.OnPreparedListener{
    companion object {
        var onPreparedListener: (() -> Unit)? = null
        val CHANNEL_ID = "channel1"
        val ACTION_PREVIUOS = "actionprevious"
        val ACTION_PLAY = "actionplay"
        val ACTION_NEXT = "actionnext"
        val ACTION_FIRST_ACTION = "action"
        val ACTION_MEDIA = "actionmedia"
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
        return START_REDELIVER_INTENT
    }

    fun runMusic(index: Int){
        try {
            notification = CreateNotification(this, listSong[index],this@MyService)
            startForeground(1, notification?.builder())
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
            }
            mediaPlayer.reset()
            mediaPlayer.setDataSource(listSong[index].path)
            mediaPlayer.setOnPreparedListener(this)
            mediaPlayer.prepareAsync()

        }catch (e: Exception) {
            e.printStackTrace()
        }
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
        Log.d("Binh", "Current: $index ${listSong.size}")

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

    inner class MyBinder : Binder() {
        fun getService(): MyService {
            return this@MyService
        }
    }

    override fun onPrepared(p0: MediaPlayer?) {
        p0?.start()
        onPreparedListener?.let { it() }
        if(isPlaying()){
            Thread{
                var intent = Intent()
                intent.action = ACTION_MEDIA
                intent.putExtra("name","name")
                sendBroadcast(intent)
            }.start()
        }
    }

}
