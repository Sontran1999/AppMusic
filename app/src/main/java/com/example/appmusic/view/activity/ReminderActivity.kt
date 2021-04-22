package com.example.appmusic.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.NumberPicker
import androidx.core.content.ContextCompat
import com.example.appmusic.R
import com.example.appmusic.common.PrefUtils
import com.example.appmusic.common.PrefsKey
import com.example.appmusic.viewmodel.SettingReminderViewModel
import kotlinx.android.synthetic.main.activity_reminder.*

class ReminderActivity : AppCompatActivity(), NumberPicker.OnValueChangeListener, View.OnClickListener {
    private var mScreenName: String? = null
    private var mSettingReminder: SettingReminderViewModel? = null

    companion object {
        const val EXTRA_VALUE_TIME_REMINDER = "EXTRA_VALUE_TIME_REMINDER"
        const val VALUE_MAX_HOUR_TIME_PICKER = 23
        const val VALUE_MIN_HOUR_TIME_PICKER = 0
        const val VALUE_DEFAULT_HOUR_TIME_PICKER = 21
        const val VALUE_MAX_MINUTES_TIME_PICKER = 59
        const val VALUE_MIN_MINUTES_TIME_PICKER = 0
        const val VALUE_DEFAULT_MINUTES_TIME_PICKER = 0
        const val VALUE_INDEX_1 = 1
        const val VALUE_INDEX_2 = 2
        const val VALUE_INDEX_0 = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder)
        mScreenName = intent.getStringExtra("EXTRA_TO_SCREEN_NAME") ?: ""
        initEvents()
        initNumberPicker()
    }

    private fun initNumberPicker() {
        setValueNumberPickerHours()
        setValueNumberPickerMinutes()
    }

    private fun setValueNumberPickerMinutes() {
        npMinutes.minValue = VALUE_MIN_MINUTES_TIME_PICKER
        npMinutes.maxValue = VALUE_MAX_MINUTES_TIME_PICKER
        npMinutes.value = VALUE_DEFAULT_MINUTES_TIME_PICKER
        npMinutes.setFormatter { value ->
            String.format("%02d", value)
        }
        val sDefault = String.format("%02d", npMinutes.value)
        setValueMinutesToTextView(sDefault)
        npMinutes.setOnValueChangedListener(this)
    }

    private fun setValueNumberPickerHours() {
        npHour.maxValue = VALUE_MAX_HOUR_TIME_PICKER
        npHour.minValue = VALUE_MIN_HOUR_TIME_PICKER
        npHour.value = VALUE_DEFAULT_HOUR_TIME_PICKER
        setValueHoursToTextView(npHour.value.toString())
        npHour.setOnValueChangedListener(this)
    }

    private fun initEvents() {
        btnSettingReminder.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
//        super.onClick(v)
        when (v?.id) {
            R.id.btnSettingReminder -> {
                PrefUtils.getInstance(this)
                    .putValue(PrefsKey.KEY_END_TIME_CREATE_DIARY, 0L)
                val hourFirst = tvHourFirst.text.toString()
                val hourSecond = tvHourSecond.text.toString()
                val minutesFirst = tvMinutesFirst.text.toString()
                val minutesSecond = tvMinutesSecond.text.toString()
                val time = "$hourFirst$hourSecond${":"}$minutesFirst$minutesSecond"
                val intent = Intent()
                intent.putExtra(EXTRA_VALUE_TIME_REMINDER, time)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }

    private fun setValueHoursToTextView(time: String) {
        if (time.length == VALUE_INDEX_1) {
            tvHourFirst.text = "0"
            tvHourSecond.text = time
        }
        if (time.length > VALUE_INDEX_1) {
            tvHourFirst.text = time.substring(VALUE_INDEX_0, VALUE_INDEX_1)
            tvHourSecond.text = time.substring(VALUE_INDEX_1, VALUE_INDEX_2)
        }
    }

    private fun setValueMinutesToTextView(time: String) {
        tvMinutesFirst.text = time.substring(VALUE_INDEX_0, VALUE_INDEX_1)
        tvMinutesSecond.text = time.substring(VALUE_INDEX_1, VALUE_INDEX_2)
    }

    override fun onValueChange(picker: NumberPicker?, oldVal: Int, newVal: Int) {
        when (picker?.id) {
            R.id.npHour -> {
                setValueHoursToTextView(newVal.toString())
            }
            R.id.npMinutes -> {
                val textMinutes = String.format("%02d", newVal)
                setValueMinutesToTextView(textMinutes)
            }
        }
    }
}