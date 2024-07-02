package eramo.resultgate.presentation.adapters.recycleview.vertical

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import eramo.resultgate.data.remote.dto.home.HomeBestCategoriesResponse
import eramo.resultgate.databinding.ItemRecentCategoriesBinding
import javax.inject.Inject


class RvHomeRecentCategoriesAdapter @Inject constructor() :
    ListAdapter<HomeBestCategoriesResponse.Data, RvHomeRecentCategoriesAdapter.ProductViewHolder>(PRODUCT_COMPARATOR) {
    private lateinit var listener: OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ProductViewHolder(
        ItemRecentCategoriesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        getItem(position).let { holder.bind(it) }
    }

    inner class ProductViewHolder(private val binding: ItemRecentCategoriesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    getItem(bindingAdapterPosition).let {
                        listener.onRecentCategoryClick(it)
                    }
                }
            }
        }

        fun bind(model: HomeBestCategoriesResponse.Data) {
            binding.apply {
                tvName.text = model.title
                Glide.with(itemView).load(model.primaryImageUrl).into(this.ivImage)
            }
        }
    }

    fun setListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onRecentCategoryClick(model: HomeBestCategoriesResponse.Data)
    }

    //check difference
    companion object {
        private val PRODUCT_COMPARATOR = object : DiffUtil.ItemCallback<HomeBestCategoriesResponse.Data>() {
            override fun areItemsTheSame(
                oldItem: HomeBestCategoriesResponse.Data,
                newItem: HomeBestCategoriesResponse.Data
            ) = oldItem == newItem

            override fun areContentsTheSame(
                oldItem: HomeBestCategoriesResponse.Data,
                newItem: HomeBestCategoriesResponse.Data
            ) = oldItem == newItem
        }
    }
}