package eramo.resultgate.presentation.ui.checkout

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import eramo.resultgate.R
import eramo.resultgate.databinding.FragmentCheckoutStepTwoBinding
import eramo.resultgate.domain.model.products.PaymentTypesModel
import eramo.resultgate.presentation.adapters.recycleview.vertical.PaymentAdapter
import eramo.resultgate.presentation.ui.dialog.LoadingDialog
import eramo.resultgate.presentation.viewmodel.SharedViewModel
import eramo.resultgate.presentation.viewmodel.checkout.CheckoutStepThreeViewModel
import eramo.resultgate.util.Constants
import eramo.resultgate.util.Constants.TAG
import eramo.resultgate.util.LocalHelperUtil
import eramo.resultgate.util.UserUtil
import eramo.resultgate.util.formatPrice
import eramo.resultgate.util.navOptionsAnimation
import eramo.resultgate.util.showToast
import eramo.resultgate.util.state.UiState
import io.kashier.sdk.Core.model.Response.Error.ErrorData
import io.kashier.sdk.Core.model.Response.Payment.PaymentResponse
import io.kashier.sdk.Core.model.Response.UserCallback
import io.kashier.sdk.Core.network.SDK_MODE
import io.kashier.sdk.Kashier
import org.json.JSONObject
import javax.inject.Inject

