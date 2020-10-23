package com.example.appmusic.view.activity

import android.content.*
import android.graphics.PorterDuff
import android.graphics.drawable.BitmapDrawable
import android.os.*
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.SeekBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.appmusic.R
import com.example.appmusic.model.Song
import com.example.appmusic.service.MyService
import com.example.appmusic.common.Utils
import com.example.appmusic.viewmodel.ViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_playing.*
import kotlinx.android.synthetic.main.activity_playing.albumArt
import java.text.SimpleDateFormat


class PlayingActivity : AppCompatActivity(), View.OnClickListener {
    var index = 0
    lateinit var listSong: ArrayList<Song>
    var animation: Animation? = null
    var viewModel: ViewModel? = null
    var rotation: Float = 0F
    var flag = 1

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playing)
        initMediaPlayer()
        btn_play.setOnClickListener(this)
        btn_next.setOnClickListener(this)
        btn_previous.setOnClickListener(this)
        btn_replay.setOnClickListener(this)
        btn_setting.setOnClickListener(this)
        setSupportActionBar(findViewById(R.id.toolbar2))
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle("")
        viewModel = ViewModel()
        if (listSong[index].image != null) {
            background.setBackgroundResource(R.color.gray_color)
        } else {
            var image =
                listSong[index].path?.let { Utils.songArt(it)?.let { viewModel!!.blur(this, it) } }
            background?.background = BitmapDrawable(resources, image)
        }
    }

    override fun onResume() {
        super.onResume()
        initBroadCast()
    }

    fun initBroadCast() {
        var intentFilter = IntentFilter()
        intentFilter.addAction(MyService.ACTION_FIRST_ACTION)
        intentFilter.addAction(MyService.ACTION_PLAY)
        intentFilter.addAction(MyService.ACTION_NEXT)
        intentFilter.addAction(MyService.ACTION_PREVIUOS)
        intentFilter.addAction(MyService.ACTION_MEDIA)
        intentFilter.addAction(MyService.AUTO)
        registerReceiver(musicReceiver, intentFilter)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun initMediaPlayer() {
        var bundle = intent.getBundleExtra("data")
        if (bundle != null) {
            this.index = bundle.getInt("index")
            this.listSong = bundle.getParcelableArrayList<Parcelable>("listSong") as ArrayList<Song>
            this.flag = bundle.getInt("flag")
            if (bundle.getBoolean("check")) {
                btn_play.setImageResource(R.drawable.pause_icon)
            } else {
                btn_play.setImageResource(R.drawable.play_icon)
            }

        }
        setMusic(listSong[index])
        setTotalTime()
    }

    fun setTotalTime() {
        tv_total_time.setText(getTimeFormatted(MainActivity.mService.getSumTime()))//sum time
        conTrol()
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun setMusic(song: Song) {
        try {
            rotation = 0F
            tvTitle.setText(song.title)
            tvSubTitle.setText(song.subTitle)
            when {
                flag == 1 -> {
                    btn_setting.setImageResource(R.drawable.ic_repeat)
                }
                flag == 2 -> {
                    btn_setting.setImageResource(R.drawable.ic_repeat_once)
                }
                else -> {
                    btn_setting.setImageResource(R.drawable.ic_shuffle)
                }
            }
            if (song.image != null) {
                Picasso.with(this).load(song.image).into(albumArt)
            } else {
                albumArt.setImageBitmap(song.path?.let { Utils.songArt(it) })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun conTrol() {
        sb_controller.max =
            MainActivity.mService.getSumTime()// gan tong seekbar bang tong thoi gian
        playCycle()
        sb_controller.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if (p2) {
                    MainActivity.mService.mediaPlayer.seekTo(p1)// di chuyen nut den doan da chon
                    tv_current_time.text = getTimeFormatted(p1)
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                MainActivity.mService.mediaPlayer.seekTo(sb_controller.progress)
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun setBackGroud() {
        setMusic(listSong[index])
        if (listSong[index].image != null) {
            background.setBackgroundResource(R.color.gray_color)
        } else {
            var image =
                listSong[index].path?.let {
                    Utils.songArt(it)?.let { viewModel!!.blur(this, it) }
                }
            background?.background = BitmapDrawable(resources, image)
        }
        MyService.onPreparedListener = {
            setTotalTime()
        }
    }

    fun play() {
        if (MainActivity.mBound) {
            if (MainActivity.mService.isPlaying()) {
                MainActivity.mService.pause()
                btn_play.setImageResource(R.drawable.play_icon)
            } else {
                intent.putExtra("run", 1)
                sendBroadcast(intent)
                MainActivity.mService.play()
                btn_play.setImageResource(R.drawable.pause_icon)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun next() {
        btn_play.setImageResource(R.drawable.play_icon)
        if (MainActivity.mService.next(index, listSong)) {
            index++
            setBackGroud()
            btn_play.setImageResource(R.drawable.pause_icon)
        } else {
            Toast.makeText(this, "PlayList Ended", Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun previous() {
        btn_play.setImageResource(R.drawable.play_icon)
        if (MainActivity.mService.previous(index, listSong)) {
            index--
            setBackGroud()
            btn_play.setImageResource(R.drawable.pause_icon)
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
                tv_current_time.setText(getTimeFormatted(MainActivity.mService.getCurrentTime()))// set thời gian hiện tại bằng vs thời gian nhạc đang phát
                sb_controller.progress =
                    MainActivity.mService.getCurrentTime()// progress dịch con trỏ đến với thời gian hiện tại nhạc phát
                handler.postDelayed(this, 50)
                if (MainActivity.mService.isPlaying()) {
                    rotation += 0.25F
                    if (rotation == 360F) {
                        rotation = 0F
                    }
                }
                albumArt.rotation = rotation
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
            when (intent?.action) {
                MyService.ACTION_NEXT -> {
                    next()
                }
                MyService.ACTION_PREVIUOS -> {
                    previous()
                }
                MyService.ACTION_PLAY -> {
                    if (MainActivity.mService.isPlaying()) {
                        btn_play.setImageResource(R.drawable.pause_icon)
                    } else {
                        btn_play.setImageResource(R.drawable.play_icon)
                    }
                }
                MyService.AUTO -> {
                    index = intent.getIntExtra("index", 0)
                    setBackGroud()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btn_play -> {
                play()
            }
            R.id.btn_next -> {
                next()
                var intent = Intent()
                intent.action = MyService.NEXT
                sendBroadcast(intent)
            }
            R.id.btn_previous -> {
                var intent = Intent()
                intent.action = MyService.PREVIUOS
                sendBroadcast(intent)
                previous()
            }
            R.id.btn_replay -> {
                if (MainActivity.mService.isPlaying()) {
                    MainActivity.mService.stop()
                }
                MainActivity.mService.runMusic(index, listSong)
            }
            R.id.btn_setting -> {
                flag++
                when {
                    flag == 1 -> {
                        btn_setting.setImageResource(R.drawable.ic_repeat)
                    }
                    flag == 2 -> {
                        btn_setting.setImageResource(R.drawable.ic_repeat_once)

                    }
                    else -> {
                        btn_setting.setImageResource(R.drawable.ic_shuffle)
                        flag = 0
                    }
                }
                MainActivity.mService.checkFlag(flag)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(musicReceiver)
    }

}