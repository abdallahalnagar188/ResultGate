package eramo.resultgate.presentation.adapters.recycleview.vertical

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
import eramo.resultgate.R
import eramo.resultgate.databinding.ItemDealBinding
import eramo.resultgate.domain.model.products.MyProductModel
import eramo.resultgate.util.Constants
import eramo.resultgate.util.MySingleton
import eramo.resultgate.util.UserUtil
import eramo.resultgate.util.formatPrice
import javax.inject.Inject

class DealAdapter @Inject constructor() : ListAdapter<MyProductModel, DealAdapter.ProductViewHolder>(
    PRODUCT_COMPARATOR
) {
    private lateinit var listener: OnItemClickListener
    private var favPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ProductViewHolder(
        ItemDealBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        getItem(position).let { holder.bind(it) }
    }

    inner class ProductViewHolder(private val binding: ItemDealBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    getItem(bindingAdapterPosition).let {
                        listener.onDealClick(it)
                    }
                }
            }
        }

        fun bind(model: MyProductModel) {
            binding.apply {
//                if (model.allImageDtos.isNotEmpty()) {
//                    model.allImageDtos[0].image.let {
                Glide.with(itemView)
                    .load(model.primaryImageUrl)
                    .into(this.itemDealIvImage)
//                    }
//                }
                itemDealTvName.text = model.title

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

                if (removePriceComma(model.fakePrice.toString()) > removePriceComma(model.realPrice.toString()) && removePriceComma(model.fakePrice.toString()) != 0.0) {
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

                itemDealIvFav.setOnClickListener {
                    favPosition = bindingAdapterPosition
                    listener.onDealFavouriteClick(
                        model,
                        model.isFav == 1
                    )
                }

//                if (model.isFav == 1)
//                    binding.itemDealIvFav.setImageResource(R.drawable.ic_heart_fill)
//                else binding.itemDealIvFav.setImageResource(R.drawable.ic_heart)

                itemDealIvNew.isVisible = model.new == 1

//                itemDealTvDiscount.isVisible = model.profitPercent!! > 0
//                itemDealTvDiscount.text = "${model.profitPercent}%"

                if (model.profitPercent!! > 0) {
                    tvPercantage.visibility = View.VISIBLE
                    tvPercantage.text = itemView.context.getString(R.string.perentt, model.profitPercent.toString())
                } else {
                    tvPercantage.visibility = View.GONE
                }

                if (UserUtil.isUserLogin()) {
                    if (model.isFav == 1)
                        itemDealIvFav.setImageResource(R.drawable.ic_heart_fill)
                    else itemDealIvFav.setImageResource(R.drawable.ic_heart)

                } else {
                    for (i in MySingleton.favouriteDbIdsList) {
                        if (i == model.id) {
                            itemDealIvFav.setImageResource(R.drawable.ic_heart_fill)
                            return
                        } else {
                            itemDealIvFav.setImageResource(R.drawable.ic_heart)
                        }
                    }

                    if (MySingleton.favouriteDbIdsList.size == 0) {
                        itemDealIvFav.setImageResource(R.drawable.ic_heart)
                    }
                }
            }
        }
    }

    @Throws(ParseException::class)
    private fun removePriceComma(value: String): Double {
        val newValue = value.replace(",", "")
        return newValue.toDouble()
    }

    fun updateFav(isChecked: Boolean) {
        currentList[favPosition].isFav = if (isChecked) 1 else 0
        this.notifyItemChanged(favPosition, currentList[favPosition])
    }

    fun setListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onDealClick(model: MyProductModel)
        fun onDealFavouriteClick(model: MyProductModel, isFav: Boolean)
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