package eramo.resultgate.presentation.ui.teams.team_details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import eramo.resultgate.databinding.FragmentTeamDetailsBinding
import eramo.resultgate.domain.model.teams.TeamsModel
import eramo.resultgate.util.LocalHelperUtil


class TeamDetailsFragment : Fragment() {
    private lateinit var binding: FragmentTeamDetailsBinding
    private val args by navArgs<TeamDetailsFragmentArgs>()
    val model get() = args.team

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        LocalHelperUtil.loadLocal(requireActivity())
        binding = FragmentTeamDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(model)
    }

    private fun initViews(model: TeamsModel) {
        binding.apply {
            tvTeamName.text = model.name
            tvPackageQuantityValue.text = model.grams.toInt().toString()
            tvCapNumberValue.text = model.cap.toString()
            tvPrice.text = model.realPrice
            tvCompleteValue.text = model.completed.toString()
            rangeSlider.setValues(0.0f,model.gramsBought.toFloat())
            joinedValue.text = model.joinedMembers.toString()
            tvLeftValue.text = model.remainingGrams.toInt().toString()
            tvBoughtValue.text = model.gramsBought.toInt().toString()
            tvDescriptionContent.text = model.details

            Glide.with(requireContext()).load(model.primaryImage).into(ivTeam)
            rangeSlider.setOnTouchListener { v, event ->
                true
            }
            if (model.isCompleted&&model.isJoined){
                // no cancel and joined green button
                joinedCardview.visibility = View.VISIBLE
                joinTeamCardview.visibility = View.GONE
                completedCardview.visibility = View.GONE
                cancelCardview.visibility = View.GONE

            }
            if (model.isCompleted&&!model.isJoined){
                // button completed gray no join
                joinedCardview.visibility = View.GONE
                joinTeamCardview.visibility = View.GONE
                completedCardview.visibility = View.VISIBLE
                cancelCardview.visibility = View.GONE

            }

            if (!model.isCompleted&&model.isJoined){
                // joined button with cancel option
                joinedCardview.visibility = View.VISIBLE
                joinTeamCardview.visibility = View.GONE
                completedCardview.visibility = View.GONE
                cancelCardview.visibility = View.VISIBLE
            }
            if (!model.isCompleted&&!model.isJoined){
                // button join and no cancel
                joinedCardview.visibility = View.GONE
                joinTeamCardview.visibility = View.VISIBLE
                completedCardview.visibility = View.GONE
                cancelCardview.visibility = View.GONE

            }

        }

    }
}