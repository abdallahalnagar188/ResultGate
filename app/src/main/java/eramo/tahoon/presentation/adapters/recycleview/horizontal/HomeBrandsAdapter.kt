package eramo.tahoon.presentation.adapters.recycleview.horizontal

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import eramo.tahoon.databinding.ItemCategoryBinding
import eramo.tahoon.domain.model.home.HomeBrandsModel
import javax.inject.Inject

class HomeBrandsAdapter @Inject constructor() :
    ListAdapter<HomeBrandsModel, HomeBrandsAdapter.ProductViewHolder>(PRODUCT_COMPARATOR) {
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
                        listener.onBrandClick(it)
                    }
                }
            }
        }

        fun bind(model: HomeBrandsModel) {
            binding.apply {
                Glide.with(itemView)
                    .load(model.imageUrl)
                    .into(this.itemCategoryIv)
                itemCategoryTvName.text = model.name.trim()
            }
        }
    }

    fun setListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onBrandClick(model: HomeBrandsModel)
    }

    //check difference
    companion object {
        private val PRODUCT_COMPARATOR = object : DiffUtil.ItemCallback<HomeBrandsModel>() {
            override fun areItemsTheSame(
                oldItem: HomeBrandsModel,
                newItem: HomeBrandsModel
            ) = oldItem == newItem

            override fun areContentsTheSame(
                oldItem: HomeBrandsModel,
                newItem: HomeBrandsModel
            ) = oldItem == newItem
        }
    }
}
