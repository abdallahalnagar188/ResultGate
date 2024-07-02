package eramo.resultgate.presentation.adapters.recycleview.horizontal

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import eramo.resultgate.R
import eramo.resultgate.data.remote.dto.products.ProductDetailsResponse
import eramo.resultgate.databinding.ItemProductSizeBinding
import javax.inject.Inject

class ProductSizeAdapter @Inject constructor() :
    ListAdapter<ProductDetailsResponse.Data.Size, ProductSizeAdapter.ProductViewHolder>(PRODUCT_COMPARATOR) {
    private lateinit var listener: OnItemClickListener
    private var selectedSize = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ProductViewHolder(
        ItemProductSizeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        getItem(position).let { holder.bind(it) }
    }

    inner class ProductViewHolder(private val binding: ItemProductSizeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    selectedSize = bindingAdapterPosition
                    notifyDataSetChanged()
                }
            }
        }

        fun bind(model: ProductDetailsResponse.Data.Size) {
            binding.apply {
                itemProductSizeTvSize.text = model.name
                if (selectedSize == bindingAdapterPosition) {
                    itemProductSizeTvSize.apply {
                        setTextColor(Color.WHITE)
                        setBackgroundResource(R.drawable.shape_purple)
                    }
                } else {
                    itemProductSizeTvSize.apply {
                        setTextColor(ContextCompat.getColor(itemView.context, R.color.eramo_color))
                        setBackgroundResource(R.drawable.stroke_blue_radius)
                    }
                }
            }
        }
    }

    fun setListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onProductMoreClick(model: ProductDetailsResponse.Data.Size)
    }

    fun getSelectedSizeId(): String {
        return if (selectedSize == -1) "" else getItem(selectedSize).id.toString() ?: ""
    }

    //check difference
    companion object {
        private val PRODUCT_COMPARATOR = object : DiffUtil.ItemCallback<ProductDetailsResponse.Data.Size>() {
            override fun areItemsTheSame(
                oldItem: ProductDetailsResponse.Data.Size,
                newItem: ProductDetailsResponse.Data.Size
            ) = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: ProductDetailsResponse.Data.Size,
                newItem: ProductDetailsResponse.Data.Size
            ) = oldItem == newItem
        }
    }
}