package com.example.appmusic.data

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.appmusic.R
import com.example.appmusic.view.activity.MainActivity
import com.example.appmusic.view.activity.PlayingActivity
import com.example.appmusic.viewmodel.ViewModel
import kotlinx.android.synthetic.main.activity_playing.*

class ReminderWorker(val context: Context, workerParams: WorkerParameters) : Worker(
    context,
    workerParams
) {
    var viewModel = ViewModel()
    companion object {
        const val WORK_NAME = "WORK_NAME"
    }

    override fun doWork(): Result {
        Log.d("son","vo 1")
        MainActivity.mService.pause()
        viewModel.checkTimer(true)
        return Result.success()
    }

}
