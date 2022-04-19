package kg.iaau.diploma.primeclinicdoctor.ui.main.chat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import kg.iaau.diploma.core.constants.MessageType
import kg.iaau.diploma.core.utils.formatForDate
import kg.iaau.diploma.core.utils.gone
import kg.iaau.diploma.core.utils.loadWithFresco
import kg.iaau.diploma.core.utils.show
import kg.iaau.diploma.data.Message
import kg.iaau.diploma.primeclinicdoctor.databinding.ListItemReceivedMessageBinding
import kg.iaau.diploma.primeclinicdoctor.databinding.ListItemSentMessageBinding

class MessageAdapter(options: FirestoreRecyclerOptions<Message>, private var listener: MessageListener) :
    FirestoreRecyclerAdapter<Message, RecyclerView.ViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if(viewType == ITEM_RECEIVE) {
            ReceivedVH.from(parent, listener)
        } else {
            SentVH.from(parent, listener)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, model: Message) {
        when(holder.javaClass) {
            SentVH::class.java -> (holder as SentVH).bind(model)
            ReceivedVH::class.java -> (holder as ReceivedVH).bind(model)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (FirebaseAuth.getInstance().currentUser?.uid == getItem(position).sender)
            ITEM_SENT
        else
            ITEM_RECEIVE
    }

    companion object {
        const val ITEM_RECEIVE = 0
        const val ITEM_SENT = 1
    }

}

class SentVH(private val vb: ListItemSentMessageBinding): RecyclerView.ViewHolder(vb.root) {

    private lateinit var message: Message

    fun bind(message: Message) {
        this.message = message
        vb.run {
            cvImage.show()
            tvMessage.show()
            tvTime.text = message.time?.toDate()?.formatForDate()
            if (message.message.isNullOrEmpty())
                tvMessage.gone()
            else
                tvMessage.text = message.message
            when(message.type) {
                MessageType.TEXT.type -> cvImage.gone()
                else -> ivSent.loadWithFresco(
                    message.image,
                    onSuccess = { progressBar.gone() },
                    onFail = { progressBar.gone() }
                )
            }
        }
    }
    companion object {
        fun from(parent: ViewGroup, listener: MessageListener): SentVH {
            val layoutInflater = LayoutInflater.from(parent.context)
            val vb = ListItemSentMessageBinding.inflate(layoutInflater, parent, false)
            return SentVH(vb).apply {
                vb.ivSent.setOnClickListener {
                    listener.onImageClick(message.image)
                }
            }
        }
    }

}

class ReceivedVH(private val vb: ListItemReceivedMessageBinding): RecyclerView.ViewHolder(vb.root) {

    private lateinit var message: Message

    fun bind(message: Message) {
        this.message = message
        vb.run {
            cvImage.show()
            tvReceived.show()
            tvTime.text = message.time?.toDate()?.formatForDate()
            if (message.message.isNullOrEmpty())
                tvReceived.gone()
            else
                tvReceived.text = message.message
            when(message.type) {
                MessageType.TEXT.type -> cvImage.gone()
                else -> ivReceived.loadWithFresco(
                    message.image,
                    onSuccess = { progressBar.gone() },
                    onFail = { progressBar.gone() }
                )
            }
        }
    }

    companion object {
        fun from(parent: ViewGroup, listener: MessageListener): ReceivedVH {
            val layoutInflater = LayoutInflater.from(parent.context)
            val vb = ListItemReceivedMessageBinding.inflate(layoutInflater, parent, false)
            return ReceivedVH(vb).apply {
                vb.ivReceived.setOnClickListener {
                    listener.onImageClick(message.image)
                }
            }
        }
    }

}

interface MessageListener {
    fun onImageClick(image: String?)
}