package eramo.resultgate.presentation.ui.teams.all_teams

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import eramo.resultgate.databinding.ItemTeamBinding
import eramo.resultgate.domain.model.teams.TeamsModel
import javax.inject.Inject

class MyTeamsAdapter @Inject constructor(): ListAdapter<TeamsModel, MyTeamsAdapter.MyTeamsViewHolder>(PRODUCT_COMPARATOR) {
    private lateinit var listener: OnItemClickListener

    inner class MyTeamsViewHolder(val binding:ItemTeamBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(model: TeamsModel){
            binding.apply {
                tvTeamName.text = model.name
                tvPackageQuantityValue.text = model.grams.toInt().toString()
                tvCapNumberValue.text = model.cap.toString()
                tvPrice.text = model.realPrice
                tvCompleteValue.text =buildString {
                    append(model.completed.toString())
                    append("%")
                }

                rangeSlider.valueFrom = 0f
                rangeSlider.valueTo = model.gramsBought.toFloat()+model.remainingGrams.toFloat()
                rangeSlider.setValues(0.0f,model.gramsBought.toFloat())


                joinedValue.text = model.joinedMembers.toString()
                tvLeftValue.text = model.remainingGrams.toInt().toString()
                tvBoughtValue.text = model.gramsBought.toInt().toString()
                Glide.with(itemView.context).load(model.primaryImage).into(ivTeamImage)
                rangeSlider.setOnTouchListener { v, event ->
                    true
                }
                if (model.isCompleted&&model.isJoined){
                    // no cancel and joined green button
                    joinedCardview.visibility = View.VISIBLE
                    joinTeamCardview.visibility = View.GONE
                    completedCardview.visibility = View.GONE
                }
                if (model.isCompleted&&!model.isJoined){
                    // button completed gray no join
                    joinedCardview.visibility = View.GONE
                    joinTeamCardview.visibility = View.GONE
                    completedCardview.visibility = View.VISIBLE
                }

                if (!model.isCompleted&&model.isJoined){
                    // joined button with cancel option
                    joinedCardview.visibility = View.VISIBLE
                    joinTeamCardview.visibility = View.GONE
                    completedCardview.visibility = View.GONE
                }
                if (!model.isCompleted&&!model.isJoined){
                    // button join and no cancel
                    joinedCardview.visibility = View.GONE
                    joinTeamCardview.visibility = View.VISIBLE
                    completedCardview.visibility = View.GONE
                }

            }
        }
        init {
            binding.root.setOnClickListener {
                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    getItem(bindingAdapterPosition).let {
                        listener.onMyTeamClick(it)
                    }
                }
            }
        }

    }
    override fun onBindViewHolder(holder: MyTeamsViewHolder, position: Int) {
        getItem(position).let { it?.let { it1 -> holder.bind(it1) } }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        MyTeamsViewHolder(ItemTeamBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    fun setListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onMyTeamClick(model: TeamsModel)
    }
    //check difference
    companion object {
        private val PRODUCT_COMPARATOR = object : DiffUtil.ItemCallback<TeamsModel>() {
            override fun areItemsTheSame(
                oldItem: TeamsModel,
                newItem: TeamsModel
            ) = oldItem == newItem

            override fun areContentsTheSame(
                oldItem: TeamsModel,
                newItem: TeamsModel
            ) = oldItem == newItem
        }
    }
}