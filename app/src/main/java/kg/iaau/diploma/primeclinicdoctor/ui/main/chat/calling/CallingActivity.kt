package kg.iaau.diploma.primeclinicdoctor.ui.main.chat.calling

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.AndroidEntryPoint
import kg.iaau.diploma.core.utils.startActivity
import kg.iaau.diploma.core.utils.toast
import kg.iaau.diploma.primeclinicdoctor.R
import kg.iaau.diploma.primeclinicdoctor.databinding.ActivityCallingBinding
import kg.iaau.diploma.primeclinicdoctor.ui.main.chat.ChatVM

@AndroidEntryPoint
class CallingActivity : AppCompatActivity() {

    private lateinit var vb: ActivityCallingBinding
    private val userId by lazy { intent.getStringExtra(USER_ID)!! }
    private var listener: ListenerRegistration? = null
    private val vm: ChatVM by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vb = ActivityCallingBinding.inflate(layoutInflater)
        setContentView(vb.root)
        setupActivityView()
    }

    private fun setupActivityView() {
        vb.run {
            val db = FirebaseFirestore.getInstance()
            db.collection("users").document(userId).get().addOnSuccessListener {
                tvUsername.text = it.getString("userPhone")
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null)
            makePhoneCall()
    }

    private fun getCallData(
        uid: String,
        receiverId: String,
        accepted: Boolean,
        declined: Boolean
    ): MutableMap<String, Any> {
        val callData = mutableMapOf<String, Any>()
        callData["uid"] = uid
        callData["receiverId"] = receiverId
        callData["accepted"] = accepted
        callData["declined"] = declined
        return callData
    }

    private fun makePhoneCall() {
        val db = FirebaseFirestore.getInstance()
        val ref =
            db.collection("users").document(userId).collection("call").document("calling")
        ref.get().addOnSuccessListener {
            when(it.exists() && !it.getString("uid").isNullOrEmpty()) {
                true -> {
                    toast(getString(R.string.patient_not_available))
                    setEndCall(ref)
                }
                else -> {
                    val callData = getCallData(vm.userId.toString(), userId, accepted = false, declined = false)
                    ref.set(callData).addOnSuccessListener {
                        addSnapListener(ref)
                        setEndCall(ref)
                    }
                }
            }
        }
    }

    private fun setEndCall(ref: DocumentReference) {
        vb.givCancel.setOnClickListener {
            val callData = getCallData("", "", accepted = false, declined = true)
            ref.set(callData).addOnSuccessListener {
                toast(getString(R.string.call_finished))
                finish()
            }
        }
    }

    private fun addSnapListener(ref: DocumentReference) {
        listener = ref.addSnapshotListener { value, _ ->
            if (value != null && value.exists()) {
                val accepted = value.getBoolean("accepted")
                val declined = value.getBoolean("declined")
                if (accepted == true) {
                    toast(getString(R.string.call_accepted))
                    VideoChatActivity.startActivity(this, ref.path, vb.tvUsername.text.toString())
                    finish()
                }
                if (declined != null && declined == true) {
                    toast(getString(R.string.call_rejected))
                    finish()
                }

            }

        }
    }

    companion object {
        private const val USER_ID = "USER_ID"
        fun startActivity(context: Context, userId: String) {
            context.startActivity<CallingActivity> {
                putExtra(USER_ID, userId)
            }
        }
    }

}