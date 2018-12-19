package com.ibotta.android.support.pickerapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.ibotta.android.support.pickerdialogs.SupportedDatePickerDialog
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        spinnerDatePickerBtn.setOnClickListener { showSpinnerDatePickerDialog() }
        calendarDatePickerBtn.setOnClickListener { showCalendarDatePickerDialog() }
        spinnerTimePickerBtn.setOnClickListener { showSpinnerTimePickerDialog() }
        calendarTimePickerBtn.setOnClickListener { showCalendarTimePickerDialog() }
    }

    private fun showSpinnerDatePickerDialog() {
        SupportedDatePickerDialog(this).show()
    }

    private fun showCalendarDatePickerDialog() {
        // The default implementation of the DatePicker is a calendar
        SupportedDatePickerDialog(this).show()
    }

    private fun showSpinnerTimePickerDialog() {
    }

    private fun showCalendarTimePickerDialog() {

    }
}
