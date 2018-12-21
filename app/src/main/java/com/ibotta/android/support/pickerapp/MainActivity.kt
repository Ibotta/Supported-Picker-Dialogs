package com.ibotta.android.support.pickerapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.DatePicker
import android.widget.TimePicker
import com.ibotta.android.support.pickerdialogs.SupportedDatePickerDialog
import com.ibotta.android.support.pickerdialogs.SupportedTimePickerDialog
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * The purpose of this class is show off the SupportedDatePickerDialog and SupportedTimePickerDialog's functionality.
 * This implementation shows off both picker dialogs being displayed in their different modes.
 *
 * Created by Lucas Newcomer 12/19/18
 */
class MainActivity : AppCompatActivity(), SupportedDatePickerDialog.OnDateSetListener,
        SupportedTimePickerDialog.OnTimeSetListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadState(savedInstanceState)
        setContentView(R.layout.activity_main)

        setDate()
        setTime()

        spinnerDatePickerBtn.setOnClickListener { showSpinnerDatePickerDialog() }
        calendarDatePickerBtn.setOnClickListener { showCalendarDatePickerDialog() }
        spinnerTimePickerBtn.setOnClickListener { showSpinnerTimePickerDialog() }
        calendarTimePickerBtn.setOnClickListener { showClockTimePickerDialog() }
    }

    private fun loadState(savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            currentDate = savedInstanceState.getSerializable(CURRENT_DATE_KEY) as Calendar
        }
    }

    private fun showSpinnerDatePickerDialog() {
        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH)
        val dayOfMonth = currentDate.get(Calendar.DAY_OF_MONTH)
        SupportedDatePickerDialog(this, R.style.SpinnerDatePickerDialogTheme, this, year, month, dayOfMonth).show()
    }

    private fun showCalendarDatePickerDialog() {
        // I added two more constructors to the SupportDatePickerDialog that is not part of the normal DatePickerDialog. This just allows
        // you to send an entire Calendar object. I always found it annoying that you only are able send the three date values
        // instead of the whole Calendar object.
        val supportedDatePickerDialog = SupportedDatePickerDialog(this, this, currentDate)
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
            // For below API 21 the default date picker is a spinner and to update the date picker to be a calendar you
            // have to do it this way. Otherwise for above API 21 the default is the Material Design calendar date picker.
            supportedDatePickerDialog.datePicker.calendarViewShown = true
            supportedDatePickerDialog.datePicker.spinnersShown = false
        }
        supportedDatePickerDialog.show()
    }

    private fun showSpinnerTimePickerDialog() {
        val month = currentDate.get(Calendar.MONTH)
        val dayOfMonth = currentDate.get(Calendar.DAY_OF_MONTH)
        SupportedTimePickerDialog(
            context = this,
            themeResId =  R.style.SpinnerTimePickerDialogTheme,
            timeSetListener = this,
            mInitialHourOfDay = month,
            mInitialMinute = dayOfMonth,
            mIs24HourView = true).show()
    }

    private fun showClockTimePickerDialog() {
        val hour = currentDate.get(Calendar.HOUR_OF_DAY)
        val minute = currentDate.get(Calendar.MINUTE)
        // For above API 21 the default implementation of a time picker is a clock. For below API 21 there is no way to
        // make the time picker a clock.
        SupportedTimePickerDialog(
            context = this,
            timeSetListener = this,
            mInitialHourOfDay = hour,
            mInitialMinute = minute,
            mIs24HourView = true).show()
    }


    override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        currentDate.set(Calendar.YEAR, year)
        currentDate.set(Calendar.MONTH, month)
        currentDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        setDate()
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        currentDate.set(Calendar.HOUR_OF_DAY, hourOfDay)
        currentDate.set(Calendar.MINUTE, minute)
        setTime()
    }

    private fun setDate() {
        val formatter = SimpleDateFormat(getString(R.string.date_format), Locale.getDefault())
        val date = getString(R.string.date_picker_row_title, formatter.format(currentDate.time))
        dateDisplay.text = date
    }

    private fun setTime() {
        val formatter = SimpleDateFormat(getString(R.string.time_format), Locale.getDefault())
        val time = getString(R.string.time_picker_row_title, formatter.format(currentDate.time))
        timeDisplay.text = time
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putSerializable(CURRENT_DATE_KEY, currentDate)
    }

    companion object {
        private var currentDate = Calendar.getInstance()
        private const val CURRENT_DATE_KEY = "currentDate"
    }
}
