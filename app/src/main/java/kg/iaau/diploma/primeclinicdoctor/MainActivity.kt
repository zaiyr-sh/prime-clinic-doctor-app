package kg.iaau.diploma.primeclinicdoctor

import android.content.Context
import android.view.LayoutInflater
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import kg.iaau.diploma.core.ui.CoreActivity
import kg.iaau.diploma.core.utils.FirebaseHelper
import kg.iaau.diploma.core.utils.startActivity
import kg.iaau.diploma.primeclinicdoctor.databinding.ActivityMainBinding
import kg.iaau.diploma.primeclinicdoctor.ui.main.chat.ChatVM
import kg.iaau.diploma.primeclinicdoctor.ui.main.chat.calling.ReceivingCallActivity

@AndroidEntryPoint
class MainActivity : CoreActivity<ActivityMainBinding, ChatVM>(ChatVM::class.java) {

    override val bindingInflater: (LayoutInflater) -> ActivityMainBinding
        get() = ActivityMainBinding::inflate

    private lateinit var navController: NavController

    override fun setupActivityView() {
        navController = findNavController(R.id.nav_host_fragment_container)
        vb.navView.setupWithNavController(navController)
        FirebaseHelper.setUserOnline(vm.userId.toString())
        FirebaseHelper.addCallListener(vm.userId.toString()) { uid ->
            ReceivingCallActivity.startActivity(this, uid)
        }
    }

    companion object {
        fun startActivity(context: Context) {
            context.startActivity<MainActivity>()
        }
    }

}