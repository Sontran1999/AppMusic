package com.example.appmusic.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Paint
import android.widget.EditText
import android.widget.NumberPicker
import androidx.core.view.children
import androidx.lifecycle.AndroidViewModel
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.appmusic.common.PrefUtils
import com.example.appmusic.common.PrefsKey
import com.example.appmusic.data.ReminderWorker
import com.example.appmusic.data.ReminderWorker.Companion.WORK_NAME
import java.lang.reflect.Field
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class SettingReminderViewModel(application: Application) : AndroidViewModel(application) {
    private val mContext: Context = getApplication<Application>().applicationContext
    var mWorkManager: WorkManager? = null

    init {
        mWorkManager = WorkManager.getInstance(mContext)
    }

    fun convertStringToCalendar(timeString: String) {
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        val date: Date? = format.parse(timeString)
        val hour = date?.hours
        val minutes = date?.minutes
        PrefUtils.getInstance(mContext).putValue(PrefsKey.KEY_VALUE_REMINDER_HOUR, hour ?: "")
        PrefUtils.getInstance(mContext).putValue(PrefsKey.KEY_VALUE_REMINDER_MINUTES, minutes ?: "")
    }

    fun setTimeReminder() {
        val request =
            OneTimeWorkRequestBuilder<ReminderWorker>()
                .setInitialDelay(
                    initDelayTimeReminder(),
                    TimeUnit.MILLISECONDS
                ).build()
        mWorkManager?.enqueue(request)
    }

    private fun initDelayTimeReminder(): Long {
        val hour =
            PrefUtils.getInstance(mContext).getValue(PrefsKey.KEY_VALUE_REMINDER_HOUR, 21)
        val minute =
            PrefUtils.getInstance(mContext).getValue(PrefsKey.KEY_VALUE_REMINDER_MINUTES, 0)
        val calendarDefault = Calendar.getInstance().getInstantCalendarUTC()
        val calendarReminder = Calendar.getInstance().getInstantCalendarUTC()
        calendarReminder.set(Calendar.HOUR_OF_DAY, hour)
        calendarReminder.set(Calendar.MINUTE, minute)
        calendarReminder.set(Calendar.SECOND, 0)
        calendarReminder.set(Calendar.MILLISECOND, 0)
        if (calendarReminder.before(calendarDefault)) {
            calendarReminder.add(Calendar.DAY_OF_MONTH, 1)
        }
        return calendarReminder.timeInMillis - calendarDefault.timeInMillis
    }

    fun cancelReminderSetting() = mWorkManager?.cancelUniqueWork(WORK_NAME)

    fun Calendar.getInstantCalendarUTC(): Calendar {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = this.timeInMillis
        this.timeZone = TimeZone.getTimeZone("UTC")
        this.set(Calendar.YEAR, calendar.get(Calendar.YEAR))
        this.set(Calendar.MONTH, calendar.get(Calendar.MONTH))
        this.set(Calendar.DATE, calendar.get(Calendar.DATE))
        this.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY))
        this.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE))
        this.set(Calendar.SECOND, calendar.get(Calendar.SECOND))
        this.set(Calendar.MILLISECOND, calendar.get(Calendar.MILLISECOND))
        return this
    }
}
