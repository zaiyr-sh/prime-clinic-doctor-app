package kg.iaau.diploma.primeclinicdoctor

import android.content.Context
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import dagger.hilt.android.AndroidEntryPoint
import kg.iaau.diploma.core.utils.startActivity
import kg.iaau.diploma.primeclinicdoctor.databinding.ActivityMainBinding
import kg.iaau.diploma.primeclinicdoctor.ui.main.chat.ChatVM
import kg.iaau.diploma.primeclinicdoctor.ui.main.chat.calling.ReceivingCallActivity

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var vb: ActivityMainBinding
    private val vm: ChatVM by viewModels()

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

    override fun onStart() {
        super.onStart()
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            setUserOnline(user)
            addCallListener(user)
        }
    }

    private fun addCallListener(user: FirebaseUser) {
        val ref = FirebaseFirestore.getInstance().collection("doctors").document(vm.userId.toString())
            .collection("call").document("calling")
        ref.addSnapshotListener { value, _ ->
            if (value != null && value.exists()) {
                val uid = value.getString("uid")
                if (uid != null && uid != "") ReceivingCallActivity.startActivity(this, uid)
            }
        }
    }

    private fun setUserOnline(user: FirebaseUser) {
        val db = FirebaseFirestore.getInstance()
        val map = mutableMapOf<String, Any>()
        map["isOnline"] = true
        db.collection("users").document(vm.userId.toString()).set(map, SetOptions.merge())
    }

    companion object {
        fun startActivity(context: Context) {
            context.startActivity<MainActivity>()
        }
    }
}