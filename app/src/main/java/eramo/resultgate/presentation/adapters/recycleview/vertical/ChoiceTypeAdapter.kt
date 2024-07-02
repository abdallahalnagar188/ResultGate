package eramo.resultgate.presentation.adapters.recycleview.vertical

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import eramo.resultgate.databinding.ItemChoiceBinding
import eramo.resultgate.domain.model.drawer.preview.AcTypesModel
import javax.inject.Inject

class ChoiceTypeAdapter @Inject constructor() :
    ListAdapter<AcTypesModel, ChoiceTypeAdapter.ProductViewHolder>(PRODUCT_COMPARATOR) {
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

        fun bind(model: AcTypesModel) {
            binding.apply {
                itemChoiceCheckBox.text = model.title
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
        fun onChoiceClick(model: AcTypesModel)
    }

    //check difference
    companion object {
        private val PRODUCT_COMPARATOR = object : DiffUtil.ItemCallback<AcTypesModel>() {
            override fun areItemsTheSame(
                oldItem: AcTypesModel,
                newItem: AcTypesModel
            ) = oldItem.typeId == newItem.typeId

            override fun areContentsTheSame(
                oldItem: AcTypesModel,
                newItem: AcTypesModel
            ) = oldItem == newItem
        }
    }
}