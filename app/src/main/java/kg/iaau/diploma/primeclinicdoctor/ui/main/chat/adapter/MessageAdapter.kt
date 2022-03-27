package kg.iaau.diploma.primeclinicdoctor.ui.main.chat.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import kg.iaau.diploma.core.utils.formatForDate
import kg.iaau.diploma.core.utils.gone
import kg.iaau.diploma.core.utils.show
import kg.iaau.diploma.data.Message
import kg.iaau.diploma.primeclinicdoctor.R
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
            tvTime.text = message.time?.toDate()?.formatForDate()
            tvMessage.text = message.message
            when(message.type) {
                "text" -> {
                    cvImage.gone()
                }
                else -> {
                    Glide.with(itemView).load(message.image)
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                progressBar.gone()
                                cvImage.show()
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                progressBar.gone()
                                cvImage.show()
                                return false
                            }

                        })
                        .error(R.drawable.ic_error)
                        .into(ivSent)
                }
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
            tvTime.text = message.time?.toDate()?.formatForDate()
            tvReceived.text = message.message
            when(message.type) {
                "text" -> {
                    ivReceived.gone()
                }
                else -> {
                    Glide.with(itemView).load(message.image).into(ivReceived)
                    ivReceived.show()
                }
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