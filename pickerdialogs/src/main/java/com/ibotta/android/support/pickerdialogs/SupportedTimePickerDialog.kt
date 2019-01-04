package com.ibotta.android.support.pickerdialogs

import android.annotation.TargetApi
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.TimePicker

/**
 * The purpose of this class is to support all types of time picker dialogs on API 14+. This class was created to fix
 * an issue with the spinner timepickermode not working on Android 7.0 (API 24) ref: https://issuetracker.google.com/issues/37119315.
 * This class is almost identical to the TimePickerDialog, which is part of the Android Platform Library, except for some library specific
 * logic in the class and the TimePicker. For instance the attribute dialogMode="true" is not something that can be used on a normal TimePicker.
 *
 * Android Open Source Project Reference: https://android.googlesource.com/platform/frameworks/base/+/master/core/java/android/app/TimePickerDialog.java
 *
 * Copyright (C) 2019 Ibotta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
class SupportedTimePickerDialog @JvmOverloads constructor(
    context: Context,
    themeResId: Int = 0,
    private val timeSetListener: OnTimeSetListener,
    mInitialHourOfDay: Int,
    mInitialMinute: Int,
    mIs24HourView: Boolean
) : AlertDialog(context, resolveDialogTheme(context, themeResId)),
    DialogInterface.OnClickListener,
    TimePicker.OnTimeChangedListener {

    /**
     * @return the time picker displayed in the dialog
     * @hide For testing only.
     */
    private val timePicker: TimePicker

    /**
     * The callback interface used to indicate the user is done filling in
     * the time (e.g. they clicked on the 'OK' button).
     */
    interface OnTimeSetListener {
        /**
         * Called when the user is done setting a new time and the dialog has
         * closed.
         *
         * @param view the view associated with this listener
         * @param hourOfDay the hour that was set
         * @param minute the minute that was set
         */
        fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int)
    }

    init {
        val themeContext = getContext()
        val inflater = LayoutInflater.from(themeContext)

        timePicker = inflater.inflate(R.layout.view_time_picker_dialog, null) as TimePicker
        timePicker.setIs24HourView(mIs24HourView)
        timePicker.currentHour = mInitialHourOfDay
        timePicker.currentMinute = mInitialMinute
        timePicker.setOnTimeChangedListener(this)
        setView(timePicker)

        setButton(DialogInterface.BUTTON_POSITIVE, themeContext.getString(android.R.string.ok), this)
        setButton(DialogInterface.BUTTON_NEGATIVE, themeContext.getString(android.R.string.cancel), this)
    }

    override fun onTimeChanged(view: TimePicker, hourOfDay: Int, minute: Int) {
        /* do nothing */
    }

    override fun onClick(dialog: DialogInterface, which: Int) {
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> {
                if (isInputValid()) {
                    // Clearing focus forces the dialog to commit any pending
                    // changes, e.g. typed text in a NumberPicker.
                    timePicker.clearFocus()
                    timeSetListener.onTimeSet(timePicker, timePicker.currentHour, timePicker.currentMinute)
                }
            }
            DialogInterface.BUTTON_NEGATIVE -> cancel()
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun isInputValid() : Boolean = ((android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O)
        || ((android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) && timePicker.validateInput()))

    /**
     * Sets the current time.
     *
     * @param hourOfDay The current hour within the day.
     * @param minuteOfHour The current minute within the hour.
     */
    fun updateTime(hourOfDay: Int, minuteOfHour: Int) {
        timePicker.currentHour = hourOfDay
        timePicker.currentMinute = minuteOfHour
    }

    override fun onSaveInstanceState(): Bundle {
        val state = super.onSaveInstanceState()
        state.putInt(HOUR, timePicker.currentHour)
        state.putInt(MINUTE, timePicker.currentMinute)
        state.putBoolean(IS_24_HOUR, timePicker.is24HourView)
        return state
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val hour = savedInstanceState.getInt(HOUR)
        val minute = savedInstanceState.getInt(MINUTE)
        timePicker.setIs24HourView(savedInstanceState.getBoolean(IS_24_HOUR))
        timePicker.currentHour = hour
        timePicker.currentMinute = minute
    }

    companion object {
        private const val HOUR = "hour"
        private const val MINUTE = "minute"
        private const val IS_24_HOUR = "is24hour"

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        private fun resolveDialogTheme(context: Context, resId: Int): Int = when ((resId == 0)) {
            true -> {
                val outValue = TypedValue()
                context.theme.resolveAttribute(android.R.attr.timePickerDialogTheme, outValue, true)
                outValue.resourceId
            }
            false -> resId
        }
    }
}
