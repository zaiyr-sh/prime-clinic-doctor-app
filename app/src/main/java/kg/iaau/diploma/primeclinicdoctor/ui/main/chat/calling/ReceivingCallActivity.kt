package kg.iaau.diploma.primeclinicdoctor.ui.main.chat.calling

import android.content.Context
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import dagger.hilt.android.AndroidEntryPoint
import kg.iaau.diploma.core.utils.startActivity
import kg.iaau.diploma.core.utils.toast
import kg.iaau.diploma.primeclinicdoctor.R
import kg.iaau.diploma.primeclinicdoctor.databinding.ActivityReceivingCallBinding
import kg.iaau.diploma.primeclinicdoctor.ui.main.chat.ChatVM

@AndroidEntryPoint
class ReceivingCallActivity : AppCompatActivity() {

    private lateinit var vb: ActivityReceivingCallBinding
    private val userUid by lazy { intent.getStringExtra(USER_UID)!! }
    private val vm: ChatVM by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vb = ActivityReceivingCallBinding.inflate(layoutInflater)
        setContentView(vb.root)
        setupActivityView()
        setupActivityViewListeners()
    }

    private fun setupActivityView() {
        vb.run {
            val ref = FirebaseFirestore.getInstance().collection("users").document(userUid)
            ref.get().addOnSuccessListener {
                tvUsername.text = it.getString("userPhone")
            }
        }
    }

    private fun setupActivityViewListeners() {
        val ref = FirebaseFirestore.getInstance().collection("doctors").document(vm.userId.toString())
            .collection("call").document("calling")
        vb.run {
            givAccept.setOnClickListener {
                val map = mutableMapOf<String, Boolean>()
                map["accepted"] = true
                ref.set(map, SetOptions.merge()).addOnSuccessListener {
                    VideoChatActivity.startActivity(this@ReceivingCallActivity, ref.path, tvUsername.text.toString())
                    finish()
                }
            }
            givCancel.setOnClickListener {
                val map = mutableMapOf<String, Any>()
                map["accepted"] = false
                map["declined"] = true
                map["uid"] = ""
                map["receiverId"] = ""
                ref.set(map, SetOptions.merge()).addOnSuccessListener {
                    finish()
                }
            }
        }
        addCallListener()
    }

    private fun addCallListener() {
        val ref = FirebaseFirestore.getInstance().collection("doctors").document(vm.userId.toString())
            .collection("call").document("calling")
        ref.addSnapshotListener { value, _ ->
            if (value?.getBoolean("declined") == true) {
                toast(getString(R.string.call_rejected))
                finish()
            }
        }
    }

    companion object {
        private const val USER_UID = "UID"
        fun startActivity(context: Context, userUid: String) {
            context.startActivity<ReceivingCallActivity> {
                putExtra(USER_UID, userUid)
            }
        }
    }

}