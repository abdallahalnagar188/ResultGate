package eramo.tahoon.presentation.adapters.recycleview.vertical

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import eramo.tahoon.R
import eramo.tahoon.databinding.ItemExtrasBinding
import eramo.tahoon.domain.model.CartProductModel
import eramo.tahoon.domain.model.products.orders.ProductExtrasModel
import eramo.tahoon.util.QuantityUtil
import javax.inject.Inject


class RvProductExtrasAdapter @Inject constructor() :
    ListAdapter<ProductExtrasModel, RvProductExtrasAdapter.ProductViewHolder>(PRODUCT_COMPARATOR) {
    private lateinit var listener: OnItemClickListener
    private var selectedExtrasList = emptyList<CartProductModel.ProductList.Extra?>()
    private var newSelectedExtrasList = HashMap<String, String>()
//    private var newSelectedExtrasList = HashMa

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ProductViewHolder(
        ItemExtrasBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        getItem(position).let { holder.bind(it) }
    }

    inner class ProductViewHolder(private val binding: ItemExtrasBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(model: ProductExtrasModel) {
            val itemQty = QuantityUtil()
            itemQty.minQty = 0
            itemQty.maxQty = 100
            itemQty.setQty(0)

            binding.apply {
                tvTitle.text = model.name
                tvUnitPrice.text = itemView.context.getString(R.string.s_cost_per_unit, model.cost.toString())
                tvCount.text = itemQty.getQty()
                tvCost.text = itemView.context.getString(R.string.s_egp, (model.cost * itemQty.getQty().toInt()).toString())

                if (!selectedExtrasList.isNullOrEmpty()) {

                    for (i in selectedExtrasList) {
                        if (i?.id == model.id) {

                            itemQty.setQty(i.quantity?.toInt() ?: 0)
                            tvCount.text = itemQty.getQty()
                            tvCost.text = itemView.context.getString(R.string.s_egp, (model.cost * itemQty.getQty().toInt()).toString())
                        }
                    }
                }


                ivPlus.setOnClickListener {
                    tvCount.text = itemQty.increase()
                    tvCost.text = itemView.context.getString(R.string.s_egp, (model.cost * itemQty.getQty().toInt()).toString())
                    newSelectedExtrasList[model.id.toString()] = itemQty.getQty()
                }

                ivMinus.setOnClickListener {
                    tvCount.text = itemQty.decrease()
                    tvCost.text = itemView.context.getString(R.string.s_egp, (model.cost * itemQty.getQty().toInt()).toString())
                    newSelectedExtrasList[model.id.toString()] = itemQty.getQty()
                }

            }

            newSelectedExtrasList[model.id.toString()] = itemQty.getQty()
        }
    }

    fun submitSelectedExtrasList(list: List<CartProductModel.ProductList.Extra?>) {
        selectedExtrasList = list
    }

    fun getNewSelectedItems() = newSelectedExtrasList

    fun setListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    interface OnItemClickListener {
//        fun onQuantityClick(model: CartProductModel.ProductList, isIncrease: Boolean)
//        fun onRemoveClick(model: CartProductModel.ProductList)
//        fun onExtrasClick(model: CartProductModel.ProductList)
//        fun onInstallationChange(model: CartProductModel.ProductList, isChecked: Boolean)
//        fun onTotalPriceCompleted(totalPrice: Double)
    }

    //check difference
    companion object {
        private val PRODUCT_COMPARATOR = object : DiffUtil.ItemCallback<ProductExtrasModel>() {
            override fun areItemsTheSame(
                oldItem: ProductExtrasModel,
                newItem: ProductExtrasModel
            ) = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: ProductExtrasModel,
                newItem: ProductExtrasModel
            ) = oldItem == newItem
        }
    }
}