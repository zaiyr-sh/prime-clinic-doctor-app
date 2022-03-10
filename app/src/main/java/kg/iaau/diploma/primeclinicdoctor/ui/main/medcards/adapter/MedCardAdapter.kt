package kg.iaau.diploma.primeclinicdoctor.ui.main.medcards.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kg.iaau.diploma.core.utils.formatDateForMedCard
import kg.iaau.diploma.data.Client
import kg.iaau.diploma.primeclinicdoctor.R
import kg.iaau.diploma.primeclinicdoctor.databinding.ListItemMedCardBinding

class MedCardAdapter : ListAdapter<Client, MedCardViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedCardViewHolder {
        return MedCardViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MedCardViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<Client> =
            object : DiffUtil.ItemCallback<Client>() {
                override fun areItemsTheSame(oldItem: Client, newItem: Client) =
                    oldItem == newItem

                override fun areContentsTheSame(oldItem: Client, newItem: Client) =
                    oldItem.id == newItem.id
            }
    }

}

class MedCardViewHolder(private val vb: ListItemMedCardBinding) : RecyclerView.ViewHolder(vb.root) {

    fun bind(client: Client) {
        vb.run {
            tvName.text = itemView.resources.getString(
                R.string.full_name,
                client.lastName ?: "",
                client.firstName ?: "",
                client.patronymic ?: ""
            )
            tvPhone.text = client.username ?: itemView.resources.getString(R.string.not_pointed)
            tvAge.text = client.birthDate.formatDateForMedCard() ?: itemView.resources.getString(R.string.not_pointed)
        }
    }

    companion object {
        fun from(parent: ViewGroup): MedCardViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val vb = ListItemMedCardBinding.inflate(layoutInflater, parent, false)
            return MedCardViewHolder(vb)
        }
    }
}