package eramo.resultgate.presentation.adapters.recycleview.vertical

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import eramo.resultgate.R
import eramo.resultgate.data.remote.dto.drawer.myaccount.MyQueriesResponse
import eramo.resultgate.databinding.ItemQueryBinding
import javax.inject.Inject

class QueryAdapter @Inject constructor() :
//    ListAdapter<RequestModel, QueryAdapter.ProductViewHolder>(PRODUCT_COMPARATOR) {
    ListAdapter<MyQueriesResponse.Data.MyQueryModel, QueryAdapter.ProductViewHolder>(PRODUCT_COMPARATOR) {

    private lateinit var listener: OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ProductViewHolder(
        ItemQueryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        getItem(position).let { holder.bind(it) }
    }

    inner class ProductViewHolder(private val binding: ItemQueryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    getItem(bindingAdapterPosition).let {
                        listener.onQueryClick(it)
                    }
                }
            }
        }

        fun bind(model: MyQueriesResponse.Data.MyQueryModel) {
            binding.apply {
//                itemQueryTvTypeName.text = ""
//                itemQueryTvSubject.text = model.type
//                itemQueryTvEmail.text = model.email
//                itemQueryTvMobile.text = model.mobile
                itemQueryTvMessage.text = model.message

                if (bindingAdapterPosition % 2 == 0) root.setBackgroundResource(R.drawable.shape_gray)
                else root.setBackgroundResource(R.drawable.shape_white)
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
        fun onQueryClick(model: MyQueriesResponse.Data.MyQueryModel)
    }

    //check difference
    companion object {
        private val PRODUCT_COMPARATOR = object : DiffUtil.ItemCallback<MyQueriesResponse.Data.MyQueryModel>() {
            override fun areItemsTheSame(
                oldItem: MyQueriesResponse.Data.MyQueryModel,
                newItem: MyQueriesResponse.Data.MyQueryModel
            ) = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: MyQueriesResponse.Data.MyQueryModel,
                newItem: MyQueriesResponse.Data.MyQueryModel
            ) = oldItem == newItem
        }
    }
}