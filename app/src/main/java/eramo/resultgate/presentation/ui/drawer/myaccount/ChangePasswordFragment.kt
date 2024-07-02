package eramo.resultgate.presentation.ui.drawer.myaccount

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import eramo.resultgate.R
import eramo.resultgate.databinding.FragmentChangePasswordBinding
import eramo.resultgate.presentation.ui.dialog.LoadingDialog
import eramo.resultgate.presentation.viewmodel.drawer.myaccount.ChangePasswordViewModel
import eramo.resultgate.util.Constants.API_SUCCESS
import eramo.resultgate.util.LocalHelperUtil
import eramo.resultgate.util.UserUtil
import eramo.resultgate.util.showToast
import eramo.resultgate.util.state.UiState
import org.json.JSONObject

@AndroidEntryPoint
class ChangePasswordFragment : Fragment(R.layout.fragment_change_password) {

    private val viewModel by viewModels<ChangePasswordViewModel>()
    private lateinit var binding: FragmentChangePasswordBinding
    private val args by navArgs<ChangePasswordFragmentArgs>()
    private val memberModel get() = args.memberModel

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
        binding = FragmentChangePasswordBinding.bind(view)

        binding.apply {
            FChangePasswordBtnSave.setOnClickListener { setupSavePass() }
            FChangePasswordIvBack.setOnClickListener { findNavController().popBackStack() }
        }

        fetchUpdatePassState()
        handleLoadingCancellation()

        Glide.with(requireContext())
            .load(memberModel.imageUrl)
            .into(binding.FChangePasswordProfile)
    }

    private fun setupSavePass() {
        binding.apply {
            val currentPass = FChangePasswordEtCurrent.text.toString().trim()
            val newPass = FChangePasswordEtNewPass.text.toString().trim()
            val rePassword = FChangePasswordEtRetypePass.text.toString().trim()

            if (TextUtils.isEmpty(currentPass)) {
                itlCurrent.error = getString(R.string.txt_password_is_required)
                return
            } else itlCurrent.error = null

            if (TextUtils.isEmpty(newPass)) {
                itlNewPass.error = getString(R.string.txt_new_password_is_required)
                return
            }
//            else if (!UserUtil.PASSWORD_PATTERN.matcher(newPass).matches()) {
//                itlNewPass.error = getString(R.string.txt_password_too_weak)
//                return
//            }
            else if (newPass.length < 8) {
                itlNewPass.error = getString(R.string.must_be_8_characters_at_least)
                return
            } else itlNewPass.error = null

            if (TextUtils.isEmpty(rePassword)) {
                itlRetypePass.error = getString(R.string.hint_retype_new_password)
                return
            } else itlRetypePass.error = null

            if (rePassword != newPass) {
                itlRetypePass.error = getString(R.string.txt_password_not_match)
                return
            } else itlRetypePass.error = null

            viewModel.updatePass(currentPass, newPass, rePassword)
        }
    }

    private fun fetchUpdatePassState() {
        lifecycleScope.launchWhenCreated {
            viewModel.updatePassState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()

                        if (state.data?.status == API_SUCCESS) {
                            showToast(state.data.message ?: getString(R.string.success))

                            UserUtil.clearUserInfo()

                            val mainNavController = Navigation.findNavController(requireActivity(), R.id.main_navHost)
                            mainNavController.navigate(
                                R.id.loginFragment, null,
                                NavOptions.Builder().setPopUpTo(R.id.mainFragment, true).build()
                            )

                        } else if (state.data?.status == 0) {
                            showToast(state.data.message ?: getString(R.string.error))
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