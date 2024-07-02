package eramo.resultgate.presentation.adapters.recycleview.vertical

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import eramo.resultgate.R
import eramo.resultgate.databinding.ItemChooseRegionRvBinding
import eramo.resultgate.domain.model.auth.RegionsInCitiesModel
import javax.inject.Inject


class RvSelectedRegionAdapter @Inject constructor() :
    ListAdapter<RegionsInCitiesModel, RvSelectedRegionAdapter.ProductViewHolder>(PRODUCT_COMPARATOR) {
    private lateinit var listener: OnItemClickListener
    var selectedPosition: Int = RecyclerView.NO_POSITION
    var imageUrl: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ProductViewHolder(
        ItemChooseRegionRvBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        getItem(position).let { holder.bind(it) }
    }

    inner class ProductViewHolder(private val binding: ItemChooseRegionRvBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {

                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    getItem(bindingAdapterPosition).let {
                    }
                }
            }
        }

        fun bind(model: RegionsInCitiesModel) {
            binding.apply {
                tvTitle.text = model.title
                imageUrl.let {
                    Glide.with(itemView).load(imageUrl).into(ivFlag)
                }

                if (selectedPosition == bindingAdapterPosition) {
                    root.strokeColor = ContextCompat.getColor(itemView.context, R.color.black)
                } else {
                    root.strokeColor = ContextCompat.getColor(itemView.context, R.color.white)
                }
            }

            itemView.setOnClickListener {
                listener.onRegionClick(model)
                selectedPosition = bindingAdapterPosition
                notifyDataSetChanged()

//                if (binding.cvBody.isVisible) {
//                    binding.cvBody.visibility = View.GONE
//                    binding.cvHeaderIvArrow.setImageResource(R.drawable.ic_arrow_down_eramo_color)
//                } else {
//                    binding.cvBody.visibility = View.VISIBLE
//                    binding.cvHeaderIvArrow.setImageResource(R.drawable.ic_arrow_up_eramo_color)
//                }
            }
        }
    }

    fun setFlagImage(imageUrl: String) {
        this.imageUrl = imageUrl
    }

    fun setListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onRegionClick(model: RegionsInCitiesModel)
    }

    //check difference
    companion object {
        private val PRODUCT_COMPARATOR = object : DiffUtil.ItemCallback<RegionsInCitiesModel>() {
            override fun areItemsTheSame(
                oldItem: RegionsInCitiesModel,
                newItem: RegionsInCitiesModel
            ) = oldItem == newItem

            override fun areContentsTheSame(
                oldItem: RegionsInCitiesModel,
                newItem: RegionsInCitiesModel
            ) = oldItem == newItem
        }
    }
}