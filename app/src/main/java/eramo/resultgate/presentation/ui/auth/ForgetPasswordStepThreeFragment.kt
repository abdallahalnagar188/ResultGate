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
import eramo.resultgate.databinding.FragmentForgetPasswordStepThreeBinding
import eramo.resultgate.presentation.ui.dialog.LoadingDialog
import eramo.resultgate.presentation.viewmodel.auth.ForgetPasswordStepThreeViewModel
import eramo.resultgate.util.Constants
import eramo.resultgate.util.LocalHelperUtil
import eramo.resultgate.util.showToast
import eramo.resultgate.util.state.UiState
import org.json.JSONObject

@AndroidEntryPoint
class ForgetPasswordStepThreeFragment : Fragment(R.layout.fragment_forget_password_step_three) {
    lateinit var binding: FragmentForgetPasswordStepThreeBinding
    private val viewModel by viewModels<ForgetPasswordStepThreeViewModel>()
    private val args by navArgs<ForgetPasswordStepThreeFragmentArgs>()
    private val validationCode get() = args.validationCode
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
        binding = FragmentForgetPasswordStepThreeBinding.bind(view)

        handleLoadingCancellation()
        binding.btnSubmit.setOnClickListener {
            changePassword()
        }
    }

    private fun changePassword() {

        if (binding.etNewPassword.text.isNullOrEmpty()) {
            binding.etNewPassword.error = getString(R.string.please_enter_you_new_password)
            return
        } else {
            binding.etNewPassword.error = null
        }

        if (binding.etRePassword.text.isNullOrEmpty()) {
            binding.etRePassword.error = getString(R.string.please_confirm_you_new_password)
            return
        } else {
            binding.etRePassword.error = null
        }

        viewModel.changePassword(
            binding.etNewPassword.text.toString().trim(),
            binding.etRePassword.text.toString().trim(),
            email
        )
        fetchNewPasswordState()
    }

    private fun fetchNewPasswordState() {
        lifecycleScope.launchWhenCreated {
            viewModel.newPasswordState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()
                        if (state.data?.status == Constants.API_SUCCESS) {
                            showToast(state.data.message ?: getString(R.string.success))
                            findNavController().navigate(
                                R.id.loginFragment
                            )
                        } else {
                            showToast(state.data?.message ?: getString(R.string.error))
                        }


                    }

                    is UiState.Error -> {
                        LoadingDialog.dismissDialog()
                        val string = state.message!!.asString(requireContext())

                        try {
                            val jsonErrorBody = JSONObject(string)
                            val errors = jsonErrorBody.getString("errors")

                            val errorsObj = JSONObject(errors)
                            val keys = errorsObj.keys()

                            val errorMessage = errorsObj.getString(keys.next())
                            val errorMessageSub = errorMessage.substring(2, (errorMessage.length - 2))

                            showToast(errorMessageSub)
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