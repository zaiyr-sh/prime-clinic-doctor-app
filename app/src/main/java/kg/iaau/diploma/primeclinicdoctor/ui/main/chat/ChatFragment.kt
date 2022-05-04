package kg.iaau.diploma.primeclinicdoctor.ui.main.chat

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import dagger.hilt.android.AndroidEntryPoint
import kg.iaau.diploma.core.constants.MIMETYPE_IMAGES
import kg.iaau.diploma.core.constants.MessageType
import kg.iaau.diploma.core.constants.UserType
import kg.iaau.diploma.core.ui.CoreFragment
import kg.iaau.diploma.core.utils.*
import kg.iaau.diploma.data.Message
import kg.iaau.diploma.primeclinicdoctor.R
import kg.iaau.diploma.primeclinicdoctor.databinding.FragmentChatBinding
import kg.iaau.diploma.primeclinicdoctor.ui.main.chat.adapter.MessageAdapter
import kg.iaau.diploma.primeclinicdoctor.ui.main.chat.adapter.MessageListener
import kg.iaau.diploma.primeclinicdoctor.ui.main.chat.calling.CallingActivity

@AndroidEntryPoint
class ChatFragment : CoreFragment<FragmentChatBinding, ChatVM>(ChatVM::class.java), MessageListener {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentChatBinding
        get() = FragmentChatBinding::inflate

    private lateinit var adapter: MessageAdapter

    private val args: ChatFragmentArgs by navArgs()
    private val ref by lazy { args.path }
    private val userType by lazy { args.type }
    private val username by lazy { args.username }

    private var docRef: DocumentReference? = null
    private lateinit var db: FirebaseFirestore

    private var messageType: String = MessageType.TEXT.type
    private var userId: String? = ""
    private var imgUri: Uri? = null

    private var pickImage: ActivityResultLauncher<String> = registerForActivityResult(
        ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            vb.run {
                ivAttach.setImageURI(uri)
                ivAttach.show()
                imgUri = uri
                messageType = MessageType.IMAGE.type
            }
        }
    }

    private var requestPickImagePermission: ActivityResultLauncher<String> = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it) pickImage.launch(MIMETYPE_IMAGES)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().toast(getString(R.string.chat_started))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.chat_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.video_call -> {
                if (userId != null && userType != UserType.ADMIN.name)
                    makeVideoCall()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun setupFragmentView() {
        showLoader()
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).setSupportActionBar(vb.toolbar)
        setupChatWithFirebase()
        vb.run {
            toolbar.setNavigationOnClickListener {
                parentFragmentManager.popBackStack()
            }
            rlAttachImage.setOnClickListener {
                requestPickImagePermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            ivSentMessage.setOnClickListener {
                if (messageType == MessageType.TEXT.type)
                    sendMessage()
                else
                    uploadPhotoToCloud()
            }
            etMessageTyping.requestFocus()
        }
    }

    private fun setupChatWithFirebase() {
        initFirebaseAuth()
        db = FirebaseFirestore.getInstance()
        docRef = ref.let { db.document(it) }
        FirebaseHelper.setupUserType(
            docRef,
            listener = { id ->
                userId = id
                when (userType) {
                    UserType.PATIENT.name ->  vb.toolbar.title = username
                    UserType.ADMIN.name -> setHasOptionsMenu(false)
                }
                setupChatMessages()
            }
        )
    }

    private fun sendMessage(image: String? = null) {
        vb.run {
            if (image.isNullOrEmpty() && etMessageTyping.text.toString().isEmpty()) return
            ivAttach.gone()
            val msg = etMessageTyping.text.toString()
            etMessageTyping.setText("")
            val user = mAuth.currentUser
            val message = Message(user?.uid, "", msg, Timestamp.now(), messageType, image)
            messageType = MessageType.TEXT.type
            docRef?.collection("messages")?.document()?.set(message)
                ?.addOnCompleteListener {
                    when(userType) {
                        UserType.PATIENT.name -> sendMessageToPatient(msg)
                        UserType.ADMIN.name -> sendMessageToAdmin(msg)
                    }
                }
        }
    }

    private fun sendMessageToPatient(msg: String) {
        val map = mutableMapOf<String, Any>()
        map["lastMessage"] = msg
        map["lastMessageSenderId"] = vm.userId.toString()
        map["lastMessageTime"] = Timestamp.now()
        docRef?.set(map, SetOptions.merge())
    }

    private fun sendMessageToAdmin(msg: String) {
        val map = mutableMapOf<String, Any>()
        map["adminId"] = "a"
        map["adminPhone"] = ""
        map["clientId"] = vm.userId.toString()
        map["lastMessage"] = msg
        map["lastMessageSenderId"] = vm.userId.toString()
        map["lastMessageTime"] = Timestamp.now()
        map["name"] = vm.phone ?: ""
        map["surname"] = "USER"
        docRef?.set(map, SetOptions.merge())
    }

    private fun setupChatMessages() {
        vb.run {
            rvChats.setHasFixedSize(true)
            val observer = object : RecyclerView.AdapterDataObserver() {
                override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                    super.onItemRangeChanged(positionStart, itemCount)
                    rvChats.scrollToPosition(positionStart)
                }
                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                    super.onItemRangeInserted(positionStart, itemCount)
                    rvChats.scrollToPosition(positionStart)
                }
            }
            FirebaseHelper.setupChat<Message>(docRef!!) { options ->
                adapter = MessageAdapter(options, this@ChatFragment)
                rvChats.adapter = adapter
                adapter.startListening()
                adapter.registerAdapterDataObserver(observer)
                goneLoader()
            }
            rvChats.scrollToLastItem()
        }
    }

    private fun uploadPhotoToCloud() {
        showLoader()
        FirebaseHelper.uploadPhoto(imgUri!!,
            onSuccess = { url ->
                sendMessage(url)
            },
            onDefault = {
                goneLoader()
            }
        )
    }

    private fun makeVideoCall() {
        CallingActivity.startActivity(requireActivity(), userId!!)
    }

    override fun onImageClick(image: String?) {
        findNavController().navigate(
            R.id.nav_image_full,
            Bundle().apply {
                putString(MessageType.IMAGE.type, image)
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        docRef = null
    }

}