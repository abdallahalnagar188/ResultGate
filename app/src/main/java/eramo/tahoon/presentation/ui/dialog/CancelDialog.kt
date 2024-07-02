package eramo.tahoon.presentation.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import eramo.tahoon.R
import eramo.tahoon.databinding.DialogCancelBinding
import eramo.tahoon.presentation.viewmodel.dialog.CancelDialogViewModel
import eramo.tahoon.util.showToast
import eramo.tahoon.util.state.UiState

@AndroidEntryPoint
class CancelDialog : DialogFragment(R.layout.dialog_cancel) {
    private lateinit var binding: DialogCancelBinding
    private val viewModel by viewModels<CancelDialogViewModel>()
    private val args by navArgs<CancelDialogArgs>()
    private val orderId get() = args.orderId

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

            DFCancelBtnConfirm.setOnClickListener {
                viewModel.cancelOrder(orderId)
            }
        }
        fetchOrderDetailsState()
        handleLoadingCancellation()
    }

    private fun fetchOrderDetailsState() {
        lifecycleScope.launchWhenCreated {
            viewModel.orderDetailsState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()
                        showToast(state.data?.message ?: getString(R.string.cancelled))
                        findNavController().popBackStack(R.id.myOrdersFragment, false)
                        dismiss()
                    }

                    is UiState.Error -> {
                        LoadingDialog.dismissDialog()
                        showToast(state.message!!.asString(requireContext()))
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