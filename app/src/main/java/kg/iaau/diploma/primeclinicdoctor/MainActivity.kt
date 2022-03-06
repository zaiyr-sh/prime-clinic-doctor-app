package kg.iaau.diploma.primeclinicdoctor

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import kg.iaau.diploma.core.utils.startActivity
import kg.iaau.diploma.primeclinicdoctor.databinding.ActivityMainBinding

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var vb: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vb = ActivityMainBinding.inflate(layoutInflater)
        setContentView(vb.root)
        setupNavController()
    }

    private fun setupNavController() {
        navController = findNavController(R.id.nav_host_fragment_container)
        vb.navView.setupWithNavController(navController)
    }

    companion object {
        fun startActivity(context: Context) {
            context.startActivity<MainActivity>()
        }
    }
}