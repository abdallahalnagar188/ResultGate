package eramo.tahoon.presentation.ui.auth

import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import eramo.tahoon.R
import eramo.tahoon.databinding.FragmentForgetPasswordBinding
import eramo.tahoon.presentation.ui.dialog.LoadingDialog
import eramo.tahoon.presentation.viewmodel.auth.ForgotPassViewModel
import eramo.tahoon.util.Constants.API_SUCCESS
import eramo.tahoon.util.LocalHelperUtil
import eramo.tahoon.util.navOptionsAnimation
import eramo.tahoon.util.showToast
import eramo.tahoon.util.state.UiState

@AndroidEntryPoint
class ForgetPasswordFragment : Fragment(R.layout.fragment_forget_password) {

    private lateinit var binding: FragmentForgetPasswordBinding
    private val viewModel by viewModels<ForgotPassViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        LocalHelperUtil.loadLocal(requireActivity())
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentForgetPasswordBinding.bind(view)

        handleLoadingCancellation()
        binding.FForgetBtnForget.setOnClickListener { setupForget() }
    }

    private fun setupForget() {
        val email = binding.FForgetEtEmail.text.toString().trim()

        if (TextUtils.isEmpty(email)) {
            binding.itlEmail.error = getString(R.string.txt_email_is_required)
            return
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.itlEmail.error = getString(R.string.txt_please_enter_a_valid_email_address)
            return
        } else binding.itlEmail.error = null

        viewModel.forgotPassApp(email)
        fetchForgotPassState()
    }

    private fun fetchForgotPassState() {
        lifecycleScope.launchWhenCreated {
            viewModel.forgotPassState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()

                        if (state.data?.status == API_SUCCESS) {
                           // showToast(state.data.message ?: getString(R.string.success))
                            showToast(getString(R.string.please_check_your_inbox_or_spam_mail))
                            findNavController().navigate(
                                R.id.forgetPasswordStepTwoFragment4, ForgetPasswordStepTwoFragmentArgs(
                                    binding.FForgetEtEmail.text.toString().trim()
                                ).toBundle(), navOptionsAnimation()
                            )
                        } else if (state.data?.status == 0) {
                            showToast(state.data.message ?: getString(R.string.error))
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