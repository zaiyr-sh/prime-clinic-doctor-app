package kg.iaau.diploma.primeclinicdoctor.ui.main.chat

import android.Manifest
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.AndroidEntryPoint
import kg.iaau.diploma.core.constants.MIMETYPE_IMAGES
import kg.iaau.diploma.core.constants.UserType
import kg.iaau.diploma.core.utils.gone
import kg.iaau.diploma.core.utils.show
import kg.iaau.diploma.core.utils.toast
import kg.iaau.diploma.data.Message
import kg.iaau.diploma.primeclinicdoctor.MainActivity
import kg.iaau.diploma.primeclinicdoctor.databinding.FragmentChatBinding
import kg.iaau.diploma.primeclinicdoctor.ui.main.chat.adapter.MessageAdapter
import kg.iaau.diploma.primeclinicdoctor.ui.main.chat.adapter.MessageListener
import kg.iaau.diploma.primeclinicdoctor.R
import java.util.*

@AndroidEntryPoint
class ChatFragment : Fragment(), MessageListener {

    private lateinit var vb: FragmentChatBinding
    private lateinit var adapter: MessageAdapter
    private val vm: ChatVM by navGraphViewModels(R.id.main_navigation) { defaultViewModelProviderFactory }

    private val args: ChatFragmentArgs by navArgs()
    private val ref by lazy { args.path }
    private val type by lazy { args.type }

    private var docRef: DocumentReference? = null
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private var canWrite: Boolean = false
    private var messageType: String = "text"
    private var userId: String? = ""
    private var image: String = ""
    private var imgUri: Uri? = null

    private var pickImage: ActivityResultLauncher<String> = registerForActivityResult(
        ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            vb.run {
                ivAttach.setImageURI(uri)
                ivAttach.show()
                imgUri = uri
                messageType = "image"
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        vb = FragmentChatBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        (requireActivity() as? MainActivity)?.setSupportActionBar(vb.toolbar)
        return vb.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.video_call -> {
                if (userId != null && canWrite && type != UserType.ADMIN.name)
                    makeVideoCall()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        docRef = ref.let { db.document(it) }

        initUser()

        val listener = docRef?.addSnapshotListener { snapshot, _ ->
            if (snapshot?.getBoolean("chatStarted") == true) canWrite = true
        }
        setupFragmentView(listener)
    }

    private fun initUser() {
        docRef?.get()?.addOnSuccessListener {
            val adminId = it.getString("adminId")
            userId = adminId
            when (type) {
                UserType.DOCTOR.name -> getDoctorData(adminId)
                UserType.ADMIN.name -> setHasOptionsMenu(false)
            }
        }
    }

    private fun getDoctorData(adminId: String?) {
        vb.run {
            val db = FirebaseFirestore.getInstance()
            db.collection("doctors").document(adminId!!).get().addOnSuccessListener {
                val image = it.getString("image")
                val name = it.getString("name")
                val fatherName = it.getString("fatherName")
                image?.let { icon ->
                    Glide.with(requireActivity())
                        .asBitmap()
                        .load(icon)
                        .into(object : CustomTarget<Bitmap>(){
                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {}
                            override fun onLoadCleared(placeholder: Drawable?) {
                                toolbar.logo = placeholder
                            }
                        })
                }
                toolbar.title = "$name $fatherName"
            }
        }
    }

    private fun setupFragmentView(listener: ListenerRegistration?) {
        vb.run {
            toolbar.setNavigationOnClickListener {
                if (canWrite) listener?.remove()
                parentFragmentManager.popBackStack()
            }
            rlAttachImage.setOnClickListener {
                requestPickImagePermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            ivSentMessage.setOnClickListener {
                if (etMessageTyping.text.toString().isNotEmpty()) {
                    if (messageType == "text")
                        sendMessage()
                    else
                        uploadPhotoToCloud()
                }
            }
            etMessageTyping.requestFocus()
            val user = firebaseAuth.currentUser
            if (user != null)
                setupRV()
        }
    }

    private fun sendMessage() {
        vb.run {
            ivAttach.gone()
            val message = etMessageTyping.text.toString()
            etMessageTyping.setText("")
            val user = firebaseAuth.currentUser!!
            val model = Message(user.uid, "", message, Timestamp.now(), messageType, image)
            messageType = "text"
            docRef?.collection("messages")?.document()?.set(model)
                ?.addOnCompleteListener {
                    val map = mutableMapOf<String, Any>()
                    map["adminId"] = "a"
                    map["adminPhone"] = ""
                    map["chatStarted"] = true
                    map["clientId"] = FirebaseAuth.getInstance().currentUser?.uid.toString()
                    map["lastMessage"] = message
                    map["lastMessageSenderId"] = user.uid
                    map["lastMessageTime"] = Timestamp.now()
                    map["name"] = vm.phone ?: ""
                    map["surname"] = "USER"
                    docRef?.set(map, SetOptions.merge())
                }
        }
    }

    private fun setupRV() {
        vb.run {
            rvChats.setHasFixedSize(true)
            val query = docRef!!.collection("messages").orderBy("time", Query.Direction.ASCENDING)
            val options: FirestoreRecyclerOptions<Message> =
                FirestoreRecyclerOptions.Builder<Message>().setQuery(query, Message::class.java)
                    .build()
            val observer = object : RecyclerView.AdapterDataObserver() {
                override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                    super.onItemRangeChanged(positionStart, itemCount)
                    rvChats.smoothScrollToPosition(positionStart)
                    adapter.notifyDataSetChanged()
                }
                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                    super.onItemRangeInserted(positionStart, itemCount)
                    rvChats.smoothScrollToPosition(positionStart)
                    adapter.notifyDataSetChanged()
                }
            }
            docRef!!.collection("messages").addSnapshotListener { _, _ ->
                adapter = MessageAdapter(options, this@ChatFragment)
                rvChats.adapter = adapter
                adapter.startListening()
                adapter.registerAdapterDataObserver(observer)
            }
        }
    }

    private fun uploadPhotoToCloud() {
        vb.progressBar.show()
        val user = firebaseAuth.currentUser
        if (user != null) {
            val ref = FirebaseStorage.getInstance().getReference("images/" + Date().time + ".jpg")
            ref.putFile(imgUri!!).addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    val url = it.toString()
                    if (url.isNotEmpty()) {
                        image = url
                        sendMessage()
                    }
                    vb.progressBar.gone()
                }
            }
        }
    }

    private fun makeVideoCall() {
        TODO("VIDEO CALL IMPLEMENTATION")
    }

    override fun onImageClick(image: String?) {
        findNavController().navigate(
            R.id.nav_image_full,
            Bundle().apply {
                putString("image", image)
            }
        )
    }

}