package com.ibotta.android.support.pickerdialogs

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.annotation.StyleRes
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.DatePicker

import java.util.Calendar

/**
 * The purpose of this class is to support all types of date picker dialogs for all types of Android versions. The reason
 * that this class was created is because spinner date pickers are not working for Android 7.0 (API 24) ref: https://issuetracker.google.com/issues/37120178.
 * This class is almost identical to the DatePickerDialog, which is part of the Android Platform Library, except for some library specific
 * logic in the class and DatePicker's xml. For instance the attribute dialogMode="true" is not something that can be used on a normal DatePicker in xml.
 *
 * Created by Lucas Newcomer 12/17/18
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

        setView(datePicker)

        setButton(DialogInterface.BUTTON_POSITIVE, themeContext.getString(android.R.string.ok), this)
        setButton(DialogInterface.BUTTON_NEGATIVE, themeContext.getString(android.R.string.cancel), this)

        calendar?.let {
            year = it.get(Calendar.YEAR)
            monthOfYear = it.get(Calendar.MONTH)
            dayOfMonth = it.get(Calendar.DAY_OF_MONTH)
        }

        datePicker.init(year, monthOfYear, dayOfMonth, this)
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

        @StyleRes
        private fun resolveDialogTheme(context: Context, @StyleRes themeResId: Int): Int = when ((themeResId == 0)) {
            true -> {
                val outValue = TypedValue()
                context.theme.resolveAttribute(android.R.attr.datePickerDialogTheme, outValue, true)
                outValue.resourceId
            }
            false -> themeResId
        }
    }
}
