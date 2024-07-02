package eramo.resultgate.presentation.adapters.recycleview.horizontal

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import eramo.resultgate.R
import eramo.resultgate.databinding.ItemFeaturedBinding
import eramo.resultgate.domain.model.products.MyProductModel
import eramo.resultgate.util.Constants
import eramo.resultgate.util.MySingleton
import eramo.resultgate.util.UserUtil
import eramo.resultgate.util.formatPrice
import javax.inject.Inject

class FeaturedAdapter @Inject constructor() :
//    ListAdapter<FeaturedProductsResponse.FeaturedProduct, FeaturedAdapter.ProductViewHolder>(PRODUCT_COMPARATOR) {
    ListAdapter<MyProductModel, FeaturedAdapter.ProductViewHolder>(PRODUCT_COMPARATOR) {
    private lateinit var listener: OnItemClickListener
    private var favPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ProductViewHolder(
        ItemFeaturedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        getItem(position).let { holder.bind(it) }
    }

    inner class ProductViewHolder(private val binding: ItemFeaturedBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    getItem(bindingAdapterPosition).let {
                        listener.onFeaturedClick(it)
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
                    .into(this.itemFeaturedIvImage)
//                    }
//                }

                itemFeaturedTvName.text = model.title
                itemFeaturedTvPrice.text = if (UserUtil.getCurrency() == Constants.CURRENCY_EGP){
                    itemView.context.getString(R.string.s_egp, formatPrice(model.realPrice?:0.0))
                }else{
                    itemView.context.getString(R.string.s_usd, formatPrice(model.realPrice?:0.0))
                }

                itemFeaturedIvFav.setOnClickListener {
                    favPosition = bindingAdapterPosition
                    listener.onFeaturedFavouriteClick(
                        model,
                        model.isFav == 1
                    )
                }

//                if (model.isFav == 1)
//                    binding.itemFeaturedIvFav.setImageResource(R.drawable.ic_heart_fill)
//                else binding.itemFeaturedIvFav.setImageResource(R.drawable.ic_heart)

                if (UserUtil.isUserLogin()){
                    if (model.isFav == 1)
                        itemFeaturedIvFav.setImageResource(R.drawable.ic_heart_fill)
                    else itemFeaturedIvFav.setImageResource(R.drawable.ic_heart)

                }else{
                    for (i in MySingleton.favouriteDbIdsList){
                        if (i == model.id){
                            itemFeaturedIvFav.setImageResource(R.drawable.ic_heart_fill)
                            return
                        }else{
                            itemFeaturedIvFav.setImageResource(R.drawable.ic_heart)
                        }
                    }

                    if (MySingleton.favouriteDbIdsList.size ==0 ){
                        itemFeaturedIvFav.setImageResource(R.drawable.ic_heart)
                    }
                }
            }
        }
    }

    fun updateFav(isChecked: Boolean) {
        currentList[favPosition].isFav = if (isChecked) 1 else 0
        this.notifyItemChanged(favPosition, currentList[favPosition])
    }

    fun setListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onFeaturedClick(model: MyProductModel)
        fun onFeaturedFavouriteClick(model: MyProductModel, isFav: Boolean)
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