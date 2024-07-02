package eramo.resultgate.presentation.adapters.recycleview.vertical

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import eramo.resultgate.R
import eramo.resultgate.databinding.ItemCartBinding
import eramo.resultgate.domain.model.CartProductModel
import eramo.resultgate.util.Constants
import eramo.resultgate.util.UserUtil
import eramo.resultgate.util.formatPrice
import javax.inject.Inject

class CartAdapter @Inject constructor() :
    ListAdapter<CartProductModel.ProductList, CartAdapter.ProductViewHolder>(PRODUCT_COMPARATOR) {
    private lateinit var listener: OnItemClickListener
    private var totalPrice: Double = 0.0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ProductViewHolder(
        ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        getItem(position).let { holder.bind(it) }
    }

    inner class ProductViewHolder(private val binding: ItemCartBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(model: CartProductModel.ProductList) {
            binding.apply {
                val currentQty = model.quantity
                val mainPrice = model.price?.toDouble()
                val currentPrice = currentQty!! * mainPrice!!
                model.price = currentPrice.toFloat() // new price value
                itemCartTvCount.text = "$currentQty"
                itemCartTvName.text = model.title
                itemCartTvCategory.text = model.productCategory
                FDProductTvVendorNameValue.text = model.vendorName
                FDProductTvColorValue.text = model.color


                itemCartTvAvailability.text = ""
                textView16.visibility = View.GONE
                itemCartTvModelNumber.text = model.modelNumber

                Glide.with(itemView)
                    .load(model.image)
                    .into(this.itemCartIvImage)

                itemCartTvPrice.text = if (UserUtil.getCurrency() == Constants.CURRENCY_EGP) {
                    itemView.context.getString(R.string.s_egp, formatPrice(currentPrice))
                } else {
                    itemView.context.getString(R.string.s_usd, formatPrice(currentPrice))
                }

                itemCartIvRemove.setOnClickListener { listener.onRemoveClick(model) }
                itemCartIvPlus.setOnClickListener { listener.onQuantityClick(model, true) }
                itemCartIvMinus.setOnClickListener {
                    if (currentQty > 1) listener.onQuantityClick(model, false)
                }

                totalPrice += currentPrice
                if (bindingAdapterPosition == (itemCount - 1))
                    listener.onTotalPriceCompleted(totalPrice)

                btnExtras.setOnClickListener {
                    listener.onExtrasClick(model)
                }

                // hide model number
                itemCartTvModelNumber.visibility = View.GONE
                textView17.visibility = View.GONE

//                if (model.hasExtra == "yes"){
//                    btnExtrasLayout.setBackgroundColor(ContextCompat.getColor(itemView.context,R.color.eramo_color))
//                    btnExtrasTv.setTextColor(ContextCompat.getColor(itemView.context,R.color.white))
//                }else{
//                    btnExtrasLayout.setBackgroundColor(ContextCompat.getColor(itemView.context,R.color.gray_low))
//                    btnExtrasTv.setTextColor(ContextCompat.getColor(itemView.context,R.color.eramo_color))
//                }

                if (UserUtil.isUserLogin()){
                    textView22.visibility = View.VISIBLE
                    FDProductTvVendorNameValue.visibility = View.VISIBLE

                    textView23.visibility = View.VISIBLE
                    FDProductTvColorValue.visibility = View.VISIBLE
                }else{
                    textView22.visibility = View.GONE
                    FDProductTvVendorNameValue.visibility = View.GONE

                    textView23.visibility = View.GONE
                    FDProductTvColorValue.visibility = View.GONE
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
        fun onQuantityClick(model: CartProductModel.ProductList, isIncrease: Boolean)
        fun onRemoveClick(model: CartProductModel.ProductList)
        fun onExtrasClick(model: CartProductModel.ProductList)
        fun onInstallationChange(model: CartProductModel.ProductList, isChecked: Boolean)
        fun onTotalPriceCompleted(totalPrice: Double)
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