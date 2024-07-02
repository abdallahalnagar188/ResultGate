package eramo.resultgate.presentation.ui.drawer.myaccount.order

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
import eramo.resultgate.R
import eramo.resultgate.data.remote.dto.drawer.myaccount.OrderDetailsResponse
import eramo.resultgate.databinding.FragmentOrderDetailsBinding
import eramo.resultgate.presentation.adapters.recycleview.vertical.OrderProductAdapter
import eramo.resultgate.presentation.ui.dialog.CancelDialogArgs
import eramo.resultgate.presentation.ui.dialog.LoadingDialog
import eramo.resultgate.presentation.viewmodel.drawer.myaccount.order.OrderDetailsViewModel
import eramo.resultgate.util.Constants
import eramo.resultgate.util.LocalHelperUtil
import eramo.resultgate.util.UserUtil
import eramo.resultgate.util.formatPrice
import eramo.resultgate.util.onBackPressed
import eramo.resultgate.util.parseErrorResponse
import eramo.resultgate.util.removePriceComma
import eramo.resultgate.util.showToast
import eramo.resultgate.util.state.UiState
import javax.inject.Inject

@AndroidEntryPoint
class OrderDetailsFragment : Fragment(R.layout.fragment_order_details), OrderProductAdapter.OnItemClickListener {

    @Inject
    lateinit var orderProductAdapter: OrderProductAdapter

    private lateinit var binding: FragmentOrderDetailsBinding
    private val viewModel by viewModels<OrderDetailsViewModel>()
    private val args by navArgs<OrderDetailsFragmentArgs>()

    private val orderId get() = args.orderId
    private val notificationId get() = args.notificationId

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
        binding = FragmentOrderDetailsBinding.bind(view)
        setupToolbar()


        viewModel.getOrderDetails(orderId, notificationId)

        fetchOrderDetailsState()

        this@OrderDetailsFragment.onBackPressed { findNavController().popBackStack() }
    }

    private fun setupToolbar() {
        binding.apply {
            FQueryToolbar.setNavigationIcon(R.drawable.ic_arrow_left_yellow)
            FQueryToolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun setupMyOrderInfo(date: OrderDetailsResponse) {
        binding.apply {

            FOrderDetailsTvNumOrder.text = date.data?.orderNumber
            FOrderDetailsTvDate.text = date.data?.date?.substring(0, 10)
            FOrderDetailsTvTotal.text = if (UserUtil.getCurrency() == Constants.CURRENCY_EGP) {
                getString(R.string.s_egp, formatPrice(removePriceComma(date.data?.totalPrice ?: "0.0").toDouble()))
            } else {
                getString(R.string.s_usd, formatPrice(removePriceComma(date.data?.totalPrice ?: "0.0").toDouble()))
            }

            FOrderDetailsTvPromoCode.text = if (UserUtil.getCurrency() == Constants.CURRENCY_EGP) {
                getString(
                    R.string.s_egp,
                    formatPrice(removePriceComma(date.data?.totelProductPrice ?: "0.0").toDouble())
                )
            } else {
                getString(
                    R.string.s_usd,
                    formatPrice(removePriceComma(date.data?.totelProductPrice ?: "0.0").toDouble())
                )
            }

            FOrderDetailsTvDiscount.text = if (UserUtil.getCurrency() == Constants.CURRENCY_EGP) {
                getString(R.string.s_egp, formatPrice(removePriceComma(date.data?.descount ?: "0.0").toDouble()))
            } else {
                getString(R.string.s_usd, formatPrice(removePriceComma(date.data?.descount ?: "0.0").toDouble()))
            }

            FOrderDetailsTvExtras.text = if (UserUtil.getCurrency() == Constants.CURRENCY_EGP) {
                getString(R.string.s_egp, formatPrice(0.0))
            } else {
                getString(R.string.s_usd, formatPrice(0.0))
            }



            FOrderDetailsTvTotalShipping.text = if (UserUtil.getCurrency() == Constants.CURRENCY_EGP) {
                getString(R.string.s_egp, formatPrice(removePriceComma(date.data?.shippingFees ?: "0.0").toDouble()))
            } else {
                getString(R.string.s_usd, formatPrice(removePriceComma(date.data?.shippingFees ?: "0.0").toDouble()))
            }


            FOrderDetailsTvTotalTaxes.text = if (UserUtil.getCurrency() == Constants.CURRENCY_EGP) {
                getString(R.string.s_egp, formatPrice(removePriceComma(date.data?.totalTaxesPrice ?: "0.0").toDouble()))
            } else {
                getString(R.string.s_usd, formatPrice(removePriceComma(date.data?.totalTaxesPrice ?: "0.0").toDouble()))
            }

            FOrderDetailsTvPayType.text = date.data?.paymentType

            when (date.data?.status) {
                "pending" -> {
                    FOrderDetailsBtnCancel.visibility = View.VISIBLE
                    root.setBackgroundResource(R.color.green_low)

                    FOrderDetailsTvStatus.text = getString(R.string.order_status_pending_txt)
                }

                "inprogress" -> {
                    FOrderDetailsBtnCancel.visibility = View.GONE
                    root.setBackgroundResource(R.color.yellow_low)

                    FOrderDetailsTvStatus.text = getString(R.string.order_status_in_progress_txt)
                }

                "delivered" -> {
                    FOrderDetailsBtnCancel.visibility = View.GONE
                    root.setBackgroundResource(R.color.blue_low)

                    FOrderDetailsTvStatus.text = getString(R.string.order_status_delivered_txt)
                }

                "canceled" -> {
                    FOrderDetailsBtnCancel.visibility = View.GONE
                    root.setBackgroundResource(R.color.red_low)

                    FOrderDetailsTvStatus.text = getString(R.string.order_status_canceled_txt)
                }
            }

            FOrderDetailsBtnCancel.setOnClickListener {
                findNavController().navigate(
                    R.id.cancelDialog,
                    CancelDialogArgs(args.orderId).toBundle()
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        orderId.let {
            viewModel.getOrderDetails(it, notificationId)
        }
    }

    private fun fetchOrderDetailsState() {
        lifecycleScope.launchWhenCreated {
            viewModel.orderDetailsState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()

                        orderProductAdapter.setListener(this@OrderDetailsFragment)
                        binding.FOrderDetailsRvProducts.adapter = orderProductAdapter
                        orderProductAdapter.submitList(state.data?.data?.products)

                        state.data?.let { setupMyOrderInfo(it) }
                    }

                    is UiState.Error -> {
                        LoadingDialog.dismissDialog()
                        try {
                            showToast(parseErrorResponse(state.message!!.asString(requireContext())))
                        } catch (e: Exception) {
                            showToast(state.message!!.asString(requireContext()))
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

    override fun onExtrasClick(model: List<OrderDetailsResponse.Data.Product.Extra?>?) {

        val myOrderExtrasBottomSheetDialogFragment = MyOrderExtrasBottomSheetDialogFragment(
            model ?: emptyList()
        )

        myOrderExtrasBottomSheetDialogFragment.show(
            activity?.supportFragmentManager!!,
            "myOrderExtrasBottomSheetDialogFragment"
        )
    }
}