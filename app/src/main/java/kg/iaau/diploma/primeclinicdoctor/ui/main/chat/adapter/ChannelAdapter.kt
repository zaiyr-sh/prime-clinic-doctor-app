package kg.iaau.diploma.primeclinicdoctor.ui.main.chat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import kg.iaau.diploma.core.utils.*
import kg.iaau.diploma.data.Chat
import kg.iaau.diploma.primeclinicdoctor.R
import kg.iaau.diploma.primeclinicdoctor.databinding.ListItemChannelBinding
import java.util.*

class ChannelAdapter(options: FirestoreRecyclerOptions<Chat>, private var listener: ChannelListener) :
    FirestoreRecyclerAdapter<Chat, ChannelViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelViewHolder {
        return ChannelViewHolder.from(parent, listener)
    }

    override fun onBindViewHolder(holder: ChannelViewHolder, position: Int, model: Chat) {
        val item = getItem(position)
        holder.bind(item)
    }

}

class ChannelViewHolder(private val vb: ListItemChannelBinding) : RecyclerView.ViewHolder(vb.root) {

    fun bind(chat: Chat) {
        vb.run {
            tvTime.text = chat.lastMessageTime?.toDate()?.formatForDate()
            tvMessage.text = when (chat.lastMessageSenderId) {
                chat.clientId -> itemView.context.getString(R.string.patient_message, getMessage(chat))
                chat.adminId -> itemView.context.getString(R.string.your_message, getMessage(chat))
                else -> ""
            }
            setupUser(chat.clientId)
            setupChannelEnabling(chat)
        }
    }

    private fun setupChannelEnabling(chat: Chat) {
        val chatStartedDate = chat.chatTime?.toDate()
        val currentDate = Date()
        when(chatStartedDate == null) {
            true -> vb.clContainer.setVisible(false)
            else -> vb.clContainer.setVisible(currentDate.remainFromInDays(chatStartedDate) == 0L)
        }
    }

    private fun setChatEnabled(isEnabled: Boolean) {
        vb.run {
            when (isEnabled) {
                true -> clContainer.setEnable(true)
                false -> {
                    tvMessage.text = itemView.context.getString(R.string.chat_not_available)
                    tvMessage.setTextColor(itemView.context.setColor(R.color.red))
                    clContainer.setEnable(false)
                }
            }
        }
    }

    private fun getMessage(chat: Chat): String? {
        return chat.lastMessage?.let { message ->
            if(message.length > 15) message.take(15) + "..."
            else message
        }
    }

    private fun setupUser(clientId: String?) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(clientId ?: "").get().addOnSuccessListener {
            val image = it.getString("image")
            val name = it.getString("name")
            val surname = it.getString("surname")
            val patronymic = it.getString("patronymic")
            vb.run {
                tvName.text = setupFullName(name, surname, patronymic)
                ivProfile.loadBase64Image(itemView.context, image, R.drawable.ic_patient)
            }
        }
    }

    private fun setupFullName(name: String?, surname: String?, patronymic: String?): String {
        return when(name.isFullyEmpty() || surname.isFullyEmpty()) {
            true -> itemView.context.getString(R.string.absent_full_name)
            else -> itemView.context.getString(R.string.full_name, surname, name, patronymic ?: "")
        }
    }

    companion object {
        fun from(parent: ViewGroup, listener: ChannelListener): ChannelViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val vb = ListItemChannelBinding.inflate(layoutInflater, parent, false)
            return ChannelViewHolder(vb).apply {
                itemView.setOnClickListener {
                    listener.onChannelClick(bindingAdapterPosition)
                }
            }
        }
    }
}

interface ChannelListener {
    fun onChannelClick(position: Int)
}