@AndroidEntryPoint
class CheckoutStepTwoFragment : Fragment(R.layout.fragment_checkout_step_two),
    PaymentAdapter.OnItemClickListener {

    @Inject
    lateinit var paymentAdapter: PaymentAdapter
    private lateinit var binding: FragmentCheckoutStepTwoBinding
    private val viewModel by viewModels<CheckoutStepThreeViewModel>()
    private val viewModelShared by activityViewModels<SharedViewModel>()
    private val args by navArgs<CheckoutStepTwoFragmentArgs>()
    private val total get() = args.total
    private var currentPaymentType = ""

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
        binding = FragmentCheckoutStepTwoBinding.bind(view)
        setupToolbar()

        paymentAdapter.setListener(this)
        binding.apply {
            FCheckoutSTwoRvPayment.adapter = paymentAdapter

//            FCheckoutSTwoTvTotal.text = getString(R.string.s_egp, total.toString())
            FCheckoutSTwoTvTotal.text = if (UserUtil.getCurrency() == Constants.CURRENCY_EGP) {
                getString(R.string.s_egp, formatPrice(total.toDouble()))
            } else {
                getString(R.string.s_usd, formatPrice(total.toDouble()))
            }

            FCheckoutSTwoTvNext.setOnClickListener {
                when (currentPaymentType) {
                    "" -> showToast(getString(R.string.select_a_payment_type))

                    getString(R.string.cash_on_delivery) -> {
//                        Log.d(TAG, viewModelShared.getOrderRequestInstance().toString())
//                        viewModel.saveOrderRequest(viewModelShared.getOrderRequestInstance())

                        checkout(Constants.PAYMENT_CASH_ON_DELIVERY)
                    }

                    getString(R.string.symbl) -> {
                        viewModelShared.paymentType = "Symbl"
                        if (total >= 300f) {
                            findNavController().navigate(R.id.creditPaymentFragment, CreditPaymentFragmentArgs(total,"Symbl").toBundle())
//                            checkout(Constants.PAYMENT_SYMBL)
                        } else {
                            showToast("Must be more than 300")
                        }
                    }
                    getString(R.string.online_payment) -> {
                        viewModelShared.paymentType = "Online Payment"
                        findNavController().navigate(R.id.creditPaymentFragment, CreditPaymentFragmentArgs(total,"Online Payment").toBundle())
//                        checkout(Constants.PAYMENT_ONLINE)

                    }
                }
            }
        }



        fetchOrderState()
        fetchPaymentState()
        fetchCheckoutState()
        setupPaymentTypesRV()
        handleLoadingCancellation()

    }

    private fun fetchOrderState() {
        lifecycleScope.launchWhenCreated {
            viewModel.orderState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()
                        viewModelShared.resetOrder()
                        findNavController().navigate(
                            R.id.checkoutStepThreeFragment,
                            null,
                            navOptionsAnimation()
                        )
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


    private fun setupPaymentTypesRV() {
        val list = mutableListOf<PaymentTypesModel>()

        list.add(
            PaymentTypesModel("", getString(R.string.cash_on_delivery), "", "", "")
        )
//        list.add(
//            PaymentTypesModel("", getString(R.string.symbl), "", "", "")
//        )

        paymentAdapter.submitList(list)
    }

    private fun fetchPaymentState() {
        lifecycleScope.launchWhenCreated {
            viewModel.paymentState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()
                        paymentAdapter.submitList(state.data)
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

    private fun checkout(paymentType: String) {
        viewModel.checkout(
            viewModelShared.checkoutModel?.userAddress!!,
            viewModelShared.checkoutModel?.coupon,
            paymentType,
            payment_id = ""
        )

    }

    private fun fetchCheckoutState() {
        lifecycleScope.launchWhenCreated {
            viewModel.checkoutState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()
                        findNavController().navigate(
                            R.id.checkoutStepThreeFragment,
                            null,
                            navOptionsAnimation()
                        )
                    }

                    is UiState.Error -> {
                        LoadingDialog.dismissDialog()
                        try {
                            parseErrorResponse(state.message!!.asString(requireContext()))
                        } catch (e: Exception) {
                            showToast(state.message!!.asString(requireContext()))
                            e.printStackTrace()
                        }
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

    private fun parseErrorResponse(string: String) {
        val jsonErrorBody = JSONObject(string)
        val errorMessage = jsonErrorBody.getString("message")
        showToast(errorMessage)
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

    private fun setupToolbar() {
        binding.apply {
            FCheckoutInToolbar.toolbar.setNavigationIcon(R.drawable.ic_arrow_left_yellow)
            FCheckoutInToolbar.toolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }

            FCheckoutInToolbar.ivSearch.visibility = View.INVISIBLE
            FCheckoutInToolbar.ivSearch.setOnClickListener {
                findNavController().navigate(
                    R.id.filterFragment,
                    null,
                    navOptionsAnimation()
                )
            }

            FCheckoutInToolbar.ivShop.visibility = View.INVISIBLE
            FCheckoutInToolbar.ivShop.setOnClickListener {
                findNavController().navigate(
                    R.id.shopFragment,
                    null,
                    navOptionsAnimation()
                )
            }
        }
    }

    private fun setupKasheirPayment() {
        val currency = "EGP"
        val apiKeyId = "7092f232-6223-4307-8909-a983eb61e18e"
        val merchantId = "MID-1762-315"
        Kashier.init(requireActivity(), merchantId, apiKeyId, currency, SDK_MODE.DEVELOPMENT)

        Kashier.startPaymentActivity(
            requireActivity() as AppCompatActivity,
            "12345",
            "123",
            total.toString(),
            object : UserCallback<PaymentResponse> {
                override fun onResponse(response: retrofit2.Response<PaymentResponse>?) {
                    response?.let {
                        if (it.isSuccessful) {
                            Log.d(TAG, "success")
                            findNavController().navigate(
                                R.id.checkoutStepThreeFragment,
                                null,
                                navOptionsAnimation()
                            )
                        } else Log.d(TAG, "failed")
                    }
                }

                override fun onFailure(error: ErrorData<PaymentResponse>?) {
                    Log.d(TAG, error?.errorMessage ?: "failure")
                }
            }
        )
    }

    override fun onPaymentClick(model: PaymentTypesModel) {

        if (model.title == getString(R.string.cash_on_delivery)) {
            currentPaymentType = model.title
        }
        if (model.title == getString(R.string.symbl)) {
            currentPaymentType = model.title
        }
    }
}