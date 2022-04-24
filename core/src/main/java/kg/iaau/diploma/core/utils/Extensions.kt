package kg.iaau.diploma.core.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Animatable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Base64
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.Nullable
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.controller.BaseControllerListener
import com.facebook.drawee.interfaces.DraweeController
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.image.ImageInfo
import kg.iaau.diploma.core.R
import java.text.SimpleDateFormat
import java.util.*


inline fun <reified T : Activity> Context.startActivity(noinline extra: Intent.() -> Unit = {}) {
    val intent = Intent(this, T::class.java)
    intent.extra()
    startActivity(intent)
}

fun String.convertPhoneNumberTo(countryCode: String): String = "+$countryCode$this"

fun Context.toast(message: String, duration: Int = Toast.LENGTH_SHORT) {
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

fun String.convertToEmail(): String = "$this@gmail.com"

fun Date.formatForDate(format: String = "dd.MM.yyyy HH:mm"): String {
    val sdf = SimpleDateFormat(format, Locale.ROOT)
    return sdf.format(this)
}

fun SimpleDraweeView.loadWithFresco(
    uri: String?,
    onSuccess: ((imageInfo: ImageInfo?) -> Unit)? = null,
    onFail: ((throwable: Throwable) -> Unit)? = null
) {
    val controller: DraweeController = Fresco.newDraweeControllerBuilder()
        .setUri(uri)
        .setTapToRetryEnabled(true)
        .setOldController(controller)
        .setControllerListener(frescoListener(onSuccess, onFail))
        .build()

    setController(controller)
}

fun SimpleDraweeView.loadWithFresco(
    uri: Uri?,
    onSuccess: ((imageInfo: ImageInfo?) -> Unit)? = null,
    onFail: ((throwable: Throwable) -> Unit)? = null
) {
    val controller: DraweeController = Fresco.newDraweeControllerBuilder()
        .setUri(uri)
        .setTapToRetryEnabled(true)
        .setOldController(controller)
        .setControllerListener(frescoListener(onSuccess, onFail))
        .build()

    setController(controller)
}

fun frescoListener(
    onSuccess: ((imageInfo: ImageInfo?) -> Unit)?,
    onFail: ((throwable: Throwable) -> Unit)?
): BaseControllerListener<ImageInfo?> {
    return object : BaseControllerListener<ImageInfo?>() {
        override fun onFinalImageSet(
            id: String?,
            @Nullable imageInfo: ImageInfo?,
            @Nullable animatable: Animatable?
        ) {
            onSuccess?.invoke(imageInfo)
        }

        override fun onFailure(id: String, throwable: Throwable) {
            onFail?.invoke(throwable)
        }
    }
}

fun RecyclerView.scrollToLastItem() {
    addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
        if (bottom < oldBottom) {
            postDelayed({
                smoothScrollToPosition(bottom)
            }, 100)
        }
    }
}

fun Context.setDrawable(@DrawableRes id: Int): Drawable? {
    return ContextCompat.getDrawable(this, id)
}

fun ImageView.loadBase64Image(context: Context, image: String?, @DrawableRes defaultResId: Int) {
    if (image.isNullOrEmpty())
        setImageDrawable(context.setDrawable(defaultResId))
    else
        setImageDrawable(image.convertBase64ToDrawable(context, defaultResId))
}

fun String.convertBase64ToBitmap(): Bitmap {
    val imageAsBytes = Base64.decode(toByteArray(), Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.size)
}

fun String.convertBase64ToDrawable(context: Context, @DrawableRes defaultResId: Int): Drawable? {
    return when(isNullOrEmpty()) {
        true -> context.setDrawable(defaultResId)
        else -> {
            val bitmap = this.convertBase64ToBitmap()
            BitmapDrawable(context.resources, bitmap)
        }
    }
}