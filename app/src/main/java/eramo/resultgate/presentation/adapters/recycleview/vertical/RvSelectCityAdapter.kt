package eramo.resultgate.presentation.adapters.recycleview.vertical

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import eramo.resultgate.R
import eramo.resultgate.databinding.ItemChooseCityRvBinding
import eramo.resultgate.domain.model.auth.CitiesWithRegionsModel
import eramo.resultgate.domain.model.auth.RegionsInCitiesModel
import javax.inject.Inject

class RvSelectCityAdapter @Inject constructor() :
    ListAdapter<CitiesWithRegionsModel, RvSelectCityAdapter.ProductViewHolder>(PRODUCT_COMPARATOR),
    RvSelectedRegionAdapter.OnItemClickListener {
    private lateinit var listener: OnItemClickListener
    var selectedPosition: Int = RecyclerView.NO_POSITION
    var imageUrl: String = ""
//    private var selectedRegionId: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ProductViewHolder(
        ItemChooseCityRvBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        getItem(position).let { holder.bind(it) }
    }

    inner class ProductViewHolder(private val binding: ItemChooseCityRvBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    getItem(bindingAdapterPosition).let {
                    }
                }
            }
        }

        fun bind(model: CitiesWithRegionsModel) {
            binding.apply {
                tvTitle.text = model.title
                imageUrl.let {
                    Glide.with(itemView).load(imageUrl).into(ivFlag)
                }

                // inner RV
                val rvSelectedRegionAdapter = RvSelectedRegionAdapter()
                rvRegions.adapter = rvSelectedRegionAdapter
                rvSelectedRegionAdapter.setListener(this@RvSelectCityAdapter)

                rvSelectedRegionAdapter.setFlagImage(imageUrl)
                rvSelectedRegionAdapter.submitList(model.regionsList)
            }



            itemView.setOnClickListener {
//                listener.onCityClick(model, imageUrl)
                selectedPosition = bindingAdapterPosition
                notifyDataSetChanged()

                if (binding.cvBody.isVisible) {
                    binding.cvBody.visibility = View.GONE
                    binding.cvHeaderIvArrow.setImageResource(R.drawable.ic_arrow_down_eramo_color)
                } else {
                    binding.cvBody.visibility = View.VISIBLE
                    binding.cvHeaderIvArrow.setImageResource(R.drawable.ic_arrow_up_eramo_color)
                }
            }
        }
    }

    fun setFlagImage(imageUrl: String) {
        this.imageUrl = imageUrl
    }

//    fun getSelectedRegionId() = selectedRegionId

    fun setListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onSelectionDone(model: RegionsInCitiesModel,imageUrl: String)
    }

    override fun onRegionClick(model: RegionsInCitiesModel) {
//        selectedRegionId = model.id
        listener.onSelectionDone(model,this.imageUrl)
    }

    //check difference
    companion object {
        private val PRODUCT_COMPARATOR = object : DiffUtil.ItemCallback<CitiesWithRegionsModel>() {
            override fun areItemsTheSame(
                oldItem: CitiesWithRegionsModel,
                newItem: CitiesWithRegionsModel
            ) = oldItem == newItem

            override fun areContentsTheSame(
                oldItem: CitiesWithRegionsModel,
                newItem: CitiesWithRegionsModel
            ) = oldItem == newItem
        }
    }
}