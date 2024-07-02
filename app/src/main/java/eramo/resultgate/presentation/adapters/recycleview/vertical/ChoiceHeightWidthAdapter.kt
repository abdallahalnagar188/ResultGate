package eramo.resultgate.presentation.adapters.recycleview.vertical

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import eramo.resultgate.R
import eramo.resultgate.data.remote.EramoApi
import eramo.resultgate.data.remote.dto.drawer.preview.WidthHeightsImgs
import eramo.resultgate.databinding.ItemImagePreviewBinding
import javax.inject.Inject

class ChoiceHeightWidthAdapter @Inject constructor() :
    ListAdapter<WidthHeightsImgs, ChoiceHeightWidthAdapter.ProductViewHolder>(PRODUCT_COMPARATOR) {
    private lateinit var listener: OnItemClickListener
    private var currentSelectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ProductViewHolder(
        ItemImagePreviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        getItem(position).let { holder.bind(it) }
    }

    inner class ProductViewHolder(private val binding: ItemImagePreviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    currentSelectedPosition = bindingAdapterPosition
                    notifyDataSetChanged()
                }
            }
        }

        fun bind(model: WidthHeightsImgs) {
            binding.apply {
                Glide.with(itemView)
                    .load(EramoApi.IMAGE_URL_GENERAL + model.imgUrl)
                    .into(this.itemImagePreviewIv)

                if (currentSelectedPosition == bindingAdapterPosition)
                    binding.root.setBackgroundResource(R.drawable.stroke_blue)
                else binding.root.setBackgroundResource(R.color.white)
            }
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun setListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onChoiceClick(model: WidthHeightsImgs)
    }

    //check difference
    companion object {
        private val PRODUCT_COMPARATOR = object : DiffUtil.ItemCallback<WidthHeightsImgs>() {
            override fun areItemsTheSame(
                oldItem: WidthHeightsImgs,
                newItem: WidthHeightsImgs
            ) = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: WidthHeightsImgs,
                newItem: WidthHeightsImgs
            ) = oldItem == newItem
        }
    }
}