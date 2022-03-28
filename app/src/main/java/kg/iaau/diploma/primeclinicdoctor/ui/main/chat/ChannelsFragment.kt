package kg.iaau.diploma.primeclinicdoctor.ui.main.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.AndroidEntryPoint
import kg.iaau.diploma.core.constants.UserType
import kg.iaau.diploma.core.utils.formatForDate
import kg.iaau.diploma.core.utils.gone
import kg.iaau.diploma.core.utils.show
import kg.iaau.diploma.core.utils.toast
import kg.iaau.diploma.data.Chat
import kg.iaau.diploma.primeclinicdoctor.R
import kg.iaau.diploma.primeclinicdoctor.databinding.FragmentChannelsBinding
import kg.iaau.diploma.primeclinicdoctor.ui.main.chat.adapter.ChannelAdapter
import kg.iaau.diploma.primeclinicdoctor.ui.main.chat.adapter.ChannelListener

@AndroidEntryPoint
class ChannelsFragment : Fragment(), ChannelListener {

    private lateinit var vb: FragmentChannelsBinding
    private lateinit var adapter: ChannelAdapter
    private val vm: ChatVM by navGraphViewModels(R.id.main_navigation) { defaultViewModelProviderFactory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        vb = FragmentChannelsBinding.inflate(inflater, container, false)
        return vb.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupFragmentView()
    }

    private fun setupFragmentView() {
        FirebaseAuth.getInstance().currentUser?.let { initAdminChat() }
        initRV()
    }

    private fun initAdminChat() {
        vb.run {
            progressBar.show()
            val db = FirebaseFirestore.getInstance()
            val ref = db.collection("chatAdmin").document(vm.userId.toString())
            ref.get().addOnSuccessListener {
                it.getString("lastMessage")?.let { lastMessage ->
                    tvMessage.text = if (it.getString("lastMessageSenderId") == vm.userId.toString())
                        getString(R.string.your_message, lastMessage)
                    else
                        getString(R.string.admin_message, lastMessage)
                }
                it.getTimestamp("lastMessageTime")?.let { time ->
                    tvTime.text = time.toDate().formatForDate()
                }
                progressBar.gone()
                clAdminChat.setOnClickListener { _ ->
                    navigateToChat(it.reference.path, UserType.ADMIN.name)
                }
            }.addOnFailureListener {
                requireActivity().toast(getString(R.string.unexpected_error))
            }
        }
    }

    private fun initRV() {
        val db = FirebaseFirestore.getInstance()
        val query = db.collection("PrimeDocChat").whereEqualTo("adminId", vm.userId.toString())
            .orderBy("lastMessageTime", Query.Direction.DESCENDING)
        val options: FirestoreRecyclerOptions<Chat> =
            FirestoreRecyclerOptions.Builder<Chat>().setQuery(query, Chat::class.java).build()
        adapter = ChannelAdapter(options, this)
        vb.rvChannels.adapter = adapter
        adapter.startListening()
    }

    private fun navigateToChat(path: String, type: String) {
        findNavController().navigate(
            R.id.nav_chat,
            Bundle().apply {
                putString("path", path)
                putString("type", type)
            }
        )
    }

    override fun onChannelClick(position: Int, chatUserPhone: String?) {
        vm.setChatUserPhone(chatUserPhone)
        val ref = adapter.snapshots.getSnapshot(position).reference.path
        navigateToChat(ref, UserType.PATIENT.name)
    }

}