package eramo.tahoon.presentation.adapters.recycleview.vertical

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.ParseException
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import eramo.tahoon.R
import eramo.tahoon.databinding.ItemProductShopBinding
import eramo.tahoon.domain.model.products.MyProductModel
import eramo.tahoon.util.Constants
import eramo.tahoon.util.UserUtil
import eramo.tahoon.util.formatPrice
import javax.inject.Inject

class ProductShopAdapter @Inject constructor() :
    ListAdapter<MyProductModel, ProductShopAdapter.ProductViewHolder>(
        PRODUCT_COMPARATOR
    ) {
    private lateinit var listener: OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ProductViewHolder(
        ItemProductShopBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        getItem(position).let { it?.let { it1 -> holder.bind(it1) } }
    }

    inner class ProductViewHolder(private val binding: ItemProductShopBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    getItem(bindingAdapterPosition).let {
                        it?.let { it1 -> listener.onProductShopClick(it1) }
                    }
                }
            }
        }

        fun bind(model: MyProductModel) {
            binding.apply {
                Glide.with(itemView)
                    .load(model.primaryImageUrl)
                    .into(this.itemProductShopIvImage)

                itemProductShopTvName.text = model.title
                itemProductShopTvCategory.text = model.category?.title

                itemDealTvPriceBefore.text = if (UserUtil.getCurrency() == Constants.CURRENCY_EGP) {
                    itemView.context.getString(R.string.s_egp, formatPrice(model.fakePrice ?: 0.0))
                } else {
                    itemView.context.getString(R.string.s_usd, formatPrice(model.fakePrice ?: 0.0))
                }

                itemDealTvPriceAfter.text = if (UserUtil.getCurrency() == Constants.CURRENCY_EGP) {
                    itemView.context.getString(R.string.s_egp, formatPrice(model.realPrice ?: 0.0))
                } else {
                    itemView.context.getString(R.string.s_usd, formatPrice(model.realPrice ?: 0.0))
                }

                if (removePriceComma(model.realPrice.toString()) > removePriceComma(model.fakePrice.toString()) && removePriceComma(model.fakePrice.toString()) != 0.0) {
                    itemDealTvPriceBefore.visibility = View.VISIBLE
                    itemDealTvPriceBefore.text = if (UserUtil.getCurrency() == Constants.CURRENCY_EGP) {
                        itemView.context.getString(R.string.s_egp, formatPrice(model.fakePrice ?: 0.0))
                    } else {
                        itemView.context.getString(R.string.s_usd, formatPrice(model.fakePrice ?: 0.0))
                    }

                } else {
                    itemDealTvPriceBefore.visibility = View.INVISIBLE
                }

                itemDealTvPriceBefore.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG


                itemProductShopIvFav.setOnClickListener {
                    listener.onFavouriteClick(
                        bindingAdapterPosition,
                        model,
                        model.isFav == 1
                    )
                }

                if (model.isFav == 1)
                    binding.itemProductShopIvFav.setImageResource(R.drawable.ic_heart_fill)
                else binding.itemProductShopIvFav.setImageResource(R.drawable.ic_heart)

                if (model.profitPercent!! > 0) {
                    itemProductShopTvDiscount.visibility = View.VISIBLE
                    itemDealTvPriceBefore.visibility = View.VISIBLE
                    itemDealTvPriceBefore.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG

                    itemProductShopTvDiscount.text = "${model.profitPercent}%"
                    itemDealTvPriceBefore.text = if (UserUtil.getCurrency() == Constants.CURRENCY_EGP) {
                        itemView.context.getString(R.string.s_egp, formatPrice(model.fakePrice ?: 0.0))
                    } else {
                        itemView.context.getString(R.string.s_usd, formatPrice(model.fakePrice ?: 0.0))
                    }

                }

                itemProductIvNew.isVisible = model.new == 1

            }
        }
    }

    @Throws(ParseException::class)
    private fun removePriceComma(value: String): Double {
        val newValue = value.replace(",", "")
        return newValue.toDouble()
    }

    fun setListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onProductShopClick(model: MyProductModel)
        fun onFavouriteClick(position: Int, model: MyProductModel, isFav: Boolean) {

        }
    }

    //check difference
    companion object {
        private val PRODUCT_COMPARATOR = object : DiffUtil.ItemCallback<MyProductModel>() {
            override fun areItemsTheSame(
                oldItem: MyProductModel,
                newItem: MyProductModel
            ) = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: MyProductModel,
                newItem: MyProductModel
            ) = oldItem == newItem
        }
    }
}