package eramo.resultgate.presentation.adapters.recycleview.vertical

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import eramo.resultgate.data.remote.dto.home.SubCategoryModel
import eramo.resultgate.databinding.ItemAllcategoryBinding
import javax.inject.Inject


class SubCategoriesAdapter @Inject constructor() :
    ListAdapter<SubCategoryModel, SubCategoriesAdapter.ProductViewHolder>(PRODUCT_COMPARATOR) {
    private lateinit var listener: OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ProductViewHolder(
        ItemAllcategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        getItem(position).let { holder.bind(it) }
    }

    inner class ProductViewHolder(private val binding: ItemAllcategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    getItem(bindingAdapterPosition).let {
                        listener.onSubCategoryClick(it)
                    }
                }
            }
        }

        fun bind(model: SubCategoryModel) {
            binding.apply {
                Glide.with(itemView)
                    .load(model.imageUrl)
                    .into(this.itemCategoryIv)
                itemCategoryTvName.text = model.title ?.trim()
            }
        }
    }

    fun setListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onSubCategoryClick(model: SubCategoryModel)
    }

    //check difference
    companion object {
        private val PRODUCT_COMPARATOR = object : DiffUtil.ItemCallback<SubCategoryModel>() {
            override fun areItemsTheSame(
                oldItem: SubCategoryModel,
                newItem: SubCategoryModel
            ) = oldItem == newItem

            override fun areContentsTheSame(
                oldItem: SubCategoryModel,
                newItem: SubCategoryModel
            ) = oldItem == newItem
        }
    }
}