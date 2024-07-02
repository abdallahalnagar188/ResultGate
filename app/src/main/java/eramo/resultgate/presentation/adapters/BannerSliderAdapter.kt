package eramo.resultgate.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.smarteist.autoimageslider.SliderViewAdapter
import eramo.resultgate.data.remote.dto.home.HomePageSliderResponse
import eramo.resultgate.databinding.ItemImageSliderBinding
import javax.inject.Inject

class BannerSliderAdapter @Inject constructor() :
    SliderViewAdapter<BannerSliderAdapter.ImageSliderViewHolder>() {
    private var sliderItems = emptyList<HomePageSliderResponse.Data?>()
    private lateinit var listener: OnItemClickListener

    fun renewItems(sliderItems: List<HomePageSliderResponse.Data?>) {
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

        fun bind(model: HomePageSliderResponse.Data?) {
            binding.apply {
                Glide.with(itemView)
                    .load(model?.imageUrl)
                    .into(itemImageSliderIv)
            }
            itemView.setOnClickListener {
                listener.onBannerClick(model)
            }
        }
    }

    interface OnItemClickListener {
        fun onBannerClick(adsModel: HomePageSliderResponse.Data?)
    }
}