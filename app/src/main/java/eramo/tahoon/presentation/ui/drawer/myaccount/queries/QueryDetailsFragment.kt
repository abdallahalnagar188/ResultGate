package eramo.tahoon.presentation.ui.drawer.myaccount.queries

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import eramo.tahoon.R
import eramo.tahoon.databinding.FragmentQueryDetailsBinding
import eramo.tahoon.presentation.ui.dialog.LoadingDialog
import eramo.tahoon.presentation.viewmodel.drawer.myaccount.queries.QueryDetailsViewModel
import eramo.tahoon.util.LocalHelperUtil
import eramo.tahoon.util.showToast
import eramo.tahoon.util.state.UiState

@AndroidEntryPoint
class QueryDetailsFragment : Fragment(R.layout.fragment_query_details) {
    private lateinit var binding: FragmentQueryDetailsBinding

    private val viewModel by viewModels<QueryDetailsViewModel>()
    private val args by navArgs<QueryDetailsFragmentArgs>()
    private val queryId get() = args.queryId

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
        binding = FragmentQueryDetailsBinding.bind(view)
        setupToolbar()

        viewModel.getQueryDetails(queryId)

        fetchQueryDetailsState()

        handleLoadingCancellation()
    }

    private fun fetchQueryDetailsState() {
        lifecycleScope.launchWhenCreated {
            viewModel.queryDetailsState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()

                        binding.apply {
//                            tvSubject.text = state.data?.data?.type
                            tvMessage.text = state.data?.data?.message
                            tvReplay.text = state.data?.data?.reply
                        }
                    }

                    is UiState.Error -> {
                        LoadingDialog.dismissDialog()
                        val message = state.message!!.asString(requireContext())
                        if (message.isNotBlank()) showToast(message)
                    }

                    is UiState.Loading -> {
                        LoadingDialog.showDialog()
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun setupToolbar() {
        binding.apply {
            FQueryToolbar.setNavigationIcon(R.drawable.ic_arrow_left_yellow)
            FQueryToolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
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