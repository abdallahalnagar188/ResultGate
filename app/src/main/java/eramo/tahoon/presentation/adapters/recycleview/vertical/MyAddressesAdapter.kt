package eramo.tahoon.presentation.adapters.recycleview.vertical

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import eramo.tahoon.data.remote.dto.drawer.myaccount.myaddresses.GetMyAddressesResponse
import eramo.tahoon.databinding.ItemMyAddressesBinding
import javax.inject.Inject

class MyAddressesAdapter @Inject constructor() :
    ListAdapter<GetMyAddressesResponse.Data, MyAddressesAdapter.ProductViewHolder>(PRODUCT_COMPARATOR) {
    private lateinit var listener: OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ProductViewHolder(
        ItemMyAddressesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        getItem(position).let { holder.bind(it) }
    }

    inner class ProductViewHolder(private val binding: ItemMyAddressesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
//            binding.root.setOnClickListener {
//                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
//                    getItem(bindingAdapterPosition).let {
//                        listener.onAddressClick(it)
//                    }
//                }
//            }
        }

        fun bind(model: GetMyAddressesResponse.Data) {
            binding.apply {
                tvAddressType.text = model.addressType
                tvAddressValue.text = model.address

                ivTrash.setOnClickListener { listener.onAddressDeleteClick(model) }
                ivEdit.setOnClickListener { listener.onAddressEditClick(model) }

            }
        }
    }

    fun setListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onAddressDeleteClick(model: GetMyAddressesResponse.Data)
        fun onAddressEditClick(model: GetMyAddressesResponse.Data)
    }

    //check difference
    companion object {
        private val PRODUCT_COMPARATOR = object : DiffUtil.ItemCallback<GetMyAddressesResponse.Data>() {
            override fun areItemsTheSame(
                oldItem: GetMyAddressesResponse.Data,
                newItem: GetMyAddressesResponse.Data
            ) = oldItem == newItem

            override fun areContentsTheSame(
                oldItem: GetMyAddressesResponse.Data,
                newItem: GetMyAddressesResponse.Data
            ) = oldItem == newItem
        }
    }
}