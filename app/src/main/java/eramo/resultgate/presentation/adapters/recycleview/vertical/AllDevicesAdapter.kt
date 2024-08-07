package eramo.resultgate.presentation.adapters.recycleview.vertical

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import eramo.resultgate.R
import eramo.resultgate.data.remote.dto.alldevices.Data
import eramo.resultgate.data.remote.dto.alldevices.DataX
import eramo.resultgate.data.remote.dto.home.HomeCategoriesResponse
import eramo.resultgate.databinding.ItemAllcategoryBinding
import eramo.resultgate.databinding.ItemProductBinding
import eramo.resultgate.domain.model.products.MyProductModel
import eramo.resultgate.util.MySingleton
import eramo.resultgate.util.UserUtil
import javax.inject.Inject
class AllDevicesAdapter @Inject constructor() :
    ListAdapter<DataX, AllDevicesAdapter.ProductViewHolder>(PRODUCT_COMPARATOR) {
    private lateinit var listener: OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ProductViewHolder(
        ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        getItem(position).let { holder.bind(it) }
    }

    inner class ProductViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    getItem(bindingAdapterPosition).let {
                        listener.onCategoryClick(it)
                    }
                }
            }
        }

        fun bind(model: DataX) {
            binding.apply {
                Glide.with(itemView)
                    .load(model.primaryImageUrl)
                    .into(itemProductIvImage)
                itemProductTvName.text = model.title?.trim()
                itemProductTvPrice.text = model.realPrice?.trim()
                itemProductIvFav.setOnClickListener {
                    listener.onDeviceFavouriteClick(
                        bindingAdapterPosition,
                        model,
                        model.isFav == 1
                    )
                }
                if (UserUtil.isUserLogin()){
                    if (model.isFav == 1)
                        itemProductIvFav.setImageResource(R.drawable.ic_heart_fill)
                    else itemProductIvFav.setImageResource(R.drawable.ic_heart)

                }else{
                    for (i in MySingleton.favouriteDbIdsList){
                        if (i == model.id){
                            itemProductIvFav.setImageResource(R.drawable.ic_heart_fill)
                            return
                        }else{
                            itemProductIvFav.setImageResource(R.drawable.ic_heart)
                        }
                    }}
                // Set initial favorite icon drawable based on isFav status
                val favoriteIconDrawable = if (model.isFav == 1) {
                    R.drawable.ic_heart_fill // Replace with your active favorite icon
                } else {
                    R.drawable.ic_heart // Replace with your inactive favorite icon
                }
                itemProductIvFav.setImageResource(favoriteIconDrawable)

                // Set click listener for favorite icon
                itemProductIvFav.setOnClickListener {
                    val isFav = model.isFav == 1
                    listener.onDeviceFavouriteClick(bindingAdapterPosition, model, isFav)

                    // Toggle favorite status
                    model.isFav = if (isFav) 0 else 1

                    // Update favorite icon drawable
                    val newFavoriteIconDrawable = if (model.isFav == 1) {
                        R.drawable.ic_heart_fill // Replace with your active favorite icon
                    } else {
                        R.drawable.ic_heart // Replace with your inactive favorite icon
                    }
                    itemProductIvFav.setImageResource(newFavoriteIconDrawable)

                    // Notify the adapter that the item has changed
                    notifyItemChanged(bindingAdapterPosition)
                }
            }
        }


    }

    fun setListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onCategoryClick(model: DataX)
        fun onDeviceFavouriteClick(position: Int,model: DataX, isFav: Boolean)
    }

    companion object {
        private val PRODUCT_COMPARATOR = object : DiffUtil.ItemCallback<DataX>() {
            override fun areItemsTheSame(oldItem: DataX, newItem: DataX) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: DataX, newItem: DataX) = oldItem == newItem
        }
    }
}
