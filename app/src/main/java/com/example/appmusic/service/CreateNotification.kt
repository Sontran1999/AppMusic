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
import com.example.appmusic.common.Utils
import com.example.appmusic.view.activity.MainActivity

class CreateNotification(var context: Context, var song: Song, var mService: MyService) {

    //previous
    var intentPrevious = Intent()
        .setAction(MyService.ACTION_PREVIUOS)
    var pendingIntentPrevious = PendingIntent.getBroadcast(
        context, 0,
        intentPrevious, PendingIntent.FLAG_UPDATE_CURRENT
    )

    //play
    var intentPlay = Intent()
        .setAction(MyService.ACTION_PLAY)
    var pendingIntentPlay = PendingIntent.getBroadcast(
        context, 0,
        intentPlay, PendingIntent.FLAG_UPDATE_CURRENT
    )

    //next
    var intentNext = Intent()
        .setAction(MyService.ACTION_NEXT)
    var pendingIntentNext = PendingIntent.getBroadcast(
        context, 0,
        intentNext, PendingIntent.FLAG_UPDATE_CURRENT
    )

    val resultIntent = Intent(
        context,
        MainActivity::class.java
    ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
    val resultPendingIntent =
        PendingIntent.getActivity(
            context,
            1,
            resultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

    fun builder(): Notification {
        createNotificationChannel()
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 0,
            Intent(context, MyService::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0
        )
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(context, MyService.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_music_note)
                .setLargeIcon(song.path?.let { Utils.songArt(it) })
                .setContentTitle(song.title)
                .setContentText(song.subTitle)
                .setOnlyAlertOnce(true)
                .setContentIntent(resultPendingIntent)
                .addAction(R.drawable.previous_icon, "Previous", pendingIntentPrevious)
                .addAction(notificationAction(MyService.ACTION_PLAY))
                .addAction(R.drawable.next_icon, "Next", pendingIntentNext)
                .setColor(Color.RED)
                .setStyle(
                    androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0, 1, 2)
                )
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
                NotificationChannel(MyService.CHANNEL_ID, name, importance)
            channel.setDescription("description")
            val notificationManager: NotificationManager = context.getSystemService(
                NotificationManager::class.java
            )
            notificationManager.deleteNotificationChannel(MyService.CHANNEL_ID)
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun setButton(): Int {
        if (mService.isPlaying()) {
            return R.drawable.pause_icon
        } else
            return R.drawable.play_icon
    }

    private fun playerAction(action: String): PendingIntent {

        val pauseIntent = Intent()
        pauseIntent.action = action

        return PendingIntent.getBroadcast(
            context, 0,
            intentPlay, PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun notificationAction(action: String): NotificationCompat.Action {
        var icon = R.drawable.pause_icon
        when (action) {
            MyService.ACTION_PLAY -> {
                if (MainActivity.mService.isPlaying())
                    icon = R.drawable.pause_icon
                else
                    icon = R.drawable.play_icon
            }
        }
        return NotificationCompat.Action.Builder(icon, action, playerAction(action)).build()
    }
}