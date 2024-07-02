package eramo.tahoon.presentation.adapters.recycleview.vertical

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import eramo.tahoon.R
import eramo.tahoon.databinding.ItemBrandsFilterBinding
import eramo.tahoon.domain.model.home.HomeBrandsModel
import javax.inject.Inject

class RvFilterBrandsAdapter @Inject constructor() :
    ListAdapter<HomeBrandsModel, RvFilterBrandsAdapter.ProductViewHolder>(PRODUCT_COMPARATOR) {
    private val selectedItems = mutableListOf<HomeBrandsModel>()

//    private lateinit var listener: OnItemClickListener
//    private var selectedManufacturersId = ArrayList<String>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ProductViewHolder(
        ItemBrandsFilterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        getItem(position).let { holder.bind(it) }
    }

    inner class ProductViewHolder(private val binding: ItemBrandsFilterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
//                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
//                    getItem(bindingAdapterPosition).let {
//                    }
//                    selectedPosition = bindingAdapterPosition
//                }
            }
        }

        fun bind(model: HomeBrandsModel) {
            binding.apply {
                tvTitle.text = model.name

                if (selectedItems.contains(model)) {
                    ivCheckbox.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ic_checkbox_checked))
                } else {
                    ivCheckbox.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ic_checkbox_unchecked))
                }

                itemView.setOnClickListener {
                    if (!selectedItems.contains(model)) {
                        selectedItems.add(model)
                        notifyItemChanged(bindingAdapterPosition)
                    } else {
                        selectedItems.remove(model)
                        notifyItemChanged(bindingAdapterPosition)
                    }
                }


            }
        }
    }

    fun getSelectedBrands(): ArrayList<String> {
        val list = ArrayList<String>()

        for (i in selectedItems){
            list.add(i.id.toString())
        }

        return list
    }

    fun clearSelectedBrands() = selectedItems.clear()

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

//    fun setListener(listener: OnItemClickListener) {
//        this.listener = listener
//    }

//    interface OnItemClickListener {
//        fun onChoiceClick(model: HomeBrandsModel)
//    }

    //check difference
    companion object {
        private val PRODUCT_COMPARATOR = object : DiffUtil.ItemCallback<HomeBrandsModel>() {
            override fun areItemsTheSame(
                oldItem: HomeBrandsModel,
                newItem: HomeBrandsModel
            ) = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: HomeBrandsModel,
                newItem: HomeBrandsModel
            ) = oldItem == newItem
        }
    }
}