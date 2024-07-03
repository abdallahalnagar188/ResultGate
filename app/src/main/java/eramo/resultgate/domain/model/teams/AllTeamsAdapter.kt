package eramo.resultgate.domain.model.teams

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import eramo.resultgate.databinding.ItemTeamBinding
import eramo.resultgate.domain.model.products.ShopProductModel
import javax.inject.Inject

class AllTeamsAdapter @Inject constructor():
    PagingDataAdapter<TeamsModel, AllTeamsAdapter.AllTeamsViewHolder>(
        TEAMS_COMPARATOR
    )
{
    inner class AllTeamsViewHolder(val binding: ItemTeamBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(model: TeamsModel){
            binding.apply {
                tvTeamName.text = model.name
                tvPackageQuantityValue.text = model.grams.toString()
                tvCapNumberValue.text = model.cap.toString()
                tvPrice.text = model.realPrice.toString()
                tvCompleteValue.text = model.completed.toString()
                rangeSlider.setValues(0.0f,model.gramsBought.toFloat())
                joinedValue.text = model.joinedMembers.toString()
                Glide.with(itemView.context).load(model.primaryImage).into(ivTeamImage)

                if (model.isCompleted){

                }
                if (model.isJoined){

                }

                if (!model.isCompleted){

                }
                if (!model.isJoined){

                }

            }
        }
    }

    override fun onBindViewHolder(holder: AllTeamsViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllTeamsViewHolder {
        TODO("Not yet implemented")
    }

    //check difference
    companion object {
        private val TEAMS_COMPARATOR = object : DiffUtil.ItemCallback<TeamsModel>() {
            override fun areItemsTheSame(
                oldItem: TeamsModel,
                newItem: TeamsModel
            ) = (oldItem.id == newItem.id )

            override fun areContentsTheSame(
                oldItem: TeamsModel,
                newItem: TeamsModel
            ) = oldItem == newItem
        }
    }
}