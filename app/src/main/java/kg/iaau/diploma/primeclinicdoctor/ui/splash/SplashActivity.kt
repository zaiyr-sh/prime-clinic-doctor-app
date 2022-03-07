package kg.iaau.diploma.primeclinicdoctor.ui.splash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import kg.iaau.diploma.primeclinicdoctor.databinding.ActivitySplashBinding
import kg.iaau.diploma.primeclinicdoctor.ui.authorization.AuthorizationActivity
import kg.iaau.diploma.primeclinicdoctor.ui.authorization.AuthorizationVM
import kg.iaau.diploma.primeclinicdoctor.ui.pin.PinActivity

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private val vm: AuthorizationVM by viewModels()
    private lateinit var vb: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vb = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(vb.root)
        startActivityWithDelay()
    }

    private fun startActivityWithDelay() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (vm.isUserSignIn())
                PinActivity.startActivity(this)
            else
                AuthorizationActivity.startActivity(this)
            finish()
        }, 1000)
    }

}