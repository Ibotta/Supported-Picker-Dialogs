package com.ibotta.android.support.pickerapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.DatePicker
import com.ibotta.android.support.pickerdialogs.SupportedDatePickerDialog
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * The purpose of this class is show off the SupportedDatePickerDialog's functionality. This implementation shows off
 * the SupportedDatePickerDialog being displayed in the spinner and calendar fashion.
 *
 * Created by Lucas Newcomer 12/19/18
 */
class MainActivity : AppCompatActivity(), SupportedDatePickerDialog.OnDateSetListener {
    private var currentDate = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setDate()

        spinnerDatePickerBtn.setOnClickListener { showSpinnerDatePickerDialog() }
        calendarDatePickerBtn.setOnClickListener { showCalendarDatePickerDialog() }
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

    override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        currentDate.set(year, month, dayOfMonth)
        setDate()
    }

    private fun setDate() {
        val formatter = SimpleDateFormat(getString(R.string.date_format), Locale.getDefault())
        val date = getString(R.string.date_picker_row_title, formatter.format(currentDate.time))

        dateDisplay.text = date
    }
}
