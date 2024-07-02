package eramo.tahoon.presentation.adapters.recycleview.vertical

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import eramo.tahoon.R
import eramo.tahoon.databinding.ItemMyorderBinding
import eramo.tahoon.domain.model.products.orders.OrderModel
import eramo.tahoon.util.Constants
import eramo.tahoon.util.UserUtil
import eramo.tahoon.util.formatPrice
import javax.inject.Inject

class MyOrderAdapter @Inject constructor() :
    ListAdapter<OrderModel, MyOrderAdapter.ProductViewHolder>(PRODUCT_COMPARATOR) {
    private lateinit var listener: OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ProductViewHolder(
        ItemMyorderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        getItem(position).let { holder.bind(it) }
    }

    inner class ProductViewHolder(private val binding: ItemMyorderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(model: OrderModel) {
            binding.apply {
                itemMyOrderTvNumOrder.text = model.orderId
                itemMyOrderTvDate.text = model.orderDate.substring(0, 10)
//                itemMyOrderTvStatus.text = model.status.asString(itemView.context)
                itemMyOrderTvTotal.text = if (UserUtil.getCurrency() == Constants.CURRENCY_EGP) {
                    itemView.context.getString(R.string.s_egp, formatPrice(model.finalTotal))
                } else {
                    itemView.context.getString(R.string.s_usd, formatPrice(model.finalTotal))
                }

                itemMyOrderBtnDetails.setOnClickListener { listener.onOrderClick(model) }

                when (model.status.asString(itemView.context)) {

                    itemView.context.getString(R.string.order_status_pending) -> {
                        root.setBackgroundResource(R.drawable.shape_green_low)
                        itemMyOrderTvStatus.text = itemView.context.getString(R.string.order_status_pending_txt)
                    }

                    itemView.context.getString(R.string.order_status_inprogress) -> {
                        root.setBackgroundResource(R.drawable.shape_yellow_low)
                        itemMyOrderTvStatus.text = itemView.context.getString(R.string.order_status_in_progress_txt)
                    }

                    itemView.context.getString(R.string.order_status_delivered) -> {
                        root.setBackgroundResource(R.drawable.shape_blue_low)
                        itemMyOrderTvStatus.text = itemView.context.getString(R.string.order_status_delivered_txt)
                    }

                    itemView.context.getString(R.string.order_status_canceled) -> {
                        root.setBackgroundResource(R.drawable.shape_red_low)
                        itemMyOrderTvStatus.text = itemView.context.getString(R.string.order_status_canceled_txt)
                    }

//                    itemView.context.getString(R.string.new_order) -> {
//                        root.setBackgroundResource(R.drawable.shape_green_low)
//                    }
//                    itemView.context.getString(R.string.in_progress) -> {
//                        root.setBackgroundResource(R.drawable.shape_yellow_low)
//                    }
//                    itemView.context.getString(R.string.completed) -> {
//                        root.setBackgroundResource(R.drawable.shape_blue_low)
//                    }
//                    itemView.context.getString(R.string.cancelled) -> {
//                        root.setBackgroundResource(R.drawable.shape_red_low)
//                    }
                }
            }
        }
    }


    fun setListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onOrderClick(model: OrderModel)
    }

    //check difference
    companion object {
        private val PRODUCT_COMPARATOR = object : DiffUtil.ItemCallback<OrderModel>() {
            override fun areItemsTheSame(
                oldItem: OrderModel,
                newItem: OrderModel
            ) = oldItem.orderId == newItem.orderId

            override fun areContentsTheSame(
                oldItem: OrderModel,
                newItem: OrderModel
            ) = oldItem == newItem
        }
    }
}