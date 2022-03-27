package kg.iaau.diploma.primeclinicdoctor.ui.main.chat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import kg.iaau.diploma.core.utils.formatForDate
import kg.iaau.diploma.data.Chat
import kg.iaau.diploma.primeclinicdoctor.R
import kg.iaau.diploma.primeclinicdoctor.databinding.ListItemChannelBinding

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
                chat.adminId -> itemView.context.getString(R.string.doctor_message, getMessage(chat))
                chat.clientId -> itemView.context.getString(R.string.your_message, getMessage(chat))
                else -> ""
            }
            setFullName(chat.adminId)
        }
    }

    private fun getMessage(chat: Chat): String? {
        return chat.lastMessage?.let { message ->
            if(message.length > 15) message.take(15) + "..."
            else message
        }
    }

    private fun setFullName(adminId: String?) {
        val db = FirebaseFirestore.getInstance()
        vb.run {
            adminId?.let { id ->
                db.collection("doctors").document(id).get().addOnSuccessListener {
                    val image = it.getString("image")
                    val name = it.getString("name")
                    val fatherName = it.getString("fatherName")
                    if (!image.isNullOrEmpty())
                        Glide.with(itemView).load(image).into(ivProfile)
                    val fullName = "$name $fatherName"
                    tvName.text = fullName
                }
            }
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