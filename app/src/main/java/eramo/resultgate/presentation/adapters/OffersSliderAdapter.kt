package eramo.resultgate.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.smarteist.autoimageslider.SliderViewAdapter
import eramo.resultgate.data.remote.EramoApi
import eramo.resultgate.databinding.ItemImageSliderBinding
import eramo.resultgate.domain.model.OffersModel
import javax.inject.Inject

class OffersSliderAdapter @Inject constructor() :
    SliderViewAdapter<OffersSliderAdapter.ImageSliderViewHolder>() {
    private var sliderItems= emptyList<OffersModel>()
    private lateinit var listener: OnItemClickListener

    fun renewItems(sliderItems: List<OffersModel>) {
        this.sliderItems = sliderItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup) = ImageSliderViewHolder(
        ItemImageSliderBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(viewHolder: ImageSliderViewHolder, position: Int) {
        //load image into view
        viewHolder.bind(sliderItems[position])
    }

    override fun getCount() = sliderItems.size

    fun setListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    inner class ImageSliderViewHolder(private val binding: ItemImageSliderBinding) :
        ViewHolder(binding.root) {

        fun bind(model: OffersModel) {
            binding.apply {
                Glide.with(itemView)
                    .load(EramoApi.IMAGE_URL_SPECIAL_OFFERS + model.image)
                    .into(itemImageSliderIv)
            }
            itemView.setOnClickListener {
                listener.onBannerClick(model)
            }
        }
    }

    interface OnItemClickListener {
        fun onBannerClick(adsModel: OffersModel)
    }
}