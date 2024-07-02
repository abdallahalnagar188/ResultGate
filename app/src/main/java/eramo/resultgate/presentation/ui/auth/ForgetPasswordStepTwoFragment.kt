package eramo.resultgate.presentation.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import eramo.resultgate.R
import eramo.resultgate.databinding.FragmentForgetPasswordStepTwoBinding
import eramo.resultgate.presentation.ui.dialog.LoadingDialog
import eramo.resultgate.presentation.viewmodel.auth.ForgetPasswordStepTwoViewModel
import eramo.resultgate.util.Constants.API_SUCCESS
import eramo.resultgate.util.LocalHelperUtil
import eramo.resultgate.util.navOptionsAnimation
import eramo.resultgate.util.showToast
import eramo.resultgate.util.state.UiState
import org.json.JSONObject

@AndroidEntryPoint
class ForgetPasswordStepTwoFragment : Fragment(R.layout.fragment_forget_password_step_two) {
    private lateinit var binding: FragmentForgetPasswordStepTwoBinding
    private val viewModel by viewModels<ForgetPasswordStepTwoViewModel>()
    private val args by navArgs<ForgetPasswordStepTwoFragmentArgs>()
    private val email get() = args.email

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
        binding = FragmentForgetPasswordStepTwoBinding.bind(view)

        handleLoadingCancellation()

        binding.FForgetBtnNext.setOnClickListener {
            if (binding.FForgetEtCode.text.isNullOrEmpty()) {
                showToast(getString(R.string.enter_your_validation_code))
            } else {
                validateCode()
                fetchCodeValidationState()
            }
        }
    }

    private fun validateCode() {
        viewModel.validateCode(binding.FForgetEtCode.text.toString().trim(), email)
    }

    private fun fetchCodeValidationState() {
        lifecycleScope.launchWhenCreated {
            viewModel.codeValidationState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()

                        if (state.data?.status == API_SUCCESS){
                            showToast(state.data.message ?: getString(R.string.success))

                            findNavController().navigate(
                                R.id.forgetPasswordStepThreeFragment, ForgetPasswordStepThreeFragmentArgs(
                                    binding.FForgetEtCode.text.toString().trim(), email
                                ).toBundle(), navOptionsAnimation()
                            )
                        }else{
                            showToast(state.data?.message ?: getString(R.string.error))
                        }

                    }
                    is UiState.Error -> {
                        LoadingDialog.dismissDialog()
                        val string = state.message!!.asString(requireContext())

                        try {
                            val jsonErrorBody = JSONObject(string)
                            val errorMessage = jsonErrorBody.getString("errors")
                            showToast(errorMessage)
                        } catch (e: Exception) {
                            showToast(string)
                        }
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