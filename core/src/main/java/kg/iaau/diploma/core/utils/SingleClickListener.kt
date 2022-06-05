package kg.iaau.diploma.core.utils

import android.os.SystemClock
import android.view.View

class SingleClickListener(
    private var defaultInterval: Int = 1000,
    private val onSingleClick: (View) -> Unit
) : View.OnClickListener {
    private var lastTimeClicked: Long = 0
    override fun onClick(v: View) {
        if (SystemClock.elapsedRealtime() - lastTimeClicked < defaultInterval) {
            return
        }
        lastTimeClicked = SystemClock.elapsedRealtime()
        onSingleClick(v)
    }
}