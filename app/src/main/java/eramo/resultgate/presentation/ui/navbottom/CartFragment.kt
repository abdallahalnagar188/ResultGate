package eramo.resultgate.presentation.ui.navbottom

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.ParseException
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import eramo.resultgate.R
import eramo.resultgate.data.local.entity.MyCartDataEntity
import eramo.resultgate.databinding.FragmentCartBinding
import eramo.resultgate.domain.model.CartProductModel
import eramo.resultgate.presentation.adapters.recycleview.vertical.CartAdapter
import eramo.resultgate.presentation.ui.checkout.CheckoutStepOneFragmentArgs
import eramo.resultgate.presentation.ui.dialog.LoadingDialog
import eramo.resultgate.presentation.ui.navbottom.extension.ExtrasBottomSheetDialogFragment
import eramo.resultgate.presentation.viewmodel.SharedViewModel
import eramo.resultgate.presentation.viewmodel.navbottom.CartViewModel
import eramo.resultgate.util.*
import eramo.resultgate.util.state.UiState
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

@AndroidEntryPoint
class CartFragment : Fragment(R.layout.fragment_cart),
    CartAdapter.OnItemClickListener, ExtrasBottomSheetDialogFragment.ExtrasBottomSheetDialogOnClickListener {

    @Inject
    lateinit var cartAdapter: CartAdapter
    private lateinit var binding: FragmentCartBinding
    private val viewModelShared: SharedViewModel by activityViewModels()
    private val viewModel: CartViewModel by viewModels()
    private var taxes = 0.0f
    private var shipping = 0.0f
    private var cartProductsModel: CartProductModel? = null

    private var modelRv: CartProductModel.ProductList? = null
    private var cartDataRv: MyCartDataEntity? = null
    var qtyRv: Int? = null

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
        binding = FragmentCartBinding.bind(view)
        cartAdapter.setListener(this)
        setupToolbar()

        if (UserUtil.isUserLogin()) viewModel.getNotificationCount()

        viewModel.cartData()
//        viewModelShared.getNotificationCount()

        binding.apply {
            FCartRvProducts.adapter = cartAdapter

            FCartBtnProceed.setOnClickListener {
                if (UserUtil.isUserLogin()) {
                    findNavController().navigate(
                        R.id.checkoutStepOneFragment,
                        CheckoutStepOneFragmentArgs(taxes, shipping).toBundle(),
                        navOptionsAnimation()
                    )
                } else {
                    findNavController().navigate(R.id.loginDialog, null, navOptionsAnimation())
                }
            }
        }
        fetchCartDataState()
        fetchAddToCartState()
        fetchUpdateCartState()
        fetchRemoveCartItemState()
        fetchCartCountState()
        fetchNotificationCount()

        fetchCheckProductStockState()
//        fetchProfileState()
        handleLoadingCancellation()
        this@CartFragment.onBackPressed { findNavController().popBackStack() }
    }

    private fun setupToolbar() {
        binding.apply {
            inTbLayout.toolbarIvMenu.setOnClickListener {
                viewModelShared.openDrawer.value = true
            }

            inTbLayout.toolbarInNotification.root.setOnClickListener {
                if (UserUtil.isUserLogin())
                    findNavController().navigate(
                        R.id.notificationFragment,
                        null,
                        navOptionsAnimation()
                    )
                else findNavController().navigate(R.id.loginDialog)
            }

            inTbLayout.toolbarIvProfile.setOnClickListener {
                if (UserUtil.isUserLogin())
                    findNavController().navigate(
                        R.id.myAccountFragment,
                        null,
                        navOptionsAnimation()
                    )
                else findNavController().navigate(R.id.loginDialog)
            }

            if (UserUtil.isUserLogin()) {
                Glide.with(requireContext())
                    .load(UserUtil.getUserProfileImageUrl())
                    .into(inTbLayout.toolbarIvProfile)
            }
        }
    }

    private fun fetchCartDataState() {
        lifecycleScope.launchWhenStarted {
            viewModel.cartDataModelState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()
                        cartAdapter.submitList(state.data?.productList)

                        binding.apply {

                            setupTaxesShippingVisibility(false)


                            FCartTvShipping.text = getString(R.string.shipping_s_egp, shipping.toString())
                            FCartTvTotal.text = if (UserUtil.getCurrency() == Constants.CURRENCY_EGP) {
                                getString(
                                    R.string.total_s_egp,
                                    formatPrice(removePriceComma(state.data?.totalPrice ?: "0.0").toDouble())
                                )
                            } else {
                                getString(
                                    R.string.total_s_usd,
                                    formatPrice(removePriceComma(state.data?.totalPrice ?: "0.0").toDouble())
                                )
                            }
//                                getString(R.string.total_s_egp, state.data?.totalPrice ?: "0.0")

                            // When cart is empty
                            if (state.data?.productList.isNullOrEmpty()) {
                                FCartBtnProceed.isEnabled = false
                                FCartBtnProceed.alpha = 0.5f

                                FCartTvTaxes.visibility = View.GONE

                                lottieNoData.visibility = View.VISIBLE
                            } else {
                                FCartBtnProceed.isEnabled = true
                                FCartBtnProceed.alpha = 1.0f

                                FCartTvTaxes.visibility = View.GONE
                                lottieNoData.visibility = View.GONE
                            }


                            if (UserUtil.isUserLogin()) {
//                                FCartTvTaxes.visibility = View.VISIBLE
//                                if (!state.data?.totalTaxes.isNullOrEmpty())   taxes = state.data?.totalTaxes?.toFloat() ?: 0.0f
                                if (!state.data?.totalTaxes.isNullOrEmpty()) taxes =
                                    removePriceComma(state.data?.totalTaxes.toString())
                                FCartTvTaxes.text = getString(R.string.taxes_s_egp, taxes.toString())
                            } else {
//                                FCartTvTaxes.visibility = View.GONE
//                                if (!state.data?.totalTaxes.isNullOrEmpty())   taxes = state.data?.totalTaxes?.toFloat() ?: 0.0f
                                if (!state.data?.totalTaxes.isNullOrEmpty()) taxes =
                                    removePriceComma(state.data?.totalTaxes.toString())
                                FCartTvTaxes.text = getString(R.string.taxes_s_egp, taxes.toString())

                                cartProductsModel = state.data
                            }
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
                        cartAdapter.submitList(emptyList())
                        setupTaxesShippingVisibility(true)
                    }
                }
            }
        }
    }

    private fun fetchAddToCartState() {
        lifecycleScope.launchWhenStarted {
            viewModel.addToCartState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()
                        viewModel.cartData()
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

    @Throws(ParseException::class)
    private fun removePriceComma(value: String): Float {
        val newValue = value.replace(",", "")
        return newValue.toFloat()
    }

    private fun setupTaxesShippingVisibility(isEmpty: Boolean) {
        binding.apply {
            if (isEmpty) {
                groupEmptyCart.visibility = View.VISIBLE
                FCartBtnProceed.visibility = View.GONE
                FCartTvTaxes.visibility = View.GONE
                FCartTvShipping.visibility = View.GONE
                FCartTvTotal.visibility = View.GONE
            } else {
                groupEmptyCart.visibility = View.GONE
                FCartBtnProceed.visibility = View.VISIBLE
//                FCartTvTaxes.isVisible = UserUtil.isUserLogin()
//                FCartTvShipping.isVisible = UserUtil.isUserLogin()
                FCartTvShipping.visibility = View.INVISIBLE
                FCartTvTotal.visibility = View.VISIBLE
            }
        }
    }

    private fun fetchUpdateCartState() {
        lifecycleScope.launchWhenStarted {
            viewModel.updateCartState.collect { state ->
                when (state) {
                    is UiState.Error -> {
                        LoadingDialog.dismissDialog()
                        showToast(state.message!!.asString(requireContext()))
                    }

                    is UiState.Loading -> {
//                        LoadingDialog.showDialog()
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun fetchRemoveCartItemState() {
        lifecycleScope.launchWhenStarted {
            viewModel.removeCartItemState.collect { state ->
                when (state) {
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

    private fun fetchCartCountState() {
        lifecycleScope.launchWhenStarted {
            viewModel.cartCountState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()
                        viewModelShared.cartCount.value = state.data?.count
//                        binding.inTbLayout.toolbarInNotification.tvCount.apply {
//                            val notificationCounter = state.data?.count.toString() ?: "0"
//                            text = notificationCounter
//                            visibility =
//                                if (notificationCounter.isNotEmpty() && notificationCounter != "0")
//                                    View.VISIBLE
//                                else View.INVISIBLE
//                        }
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

    private fun fetchNotificationCount() {
        lifecycleScope.launchWhenStarted {
            viewModel.notificationCount.collect { state ->
                viewModelShared.notificationsCount.value = state
//                binding.inTbLayout.toolbarInNotification.tvCount.apply {
//                    val notificationCounter = state.toString()
//                    text = if (LocalHelperUtil.isEnglish()) {
//                        notificationCounter
//                    } else {
//                        convertToArabicNumber(state)
//                    }
//                    visibility =
////                        if (notificationCounter.isNotEmpty() && notificationCounter != "0")
//                        if (notificationCounter.isNotEmpty())
//                            View.VISIBLE
//                        else View.INVISIBLE
//                }
            }
        }
    }

    private fun fetchProfileState() {
        lifecycleScope.launchWhenStarted {
            viewModel.getProfileState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()
                        if (UserUtil.isUserLogin() && state.data != null) {
                            viewModelShared.profileData.value = state.data
                            Glide.with(requireContext())
//                                .load(EramoApi.IMAGE_URL_PROFILE + state.data.mImage)
                                .load(state.data.imageUrl)
                                .into(binding.inTbLayout.toolbarIvProfile)
                        }
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

    private fun fetchCheckProductStockState() {
        lifecycleScope.launchWhenStarted {
            viewModel.checkProductStockState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()

                        viewModel.updateCartItem(modelRv?.id.toString(), qtyRv.toString(), cartDataRv!!)

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

    private fun createCartJsonObject(product_id: Int, quantity: Int, hashMap: HashMap<String, String>): String {
//        val jsonArray = arrayListOf<JSONObject>()
//        val jsonObject = JSONObject()
//
//        val extraObject = JSONObject()
//        val extrasArray = arrayListOf<JSONObject>()
//
//        jsonObject.put("product_id", product_id)
//        jsonObject.put("quantity", quantity)
//
//        for ((key, value) in hashMap) {
//            println("Key: $key, Value: $value")
//            extraObject.put("extra_id", key)
//            extraObject.put("extras_quantity", value)
//
//            extrasArray.add(extraObject)
//        }
//
//        jsonObject.put("extras", extrasArray)
//
//        jsonArray.add(jsonObject)
//
//        return jsonArray.toString()

        val jsonArray = arrayListOf<JSONObject>()
        val jsonObject = JSONObject()

        jsonObject.put("product_id", product_id)
        jsonObject.put("quantity", quantity)


        val extrasArray = JSONArray()

        for (i in hashMap) {
            val extraObject = JSONObject()

            extraObject.put("extra_id", i.key)
            extraObject.put("extras_quantity", i.value)

            extrasArray.put(extraObject)
        }

//        extrasArray.put(extraObject)

        jsonObject.put("extras", extrasArray)

        jsonArray.add(jsonObject)

        return jsonArray.toString()
    }

    override fun onQuantityClick(model: CartProductModel.ProductList, isIncrease: Boolean) {
        var qty = model.quantity
        qty = if (isIncrease) qty!! + 1 else qty!! - 1

        val cartData = MyCartDataEntity(
            productId = model.id,
            productName = model.title,
            categoryName = model.productCategory,
            imageUrl = model.image,
            modelNumber = model.modelNumber,
            price = model.price,
            productQty = qty,
            sizeId = model.sizeId?.toInt(),
            colorId = model.colorId?.toInt()
        )

        if (UserUtil.isUserLogin()) {
            viewModel.updateCartItem(model.productCartId.toString(), qty.toString(), cartData)
        } else {

            if (isIncrease) {
                for (i in cartProductsModel?.productList!!) {
                    if (i?.id == model.id) {
                        if ((qty) <= i?.limitation!!) {

//                        viewModel.updateCartItem(model.productCartId.toString(), qty.toString(), cartData)
                            modelRv = model
                            cartDataRv = cartData
                            qtyRv = qty
                            viewModel.checkProductStock(
                                model.id.toString(), qty.toString(), model.sizeId!!, model.colorId!!
                            )

                        } else {
                            showToast(getString(R.string.you_cant_buy_more_than_limit))
                        }
                    }
                }

                if (cartProductsModel?.productList!!.isEmpty()) {
                    if (qty <= model.limitation!!.toInt()) {
//                        viewModel.updateCartItem(model.productCartId.toString(), qty.toString(), cartData)
                        modelRv = model
                        cartDataRv = cartData
                        qtyRv = qty
                        viewModel.checkProductStock(
                            model.id.toString(), qty.toString(), model.sizeId!!, model.colorId!!
                        )

                    } else {
                        showToast(getString(R.string.you_cant_buy_more_than_limit))
                    }
                }

            } else {
                viewModel.updateCartItem(model.productCartId.toString(), qty.toString(), cartData)
            }
        }
    }

    override fun onRemoveClick(model: CartProductModel.ProductList) {
        viewModel.removeCartItem(model.productCartId.toString(), model.id.toString())
    }

    override fun onExtrasClick(model: CartProductModel.ProductList) {

        if (UserUtil.isUserLogin()) {
            val extrasBottomSheetDialogFragment = ExtrasBottomSheetDialogFragment(
                model.id.toString(), model.quantity.toString(),
                model.extras ?: emptyList()
            )

            extrasBottomSheetDialogFragment.setListener(this@CartFragment)
            extrasBottomSheetDialogFragment.show(
                activity?.supportFragmentManager!!,
                "extrasBottomSheetDialogFragment"
            )
        }else{
            findNavController().navigate(R.id.loginDialog)
        }
    }

    override fun onInstallationChange(model: CartProductModel.ProductList, isChecked: Boolean) {
//        viewModel.updateCartItem(
//            model.id!!,
//            model.productIdFk!!,
//            model.productQty!!,
//            model.productPrice!!,
//            model.productSize!!,
//            model.productColor!!,
//        )
    }

    override fun onTotalPriceCompleted(totalPrice: Double) {
        if (!UserUtil.isUserLogin()) {
            binding.FCartTvTotal.text = if (UserUtil.getCurrency() == Constants.CURRENCY_EGP) {
                getString(R.string.total_s_egp, formatPrice(removePriceComma(totalPrice.toString()).toDouble()))
            } else {
                getString(R.string.total_s_usd, formatPrice(removePriceComma(totalPrice.toString()).toDouble()))
            }
            //getString(R.string.total_s_egp, totalPrice.toString())
            cartAdapter.clearTotalPrice()
        }
    }

    override fun onExtrasDoneSelectionClick(productId: String, productQuantity: String, hashMap: HashMap<String, String>) {
        val addToCartJson = createCartJsonObject(productId.toInt(), productQuantity.toInt(), hashMap)

        viewModel.addToCart(addToCartJson)

        Log.e("createCartJsonObject", createCartJsonObject(productId.toInt(), productQuantity.toInt(), hashMap))
    }
}