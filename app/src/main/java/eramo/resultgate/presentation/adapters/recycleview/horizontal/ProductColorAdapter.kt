package eramo.resultgate.presentation.adapters.recycleview.horizontal

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import eramo.resultgate.data.remote.dto.products.ProductDetailsResponse
import eramo.resultgate.databinding.ItemProductColorBinding
import javax.inject.Inject

class ProductColorAdapter @Inject constructor() :
    ListAdapter<ProductDetailsResponse.Data.Color, ProductColorAdapter.ProductViewHolder>(PRODUCT_COMPARATOR) {
    private lateinit var listener: OnItemClickListener
    private var selectedColor = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ProductViewHolder(
        ItemProductColorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        getItem(position).let { holder.bind(it) }
    }

    inner class ProductViewHolder(private val binding: ItemProductColorBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    selectedColor = bindingAdapterPosition
                    notifyDataSetChanged()
                }
            }
        }

        fun bind(model: ProductDetailsResponse.Data.Color) {
            binding.apply {
//                itemProductColorIvColor.setBackgroundColor(Color.parseColor(model.code))
                itemProductColorIvColor.setBackgroundColor(Color.parseColor(model.code))
                if (selectedColor == bindingAdapterPosition)
                    itemProductColorIvCheck.visibility = View.VISIBLE
                else
                    itemProductColorIvCheck.visibility = View.INVISIBLE
            }
        }
    }

    fun setListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onProductMoreClick(model: ProductDetailsResponse.Data.Color)
    }

    fun getSelectedColorId(): String {
        return if (selectedColor == -1) "" else getItem(selectedColor).id.toString() ?: ""
    }

    //check difference
    companion object {
        private val PRODUCT_COMPARATOR = object : DiffUtil.ItemCallback<ProductDetailsResponse.Data.Color>() {
            override fun areItemsTheSame(
                oldItem: ProductDetailsResponse.Data.Color,
                newItem: ProductDetailsResponse.Data.Color
            ) = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: ProductDetailsResponse.Data.Color,
                newItem: ProductDetailsResponse.Data.Color
            ) = oldItem == newItem
        }
    }
}