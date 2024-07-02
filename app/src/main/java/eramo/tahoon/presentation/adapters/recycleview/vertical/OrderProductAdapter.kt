package eramo.tahoon.presentation.adapters.recycleview.vertical

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import eramo.tahoon.R
import eramo.tahoon.data.remote.dto.drawer.myaccount.OrderDetailsResponse
import eramo.tahoon.databinding.ItemOrderDetailBinding
import eramo.tahoon.util.Constants
import eramo.tahoon.util.UserUtil
import javax.inject.Inject

class OrderProductAdapter @Inject constructor() :
    ListAdapter<OrderDetailsResponse.Data.Product, OrderProductAdapter.ProductViewHolder>(PRODUCT_COMPARATOR) {
    private lateinit var listener: OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ProductViewHolder(
        ItemOrderDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        getItem(position).let { holder.bind(it) }
    }

    inner class ProductViewHolder(private val binding: ItemOrderDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(model: OrderDetailsResponse.Data.Product) {
            binding.apply {
                itemCheckoutTvName.text = model.title.toString()
                itemCheckoutTvCategory.text = model.categoryName.toString()
//                itemCheckoutTvKarat.text = model.gramKirat.toString()
//                itemCheckoutTvGramPrice.text = model.gramPrice.toString()
//                itemCheckoutTvWeight.text = model.weight.toString()
                itemCheckoutTvQty.text = model.quantity.toString()
//                itemCheckoutTvCategory.text = model.mainCat
//                itemCheckoutTvAvailability.text = model.status
//                itemCheckoutTvModelNumber.text = model.modelNumber
                itemCheckoutTvPrice.text = if (UserUtil.getCurrency() == Constants.CURRENCY_EGP) {
                    itemView.context.getString(R.string.s_egp, model.price.toString())
                } else {
                    itemView.context.getString(R.string.s_usd, model.price.toString())
                }

                btnExtras.setOnClickListener {
                    listener.onExtrasClick(model.extras)
                }

                if (!model.extras.isNullOrEmpty()) {
                    btnExtras.visibility = View.VISIBLE
                } else {
                    btnExtras.visibility = View.INVISIBLE
                }

                Glide.with(itemView)
                    .load(model.image)
                    .into(this.itemCheckoutIvImage)
            }
        }
    }


    fun setListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onExtrasClick(model: List<OrderDetailsResponse.Data.Product.Extra?>?)
    }

    //check difference
    companion object {
        private val PRODUCT_COMPARATOR = object : DiffUtil.ItemCallback<OrderDetailsResponse.Data.Product>() {
            override fun areItemsTheSame(
                oldItem: OrderDetailsResponse.Data.Product,
                newItem: OrderDetailsResponse.Data.Product
            ) = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: OrderDetailsResponse.Data.Product,
                newItem: OrderDetailsResponse.Data.Product
            ) = oldItem == newItem
        }
    }
}