package com.example.appmusic.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.os.Parcelable
import com.example.appmusic.model.Song
import com.example.appmusic.view.activity.PlayingActivity

class MyService: Service() {
   private val mBinder: IBinder = MyBinder()
    var index = 0
    lateinit var listSong: ArrayList<Song>
    override fun onCreate() {
        super.onCreate()
    }
    override fun onBind(intent: Intent?): IBinder? {
        var bundle = intent?.getBundleExtra("data")
        if (bundle != null) {
            this.index = bundle.getInt("index")
            this.listSong = bundle.getParcelableArrayList<Parcelable>("listSong") as ArrayList<Song>
        }
        if (PlayingActivity.mediaPlayer.isPlaying) {
            PlayingActivity.mediaPlayer.stop()
        }
        PlayingActivity.mediaPlayer.reset()
        PlayingActivity.mediaPlayer.setDataSource(listSong[index].path)
        PlayingActivity.mediaPlayer.prepare()
        PlayingActivity.mediaPlayer.start()
        return mBinder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    class MyBinder : Binder() {
        fun getService(): MyService? {
            return MyService()
        }
    }

}