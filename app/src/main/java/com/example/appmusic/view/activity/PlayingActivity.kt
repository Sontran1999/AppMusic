package com.example.appmusic.view.activity

import android.content.*
import android.graphics.drawable.BitmapDrawable
import android.media.MediaPlayer
import android.os.*
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.SeekBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.appmusic.R
import com.example.appmusic.model.Song
import com.example.appmusic.service.CreateNotification
import com.example.appmusic.service.MyService
import com.example.appmusic.viewmodel.Utils
import com.example.appmusic.viewmodel.ViewModel
import kotlinx.android.synthetic.main.activity_playing.*
import java.text.SimpleDateFormat


class PlayingActivity : AppCompatActivity(), View.OnClickListener {
    var index = 0
    lateinit var listSong: ArrayList<Song>
    var animation: Animation? = null
    var viewModel: ViewModel? = null

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

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playing)
        initBroadCast()
        initMediaPlayer()
        btn_play.setOnClickListener(this)
        btn_next.setOnClickListener(this)
        btn_previous.setOnClickListener(this)
        btn_replay.setOnClickListener(this)
        animation = AnimationUtils.loadAnimation(this, R.anim.rotate)
        albumArt.startAnimation(animation)
        viewModel = ViewModel()
        var image =
            listSong[index].path?.let { Utils.songArt(it)?.let { viewModel!!.blur(this, it) } }
        background.background = BitmapDrawable(resources, image)

    }

    fun initBroadCast() {
        var intentFilter = IntentFilter()
        intentFilter.addAction(MyService.ACTION_FIRST_ACTION)
        intentFilter.addAction(MyService.ACTION_PLAY)
        intentFilter.addAction(MyService.ACTION_NEXT)
        intentFilter.addAction(MyService.ACTION_PREVIUOS)
        intentFilter.addAction(MyService.ACTION_MEDIA)
        registerReceiver(musicReceiver, intentFilter)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun initMediaPlayer() {
        var bundle = intent.getBundleExtra("data")
        if (bundle != null) {
            this.index = bundle.getInt("index")
            this.listSong = bundle.getParcelableArrayList<Parcelable>("listSong") as ArrayList<Song>
        }
        setMusic(listSong[index])
    }


    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun setMusic(song: Song) {
        MyService.onPreparedListener = {
            try {
                tvTitle.setText(song.title)
                tvSubTitle.setText(song.subTitle)
                tv_total_time.setText(getTimeFormatted(mService.getSumTime()))//sum time
                albumArt.setImageBitmap(song.path?.let { Utils.songArt(it) })
                conTrol()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            mService.mediaPlayer.setOnCompletionListener(MediaPlayer.OnCompletionListener {
               next()
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
                    tv_current_time.text = getTimeFormatted(p1)
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                mService.mediaPlayer.seekTo(sb_controller.progress)
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btn_play -> {
                play()
            }
            R.id.btn_next -> {
                next()
            }
            R.id.btn_previous -> {
                previous()
            }
            R.id.btn_replay -> {
                if (mService.isPlaying()) {
                    mService.stop()
                }
                mService.runMusic(index)
            }
        }
    }

    fun play(){
        if (mBound) {
            if (mService.isPlaying()) {
                mService.pause()
                btn_play.setImageResource(R.drawable.play_icon)
                albumArt.clearAnimation()
            } else {
                mService.play()
                btn_play.setImageResource(R.drawable.pause_icon)
                albumArt.startAnimation(animation)
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun next() {
        btn_play.setImageResource(R.drawable.play_icon)
        if (mService.next()) {
            setMusic(listSong[index + 1])
            var image = listSong[index + 1].path?.let {
                Utils.songArt(it)?.let { viewModel!!.blur(this, it) }
            }
            background.background = BitmapDrawable(resources, image)
            index++
            albumArt.clearAnimation()
            albumArt.startAnimation(animation)
        } else {
            Toast.makeText(this, "PlayList Ended", Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun previous() {
        if (mService.previous()) {
            setMusic(listSong[index - 1])
            var image = listSong[index - 1].path?.let {
                Utils.songArt(it)?.let { viewModel!!.blur(this, it) }
            }
            background.background = BitmapDrawable(resources, image)
            index--
            albumArt.clearAnimation()
            albumArt.startAnimation(animation)
        } else {
            Toast.makeText(this, "PlayList Ended", Toast.LENGTH_SHORT).show()
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    var musicReceiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d("activity", intent?.action.toString())
            when (intent?.action) {
                MyService.ACTION_NEXT -> {
                    next()
                }
                MyService.ACTION_PREVIUOS -> {
                    previous()
                }
                MyService.ACTION_PLAY ->{
                    play()
                }
                else ->{
                    Toast.makeText(context,"222",Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(musicReceiver)
    }

}