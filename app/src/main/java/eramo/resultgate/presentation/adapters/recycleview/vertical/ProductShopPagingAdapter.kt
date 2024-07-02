package eramo.resultgate.presentation.adapters.recycleview.vertical

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.ParseException
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import eramo.resultgate.R
import eramo.resultgate.databinding.ItemProductShopBinding
import eramo.resultgate.domain.model.products.ShopProductModel
import eramo.resultgate.util.Constants
import eramo.resultgate.util.MySingleton
import eramo.resultgate.util.UserUtil
import eramo.resultgate.util.formatPrice
import javax.inject.Inject

class ProductShopPagingAdapter @Inject constructor() :
    PagingDataAdapter<ShopProductModel, ProductShopPagingAdapter.ProductViewHolder>(
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

        fun bind(model: ShopProductModel) {
            binding.apply {

                Glide.with(itemView)
                    .load(model.primaryImageUrl)
                    .into(this.itemProductShopIvImage)

                itemProductShopTvName.text = model.title

                itemProductShopTvCategory.text = model.category?.title

                itemDealTvPriceBefore.text = if (UserUtil.getCurrency() == Constants.CURRENCY_EGP){
                    itemView.context.getString(R.string.s_egp, formatPrice(model.fakePrice?:0.0))
                }else{
                    itemView.context.getString(R.string.s_usd, formatPrice(model.fakePrice?:0.0))
                }

                itemDealTvPriceAfter.text = if (UserUtil.getCurrency() == Constants.CURRENCY_EGP){
                    itemView.context.getString(R.string.s_egp, formatPrice(model.realPrice?:0.0))
                }else{
                    itemView.context.getString(R.string.s_usd, formatPrice(model.realPrice?:0.0))
                }

                if (removePriceComma(model.fakePrice.toString()) > removePriceComma(model.realPrice.toString()) && removePriceComma(model.fakePrice.toString()) != 0.0) {
                    itemDealTvPriceBefore.visibility = View.VISIBLE
                    itemDealTvPriceBefore.text = if (UserUtil.getCurrency() == Constants.CURRENCY_EGP){
                        itemView.context.getString(R.string.s_egp, formatPrice(model.fakePrice?:0.0))
                    }else{
                        itemView.context.getString(R.string.s_usd, formatPrice(model.fakePrice?:0.0))
                    }
                    itemDealTvPriceBefore.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                } else {
                    itemDealTvPriceBefore.visibility = View.GONE
                }


                itemProductShopIvFav.setOnClickListener {
                    listener.onFavouriteClick(
                        bindingAdapterPosition,
                        model,
                        model.isFav == 1
                    )
                }


                if ((model.profitPercent ?: 0) > 0) {
                    itemProductShopTvDiscount.visibility = View.VISIBLE
//                    itemDealTvPriceBefore.visibility = View.VISIBLE
//                    itemDealTvPriceBefore.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG

                    itemProductShopTvDiscount.text = "${model.profitPercent}%"
//                    itemDealTvPriceBefore.text = itemView.context.getString(R.string.s_egp, model.fakePrice.toString())
                }else{
                    itemProductShopTvDiscount.visibility = View.GONE
//                    itemDealTvPriceBefore.visibility = View.GONE
                }
//                itemProductShopIvNew.isVisible = model.new == 1
                itemProductIvNew.isVisible = model.new == 1



                if (UserUtil.isUserLogin()){
                    if (model.isFav == 1)
                        itemProductShopIvFav.setImageResource(R.drawable.ic_heart_fill)
                    else itemProductShopIvFav.setImageResource(R.drawable.ic_heart)

                }else{
                    for (i in MySingleton.favouriteDbIdsList){
                        if (i == model.id){
                            itemProductShopIvFav.setImageResource(R.drawable.ic_heart_fill)
                            return
                        }else{
                            itemProductShopIvFav.setImageResource(R.drawable.ic_heart)
                        }
                    }

                    if (MySingleton.favouriteDbIdsList.size ==0 ){
                        itemProductShopIvFav.setImageResource(R.drawable.ic_heart)
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

    fun setListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onProductShopClick(model: ShopProductModel)
        fun onFavouriteClick(position: Int, model: ShopProductModel, isFav: Boolean) {

        }
    }

    //check difference
    companion object {
        private val PRODUCT_COMPARATOR = object : DiffUtil.ItemCallback<ShopProductModel>() {
            override fun areItemsTheSame(
                oldItem: ShopProductModel,
                newItem: ShopProductModel
            ) = (oldItem.id == newItem.id )

            override fun areContentsTheSame(
                oldItem: ShopProductModel,
                newItem: ShopProductModel
            ) = oldItem == newItem
        }
    }
}