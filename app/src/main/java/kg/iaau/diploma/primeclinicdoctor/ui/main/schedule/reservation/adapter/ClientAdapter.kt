package kg.iaau.diploma.primeclinicdoctor.ui.main.schedule.reservation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kg.iaau.diploma.data.Slot
import kg.iaau.diploma.primeclinicdoctor.R
import kg.iaau.diploma.primeclinicdoctor.databinding.ListItemClientBinding

class ClientAdapter : ListAdapter<Slot, ClientViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientViewHolder {
        return ClientViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ClientViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(position + 1, item)
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<Slot> =
            object : DiffUtil.ItemCallback<Slot>() {
                override fun areItemsTheSame(oldItem: Slot, newItem: Slot) =
                    oldItem == newItem

                override fun areContentsTheSame(oldItem: Slot, newItem: Slot) =
                    oldItem.id == newItem.id
            }
    }

}

class ClientViewHolder(private val vb: ListItemClientBinding) : RecyclerView.ViewHolder(vb.root) {

    fun bind(number: Int, slot: Slot) {
        vb.run {
            tvName.text = itemView.resources.getString(R.string.client_number, number.toString())
            tvNumber.text = slot.phoneNumber
            tvTime.text = slot.start?.substring(0, 5)
        }
    }

    companion object {
        fun from(parent: ViewGroup): ClientViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val vb = ListItemClientBinding.inflate(layoutInflater, parent, false)
            return ClientViewHolder(vb)
        }
    }
}