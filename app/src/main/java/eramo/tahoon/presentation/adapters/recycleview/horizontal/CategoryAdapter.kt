package eramo.tahoon.presentation.adapters.recycleview.horizontal

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import eramo.tahoon.data.remote.dto.home.HomeCategoriesResponse
import eramo.tahoon.databinding.ItemCategoryBinding
import javax.inject.Inject

class CategoryAdapter @Inject constructor() :
    ListAdapter<HomeCategoriesResponse.Data, CategoryAdapter.ProductViewHolder>(PRODUCT_COMPARATOR) {
    private lateinit var listener: OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ProductViewHolder(
        ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        getItem(position).let { holder.bind(it) }
    }

    inner class ProductViewHolder(private val binding: ItemCategoryBinding) :
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

        fun bind(model: HomeCategoriesResponse.Data) {
            binding.apply {
                Glide.with(itemView)
//                    .load(EramoApi.IMAGE_URL_MANUFACTURERS + model.image)
                    .load(model.imageUrl)
                    .into(this.itemCategoryIv)
                itemCategoryTvName.text = model.title?.trim()
            }
        }
    }

    fun setListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onCategoryClick(model: HomeCategoriesResponse.Data)
    }

    //check difference
    companion object {
        private val PRODUCT_COMPARATOR = object : DiffUtil.ItemCallback<HomeCategoriesResponse.Data>() {
            override fun areItemsTheSame(
                oldItem: HomeCategoriesResponse.Data,
                newItem: HomeCategoriesResponse.Data
            ) = oldItem == newItem

            override fun areContentsTheSame(
                oldItem: HomeCategoriesResponse.Data,
                newItem: HomeCategoriesResponse.Data
            ) = oldItem == newItem
        }
    }
}