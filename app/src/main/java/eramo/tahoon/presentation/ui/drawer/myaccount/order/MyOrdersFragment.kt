package eramo.tahoon.presentation.ui.drawer.myaccount.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import eramo.tahoon.R
import eramo.tahoon.databinding.FragmentMyOrdersBinding
import eramo.tahoon.domain.model.products.orders.OrderModel
import eramo.tahoon.presentation.adapters.recycleview.vertical.MyOrderAdapter
import eramo.tahoon.presentation.ui.dialog.LoadingDialog
import eramo.tahoon.presentation.viewmodel.drawer.myaccount.order.MyOrdersViewModel
import eramo.tahoon.util.LocalHelperUtil
import eramo.tahoon.util.navOptionsAnimation
import eramo.tahoon.util.onBackPressed
import eramo.tahoon.util.parseErrorResponse
import eramo.tahoon.util.showToast
import eramo.tahoon.util.state.UiState
import javax.inject.Inject

@AndroidEntryPoint
class MyOrdersFragment : Fragment(R.layout.fragment_my_orders),
    MyOrderAdapter.OnItemClickListener {

    @Inject
    lateinit var myOrderAdapter: MyOrderAdapter
    private val viewModel by viewModels<MyOrdersViewModel>()
    private lateinit var binding: FragmentMyOrdersBinding

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
        binding = FragmentMyOrdersBinding.bind(view)
        setupToolbar()

        myOrderAdapter.setListener(this)
        binding.apply {
            FMyOrdersRvOrders.adapter = myOrderAdapter
        }
        fetchMyOrdersState()
        handleLoadingCancellation()

        this@MyOrdersFragment.onBackPressed { findNavController().popBackStack() }
    }

    override fun onStart() {
        super.onStart()
        viewModel.myOrders()
    }

    private fun fetchMyOrdersState() {
        lifecycleScope.launchWhenCreated {
            viewModel.myOrdersState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()

                        val sortedOrderList =
                            state.data?.sortedWith(compareByDescending { it.orderId })

                        myOrderAdapter.submitList(sortedOrderList)

                        if (state.data.isNullOrEmpty()) {
                            binding.lottieNoData.visibility = View.VISIBLE
                            binding.tvNoQueries.visibility = View.VISIBLE
                        } else {
                            binding.lottieNoData.visibility = View.GONE
                            binding.tvNoQueries.visibility = View.GONE
                        }

                    }

                    is UiState.Error -> {
                        LoadingDialog.dismissDialog()
                        try {
                            showToast(parseErrorResponse(state.message!!.asString(requireContext())))
                        } catch (e: Exception) {
                            showToast(state.message!!.asString(requireContext()))
                        }                    }

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

    private fun setupToolbar() {
        binding.apply {
            FQueryToolbar.setNavigationIcon(R.drawable.ic_arrow_left_yellow)
            FQueryToolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    override fun onOrderClick(model: OrderModel) {
        findNavController().navigate(
            R.id.orderDetailsFragment,
            OrderDetailsFragmentArgs(model.orderId).toBundle(), navOptionsAnimation()
        )
    }
}