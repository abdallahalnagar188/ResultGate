package eramo.resultgate.presentation.adapters.recycleview.vertical

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import eramo.resultgate.data.remote.dto.home.HomeCategoriesResponse
import eramo.resultgate.databinding.ItemChoiceBinding
import javax.inject.Inject

class ChoiceFilterManufacturerAdapter @Inject constructor() :
    ListAdapter<HomeCategoriesResponse.Data.SubCatagory, ChoiceFilterManufacturerAdapter.ProductViewHolder>(
        PRODUCT_COMPARATOR
    ) {
    private lateinit var listener: OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ProductViewHolder(
        ItemChoiceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        getItem(position).let { holder.bind(it) }
    }

    inner class ProductViewHolder(private val binding: ItemChoiceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.itemChoiceCheckBox.isClickable = true
        }

        fun bind(model: HomeCategoriesResponse.Data.SubCatagory) {
            binding.apply {
                itemChoiceCheckBox.text = model.title.toString().trim()
                itemChoiceCheckBox.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) listener.addToSelectedList(model.id.toString())
                    else listener.removeFromSelectedList(model.id.toString())
                }
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
        fun onChoiceClick(model: HomeCategoriesResponse.Data.SubCatagory){}
        fun addToSelectedList(id: String)
        fun removeFromSelectedList(id: String)
    }

    //check difference
    companion object {
        private val PRODUCT_COMPARATOR = object : DiffUtil.ItemCallback<HomeCategoriesResponse.Data.SubCatagory>() {
            override fun areItemsTheSame(
                oldItem: HomeCategoriesResponse.Data.SubCatagory,
                newItem: HomeCategoriesResponse.Data.SubCatagory
            ) = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: HomeCategoriesResponse.Data.SubCatagory,
                newItem: HomeCategoriesResponse.Data.SubCatagory
            ) = oldItem == newItem
        }
    }
}