package eramo.resultgate.presentation.adapters.recycleview.vertical

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import eramo.resultgate.data.remote.dto.home.HomeCategoriesResponse
import eramo.resultgate.databinding.ItemCategoryProductsBinding
import javax.inject.Inject

class FilterCategoryProductsAdapter @Inject constructor() :
    ListAdapter<HomeCategoriesResponse.Data, FilterCategoryProductsAdapter.ProductViewHolder>(
        PRODUCT_COMPARATOR
    ) ,ChoiceFilterManufacturerAdapter.OnItemClickListener{
    private lateinit var listener: OnItemClickListener
    private var selectedManufacturersId = ArrayList<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ProductViewHolder(
        ItemCategoryProductsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        getItem(position).let { holder.bind(it) }
    }

    inner class ProductViewHolder(private val binding: ItemCategoryProductsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(model: HomeCategoriesResponse.Data) {
            binding.apply {
                itemCatProductsTvTitle.text = model.title
//                itemCatProductsTvTitle.text = "model.title"

                val choiceFilterManufacturerAdapter = ChoiceFilterManufacturerAdapter()
                choiceFilterManufacturerAdapter.submitList(model.subCatagories)
                choiceFilterManufacturerAdapter.setListener(this@FilterCategoryProductsAdapter)
                itemCatProductsRvProducts.adapter = choiceFilterManufacturerAdapter
            }
        }
    }

    fun getSelectedManufacturers() = selectedManufacturersId

    fun clearSelectedManufacturers() = selectedManufacturersId.clear()

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
        fun onChoiceClick(model: HomeCategoriesResponse.Data)
    }

    //check difference
    companion object {
        private val PRODUCT_COMPARATOR = object : DiffUtil.ItemCallback<HomeCategoriesResponse.Data>() {
            override fun areItemsTheSame(
                oldItem: HomeCategoriesResponse.Data,
                newItem: HomeCategoriesResponse.Data
            ) = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: HomeCategoriesResponse.Data,
                newItem: HomeCategoriesResponse.Data
            ) = oldItem == newItem
        }
    }

    override fun addToSelectedList(id: String) {
        selectedManufacturersId.add(id)
    }

    override fun removeFromSelectedList(id: String) {
        selectedManufacturersId.remove(id)
    }
}