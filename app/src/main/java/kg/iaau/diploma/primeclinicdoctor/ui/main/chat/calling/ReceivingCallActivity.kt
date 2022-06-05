package kg.iaau.diploma.primeclinicdoctor.ui.main.chat.calling

import android.content.Context
import android.media.MediaPlayer
import android.view.LayoutInflater
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import dagger.hilt.android.AndroidEntryPoint
import kg.iaau.diploma.core.ui.CoreActivity
import kg.iaau.diploma.core.utils.FirebaseHelper
import kg.iaau.diploma.core.utils.loadBase64Image
import kg.iaau.diploma.core.utils.startActivity
import kg.iaau.diploma.core.utils.toast
import kg.iaau.diploma.primeclinicdoctor.R
import kg.iaau.diploma.primeclinicdoctor.databinding.ActivityReceivingCallBinding
import kg.iaau.diploma.primeclinicdoctor.ui.main.chat.ChatVM

@AndroidEntryPoint
class ReceivingCallActivity : CoreActivity<ActivityReceivingCallBinding, ChatVM>(ChatVM::class.java) {

    override val bindingInflater: (LayoutInflater) -> ActivityReceivingCallBinding
        get() = ActivityReceivingCallBinding::inflate

    private lateinit var mp: MediaPlayer
    private lateinit var ref: DocumentReference

    private val userUid by lazy { intent.getStringExtra(USER_UID)!! }

    override fun setupActivityView() {
        playCallingSound()
        vb.run {
            FirebaseHelper.setupPatientData(userUid) {
                val image = it.getString("image")
                val name = it.getString("name")
                val surname = it.getString("surname")
                ivUser.loadBase64Image(this@ReceivingCallActivity, image, R.drawable.ic_patient)
                tvUsername.text = getString(R.string.name_with_patronymic, name, surname)
            }
        }
        setupActivityViewListeners()
    }

    private fun playCallingSound() {
        mp = MediaPlayer.create(this, R.raw.calling)
        mp.isLooping = true
        mp.start()
    }

    private fun setupActivityViewListeners() {
        ref = FirebaseFirestore.getInstance().collection("doctors").document(vm.userId.toString())
            .collection("call").document("calling")
        vb.run {
            givAccept.setOnClickListener {
                val map = mutableMapOf<String, Boolean>().apply {
                    this["accepted"] = true
                }
                ref.set(map, SetOptions.merge()).addOnSuccessListener {
                    mp.stop()
                    finish()
                    VideoChatActivity.startActivity(this@ReceivingCallActivity, ref.path, tvUsername.text.toString())
                }
            }
            givCancel.setOnClickListener {
                endCall()
            }
        }
        addCallListener()
    }

    private fun endCall() {
        val map = FirebaseHelper.getCallData("", "", accepted = false, declined = true)
        ref.set(map, SetOptions.merge()).addOnSuccessListener {
            mp.stop()
            ref.delete()
            finish()
        }
    }

    private fun addCallListener() {
        val ref = FirebaseFirestore.getInstance().collection("doctors").document(vm.userId.toString())
            .collection("call").document("calling")
        ref.addSnapshotListener { value, _ ->
            if (value?.getBoolean("declined") == true) {
                toast(getString(R.string.call_rejected))
                mp.stop()
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