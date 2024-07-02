package eramo.tahoon.presentation.adapters.recycleview.vertical

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import eramo.tahoon.databinding.ItemChoiceBinding
import eramo.tahoon.domain.model.drawer.preview.FloorTypesModel
import javax.inject.Inject

class ChoiceFloorAdapter @Inject constructor() :
    ListAdapter<FloorTypesModel, ChoiceFloorAdapter.ProductViewHolder>(PRODUCT_COMPARATOR) {
    private lateinit var listener: OnItemClickListener
    private var currentSelectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ProductViewHolder(
        ItemChoiceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        getItem(position).let { holder.bind(it) }
    }

    inner class ProductViewHolder(private val binding: ItemChoiceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    currentSelectedPosition = bindingAdapterPosition
                    getItem(bindingAdapterPosition).let { listener.onChoiceClick(it) }
                    notifyDataSetChanged()
                }
            }
        }

        fun bind(model: FloorTypesModel) {
            binding.apply {
                itemChoiceCheckBox.text = model.floorName
                itemChoiceCheckBox.isChecked = (bindingAdapterPosition == currentSelectedPosition)
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
        fun onChoiceClick(model: FloorTypesModel)
    }

    //check difference
    companion object {
        private val PRODUCT_COMPARATOR = object : DiffUtil.ItemCallback<FloorTypesModel>() {
            override fun areItemsTheSame(
                oldItem: FloorTypesModel,
                newItem: FloorTypesModel
            ) = oldItem.floorId == newItem.floorId

            override fun areContentsTheSame(
                oldItem: FloorTypesModel,
                newItem: FloorTypesModel
            ) = oldItem == newItem
        }
    }
}