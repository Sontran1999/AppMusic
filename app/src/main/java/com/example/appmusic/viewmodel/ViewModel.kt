package com.example.appmusic.viewmodel

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.ViewModel

class ViewModel : ViewModel() {
    var serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, iBinder: IBinder) {

        }

        override fun onServiceDisconnected(arg0: ComponentName) {

        }
    }
}