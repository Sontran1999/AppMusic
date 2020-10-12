package com.example.appmusic.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.appmusic.R
import com.example.appmusic.model.Song

class CreateNotification(context: Context, song: Song) {
    val CHANNEL_ID = "channel1"
    val ACTION_PREVIUOS = "actionprevious"
    val ACTION_PLAY = "actionplay"
    val ACTION_NEXT = "actionnext"
    private val context: Context = context
    private val song: Song = song
    var pendingIntentNext: PendingIntent? = null
    var pendingIntentPrevious: PendingIntent? = null
    var intentPlay = Intent(context, NotificationActionService::class.java)
        .setAction(ACTION_PLAY)
    var pendingIntentPlay = PendingIntent.getBroadcast(
        context, 0,
        intentPlay, PendingIntent.FLAG_UPDATE_CURRENT
    )

    fun builder(): Notification {
        createNotificationChannel()
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 0,
            Intent(context, MyService::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0
        )
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_music_note)
                .setContentTitle(song.title)
                .setContentText(song.subTitle)
                .addAction(
                    R.drawable.previous_icon,
                    "Previous",
                    pendingIntentPrevious
                )
                .addAction(R.drawable.pause_icon, "Play", pendingIntentPlay)
                .addAction(R.drawable.next_icon, "Next", pendingIntentNext)
                .setColor(Color.RED)
                .setSound(null)
                .setPriority(NotificationCompat.PRIORITY_LOW)
        synchronized(builder) {}
        return builder.build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "1"
            val importance: Int = NotificationManager.IMPORTANCE_DEFAULT
            val channel =
                NotificationChannel(CHANNEL_ID, name, importance)
            channel.setDescription("description")
            val notificationManager: NotificationManager = context.getSystemService(
                NotificationManager::class.java
            )
            notificationManager.deleteNotificationChannel(CHANNEL_ID)
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val CHANNEL_ID = "Foreground"
    }
}