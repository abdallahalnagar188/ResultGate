package eramo.tahoon.presentation.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import eramo.tahoon.R
import eramo.tahoon.databinding.DialogCancelBinding
import eramo.tahoon.presentation.ui.auth.LoginFragmentArgs
import eramo.tahoon.presentation.viewmodel.SharedViewModel
import eramo.tahoon.presentation.viewmodel.dialog.SuspendAccountDialogViewModel
import eramo.tahoon.util.UserUtil
import eramo.tahoon.util.showToast
import eramo.tahoon.util.state.UiState


@AndroidEntryPoint
class SuspendAccountDialog : DialogFragment(R.layout.dialog_suspend_account) {
    private lateinit var binding: DialogCancelBinding
    private val viewModel by viewModels<SuspendAccountDialogViewModel>()
    private val viewModelShared: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setStyle(STYLE_NORMAL, R.style.DialogStyle)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DialogCancelBinding.bind(view)

        binding.apply {
            DFCancelBtnCancel.setOnClickListener { findNavController().popBackStack() }

            DFCancelBtnConfirm.setOnClickListener { viewModel.suspendAccount() }
        }

        fetchSuspendAccountState()
        handleLoadingCancellation()
    }

    private fun fetchSuspendAccountState() {
        lifecycleScope.launchWhenCreated {
            viewModel.suspendAccountState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()
                        showToast(state.data?.message ?: getString(R.string.success))
                        clearUserData()

                        findNavController().popBackStack()
                        Navigation.findNavController(requireActivity(), R.id.main_navHost)
                            .navigate(
                                R.id.loginFragment,
                                LoginFragmentArgs(false).toBundle(),
                                NavOptions.Builder().setPopUpTo(R.id.suspendAccountDialog, true).build()
                            )
                    }

                    is UiState.Error -> {
                        LoadingDialog.dismissDialog()
                        showToast(state.message!!.asString(requireContext()))
                        clearUserData()
                    }

                    is UiState.Empty -> {
                        LoadingDialog.dismissDialog()
                    }

                    is UiState.Loading -> {
                        LoadingDialog.showDialog()
                    }
                }
            }
        }
    }

    private fun clearUserData(){
        UserUtil.clearUserInfo()
        UserUtil.isRememberUser()
        viewModelShared.profileData.value = null
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