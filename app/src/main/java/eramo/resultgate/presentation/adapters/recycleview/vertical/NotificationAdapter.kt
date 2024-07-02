package eramo.resultgate.presentation.adapters.recycleview.vertical

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import eramo.resultgate.R
import eramo.resultgate.data.remote.dto.NotificationResponse
import eramo.resultgate.databinding.ItemNotificationBinding
import javax.inject.Inject

class NotificationAdapter @Inject constructor() :
    ListAdapter<NotificationResponse.Data, NotificationAdapter.ProductViewHolder>(PRODUCT_COMPARATOR) {
    private lateinit var listener: OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ProductViewHolder(
        ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        getItem(position).let { holder.bind(it) }
    }

    inner class ProductViewHolder(private val binding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    getItem(bindingAdapterPosition).let {
                        listener.onNotificationClick(it)
                    }
                }
            }
        }

        fun bind(model: NotificationResponse.Data) {
            binding.apply {
                itemCategoryTvTitle.text = model.title
                itemCategoryTvBody.text = model.body
                itemCategoryTvTime.text = model.createdAt?.substring(0, 10)

                if (model.seen == 1) {
                    layout.background = itemView.resources.getDrawable(R.drawable.shape_white)
                    itemCategoryTvTime.setTextColor(itemView.resources.getColor(R.color.gray))

                    itemCategoryTvTitle.setTextColor(itemView.resources.getColor(R.color.black))
                    itemCategoryTvBody.setTextColor(itemView.resources.getColor(R.color.black))
                } else {
                    layout.background = itemView.resources.getDrawable(R.drawable.shape_eramo_color)
                    itemCategoryTvTime.setTextColor(itemView.resources.getColor(R.color.white))

                    itemCategoryTvTitle.setTextColor(itemView.resources.getColor(R.color.white))
                    itemCategoryTvBody.setTextColor(itemView.resources.getColor(R.color.white))
                }
            }
        }
    }

    fun setListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onNotificationClick(model: NotificationResponse.Data)
    }

    //check difference
    companion object {
        private val PRODUCT_COMPARATOR = object : DiffUtil.ItemCallback<NotificationResponse.Data>() {
            override fun areItemsTheSame(
                oldItem: NotificationResponse.Data,
                newItem: NotificationResponse.Data
            ) = oldItem == newItem

            override fun areContentsTheSame(
                oldItem: NotificationResponse.Data,
                newItem: NotificationResponse.Data
            ) = oldItem == newItem
        }
    }
}