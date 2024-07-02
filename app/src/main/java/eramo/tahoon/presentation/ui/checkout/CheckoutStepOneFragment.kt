package eramo.tahoon.presentation.ui.checkout

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.net.ParseException
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import eramo.tahoon.R
import eramo.tahoon.data.remote.dto.drawer.myaccount.myaddresses.GetMyAddressesResponse
import eramo.tahoon.databinding.FragmentCheckoutStepOneBinding
import eramo.tahoon.domain.model.request.CheckoutModel
import eramo.tahoon.presentation.adapters.recycleview.vertical.CheckoutAdapter
import eramo.tahoon.presentation.ui.dialog.LoadingDialog
import eramo.tahoon.presentation.ui.dialog.MyAddressesDialogArgs
import eramo.tahoon.presentation.viewmodel.SharedViewModel
import eramo.tahoon.presentation.viewmodel.navbottom.CartViewModel
import eramo.tahoon.util.Constants
import eramo.tahoon.util.LocalHelperUtil
import eramo.tahoon.util.NavKeys
import eramo.tahoon.util.StringIdShipping
import eramo.tahoon.util.UserUtil
import eramo.tahoon.util.formatPrice
import eramo.tahoon.util.hideSoftKeyboard
import eramo.tahoon.util.navOptionsAnimation
import eramo.tahoon.util.showToast
import eramo.tahoon.util.state.UiState
import javax.inject.Inject

