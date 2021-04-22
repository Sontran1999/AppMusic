package com.example.appmusic.view.activity

import android.app.SearchManager
import android.content.*
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.*
import android.util.Log
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.SearchView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appmusic.R
import com.example.appmusic.adapter.SongAdapter
import com.example.appmusic.model.Song
import com.example.appmusic.service.MyService
import com.example.appmusic.view.fragment.AllSongFragment
import com.example.appmusic.view.fragment.FavoriteFragment
import com.example.appmusic.common.Utils
import com.example.appmusic.viewmodel.ViewModel
import com.google.android.material.navigation.NavigationView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.albumArt
import kotlinx.android.synthetic.main.activity_main.btnNext
import kotlinx.android.synthetic.main.activity_main.btnPrevious
import kotlinx.android.synthetic.main.fragment_tab.*


class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    lateinit var navigationView: NavigationView
    var index = 0
    var listSong: ArrayList<Song> = arrayListOf()
    var check = false
    var viewModel: ViewModel? = null
    var flag = 1
    var listFavorite: MutableList<Song> = mutableListOf()
    var image: Bitmap? = null

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
        viewModel = ViewModel()
        setContentView(R.layout.activity_main)
        loadFragment(AllSongFragment(1, onItemClick, onClickFavorite))
        btnPlay.setOnClickListener(this)
        btnNext.setOnClickListener(this)
        btnPrevious.setOnClickListener(this)
        layoutMusic.setOnClickListener(this)
        setDrawer()
        setFocus(false)
    }

    override fun onResume() {
        super.onResume()
        initMusic()
    }

    fun setFocus(boolean: Boolean) {
        if (boolean) {
            layoutMusic.visibility = View.VISIBLE
        } else {
            layoutMusic.visibility = View.GONE
        }
    }

    fun initMusic() {
        var intentFilter = IntentFilter()
        intentFilter.addAction(MyService.ACTION_MEDIA)
        intentFilter.addAction(MyService.ACTION_NEXT)
        intentFilter.addAction(MyService.ACTION_PLAY)
        intentFilter.addAction(MyService.PREVIUOS)
        intentFilter.addAction(MyService.ACTION_PREVIUOS)
        intentFilter.addAction(MyService.NEXT)
        intentFilter.addAction(MyService.AUTO)
        intentFilter.addAction(MyService.PLAY)
        intentFilter.addAction(MyService.ACTION_FLAG)
        registerReceiver(receiver, intentFilter)
        var intentService = Intent(this, MyService::class.java)
        ContextCompat.startForegroundService(this, intentService)
        bindService(intentService, serviceConnection, BIND_AUTO_CREATE)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun setMusicPlayer(song: Song) {
        tv_Title.text = song.title
        tv_Artist.text = song.subTitle
        btnPlay.setImageResource(R.drawable.pause_icon)
        check = true
        if (song.image != null) {
            layoutMusic.setBackgroundResource(R.color.gray_color)
            Picasso.with(this).load(song.image).into(albumArt)
        } else {
            albumArt.setImageBitmap(song.path?.let { Utils.songArt(it) })
            image = song.path?.let { Utils.songArt(it)?.let { viewModel?.blur(this, it) } }
            recyclerView.background = BitmapDrawable(resources, image)
            layoutMusic.background = BitmapDrawable(resources, image)
            toolbar.background = BitmapDrawable(resources, image)
            constrain.background = BitmapDrawable(resources, image)
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private val onItemClick: (Int, ArrayList<Song>) -> Unit = { index, listSong ->
        this.index = index
        this.listSong = listSong
        mService.runMusic(index, listSong)
        setFocus(true)
        check = true
        btnPlay.setImageResource(R.drawable.pause_icon)
        setMusicPlayer(listSong[index])
    }

    private val onClickFavorite: (MutableList<Song>) -> Unit = {
        this.listFavorite = it
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun setDrawer() {
        val typedValue = TypedValue()
        theme.resolveAttribute(R.attr.colorStatusBar, typedValue, true)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            toolbar?.setBackgroundColor(typedValue.data)
            constrain.setBackgroundColor(typedValue.data)
        }
        drawer = findViewById(R.id.drawer_layout)
        setSupportActionBar(findViewById(R.id.toolbar))
        toggle = ActionBarDrawerToggle(
            this,
            drawer,
            toolbar,
            R.string.nav_drawer_open,
            R.string.nav_drawer_close
        )
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        drawer.addDrawerListener(toggle)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.title = ""
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.all_Songs -> {
                    loadFragment(AllSongFragment(1, onItemClick, onClickFavorite))
                }
                R.id.API_Song -> {
                    loadFragment(AllSongFragment(2, onItemClick, onClickFavorite))
                }
                R.id.Favorite_Song -> {
                    loadFragment(FavoriteFragment(listFavorite, onItemClick, onClickFavorite))
                }
            }

            drawer.closeDrawer(GravityCompat.START)
            true
        }

    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun loadFragment(fragment: Fragment) {
        var fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameContent, fragment)
        fragmentTransaction.commit()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toggle.syncState()
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        toggle.onConfigurationChanged(newConfig)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.action_bar_menu, menu)
        val manager = getSystemService(SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.menu_search).actionView as SearchView
        searchView.setSearchableInfo(manager.getSearchableInfo(componentName))
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                viewModel?.search(query, listSong)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        when (item.itemId) {

        }
        return super.onOptionsItemSelected(item)
    }

    fun play() {
        if (mBound) {
            check = if (mService.isPlaying()) {
                mService.pause()
                btnPlay.setImageResource(R.drawable.play_icon)
                false
            } else {
                mService.play()
                btnPlay.setImageResource(R.drawable.pause_icon)
                true
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun next() {
        btnPlay.setImageResource(R.drawable.play_icon)
        if (listSong.size != 0) {
            if (mService.next(index, listSong)) {
                setMusicPlayer(listSong[index + 1])
                index++
            } else {
                Toast.makeText(this, "PlayList Ended", Toast.LENGTH_SHORT).show()
            }
        } else {
            if (mService.next(mService.index, mService.listSong)) {
                Log.d("activity", "nextmain")
                setMusicPlayer(mService.listSong[mService.index + 1])
                this.index = mService.index++
            } else {
                Toast.makeText(this, "PlayList Ended", Toast.LENGTH_SHORT).show()
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun previous() {
        btnPlay.setImageResource(R.drawable.play_icon)
        if (listSong.size != 0) {
            if (mService.previous(index, listSong)) {
                Log.d("activity", "primain")
                setMusicPlayer(listSong[index - 1])
                index--
            } else {
                Toast.makeText(this, "PlayList Ended", Toast.LENGTH_SHORT).show()
            }
        } else {
            if (mService.previous(mService.index, mService.listSong)) {
                Log.d("activity", "primain")
                setMusicPlayer(mService.listSong[mService.index - 1])
                this.index = mService.index--
            } else {
                Toast.makeText(this, "PlayList Ended", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnPlay -> {
                play()
            }
            R.id.btnNext -> {
                next()
            }
            R.id.btnPrevious -> {
                previous()
            }
            R.id.layoutMusic -> {
                var bundle: Bundle = Bundle()
                if (listSong.size != 0) {
                    bundle.putInt("index", index)
                    bundle.putBoolean("check", check)
                    bundle.putInt("flag", flag)
                    bundle.putParcelableArrayList(
                        "listSong",
                        listSong as java.util.ArrayList<out Parcelable>
                    )
                } else {
                    bundle.putInt("index", mService.index)
                    if (mService.isPlaying()) {
                        bundle.putInt("check", 1)
                    } else {
                        bundle.putInt("check", 0)
                    }
                    bundle.putParcelableArrayList(
                        "listSong",
                        mService.listSong as java.util.ArrayList<out Parcelable>
                    )
                    bundle.putInt("flag", mService.flag)
                }
                var intent = Intent(this, PlayingActivity::class.java)
                intent.putExtra("data", bundle)
                startActivity(intent)
            }
        }
    }

    var receiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                MyService.ACTION_MEDIA -> {
                    setFocus(true)
                    btnPlay.setImageResource(R.drawable.pause_icon)
                    setMusicPlayer(intent.getSerializableExtra("song") as Song)
                    index = intent.getIntExtra("index", 0)
                    listSong = mService.listSong

                }
                MyService.ACTION_NEXT -> {
                    next()
                }
                MyService.ACTION_PREVIUOS -> {
                    previous()
                }
                MyService.NEXT -> {
                    mService.next(index, listSong)
                    index++
                }
                MyService.PREVIUOS -> {
                    mService.previous(index, listSong)
                    index--
                }
                MyService.ACTION_PLAY -> {
                    play()
                }
                MyService.PLAY -> {
                    if (intent.getIntExtra("run", 0) == 1) {
                        btnPlay.setImageResource(R.drawable.play_icon)
                        check = false
                    } else {
                        btnPlay.setImageResource(R.drawable.pause_icon)
                        check = true
                    }
                }
                MyService.AUTO -> {
                    index = intent.getIntExtra("index", 0)
                    setMusicPlayer(listSong[index])
                }
                MyService.ACTION_FLAG -> {
                    flag = intent.getIntExtra("flag", 0)
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
//        stopService( Intent(this, MyService::class.java))
    }

}