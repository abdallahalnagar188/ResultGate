package eramo.tahoon.presentation.adapters.recycleview.horizontal

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import eramo.tahoon.databinding.ItemPagerSliderBinding
import eramo.tahoon.domain.model.OffersModel

class PagerSliderAdapter(private val viewPager: ViewPager2, val offersList: MutableList<OffersModel>) :
    RecyclerView.Adapter<PagerSliderAdapter.ProductViewHolder>() {
    private lateinit var listener: OnItemClickListener
    private val run = Runnable {
        offersList.addAll(offersList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ProductViewHolder(
        ItemPagerSliderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        offersList[position].let { holder.bind(it) }
        if (position == offersList.size - 2) viewPager.post(run)
    }

    inner class ProductViewHolder(private val binding: ItemPagerSliderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    offersList[bindingAdapterPosition].let {
                        listener.onOfferClick(it)
                    }
                }
            }
        }

        fun bind(model: OffersModel) {
            binding.apply {
                Glide.with(itemView)
//                    .load(EramoApi.ADS_SLIDER_IMAGE_URL + model.image)
                    .load(model.image)
                    .into(this.itemPagerSliderIv)
            }
        }
    }

    fun setListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onOfferClick(model: OffersModel)
    }

    override fun getItemCount(): Int = offersList.size
}