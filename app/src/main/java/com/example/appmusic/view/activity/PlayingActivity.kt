package com.example.appmusic.view.activity

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.example.appmusic.R
import com.example.appmusic.model.Song
import com.example.appmusic.service.MyService
import kotlinx.android.synthetic.main.activity_playing.*
import java.text.SimpleDateFormat


class PlayingActivity : AppCompatActivity(), View.OnClickListener {
    var index = 0
    lateinit var listSong: ArrayList<Song>

    companion object {
        var mBound: Boolean = false
        var mService: MyService = MyService()
        var serviceConnection: ServiceConnection = object : ServiceConnection {
            override fun onServiceConnected(className: ComponentName, iBinder: IBinder) {
                val binder: MyService.MyBinder = iBinder as MyService.MyBinder
                mService = binder.getService()
                mBound = true
            }

            override fun onServiceDisconnected(arg0: ComponentName) {

            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playing)
        initMediaPlayer()
        btn_play.setOnClickListener(this)
        btn_next.setOnClickListener(this)
        btn_previous.setOnClickListener(this)
        btn_replay.setOnClickListener(this)
    }


    fun initMediaPlayer() {
        var bundle = intent.getBundleExtra("data")
        if (bundle != null) {
            this.index = bundle.getInt("index")
            this.listSong = bundle.getParcelableArrayList<Parcelable>("listSong") as ArrayList<Song>
        }
        setMusic(listSong[index])
    }


    fun setMusic(song: Song) {
        MyService.onPreparedListener = {
            try {
                tvTitle.setText(song.title)
                tvSubTitle.setText(song.subTitle)
                tv_total_time.setText(getTimeFormatted(mService.getSumTime()))//sum time
                conTrol()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if(mService.completion()){
                if (index + 1 < listSong.size) {
                    setMusic(listSong[index + 1])
                    index++
                } else {
                    Toast.makeText(this, "PlayList Ended", Toast.LENGTH_SHORT).show()
                }
            }
            mService.mediaPlayer.setOnCompletionListener(MediaPlayer.OnCompletionListener {
                btn_play.setImageResource(R.drawable.play_icon)
                if (index + 1 < listSong.size) {
                    setMusic(listSong[index + 1])
                    mService.runMusic(index + 1)
                    index++
                } else {
                    Toast.makeText(this, "PlayList Ended", Toast.LENGTH_SHORT).show()
                }
            }
            )
        }
    }

    fun conTrol() {
        btn_play.setImageResource(R.drawable.pause_icon)
        sb_controller.max = mService.getSumTime()// gan tong seekbar bang tong thoi gian
        playCycle()
        sb_controller.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if (p2) {
                    mService.mediaPlayer.seekTo(p1)// di chuyen nut den doan da chon
                    tv_current_time.setText(getTimeFormatted(p1))
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                mService.mediaPlayer.seekTo(sb_controller.progress)
            }
        })
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btn_play -> {
                if (mBound) {
                    if (mService.isPlaying()) {
                        mService.pause()
                        btn_play.setImageResource(R.drawable.play_icon)
                    } else {
                        mService.play()
                        btn_play.setImageResource(R.drawable.pause_icon)
//                    playCycle()
                    }
                }

            }
            R.id.btn_next -> {
                btn_play.setImageResource(R.drawable.play_icon)
                if (mService.next()) {
                    setMusic(listSong[index + 1])
                    index++
                } else {
                    Toast.makeText(this, "PlayList Ended", Toast.LENGTH_SHORT).show()
                }

            }
            R.id.btn_previous -> {
                if (mService.previous()) {
                    setMusic(listSong[index - 1])
                    index--
                } else {
                    Toast.makeText(this, "PlayList Ended", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.btn_replay -> {
                if (mService.isPlaying()) {
                    mService.stop()
                }
                mService.runMusic(index)
            }
        }
    }

    private fun getTimeFormatted(milliSeconds: Int): String? {
        var time = SimpleDateFormat("mm:ss")
        return time.format(milliSeconds)
    }

    private fun playCycle() {
        var handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                tv_current_time.setText(getTimeFormatted(mService.getCurrentTime()))// set thời gian hiện tại bằng vs thời gian nhạc đang phát
                sb_controller.progress =
                    mService.getCurrentTime()// progress dịch con trỏ đến với thời gian hiện tại nhạc phát
                handler.postDelayed(this, 500)
            }
        }, 100)

    }

}