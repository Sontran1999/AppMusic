package com.example.appmusic.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.os.Parcelable
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.appmusic.model.Song
import com.example.appmusic.view.activity.MainActivity
import com.example.appmusic.view.activity.PlayingActivity


class MyService : Service(), MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {
    companion object {
        var onPreparedListener: (() -> Unit)? = null
        val CHANNEL_ID = "channel1"
        val ACTION_PREVIUOS = "actionprevious"
        val ACTION_PLAY = "actionplay"
        val ACTION_NEXT = "actionnext"
        val ACTION_FIRST_ACTION = "action"
        val ACTION_MEDIA = "actionmedia"
        val PREVIUOS = "privious"
        val NEXT = "next"
        val AUTO = "auto"
        val PLAY = "play"
        val ACTION_FLAG = "flag"
    }

    var mBinder: IBinder = MyBinder()
    lateinit var mediaPlayer: MediaPlayer
    var index = 0
    var listSong: ArrayList<Song> = arrayListOf()
    var notification: CreateNotification? = null
    var isPreparing = false
    var flag = 1

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
        mediaPlayer.setOnPreparedListener(this)
        mediaPlayer.setOnCompletionListener(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        var intent = Intent()
        if (listSong.size != 0) {
            intent.action = ACTION_MEDIA
            intent.putExtra("song", listSong[index])
            intent.putExtra("index", index)
            sendBroadcast(intent)
            if (isPlaying()) {
                intent.action = PLAY
                intent.putExtra("run", 0)
                sendBroadcast(intent)
            } else {
                intent.action = PLAY
                intent.putExtra("run", 1)
                sendBroadcast(intent)
            }
            intent.action = ACTION_FLAG
            intent.putExtra("flag",flag)
            sendBroadcast(intent)
        }

        return START_REDELIVER_INTENT
    }

    fun runMusic(index: Int, listSong: ArrayList<Song>) {
        try {
            this.index = index
            this.listSong = listSong
            notification = CreateNotification(this, listSong[index], this@MyService)
            startForeground(1, notification?.builder())
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
            }
            mediaPlayer.reset()
            mediaPlayer.setDataSource(listSong[index].path)
            mediaPlayer.prepareAsync()
            isPreparing = true

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return mBinder
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    fun pause() {
        mediaPlayer.pause()
    }

    fun stop() {
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

    fun next(index: Int, listSong: ArrayList<Song>): Boolean {
        if (index + 1 < listSong.size) {
            runMusic(index + 1, listSong)
            return true
        } else {
            return false
        }
    }

    fun previous(index: Int, listSong: ArrayList<Song>): Boolean {
        if (index - 1 >= 0) {
            runMusic(index - 1, listSong)
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
        isPreparing = false
        onPreparedListener?.let { it() }
    }

    fun checkFlag(flags: Int) {
        this.flag = flags
    }

    override fun onCompletion(mp: MediaPlayer?) {
        if (isPreparing) return
        var intent = Intent()
        intent.action = AUTO
        when {
            flag == 1 -> {
                next(index, listSong)
            }
            flag == 2 -> {
                runMusic(index, listSong)
            }
            else -> {
                index = (0 until listSong.size - 1).random()
                runMusic(index, listSong)
            }
        }
        intent.putExtra("index", index)
        sendBroadcast(intent)
    }


}
