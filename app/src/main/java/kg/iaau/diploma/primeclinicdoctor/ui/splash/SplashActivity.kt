package kg.iaau.diploma.primeclinicdoctor.ui.splash

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import dagger.hilt.android.AndroidEntryPoint
import kg.iaau.diploma.core.ui.CoreActivity
import kg.iaau.diploma.primeclinicdoctor.databinding.ActivitySplashBinding
import kg.iaau.diploma.primeclinicdoctor.ui.authorization.AuthorizationActivity
import kg.iaau.diploma.primeclinicdoctor.ui.authorization.AuthorizationVM
import kg.iaau.diploma.primeclinicdoctor.ui.pin.PinActivity

@AndroidEntryPoint
class SplashActivity : CoreActivity<ActivitySplashBinding, AuthorizationVM>(AuthorizationVM::class.java) {

    override val bindingInflater: (LayoutInflater) -> ActivitySplashBinding
        get() = ActivitySplashBinding::inflate

    override fun setupActivityView() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (vm.isUserSignIn())
                PinActivity.startActivity(this)
            else
                AuthorizationActivity.startActivity(this)
            finish()
        }, 1000)
    }

}