package kg.iaau.diploma.primeclinicdoctor.ui.main.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.DocumentSnapshot
import dagger.hilt.android.AndroidEntryPoint
import kg.iaau.diploma.core.constants.UserType
import kg.iaau.diploma.core.ui.CoreFragment
import kg.iaau.diploma.core.ui.LoadingScreen
import kg.iaau.diploma.core.utils.FirebaseHelper
import kg.iaau.diploma.core.utils.formatForDate
import kg.iaau.diploma.core.utils.toast
import kg.iaau.diploma.data.Chat
import kg.iaau.diploma.primeclinicdoctor.R
import kg.iaau.diploma.primeclinicdoctor.databinding.FragmentChannelsBinding
import kg.iaau.diploma.primeclinicdoctor.ui.main.chat.adapter.ChannelAdapter
import kg.iaau.diploma.primeclinicdoctor.ui.main.chat.adapter.ChannelListener

@AndroidEntryPoint
class ChannelsFragment : CoreFragment<FragmentChannelsBinding, ChatVM>(ChatVM::class.java), ChannelListener {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentChannelsBinding
        get() = FragmentChannelsBinding::inflate

    private lateinit var adapter: ChannelAdapter

    override fun setupFragmentView() {
        setupAdminChatListener()
        setupChannels()
    }

    private fun setupAdminChatListener() {
        showLoader()
        FirebaseHelper.addAdminChatListener(
            vm.userId.toString(),
            onSuccess = { doc -> setupAdminChat(vm.userId.toString(), doc) },
            onFail = {
                requireActivity().toast(getString(R.string.unexpected_error))
                goneLoader()
            }
        )
    }

    private fun setupAdminChat(uid: String, doc: DocumentSnapshot) {
        vb.run {
            doc.getString("lastMessage")?.let { lastMessage ->
                tvMessage.text = if (doc.getString("lastMessageSenderId") == uid)
                    getString(R.string.your_message, lastMessage)
                else
                    getString(R.string.admin_message, lastMessage)
            }
            doc.getTimestamp("lastMessageTime")?.let { time ->
                tvTime.text = time.toDate().formatForDate()
            }
            LoadingScreen.hideLoading()
            clAdminChat.setOnClickListener {
                navigateToChat(
                    doc.reference.path,
                    UserType.ADMIN.name,
                    null,
                    getString(R.string.administrator)
                )
            }
        }
    }

    private fun navigateToChat(path: String, type: String, phone: String?, fullName: String?) {
        findNavController().navigate(
            R.id.nav_chat,
            Bundle().apply {
                putString("path", path)
                putString("type", type)
                putString("phone", phone ?: getString(R.string.absent_phone_number))
                putString("username", fullName)
            }
        )
    }

    private fun setupChannels() {
        val channels = FirebaseHelper.createChannels<Chat>(vm.userId.toString())
        adapter = ChannelAdapter(channels, this)
        vb.rvChannels.adapter = adapter
        adapter.startListening()
    }

    override fun onChannelClick(position: Int, phone: String?, fullName: String?) {
        val ref = adapter.snapshots.getSnapshot(position).reference.path
        navigateToChat(ref, UserType.PATIENT.name, phone, fullName)
    }

}