package com.example.appmusic.view.activity

import android.app.SearchManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.appmusic.R
import com.example.appmusic.model.Song
import com.example.appmusic.service.MyService
import com.example.appmusic.view.fragment.AllSongFragment
import com.example.appmusic.view.fragment.FavoriteFragment
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    lateinit var navigationView: NavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadFragment(AllSongFragment(onItemClick))
        setDrawer()
    }

    override fun onResume() {
        super.onResume()
        MyService.onPreparedListener ={
            initMusic()
        }

    }

    var receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when(intent?.action){
                MyService.ACTION_MEDIA -> {
                    Toast.makeText(context,"sơn",Toast.LENGTH_LONG).show()
                    var intent = Intent(context, PlayingActivity::class.java)
                    startActivity(intent)
                }
            }
        }

    }

    fun initMusic(){
        var intentFilter = IntentFilter()
        intentFilter.addAction(MyService.ACTION_MEDIA)
        registerReceiver(receiver, intentFilter)
    }

    private val onItemClick: (Int, ArrayList<Song>) -> Unit = { index, listSong->
        var bundle: Bundle = Bundle()
        bundle.putInt("index", index)
        bundle.putParcelableArrayList("listSong", listSong as java.util.ArrayList<out Parcelable>)
        var intent = Intent(this, PlayingActivity::class.java)
        intent.putExtra("data", bundle)
        startActivity(intent)
        var intentService = Intent(this, MyService::class.java)
        intentService.putExtra("data", bundle)
        ContextCompat.startForegroundService(this, intentService)
        bindService(intentService, PlayingActivity.serviceConnection, BIND_AUTO_CREATE)

    }

    fun setDrawer() {
        drawer = findViewById(R.id.drawer_layout)
        setSupportActionBar(findViewById(R.id.toolbar))
        toggle = ActionBarDrawerToggle(
            this,
            drawer,
            toolbar,
            R.string.nav_drawer_open,
            R.string.nav_drawer_close
        )
        drawer.addDrawerListener(toggle)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener { menuItem ->
            val id = menuItem.itemId
            when (id) {
                R.id.all_Songs -> {
                    loadFragment(AllSongFragment(onItemClick))
                }
                R.id.favorite_Song -> {
                    loadFragment(FavoriteFragment(onItemClick))
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

    fun loadFragment(fragment: Fragment) {
        var fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameContent, fragment)
        fragmentTransaction.commit()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        // Sync the toggle state after onRestoreInstanceState has occurred.
        toggle.syncState()
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        toggle.onConfigurationChanged(newConfig)
    }


//    fun setPageLayout(){
//         val adapter = ViewPageAdapter(supportFragmentManager)
//        songs_viewpager.adapter = adapter
//        tabLayout.setupWithViewPager(songs_viewpager)
//        tabLayout.addOnTabSelectedListener(object  : TabLayout.OnTabSelectedListener{
//            override fun onTabSelected(p0: TabLayout.Tab?) {
//                if (p0 != null) {
//                    songs_viewpager.setCurrentItem(p0.getPosition())
//                }
//            }
//
//            override fun onTabUnselected(p0: TabLayout.Tab?) {
//
//            }
//
//            override fun onTabReselected(p0: TabLayout.Tab?) {
//
//            }
//
//        })
//    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.action_bar_menu, menu)
        val manager = getSystemService(SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.menu_search).actionView as SearchView
        searchView.setSearchableInfo(manager.getSearchableInfo(componentName))
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
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
    
}