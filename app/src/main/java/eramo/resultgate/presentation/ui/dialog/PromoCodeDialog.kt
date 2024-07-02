package eramo.resultgate.presentation.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import eramo.resultgate.R
import eramo.resultgate.data.remote.dto.products.orders.CustomerPromoCodes
import eramo.resultgate.databinding.DialogPromoCodeBinding
import eramo.resultgate.presentation.adapters.recycleview.vertical.TextAdapter
import eramo.resultgate.presentation.viewmodel.SharedViewModel
import eramo.resultgate.presentation.viewmodel.dialog.PromoCodeViewModel
import eramo.resultgate.util.NavKeys
import eramo.resultgate.util.showToast
import eramo.resultgate.util.state.UiState
import javax.inject.Inject

@AndroidEntryPoint
class PromoCodeDialog : DialogFragment(R.layout.dialog_promo_code),
    TextAdapter.OnItemClickListener {

    @Inject
    lateinit var textAdapter: TextAdapter
    private lateinit var binding: DialogPromoCodeBinding
    private val viewModel by viewModels<PromoCodeViewModel>()
    private val viewModelShared by activityViewModels<SharedViewModel>()

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
        binding = DialogPromoCodeBinding.bind(view)

        textAdapter.setListener(this)
        binding.apply {
            FDPromoCodeIvClose.setOnClickListener { findNavController().popBackStack() }
            FDPromoCodeRvPromoCodes.adapter = textAdapter

            fetchPromoCodeState()
            handleLoadingCancellation()
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.promoCode()
    }

    private fun fetchPromoCodeState() {
        lifecycleScope.launchWhenCreated {
            viewModel.promoCodeState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()
                        binding.groupNoPromoCodes.visibility = View.GONE
                        textAdapter.submitList(state.data)
                    }
                    is UiState.Error -> {
                        LoadingDialog.dismissDialog()
                        val errorMessage=state.message?.asString(requireContext())?:""
                        if(errorMessage.isNotEmpty()) showToast(errorMessage)
                    }
                    is UiState.Empty -> {
                        LoadingDialog.dismissDialog()
                        binding.groupNoPromoCodes.visibility = View.VISIBLE
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

    override fun onChoiceClick(model: CustomerPromoCodes) {
        viewModelShared.orderPromoCode = model.promoIdFk?.toInt() ?: 0
        findNavController().previousBackStackEntry?.savedStateHandle?.set(
            NavKeys.DISCOUNT,
            model.promoCodePercent
        )
        findNavController().popBackStack()
    }
}