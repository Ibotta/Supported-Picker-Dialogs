package com.ibotta.android.support.pickerdialogs

import android.annotation.TargetApi
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.support.annotation.StyleRes
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.DatePicker

import java.util.Calendar

/**
 * The purpose of this class is to support all types of date picker dialogs on API 14+. This class was created to fix
 * an issue with the spinner datepickermode not working on Android 7.0 (API 24) ref: https://issuetracker.google.com/issues/37120178.
 * This class is almost identical to the DatePickerDialog, which is part of the Android Platform Library, except for some library specific
 * logic in the class and the DatePicker. For instance the attribute dialogMode="true" is not something that can be used on a normal DatePicker.
 *
 * Android Open Source Project Reference: https://android.googlesource.com/platform/frameworks/base/+/master/core/java/android/app/DatePickerDialog.java
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
class SupportedDatePickerDialog private constructor(
    context: Context,
    @StyleRes themeResId: Int,
    private var dateSetListener: OnDateSetListener?,
    calendar: Calendar?,
    private var year: Int,
    private var monthOfYear: Int,
    private var dayOfMonth: Int
) : AlertDialog(context, resolveDialogTheme(context, themeResId)),
    DialogInterface.OnClickListener,
    DatePicker.OnDateChangedListener {

    /**
     * Returns the [DatePicker] contained in this dialog.
     *
     * @return the date picker
     */
    val datePicker: DatePicker

    /**
     * Creates a new date picker dialog for the current date using the parent
     * context's default date picker dialog theme.
     *
     * @param context the parent context
     */
    constructor(context: Context) : this(context, 0)

    /**
     * Creates a new date picker dialog for the current date.
     *
     * @param context        the parent context
     * @param themeResId     the resource ID of the theme against which to inflate
     * this dialog, or `0` to use the parent
     * `context`'s default alert dialog theme
     */
    constructor(context: Context, @StyleRes themeResId: Int)
        : this(context, themeResId, null, Calendar.getInstance(), -1, -1, -1)

    /**
     * Creates a new date picker dialog for the specified date using the parent
     * context's default date picker dialog theme.
     *
     * @param context    the parent context
     * @param listener   the listener to call when the user sets the date
     * @param calendar   the calendar object used to populate the DatePicker
     */
    constructor(
        context: Context,
        listener: OnDateSetListener,
        calendar: Calendar
    ) : this(context, 0, listener, calendar)

    /**
     * Creates a new date picker dialog for the specified date using the parent
     * context's default date picker dialog theme.
     *
     * @param context    the parent context
     * @param listener   the listener to call when the user sets the date
     * @param calendar   the calendar object used to populate the DatePicker
     */
    constructor(
        context: Context,
        @StyleRes themeResId: Int,
        listener: OnDateSetListener,
        calendar: Calendar
    ) : this(context, themeResId, listener, calendar, -1, -1, -1)


    /**
     * Creates a new date picker dialog for the specified date using the parent
     * context's default date picker dialog theme.
     *
     * @param context    the parent context
     * @param listener   the listener to call when the user sets the date
     * @param year       the initially selected year
     * @param month      the initially selected month (0-11 for compatibility with
     * [Calendar.MONTH])
     * @param dayOfMonth the initially selected day of month (1-31, depending
     * on month)
     */
    constructor(
        context: Context,
        listener: OnDateSetListener,
        year: Int,
        month: Int,
        dayOfMonth: Int
    ) : this(context, 0, listener, year, month, dayOfMonth)

    /**
     * Creates a new date picker dialog for the specified date.
     *
     * @param context     the parent context
     * @param themeResId  the resource ID of the theme against which to inflate
     * this dialog, or `0` to use the parent
     * `context`'s default alert dialog theme
     * @param listener    the listener to call when the user sets the date
     * @param year        the initially selected year
     * @param monthOfYear the initially selected month of the year (0-11 for
     * compatibility with [Calendar.MONTH])
     * @param dayOfMonth  the initially selected day of month (1-31, depending
     * on month)
     */
    constructor(
        context: Context,
        @StyleRes themeResId: Int,
        listener: OnDateSetListener,
        year: Int,
        monthOfYear: Int,
        dayOfMonth: Int
    ) : this(context, themeResId, listener, null, year, monthOfYear, dayOfMonth)

    init {
        val themeContext = getContext()
        val inflater = LayoutInflater.from(themeContext)

        datePicker = inflater.inflate(R.layout.view_date_picker, null) as DatePicker
        calendar?.let {
            year = it.get(Calendar.YEAR)
            monthOfYear = it.get(Calendar.MONTH)
            dayOfMonth = it.get(Calendar.DAY_OF_MONTH)
        }
        datePicker.init(year, monthOfYear, dayOfMonth, this)
        setView(datePicker)

        setButton(DialogInterface.BUTTON_POSITIVE, themeContext.getString(android.R.string.ok), this)
        setButton(DialogInterface.BUTTON_NEGATIVE, themeContext.getString(android.R.string.cancel), this)
    }

    override fun onDateChanged(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        datePicker.init(year, month, dayOfMonth, this)
    }

    /**
     * Sets the listener to call when the user sets the date.
     *
     * @param listener the listener to call when the user sets the date
     */
    fun setOnDateSetListener(listener: OnDateSetListener) {
        dateSetListener = listener
    }

    override fun onClick(dialog: DialogInterface, which: Int) {
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> dateSetListener?.let {
                // Clearing focus forces the dialog to commit any pending
                // changes, e.g. typed text in a NumberPicker.
                datePicker.clearFocus()
                it.onDateSet(datePicker, datePicker.year, datePicker.month, datePicker.dayOfMonth)
            }
            DialogInterface.BUTTON_NEGATIVE -> cancel()
        }
    }

    /**
     * Sets the current date.
     *
     * @param year       the year
     * @param month      the month (0-11 for compatibility with
     * [Calendar.MONTH])
     * @param dayOfMonth the day of month (1-31, depending on month)
     */
    fun updateDate(year: Int, month: Int, dayOfMonth: Int) {
        datePicker.updateDate(year, month, dayOfMonth)
    }

    override fun onSaveInstanceState(): Bundle {
        val state = super.onSaveInstanceState()
        state.putInt(YEAR, datePicker.year)
        state.putInt(MONTH, datePicker.month)
        state.putInt(DAY, datePicker.dayOfMonth)
        return state
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val year = savedInstanceState.getInt(YEAR)
        val month = savedInstanceState.getInt(MONTH)
        val day = savedInstanceState.getInt(DAY)
        datePicker.init(year, month, day, this)
    }

    /**
     * The listener used to indicate the user has finished selecting a date.
     */
    interface OnDateSetListener {
        /**
         * @param view the picker associated with the dialog
         * @param year the selected year
         * @param month the selected month (0-11 for compatibility with
         * [Calendar.MONTH])
         * @param dayOfMonth th selected day of the month (1-31, depending on
         * month)
         */
        fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int)
    }

    companion object {
        private const val YEAR = "year"
        private const val MONTH = "month"
        private const val DAY = "day"

        @StyleRes @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        private fun resolveDialogTheme(context: Context, @StyleRes themeResId: Int): Int = when ((themeResId == 0)) {
            true -> {
                val outValue = TypedValue()
                // android.R.attr.datePickerDialogTheme was added in API 21, so the outValue.resourceId will be 0, and the view will receive no dialog theme
                context.theme.resolveAttribute(android.R.attr.datePickerDialogTheme, outValue, true)
                outValue.resourceId
            }
            false -> themeResId
        }
    }
}
