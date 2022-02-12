package kg.iaau.diploma.primeclinicdoctor

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kg.iaau.diploma.core.utils.startActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    companion object {
        fun startActivity(context: Context) {
            context.startActivity<MainActivity>()
        }
    }
}