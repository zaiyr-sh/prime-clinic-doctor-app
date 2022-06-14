package kg.iaau.diploma.primeclinicdoctor.ui.main.chat.calling

import android.Manifest
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.opengl.GLSurfaceView
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.opentok.android.*
import dagger.hilt.android.AndroidEntryPoint
import kg.iaau.diploma.core.utils.FirebaseHelper
import kg.iaau.diploma.core.utils.startActivity
import kg.iaau.diploma.core.utils.toast
import kg.iaau.diploma.primeclinicdoctor.R
import kg.iaau.diploma.primeclinicdoctor.databinding.ActivityVideoChatBinding

@AndroidEntryPoint
class VideoChatActivity : AppCompatActivity(), Session.SessionListener,
    PublisherKit.PublisherListener {

    private lateinit var vb: ActivityVideoChatBinding

    private var mp: MediaPlayer? = null

    private val refPath by lazy { intent.getStringExtra(REF)!! }
    private val username by lazy { intent.getStringExtra(USERNAME)!! }

    private lateinit var ref: DocumentReference
    private lateinit var mSession: Session
    private var mPublisher: Publisher? = null
    private var mSubscriber: Subscriber? = null

    private var requestPermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        val allPermitted = permissions.entries.all { it.value }
        if (allPermitted) {
            mSession = Session.Builder(this, API_KEY, SESSION_ID).build()
            mSession.setSessionListener(this)
            mSession.connect(TOKEN)
            playConnectingSound()
        } else finish()
    }

    private fun playConnectingSound() {
        mp = MediaPlayer.create(this, R.raw.connecting)
        mp?.isLooping = true
        mp?.start()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vb = ActivityVideoChatBinding.inflate(layoutInflater)
        setContentView(vb.root)
        setupActivityView()
    }

    private fun setupActivityView() {
        requestPermissions.launch(permissions)
        ref = FirebaseFirestore.getInstance().document(refPath)
        addSnapshotListener()
        vb.run {
            tvUsername.text = username
            givCancel.setOnClickListener {
                endCall()
            }
        }
    }

    private fun addSnapshotListener() {
        ref.addSnapshotListener { value, _ ->
            if (value != null && value.exists()) {
                val uid = value.getString("uid")
                if (uid.isNullOrEmpty()) {
                    endCall()
                }
            }
        }
    }

    private fun endCall() {
        val map = FirebaseHelper.getCallData("", "", accepted = false, declined = false)
        ref.set(map, SetOptions.merge()).addOnSuccessListener {
            goBack()
            ref.delete()
        }
    }

    private fun goBack() {
        mSubscriber?.destroy()
        mPublisher?.destroy()
        toast(getString(R.string.call_finished))
        finish()
    }

    override fun onConnected(p0: Session?) {
        Log.d("VideoChatActivity", "onConnected(): ")
        mPublisher = Publisher.Builder(this).build()
        mPublisher?.setPublisherListener(this)
        vb.flDoctor.addView(mPublisher?.view)
        if (mPublisher?.view is GLSurfaceView) {
            (mPublisher?.view as GLSurfaceView).setZOrderOnTop(true)
        }
        mSession.publish(mPublisher)
        mp?.stop()
    }

    override fun onDisconnected(p0: Session?) {
        mp?.stop()
        Log.d("VideoChatActivity", "onDisconnected(): ")
    }

    override fun onStreamReceived(p0: Session?, p1: Stream?) {
        Log.d("VideoChatActivity", "onStreamReceived(): ")
        if (mSubscriber == null) {
            mSubscriber = Subscriber.Builder(this, p1!!).build()
            mSession.subscribe(mSubscriber)
            vb.flContainer.addView(mSubscriber!!.view)
        }
    }

    override fun onStreamDropped(p0: Session?, p1: Stream?) {
        Log.d("VideoChatActivity", "onStreamDropped (): ")
        if (mSubscriber != null) {
            mSubscriber = null
            vb.flContainer.removeAllViews()
        }
    }

    override fun onError(p0: Session?, p1: OpentokError?) {
        Log.d("VideoChatActivity", "onError(): $p1")
    }

    override fun onStreamCreated(p0: PublisherKit?, p1: Stream?) {
        Log.d("VideoChatActivity", "onStreamCreated(): ")
    }

    override fun onStreamDestroyed(p0: PublisherKit?, p1: Stream?) {
        Log.d("VideoChatActivity", "onStreamDestroyed(): ")
    }

    override fun onError(p0: PublisherKit?, p1: OpentokError?) {
        Log.d("VideoChatActivity", "onError(): ")
    }

    override fun onBackPressed() {
        super.onBackPressed()
        endCall()
    }

    companion object {
        private const val API_KEY = "47510191"
        private var SESSION_ID =
            "2_MX40NzUxMDE5MX5-MTY1NDQwNTE4NTU5OX5Jam40UW9uRktFYXhlNjI1bFRDVEI0N1p-fg"
        private var TOKEN =
            "T1==cGFydG5lcl9pZD00NzUxMDE5MSZzaWc9YjAxMDcxODE4ZTM1M2IxZTg0MzdjNDUxMTNlMGNjZGMzZTBhYWFkYTpzZXNzaW9uX2lkPTJfTVg0ME56VXhNREU1TVg1LU1UWTFORFF3TlRFNE5UVTVPWDVKYW00MFVXOXVSa3RGWVhobE5qSTFiRlJEVkVJME4xcC1mZyZjcmVhdGVfdGltZT0xNjU0NDA1MjM2Jm5vbmNlPTAuOTAwMDM4MzE5Njc3NTAxNyZyb2xlPXB1Ymxpc2hlciZleHBpcmVfdGltZT0xNjU2OTk3MjM1JmluaXRpYWxfbGF5b3V0X2NsYXNzX2xpc3Q9"

        private val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA,
                Manifest.permission.BLUETOOTH_CONNECT,
            )
        } else {
            arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA,
            )
        }

        private const val REF = "ref"
        private const val USERNAME = "username"
        fun startActivity(context: Context, ref: String, username: String) {
            context.startActivity<VideoChatActivity> {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                putExtra(REF, ref)
                putExtra(USERNAME, username)
            }
        }
    }

}