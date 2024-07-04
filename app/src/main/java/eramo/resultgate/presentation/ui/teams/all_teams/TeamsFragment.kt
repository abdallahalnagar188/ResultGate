package eramo.resultgate.presentation.ui.teams.all_teams

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import eramo.resultgate.R
import eramo.resultgate.databinding.FragmentTeamsBinding
import eramo.resultgate.domain.model.teams.TeamsModel
import eramo.resultgate.presentation.ui.dialog.LoadingDialog
import eramo.resultgate.presentation.ui.teams.team_details.TeamDetailsFragmentArgs
import eramo.resultgate.presentation.viewmodel.SharedViewModel
import eramo.resultgate.util.LocalHelperUtil
import eramo.resultgate.util.UserUtil
import eramo.resultgate.util.navOptionsAnimation
import eramo.resultgate.util.showToast
import eramo.resultgate.util.state.UiState
import javax.inject.Inject

@AndroidEntryPoint
class TeamsFragment : Fragment(), AllTeamsAdapter.OnItemClickListener, MyTeamsAdapter.OnItemClickListener {
    private lateinit var binding: FragmentTeamsBinding

    @Inject
    lateinit var allTeamsAdapter: AllTeamsAdapter

    @Inject
    lateinit var myTeamsAdapter: MyTeamsAdapter


    val viewModel by viewModels<TeamsViewModel>()
    private val viewModelShared: SharedViewModel by activityViewModels()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        LocalHelperUtil.loadLocal(requireActivity())
        binding = FragmentTeamsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindTabs()
        tabsClickListeners()
        getApis()
        initViews()
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
        lifecycleScope.launchWhenStarted {
            viewModel.getAllTeamsState.collect(){
                allTeamsAdapter.submitData(it!!)
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.getMyTeamsState.collect(){
                    when(it){
                        is UiState.Success ->{
                            val data = it.data?.data?.data
                            val list = mutableListOf<TeamsModel>()
                            if (data != null) {
                                for (item in data){
                                    list.add(item.toTeamModel())
                                }
                            }

                            myTeamsAdapter.submitList(list)
                            LoadingDialog.dismissDialog()
                        }
                        is UiState.Loading->{
                            LoadingDialog.showDialog()
                        }
                        is UiState.Error->{



                            LoadingDialog.dismissDialog()
                        }
                        else ->{}
                    }
                }
            }
        }
    }

    private fun initViews() {
        binding.rvTeams.adapter = allTeamsAdapter
        allTeamsAdapter.setListener(this)

        binding.rvMyTeams.adapter = myTeamsAdapter
        myTeamsAdapter.setListener(this)

    }

    private fun getApis() {
        viewModel.getAllTeams()
        viewModel.getMyTeams()
    }

    private fun bindTabs() {
        val allTab = binding.tabLayout.newTab()
        allTab.text = getText(R.string.all_teamss)
        allTab.id = 0
        binding.tabLayout.addTab(allTab)

        val myTab = binding.tabLayout.newTab()
        myTab.text = getText(R.string.my_teams)
        myTab.id = 1
        binding.tabLayout.addTab(myTab)

        for (i in 0 until binding.tabLayout.tabCount) {
            val tab = (binding.tabLayout.getChildAt(0) as ViewGroup).getChildAt(i)
            val p = tab.layoutParams as ViewGroup.MarginLayoutParams
            p.marginEnd = 12
            p.marginStart = 12
            tab.requestLayout()
        }
        binding.tabLayout.getTabAt(0)?.select()

    }
    private fun tabsClickListeners() {
        binding.tabLayout.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    var tabIndex = tab?.position
                    if (tabIndex == 0){
                        binding.rvTeams.visibility = View.VISIBLE
                        binding.rvMyTeams.visibility = View.GONE
                    }else if (tabIndex ==1){
                        binding.rvMyTeams.visibility = View.VISIBLE
                        binding.rvTeams.visibility = View.GONE
                    }
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                }
            }
        )
    }


    override fun onTeamClick(model: TeamsModel) {
        findNavController().navigate(R.id.teamDetailsFragment,TeamDetailsFragmentArgs(model).toBundle(), navOptionsAnimation())
    }

    override fun onMyTeamClick(model: TeamsModel) {
        findNavController().navigate(R.id.teamDetailsFragment,TeamDetailsFragmentArgs(model).toBundle(), navOptionsAnimation())
    }


}