package eramo.tahoon.presentation.ui.drawer.myaccount

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import eramo.tahoon.R
import eramo.tahoon.data.remote.dto.general.Member
import eramo.tahoon.databinding.FragmentMyAccountBinding
import eramo.tahoon.presentation.ui.dialog.LoadingDialog
import eramo.tahoon.presentation.viewmodel.SharedViewModel
import eramo.tahoon.presentation.viewmodel.drawer.myaccount.MyAccountViewModel
import eramo.tahoon.util.LocalHelperUtil
import eramo.tahoon.util.NavKeys
import eramo.tahoon.util.navOptionsAnimation
import eramo.tahoon.util.onBackPressed
import eramo.tahoon.util.showToast
import eramo.tahoon.util.state.UiState

@AndroidEntryPoint
class MyAccountFragment : Fragment(R.layout.fragment_my_account), View.OnClickListener {

    private val viewModel by viewModels<MyAccountViewModel>()
    private val viewModelShared: SharedViewModel by activityViewModels()
    private lateinit var binding: FragmentMyAccountBinding
    private var memberModel: Member? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        LocalHelperUtil.loadLocal(requireActivity())
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMyAccountBinding.bind(view)

        binding.apply {
            FMyAccountTvEdit.setOnClickListener(this@MyAccountFragment)
            FMyAccountTvAddresses.setOnClickListener(this@MyAccountFragment)
            FMyAccountTvChangePassword.setOnClickListener(this@MyAccountFragment)
            FMyAccountTvMyOrders.setOnClickListener(this@MyAccountFragment)
            FMyAccountTvMyQueries.setOnClickListener(this@MyAccountFragment)
            FMyAccountTvSuspend.setOnClickListener(this@MyAccountFragment)
            FMyAccountIvBack.setOnClickListener(this@MyAccountFragment)
        }

        // Request
        viewModel.getProfile()

        // Fetch
        fetchGetProfileState()

        handleLoadingCancellation()

        this@MyAccountFragment.onBackPressed {
            lifecycleScope.launchWhenResumed{
                findNavController().navigate(R.id.homeFragment, null, navOptionsAnimation())
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.FMyAccount_tv_edit -> {
                if (memberModel == null) {
                    showToast("There is no member data")
                    return
                }
                val args = Bundle()
                args.putParcelable(NavKeys.MEMBER_MODEL, memberModel)
                findNavController().navigate(
                    R.id.editPersonalDetailsFragment, args,
                    navOptionsAnimation()
                )
            }

            R.id.FMyAccount_tv_addresses -> {
                findNavController().navigate(R.id.myAddressesFragment, null, navOptionsAnimation())
            }

            R.id.FMyAccount_tv_changePassword -> {
                if (memberModel == null) {
                    showToast("There is no member data")
                    return
                }
                val args = Bundle()
                args.putParcelable(NavKeys.MEMBER_MODEL, memberModel)
                findNavController().navigate(
                    R.id.changePasswordFragment,
                    args,
                    navOptionsAnimation()
                )
            }

            R.id.FMyAccount_tv_myOrders -> {
                findNavController().navigate(R.id.myOrdersFragment, null, navOptionsAnimation())
            }

            R.id.FMyAccount_tv_myQueries -> {
                findNavController().navigate(R.id.myQueriesFragment, null, navOptionsAnimation())
            }

            R.id.FMyAccount_tv_suspend -> {
                findNavController().navigate(R.id.suspendAccountDialog)
            }

            R.id.FMyAccount_iv_back -> findNavController().navigate(R.id.homeFragment, null, navOptionsAnimation())

//            findNavController().popBackStack()
        }
    }

    private fun fetchGetProfileState() {
        lifecycleScope.launchWhenCreated {
            viewModel.getProfileState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()
                        memberModel = state.data
                        viewModelShared.profileData.value = memberModel
                        binding.apply {
                            FMyAccountTvName.text = memberModel?.name
                            FMyAccountTvEmail.text = memberModel?.email

                            Glide.with(requireContext())
//                                .load(EramoApi.IMAGE_URL_PROFILE + memberModel?.mImage)
                                .load(memberModel?.imageUrl)
                                .into(binding.FMyAccountCircleImage)
                        }
                    }

                    is UiState.Error -> {
                        LoadingDialog.dismissDialog()
                        showToast(state.message!!.asString(requireContext()))
                    }

                    is UiState.Loading -> {
                        LoadingDialog.showDialog()
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun handleLoadingCancellation() {
        LoadingDialog.cancelCurrentRequest.observe(viewLifecycleOwner) { isCancel ->
            if (isCancel) {
                viewModel.cancelRequest()
                LoadingDialog.dismissDialog()
                LoadingDialog.cancelCurrentRequest.value = false
            }
        }
    }
}