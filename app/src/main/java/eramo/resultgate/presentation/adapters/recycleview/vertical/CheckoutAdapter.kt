package eramo.resultgate.presentation.adapters.recycleview.vertical

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import eramo.resultgate.R
import eramo.resultgate.databinding.ItemCheckoutBinding
import eramo.resultgate.domain.model.CartProductModel
import eramo.resultgate.util.Constants
import eramo.resultgate.util.UserUtil
import eramo.resultgate.util.formatPrice
import javax.inject.Inject

class CheckoutAdapter @Inject constructor() :
    ListAdapter<CartProductModel.ProductList, CheckoutAdapter.ProductViewHolder>(PRODUCT_COMPARATOR) {
    private lateinit var listener: OnItemClickListener
    private var totalPrice = 0.0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ProductViewHolder(
        ItemCheckoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        getItem(position).let { holder.bind(it) }
    }

    inner class ProductViewHolder(private val binding: ItemCheckoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(model: CartProductModel.ProductList) {
            binding.apply {
                val currentQty = model.quantity
                val mainPrice = model.price?.toDouble()
                val currentPrice = currentQty!! * mainPrice!!
                itemCheckoutTvName.text = model.title
                itemCheckoutTvCategory.text = model.productCategory
                itemCheckoutTvAvailability.text = ""
                itemCheckoutTvModelNumber.text = model.modelNumber
                itemCheckoutTvQty.text = model.quantity.toString()

                itemCheckoutTvPrice.text = if (UserUtil.getCurrency() == Constants.CURRENCY_EGP){
                    itemView.context.getString(R.string.s_egp, formatPrice(currentPrice))
                }else{
                    itemView.context.getString(R.string.s_usd, formatPrice(currentPrice))
                }
//                    itemView.context.getString(R.string.s_egp, currentPrice.toString())

                Glide.with(itemView)
                    .load(model.image).into(this.itemCheckoutIvImage)

                totalPrice += currentPrice
                if (bindingAdapterPosition == (itemCount - 1)) {
                    listener.onTotalCompleted(totalPrice)
                }
            }
        }
    }

    fun clearTotalPrice() {
        totalPrice = 0.0
    }

    fun setListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onTotalCompleted(total: Double){}
    }

    //check difference
    companion object {
        private val PRODUCT_COMPARATOR = object : DiffUtil.ItemCallback<CartProductModel.ProductList>() {
            override fun areItemsTheSame(
                oldItem: CartProductModel.ProductList,
                newItem: CartProductModel.ProductList
            ) = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: CartProductModel.ProductList,
                newItem: CartProductModel.ProductList
            ) = oldItem == newItem
        }
    }
}