@AndroidEntryPoint
class CheckoutStepOneFragment : Fragment(R.layout.fragment_checkout_step_one),
    CheckoutAdapter.OnItemClickListener {

    @Inject
    lateinit var checkoutAdapter: CheckoutAdapter
    private lateinit var binding: FragmentCheckoutStepOneBinding
    private val viewModelShared by activityViewModels<SharedViewModel>()
    private val viewModel: CartViewModel by viewModels()
    private var checkoutTotal: Float = 0.0f
    private var shippingValue: Float = 0.0f
    private var promoCodeValue: Float = 0.0f
    private var promoCodeType = ""

    private val addressesList = java.util.ArrayList<StringIdShipping>()
    private var addressId = -1

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
        binding = FragmentCheckoutStepOneBinding.bind(view)
        setupToolbar()

        checkoutAdapter.setListener(this)
        binding.apply {
            FCheckoutSOneRvProducts.adapter = checkoutAdapter

            FCheckoutSOneTvAddPromoCode.setOnClickListener {
                if (viewModelShared.orderPromoCode == 0) findNavController().navigate(R.id.promoCodeDialog)
                else showToast(getString(R.string.promo_code_is_used))
            }

            FCheckoutSOneTvNext.setOnClickListener {
                saveCartToOrder()
            }

            ivAddAddress.setOnClickListener {
                findNavController().navigate(
                    R.id.myAddressesDialog,
                    MyAddressesDialogArgs(null, null, null, null,null, null, null, Constants.CHECKOUT_FRAGMENT).toBundle(),
                    navOptionsAnimation()
                )
            }
        }
        fetchCartDataState()
        fetchGetMyAddressesState()
        fetchCheckPromoCodeState()
        fetchPromoCodeFromDialog()
        fetchCheckoutTotalState()
        handleLoadingCancellation()

        promoCodeEditTextListener()
    }

    override fun onStart() {
        super.onStart()
        viewModel.cartData()
        viewModel.getMyAddresses()
        checkoutAdapter.clearTotalPrice()
    }

    private fun fetchCartDataState() {
        lifecycleScope.launchWhenStarted {
            viewModel.cartDataModelState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()
//                        checkoutAdapter.submitList(state.data?.get("list") as MutableList<CartDataModel>)
                        checkoutAdapter.submitList(state.data?.productList)

                        viewModel.checkoutTotal.value = removePriceComma((state.data?.totalPrice.toString()))
                        checkoutTotal = removePriceComma((state.data?.totalPrice.toString()))
//                        binding.FCheckoutSOneTvTotal.text = getString(R.string.s_egp, checkoutTotal.toString())

//                        binding.FCheckoutSOneTvTotalTaxes.text = getString(R.string.s_egp, removePriceComma((state.data?.totalTaxes.toString())).toString())
                        binding.FCheckoutSOneTvTotalTaxes.text =
                            getString(R.string.s_egp, formatPrice(removePriceComma(state.data?.totalTaxes ?: "0.0").toDouble()))
                        binding.FCheckoutSOneTvSubTotal.text =
//                            getString(R.string.s_egp, removePriceComma((state.data?.subTotal.toString())).toString())
                            if (UserUtil.getCurrency() == Constants.CURRENCY_EGP) {
                                getString(R.string.s_egp, formatPrice(removePriceComma(state.data?.subTotal ?: "0.0").toDouble()))
                            } else {
                                getString(R.string.s_usd, formatPrice(removePriceComma(state.data?.subTotal ?: "0.0").toDouble()))
                            }
                    }

                    is UiState.Error -> {
                        LoadingDialog.dismissDialog()
                        showToast(state.message!!.asString(requireContext()))
                    }

                    is UiState.Loading -> {
                        LoadingDialog.showDialog()
                    }

                    is UiState.Empty -> {
                        LoadingDialog.dismissDialog()
                        checkoutAdapter.submitList(emptyList())
                    }
                }
            }
        }
    }

    private fun fetchGetMyAddressesState() {
        lifecycleScope.launchWhenStarted {
            viewModel.getMyAddressesState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()
                        setupAddresses(state.data?.data)
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

    private fun setupAddresses(list: List<GetMyAddressesResponse.Data?>?) {
        binding.apply {

            // Initial item
            addressesList.clear()
            addressesList.add(StringIdShipping(getString(R.string.address), 0, "0"))

            // Assign the countries
            for (model in list!!)
                addressesList.add(StringIdShipping(model?.addressType.toString(), model?.id!!, model.shipping.toString()))

            // Assign the spinner adapter
            checkoutInAddress.spinner.adapter = Constants.createCountrySpinnerAdapter(requireContext(), addressesList)

            var selectedAddressShippingFees = 0.0f

            // Listener
            checkoutInAddress.spinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parentView: AdapterView<*>?,
                        selectedItemView: View?,
                        position: Int,
                        id: Long
                    ) {
                        if (position == 0) {
                            addressId = -1
                            binding.FCheckoutSOneTvChooseDeliveryValue.text = getString(
                                R.string.s_egp,
                                (parentView?.getItemAtPosition(position) as StringIdShipping).value
                            )
                            viewModel.checkoutTotal.value -= selectedAddressShippingFees

                        } else {
                            addressId = (parentView?.getItemAtPosition(position) as StringIdShipping).id
                            binding.FCheckoutSOneTvChooseDeliveryValue.text =
                                getString(R.string.s_egp, (parentView.getItemAtPosition(position) as StringIdShipping).value)

                            viewModel.checkoutTotal.value -= selectedAddressShippingFees
                            selectedAddressShippingFees = (parentView.getItemAtPosition(position) as StringIdShipping).value.toFloat()
                            viewModel.checkoutTotal.value += (parentView.getItemAtPosition(position) as StringIdShipping).value.toFloat()
                        }
                    }

                    override fun onNothingSelected(parentView: AdapterView<*>?) {
                        // your code here
                    }
                }
        }
    }

    private fun fetchCheckPromoCodeState() {
        lifecycleScope.launchWhenStarted {
            viewModel.checkPromoCodeState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()
                        showToast(state.data?.message.toString())

                        binding.FCheckoutSOneTvDiscountValue.text = if (state.data?.data?.promoCodeType == "amount") {
                            getString(R.string.s_egp, state.data.data.promoCodeValue)
                        } else {
                            getString(R.string.perentt, state.data?.data?.promoCodeValue)
                        }


                        promoCodeType = state.data?.data?.promoCodeType.toString()
                        promoCodeValue = getPromoCodeValueAccordingToType(
                            state.data?.data?.promoCodeType.toString(), state.data?.data?.promoCodeValue?.toFloat()!!, checkoutTotal
                        )

                        viewModel.checkoutTotal.value -= getPromoCodeValueAccordingToType(
                            state.data.data.promoCodeType.toString(), state.data.data.promoCodeValue.toFloat(), checkoutTotal
                        )

                        requireActivity().hideSoftKeyboard()
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

    private fun getPromoCodeValueAccordingToType(type: String, value: Float, checkoutTotal: Float): Float {
        return when (type) {
            "amount" -> value
            "percent" -> ((value / 100.0f) * checkoutTotal)
            else -> 0.0f
        }
    }

    private fun promoCodeEditTextListener() {
        binding.etPromo.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

//                Handler().postDelayed({
//                    if (binding.etPromo.text!!.isNotEmpty()) {
//                        viewModel.checkoutTotal.value += promoCodeValue
//                        promoCodeValue = 0.0f
//                        promoCodeType = ""
//
//                        viewModel.checkPromoCode(binding.etPromo.text.toString().trim())
//                    } else {
//                        viewModel.checkoutTotal.value += promoCodeValue
//                        promoCodeValue = 0.0f
//                        promoCodeType = ""
//                    }
//                }, 600)


            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

//        binding.etPromo.setOnFocusChangeListener(object : View.OnFocusChangeListener{
//            override fun onFocusChange(p0: View?, hasFoucs: Boolean) {
//
//                if (hasFoucs){
//                    binding.tvAddPromo.visibility = View.VISIBLE
//                }else{
//                    binding.tvAddPromo.visibility = View.GONE
//                }
//            }
//        })

//        binding.etPromo.setOnKeyListener(object : View.OnKeyListener{
//            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
//                if (keyCode==KeyEvent.KEYCODE_ENTER){
//                    if (binding.etPromo.text!!.isNotEmpty()) {
//                        viewModel.checkoutTotal.value += promoCodeValue
//                        promoCodeValue = 0.0f
//                        promoCodeType = ""
//
//                        viewModel.checkPromoCode(binding.etPromo.text.toString().trim())
//                    } else {
//                        viewModel.checkoutTotal.value += promoCodeValue
//                        promoCodeValue = 0.0f
//                        promoCodeType = ""
//                    }
//                }
//                return false
//            }
//
//        })

        binding.etPromo.setOnFocusChangeListener(object : View.OnFocusChangeListener {
            override fun onFocusChange(p0: View?, hasFocus: Boolean) {
                if (hasFocus) {
//                    viewModel.checkPromo(binding.etPromo.text.toString().trim())
//                    binding.tvAddPromoCode.visibility = View.VISIBLE
                } else {
//                    binding.tvAddPromoCode.visibility = View.GONE
                }
            }

        })

//        binding.tvAddPromoCode.setOnClickListener {
//            if (binding.etPromo.text!!.isNotEmpty()) {
//                viewModel.checkoutTotal.value += promoCodeValue
//                promoCodeValue = 0.0f
//                promoCodeType = ""
//
//                viewModel.checkPromoCode(binding.etPromo.text.toString().trim())
//            }
////            else {
////                viewModel.checkoutTotal.value += promoCodeValue
////                promoCodeValue = 0.0f
////                promoCodeType = ""
////            }
//        }

        binding.FCheckoutSOneTvAddPromo.setOnClickListener {
            if (binding.etPromo.text!!.isNotEmpty()) {
                viewModel.checkoutTotal.value += promoCodeValue
                promoCodeValue = 0.0f
                promoCodeType = ""

                viewModel.checkPromoCode(binding.etPromo.text.toString().trim())
                viewModelShared.checkoutModel?.coupon = binding.etPromo.text.toString().trim()
            }
//            else {
//                viewModel.checkoutTotal.value += promoCodeValue
//                promoCodeValue = 0.0f
//                promoCodeType = ""
//            }
        }

    }

    private fun fetchCheckoutTotalState() {
        lifecycleScope.launchWhenStarted {
            viewModel.checkoutTotal.collect { total ->
//                binding.FCheckoutSOneTvTotal.text = getString(R.string.s_egp, total.toString())
                binding.FCheckoutSOneTvTotal.text = if (UserUtil.getCurrency() == Constants.CURRENCY_EGP) {
                    getString(R.string.s_egp, formatPrice(total.toDouble()))
                } else {
                    getString(R.string.s_usd, formatPrice(total.toDouble()))
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

    private fun saveCartToOrder() {
        lifecycleScope.launchWhenStarted {
//            var item: OrderItemList
//            val itemList = ArrayList<OrderItemList>()
//            for (model in checkoutAdapter.currentList) {
//                item = OrderItemList(
//                    productId = model.productIdFk?.toInt(),
//                    productQty = model.productQty?.toInt(),
//                    productPrice = model.productPrice?.toFloat(),
//                    productSize = model.productSize,
//                    productColor = model.productColor
//                )
//                itemList.add(item)
//            }
//            viewModelShared.orderItemList = itemList

            if (addressId == -1) {
                showToast(getString(R.string.txt_address_is_required))
                return@launchWhenStarted
            }

            viewModelShared.checkoutModel = CheckoutModel(
                addressId.toString(),
                if (binding.etPromo.text.isNullOrEmpty() || promoCodeType == "") null else binding.etPromo.text.toString().trim(),
                if (promoCodeType == "") null else promoCodeType
            )

            findNavController().navigate(
                R.id.checkoutStepTwoFragment,
                CheckoutStepTwoFragmentArgs(viewModel.checkoutTotal.value).toBundle(),
                navOptionsAnimation()
            )
        }
    }

    private fun fetchPromoCodeFromDialog() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>(NavKeys.DISCOUNT)
            ?.observe(viewLifecycleOwner) { percent ->
                // Do something with the result.
                checkoutTotal -= (checkoutTotal * (percent.toFloat()) / 100)
//                binding.FCheckoutSOneTvTotal.text = getString(R.string.s_egp, checkoutTotal.toString())
                binding.FCheckoutSOneTvTotal.text = if (UserUtil.getCurrency() == Constants.CURRENCY_EGP) {
                    getString(R.string.s_usd, formatPrice(checkoutTotal.toDouble()))
                } else {
                    getString(R.string.s_usd, formatPrice(checkoutTotal.toDouble()))
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

    @Throws(ParseException::class)
    private fun removePriceComma(value: String): Float {
        val newValue = value.replace(",", "")
        return newValue.toFloat()
    }
}