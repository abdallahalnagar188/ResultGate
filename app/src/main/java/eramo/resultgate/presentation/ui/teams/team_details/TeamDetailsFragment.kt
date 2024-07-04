package eramo.resultgate.presentation.ui.teams.team_details

import android.os.Bundle
import android.provider.Contacts.Intents.UI
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import eramo.resultgate.R
import eramo.resultgate.databinding.FragmentTeamDetailsBinding
import eramo.resultgate.domain.model.teams.TeamsModel
import eramo.resultgate.presentation.ui.dialog.LoadingDialog
import eramo.resultgate.presentation.ui.teams.all_teams.TeamsViewModel
import eramo.resultgate.presentation.viewmodel.SharedViewModel
import eramo.resultgate.util.LocalHelperUtil
import eramo.resultgate.util.UserUtil
import eramo.resultgate.util.navOptionsAnimation
import eramo.resultgate.util.state.UiState

@AndroidEntryPoint
class TeamDetailsFragment : Fragment() {
    private lateinit var binding: FragmentTeamDetailsBinding
    private val args by navArgs<TeamDetailsFragmentArgs>()
    val viewModel by viewModels<TeamsViewModel>()
    private val viewModelShared: SharedViewModel by activityViewModels()

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
        clickListeners()
        fetchData()
        setupToolbar()

    }
    private fun setupToolbar() {
        binding.apply {
            inTbLayout.toolbarIvMenu.setOnClickListener {
                viewModelShared.openDrawer.value = true
            }

            inTbLayout.toolbarInNotification.root.setOnClickListener {
                if (UserUtil.isUserLogin())
                    findNavController().navigate(
                        R.id.notificationFragment,
                        null,
                        navOptionsAnimation()
                    )
                else findNavController().navigate(R.id.loginDialog)
            }

            inTbLayout.toolbarIvProfile.setOnClickListener {
                if (UserUtil.isUserLogin())
                    findNavController().navigate(
                        R.id.myAccountFragment,
                        null,
                        navOptionsAnimation()
                    )
                else findNavController().navigate(R.id.loginDialog)
            }

            if (UserUtil.isUserLogin()) {
                Glide.with(requireContext())
                    .load(UserUtil.getUserProfileImageUrl())
                    .into(inTbLayout.toolbarIvProfile)
            }
        }
    }

    private fun fetchData() {
        fetchJoinTeam()
        fetchExitTeam()
    }

    private fun fetchExitTeam() {
        lifecycleScope.launchWhenResumed {
            viewModel.exitTeamState.collect(){
                when(it){
                    is UiState.Success->{
                        Toast.makeText(requireContext(), getString(R.string.you_exited_successfully), Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                        LoadingDialog.dismissDialog()
                    }
                    is UiState.Error->{
                        Toast.makeText(requireContext(), it.data?.massage!!, Toast.LENGTH_SHORT).show()
                        LoadingDialog.dismissDialog()
                    }
                    is UiState.Loading->{
                        LoadingDialog.showDialog()
                    }
                    else->{}
                }
            }
        }
    }

    private fun fetchJoinTeam() {
        lifecycleScope.launchWhenResumed {
            viewModel.joinTeamState.collect(){
                when(it){
                    is UiState.Success->{
                        Toast.makeText(requireContext(), getString(R.string.team_joined_successfully), Toast.LENGTH_SHORT).show()
                        LoadingDialog.dismissDialog()
                    }
                    is UiState.Error->{
                        Toast.makeText(requireContext(), it.data?.massage!!, Toast.LENGTH_SHORT).show()
                        LoadingDialog.dismissDialog()
                    }
                    is UiState.Loading->{
                        LoadingDialog.showDialog()
                    }
                    else->{}
                }
            }
        }
    }

    private fun clickListeners() {
        binding.apply {
            joinTeamCardview.setOnClickListener(){
                if (etAmount.text.toString().isNotEmpty()){
                    viewModel.joinTeam(model.id,etAmount.text.toString().toInt())
                }else{
                    Toast.makeText(requireContext(), getString(R.string.please_provide_a_quantity), Toast.LENGTH_SHORT).show()
                }
            }
            cancelCardview.setOnClickListener(){
                viewModel.exitTeam(model.id)
            }
        }
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
//            etAmount.text = model.gramsBought.toString()
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
                tvAmount.visibility = View.GONE
                itlAmount.visibility = View.GONE

            }
            if (model.isCompleted&&!model.isJoined){
                // button completed gray no join
                joinedCardview.visibility = View.GONE
                joinTeamCardview.visibility = View.GONE
                completedCardview.visibility = View.VISIBLE
                cancelCardview.visibility = View.GONE
                tvAmount.visibility = View.GONE
                itlAmount.visibility = View.GONE

            }

            if (!model.isCompleted&&model.isJoined){
                // joined button with cancel option
                joinedCardview.visibility = View.VISIBLE
                joinTeamCardview.visibility = View.GONE
                completedCardview.visibility = View.GONE
                cancelCardview.visibility = View.VISIBLE
                tvAmount.visibility = View.GONE
                itlAmount.visibility = View.GONE
            }
            if (!model.isCompleted&&!model.isJoined){
                // button join and no cancel
                joinedCardview.visibility = View.GONE
                joinTeamCardview.visibility = View.VISIBLE
                completedCardview.visibility = View.GONE
                cancelCardview.visibility = View.GONE
                tvAmount.visibility = View.VISIBLE
                itlAmount.visibility = View.VISIBLE
            }

        }

    }
}