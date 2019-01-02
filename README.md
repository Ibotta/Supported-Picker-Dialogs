# Supported-Picker-Dialogs

![Travis CI Badge](https://travis-ci.com/Ibotta/Supported-Picker-Dialogs.svg?token=a1Fz6AJc1ZbpXy1CH2gh&branch=master)

<!-- Add CodeClimate Badge https://docs.codeclimate.com/docs/overview#badges -->

## Installation
Step 1: Add jitpack.io repository to your root build.gradle
```
allprojects {
 repositories {
    jcenter()
    maven { url "https://jitpack.io" }
 }
}
```
Step 2. Add this dependency
```
dependencies {
    implementation 'com.github.Ibotta:supported-picker-dialogs:1.0'
}
```

## Usage
An Android DatePicker and TimePickerDialog that works correctly across OS versions.

The main reason for this library is to fix an issue with the spinner datepickermode and timepickermode style attributes in themes not being applied to
the DatePickerDialog and TimePickerDialog on devices running Android 7.0. Google's response to this issue is that they wont
fix it due to it being obsolete via these tracked issues: https://issuetracker.google.com/issues/37120178, https://issuetracker.google.com/issues/37119315.
Its a strange issue that with how the Android platform library handles the style attribute for the picker views, but fear not!
This library adds a SupportedDatePickerDialog and SupportedTimePickerDialog to your project to be used in place of any DatePickerDialog or TimePickerDialog
to fix the issue! Also these supported dialogs continue to still have all the functionality that the Android platform library's picker dialogs have.

## Development
SupportedDatePickerDialog style:
```
<style name="SpinnerDatePickerDialogTheme" parent="Theme.AppCompat.Light.Dialog">
    <!-- android:datePickerStyle was not added until API 21 -->
    <item name="android:datePickerStyle">@style/DatePickerStyle</item>
</style>

<style name="DatePickerStyle" parent="@android:style/Widget.Material.Light.DatePicker">
    <item name="android:datePickerMode">spinner</item>
</style>
```
SupportedDatePickerDialog code:
```
var currentDate = Calendar.getInstance()
val year = currentDate.get(Calendar.YEAR)
val month = currentDate.get(Calendar.MONTH)
val dayOfMonth = currentDate.get(Calendar.DAY_OF_MONTH)
SupportedDatePickerDialog(this, R.style.SpinnerDatePickerDialogTheme, this, year, month, dayOfMonth).show()
```

SupportedTimePickerDialog style:
```
<style name="SpinnerTimePickerDialogTheme" parent="Theme.AppCompat.Light.Dialog">
    <!-- android:timePickerStyle was not added until API 21 -->
    <item name="android:timePickerStyle">@style/TimePickerStyle</item>
</style>

<style name="TimePickerStyle" parent="@android:style/Widget.Material.Light.TimePicker">
    <item name="android:timePickerMode">spinner</item>
</style>
```
SupportedTimePickerDialog code:
```
var currentDate = Calendar.getInstance()
val month = currentDate.get(Calendar.MONTH)
val dayOfMonth = currentDate.get(Calendar.DAY_OF_MONTH)
SupportedTimePickerDialog(
    context = this,
    themeResId =  R.style.SpinnerTimePickerDialogTheme,
    timeSetListener = this,
    mInitialHourOfDay = month,
    mInitialMinute = dayOfMonth,
    mIs24HourView = true)
    .show()
```
