package eramo.tahoon.presentation.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import eramo.tahoon.R
import eramo.tahoon.databinding.FragmentSignUpVerificationBinding
import eramo.tahoon.presentation.ui.dialog.LoadingDialog
import eramo.tahoon.presentation.viewmodel.auth.SignUpVerificationViewModel
import eramo.tahoon.util.Constants
import eramo.tahoon.util.LocalHelperUtil
import eramo.tahoon.util.onBackPressed
import eramo.tahoon.util.showToast
import eramo.tahoon.util.state.UiState
import org.json.JSONObject

@AndroidEntryPoint
class SignUpVerificationFragment : Fragment(R.layout.fragment_sign_up_verification) {
    private lateinit var binding: FragmentSignUpVerificationBinding
    private val viewModel by viewModels<SignUpVerificationViewModel>()
    private val args by navArgs<SignUpVerificationFragmentArgs>()
    private val email get() = args.email
    private val phone get() = args.phone
    private val password get() = args.password
    private val destination get() = args.destination

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
        binding = FragmentSignUpVerificationBinding.bind(view)
        setupToolbar()

        if (destination == Constants.DESTINATION_FROM_LOGIN){
            viewModel.sendVerifyMail(email)
        }

        binding.apply {
            FForgetBtnNext.setOnClickListener {
                if (SVerifyEtCode.text.isNullOrEmpty()) {
                    showToast(getString(R.string.txt_please_enter_verification_code))
                } else {
                    viewModel.checkVerifyMailCode(SVerifyEtCode.text.toString().trim(), email)
                }
            }
        }

        fetchSendVerifyMailState()
        fetchCheckVerifyMailCodeState()
        fetchLoginState()
        this@SignUpVerificationFragment.onBackPressed { findNavController().navigate(R.id.loginFragment) }
    }

    private fun fetchSendVerifyMailState() {
        lifecycleScope.launchWhenCreated {
            viewModel.sendVerifyMailState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()

                    }

                    is UiState.Error -> {
                        LoadingDialog.dismissDialog()
                        val string = state.message!!.asString(requireContext())
                        showToast(string)
                    }

                    is UiState.Loading -> {
                        LoadingDialog.showDialog()
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun fetchCheckVerifyMailCodeState() {
        lifecycleScope.launchWhenCreated {
            viewModel.checkVerifyMailCodeState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()

                        showToast(state.data?.message.toString())
                        viewModel.loginApp(phone, password, true)
                    }

                    is UiState.Error -> {
                        LoadingDialog.dismissDialog()
                        val string = state.message!!.asString(requireContext())
                        showToast(string)
                    }

                    is UiState.Loading -> {
                        LoadingDialog.showDialog()
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun fetchLoginState() {
        lifecycleScope.launchWhenCreated {
            viewModel.loginState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()

                        findNavController().navigate(
                            R.id.mainFragment, null,
                            NavOptions.Builder().setPopUpTo(R.id.loginFragment, true).build()
                        )
                    }

                    is UiState.Error -> {
                        LoadingDialog.dismissDialog()
                        val string = state.message!!.asString(requireContext())

                        try {
                            parseErrorResponse(string)
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

    private fun parseErrorResponse(string: String) {
        val jsonErrorBody = JSONObject(string)
        val errorMessage = jsonErrorBody.getString("errors")
        showToast(errorMessage)
    }

    private fun setupToolbar() {
        binding.apply {
            SVToolbar.setNavigationIcon(R.drawable.ic_arrow_left_yellow)
            SVToolbar.setNavigationOnClickListener {
                findNavController().navigate(R.id.loginFragment)
            }
        }
    }
}