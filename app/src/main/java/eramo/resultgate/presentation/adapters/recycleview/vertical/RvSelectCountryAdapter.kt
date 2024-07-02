package eramo.resultgate.presentation.adapters.recycleview.vertical

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import eramo.resultgate.R
import eramo.resultgate.databinding.ItemChooseCountryRvBinding
import eramo.resultgate.domain.model.auth.CountriesModel
import javax.inject.Inject

class RvSelectCountryAdapter @Inject constructor() :
    ListAdapter<CountriesModel, RvSelectCountryAdapter.ProductViewHolder>(PRODUCT_COMPARATOR) {
    private lateinit var listener: OnItemClickListener
    var selectedPosition: Int = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ProductViewHolder(
        ItemChooseCountryRvBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        getItem(position).let { holder.bind(it) }
    }

    inner class ProductViewHolder(private val binding: ItemChooseCountryRvBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
//            selectedPosition = 0
            binding.root.setOnClickListener {
                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    getItem(bindingAdapterPosition).let {
//                        listener.onCountryClick(it)

                    }
                }
            }
        }

        fun bind(model: CountriesModel) {
            binding.apply {
                tvTitle.text = model.title
                Glide.with(itemView).load(model.imageUrl).into(ivFlag)

                if (selectedPosition == bindingAdapterPosition) {
                    root.strokeColor = ContextCompat.getColor(itemView.context, R.color.black)
                } else {
                    root.strokeColor = ContextCompat.getColor(itemView.context, R.color.white)
                }
            }

            itemView.setOnClickListener {
                listener.onCountryClick(model)
                selectedPosition = bindingAdapterPosition
                notifyDataSetChanged()

            }
        }
    }

    fun setListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onCountryClick(model: CountriesModel)
    }

    //check difference
    companion object {
        private val PRODUCT_COMPARATOR = object : DiffUtil.ItemCallback<CountriesModel>() {
            override fun areItemsTheSame(
                oldItem: CountriesModel,
                newItem: CountriesModel
            ) = oldItem == newItem

            override fun areContentsTheSame(
                oldItem: CountriesModel,
                newItem: CountriesModel
            ) = oldItem == newItem
        }
    }
}