package kg.iaau.diploma.primeclinicdoctor.ui.main.chat.calling

import android.content.Context
import android.media.MediaPlayer
import android.view.LayoutInflater
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import dagger.hilt.android.AndroidEntryPoint
import kg.iaau.diploma.core.ui.CoreActivity
import kg.iaau.diploma.core.utils.FirebaseHelper
import kg.iaau.diploma.core.utils.loadBase64Image
import kg.iaau.diploma.core.utils.startActivity
import kg.iaau.diploma.core.utils.toast
import kg.iaau.diploma.primeclinicdoctor.R
import kg.iaau.diploma.primeclinicdoctor.databinding.ActivityCallingBinding
import kg.iaau.diploma.primeclinicdoctor.ui.main.chat.ChatVM

@AndroidEntryPoint
class CallingActivity : CoreActivity<ActivityCallingBinding, ChatVM>(ChatVM::class.java) {

    override val bindingInflater: (LayoutInflater) -> ActivityCallingBinding
        get() = ActivityCallingBinding::inflate

    private lateinit var mp: MediaPlayer
    private lateinit var ref: DocumentReference

    private val userId by lazy { intent.getStringExtra(USER_ID)!! }
    private var listener: ListenerRegistration? = null

    override fun setupActivityView() {
        playPhoneBeepSound()
        vb.run {
            FirebaseHelper.setupPatientData(userId) {
                val image = it.getString("image")
                val name = it.getString("name")
                val surname = it.getString("surname")
                ivUser.loadBase64Image(this@CallingActivity, image, R.drawable.ic_patient)
                tvUsername.text = getString(R.string.name_with_patronymic, name, surname)
            }
        }
        makePhoneCall()
    }

    private fun playPhoneBeepSound() {
        mp = MediaPlayer.create(this, R.raw.phone_beep)
        mp.isLooping = true
        mp.start()
    }

    private fun makePhoneCall() {
        FirebaseHelper.makeCall(userId,
            onSuccess = { ref ->
                this.ref = ref
                val callData = FirebaseHelper.getCallData(vm.userId.toString(), userId, accepted = false, declined = false)
                ref.set(callData, SetOptions.merge()).addOnSuccessListener {
                    addSnapListener()
                    setEndCall()
                }
            },
            onFail = {
                toast(getString(R.string.patient_not_available))
                mp.stop()
                finish()
            }
        )
    }

    private fun setEndCall() {
        vb.givCancel.setOnClickListener {
            endCall()
        }
    }

    private fun addSnapListener() {
        listener = FirebaseHelper.addCallAcceptanceListener(
            ref,
            onSuccess = {
                toast(getString(R.string.call_accepted))
                mp.stop()
                listener?.remove()
                finish()
                VideoChatActivity.startActivity(this, ref.path, vb.tvUsername.text.toString())
            },
            onFail = {
                toast(getString(R.string.call_rejected))
                mp.stop()
                listener?.remove()
                finish()
            }
        )
    }

    private fun endCall() {
        val callData = FirebaseHelper.getCallData("", "", accepted = false, declined = true)
        ref.set(callData, SetOptions.merge()).addOnSuccessListener {
            toast(getString(R.string.call_finished))
            ref.delete()
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        endCall()
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