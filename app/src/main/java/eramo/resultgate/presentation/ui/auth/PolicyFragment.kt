package eramo.resultgate.presentation.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import eramo.resultgate.R
import eramo.resultgate.databinding.FragmentPolicyBinding
import eramo.resultgate.presentation.ui.dialog.LoadingDialog
import eramo.resultgate.presentation.viewmodel.auth.PolicyViewModel
import eramo.resultgate.util.LocalHelperUtil
import eramo.resultgate.util.htmlFormatToString
import eramo.resultgate.util.onBackPressed
import eramo.resultgate.util.showToast
import eramo.resultgate.util.state.UiState

@AndroidEntryPoint
class PolicyFragment : Fragment(R.layout.fragment_policy) {

    private val viewModel by viewModels<PolicyViewModel>()
    private lateinit var binding: FragmentPolicyBinding

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
        binding = FragmentPolicyBinding.bind(view)

        setupToolbar()
        fetchGetAppPolicyState()
        handleLoadingCancellation()

        this@PolicyFragment.onBackPressed {
            findNavController().popBackStack()
//            findNavController().navigate(R.id.signUpFragment)
        }
    }

    private fun setupToolbar() {
        binding.apply {
            FPolicyToolbar.setNavigationIcon(R.drawable.ic_arrow_left_yellow)
            FPolicyToolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun fetchGetAppPolicyState() {
        lifecycleScope.launchWhenCreated {
            viewModel.getAppPolicyState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()
                        binding.FPolicyTv.text = htmlFormatToString(state.data?.get(0)?.term_and_condition_!!)
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