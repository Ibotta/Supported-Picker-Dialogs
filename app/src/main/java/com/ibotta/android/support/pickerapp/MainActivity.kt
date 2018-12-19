package com.ibotta.android.support.pickerapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.DatePicker
import com.ibotta.android.support.pickerdialogs.SupportedDatePickerDialog
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*

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
        // I added two more constructors to the SupportDatePickerDialog that is not par of the normal DatePickerDialog. This just allows
        // users to be able to send a entire Calendar object or the year, month, dayOfMonth. I always found it annoying that you only are
        // able send the three date values instead of the whole Calendar object.
        SupportedDatePickerDialog(this, this, currentDate).show()
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
