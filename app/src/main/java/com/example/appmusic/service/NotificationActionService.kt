package com.example.appmusic.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

internal class NotificationActionService : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        context.sendBroadcast(
            Intent("TRACKS_TRACKS")
                .putExtra("actionname", intent.action)
        )
    }
}