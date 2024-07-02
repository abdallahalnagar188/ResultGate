package eramo.resultgate.presentation.adapters.recycleview.vertical

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import eramo.resultgate.data.remote.dto.products.orders.CustomerPromoCodes
import eramo.resultgate.databinding.ItemTextBinding
import javax.inject.Inject

class TextAdapter @Inject constructor() :
    ListAdapter<CustomerPromoCodes, TextAdapter.ProductViewHolder>(PRODUCT_COMPARATOR) {
    private lateinit var listener: OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ProductViewHolder(
        ItemTextBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        getItem(position).let { holder.bind(it) }
    }

    inner class ProductViewHolder(private val binding: ItemTextBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    getItem(bindingAdapterPosition).let { listener.onChoiceClick(it) }
                }
            }
        }

        fun bind(model: CustomerPromoCodes) {
            binding.apply {
                itemTextTv.text = model.promoCodeName
            }
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun setListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onChoiceClick(model: CustomerPromoCodes)
    }

    //check difference
    companion object {
        private val PRODUCT_COMPARATOR = object : DiffUtil.ItemCallback<CustomerPromoCodes>() {
            override fun areItemsTheSame(
                oldItem: CustomerPromoCodes,
                newItem: CustomerPromoCodes
            ) = oldItem.promoIdFk == newItem.promoIdFk

            override fun areContentsTheSame(
                oldItem: CustomerPromoCodes,
                newItem: CustomerPromoCodes
            ) = oldItem == newItem
        }
    }
}