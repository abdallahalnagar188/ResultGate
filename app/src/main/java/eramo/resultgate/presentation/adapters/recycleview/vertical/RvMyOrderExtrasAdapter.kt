package eramo.resultgate.presentation.adapters.recycleview.vertical

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import eramo.resultgate.R
import eramo.resultgate.data.remote.dto.drawer.myaccount.OrderDetailsResponse
import eramo.resultgate.databinding.ItemMyOrderExtrasBinding
import javax.inject.Inject


class RvMyOrderExtrasAdapter @Inject constructor() :
    ListAdapter<OrderDetailsResponse.Data.Product.Extra, RvMyOrderExtrasAdapter.ProductViewHolder>(PRODUCT_COMPARATOR) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ProductViewHolder(
        ItemMyOrderExtrasBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        getItem(position).let { holder.bind(it) }
    }

    inner class ProductViewHolder(private val binding: ItemMyOrderExtrasBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(model: OrderDetailsResponse.Data.Product.Extra) {
            binding.apply {
                tvTitle.text = model.title
                tvQuantityValue.text = model.quantity
                tvTotalCostValue.text = itemView.context.getString(R.string.s_egp, model.totalCost.toString())
            }
        }
    }

    //check difference
    companion object {
        private val PRODUCT_COMPARATOR = object : DiffUtil.ItemCallback<OrderDetailsResponse.Data.Product.Extra>() {
            override fun areItemsTheSame(
                oldItem: OrderDetailsResponse.Data.Product.Extra,
                newItem: OrderDetailsResponse.Data.Product.Extra
            ) = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: OrderDetailsResponse.Data.Product.Extra,
                newItem: OrderDetailsResponse.Data.Product.Extra
            ) = oldItem == newItem
        }
    }
}