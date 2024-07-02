package eramo.tahoon.presentation.adapters.recycleview.vertical

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import eramo.tahoon.R
import eramo.tahoon.databinding.ItemPaymentBinding
import eramo.tahoon.domain.model.products.PaymentTypesModel
import javax.inject.Inject

class PaymentAdapter @Inject constructor() :
    ListAdapter<PaymentTypesModel, PaymentAdapter.ProductViewHolder>(PRODUCT_COMPARATOR) {
    private lateinit var listener: OnItemClickListener
    private var currentSelectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ProductViewHolder(
        ItemPaymentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        getItem(position).let { holder.bind(it) }
    }

    inner class ProductViewHolder(private val binding: ItemPaymentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    currentSelectedPosition = bindingAdapterPosition
                    getItem(bindingAdapterPosition).let { listener.onPaymentClick(it) }
                    notifyDataSetChanged()
                }
            }
        }

        fun bind(model: PaymentTypesModel) {
            binding.apply {
                if (currentSelectedPosition == bindingAdapterPosition)
                    binding.root.setBackgroundResource(R.drawable.stroke_red_radius)
                else binding.root.setBackgroundResource(R.drawable.shape_white)

                itemPaymentTvTitle.text = model.title
//                Glide.with(itemView)
//                    .load(EramoApi.IMAGE_URL_GENERAL + model.imgUrl)
//                    .into(this.itemPaymentIv)
                if (model.title == itemView.context.getString(R.string.cash_on_delivery)) {
                    Glide.with(itemView)
                        .load(R.drawable.ic_wallet)
                        .into(this.itemPaymentIv)
                } else if (model.title == itemView.context.getString((R.string.online_payment))) {
                    Glide.with(itemView)
                        .load(R.drawable.ic_visa)
                        .into(this.itemPaymentIv)
                }
                if (model.title == itemView.context.getString(R.string.online_payment)){
                    binding.root.setBackgroundResource(R.drawable.shape_white)
                    binding.root.alpha = 0.5f
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
        fun onPaymentClick(model: PaymentTypesModel)
    }

    //check difference
    companion object {
        private val PRODUCT_COMPARATOR = object : DiffUtil.ItemCallback<PaymentTypesModel>() {
            override fun areItemsTheSame(
                oldItem: PaymentTypesModel,
                newItem: PaymentTypesModel
            ) = oldItem.payId == newItem.payId

            override fun areContentsTheSame(
                oldItem: PaymentTypesModel,
                newItem: PaymentTypesModel
            ) = oldItem == newItem
        }
    }
}