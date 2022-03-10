package kg.iaau.diploma.core.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import kg.iaau.diploma.core.R
import java.text.SimpleDateFormat
import java.util.*

inline fun <reified T : Activity> Context.startActivity(noinline extra: Intent.() -> Unit = {}) {
    val intent = Intent(this, T::class.java)
    intent.extra()
    startActivity(intent)
}

fun String.convertPhoneNumberTo(countryCode: String): String = "+$countryCode$this"

fun Context.toast(message: String, duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, message, duration).show()
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.setEnable(enabled: Boolean) {
    isEnabled = enabled
    alpha = if (enabled) 1f else 0.5f
}

fun View.setAnimateAlpha(value: Float) {
    animate().alpha(value)
}

fun String.formatForCurrentDate(): String {
    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:SSS", Locale.getDefault()).parse(this)
    return SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(format)
}

fun convertToDateFormat(dayOfMonth: Int, month: Int, year: Int): String {
    val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    return sdf.format(Date(year - 1900, month, dayOfMonth))
}

fun Context.showDialog(
    @StringRes title: Int,
    positiveCallback: (() -> Unit)? = null,
    negativeCallback: (() -> Unit)? = null
) {
    AlertDialog.Builder(this, R.style.AlertDialogTheme)
        .setTitle(getString(title))
        .setCancelable(false)
        .setPositiveButton(R.string.ok) { dialog, _ ->
            positiveCallback?.invoke()
            dialog.cancel()
        }
        .setNegativeButton(R.string.cancel) { dialog, _ ->
            negativeCallback?.invoke()
            dialog.cancel()
        }
        .show()
}

fun String?.formatDateForMedCard(): String? {
    if (this == null) return null
    val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(this)
    return SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(format)
}