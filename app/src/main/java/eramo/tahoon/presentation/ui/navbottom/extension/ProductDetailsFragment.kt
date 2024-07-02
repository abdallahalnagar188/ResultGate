package eramo.tahoon.presentation.ui.navbottom.extension

import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.ParseException
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import eramo.tahoon.R
import eramo.tahoon.data.local.entity.MyCartDataEntity
import eramo.tahoon.data.local.entity.MyFavouriteEntity
import eramo.tahoon.data.remote.EramoApi
import eramo.tahoon.data.remote.dto.products.ProductDetailsResponse
import eramo.tahoon.databinding.FragmentProductDetailsBinding
import eramo.tahoon.domain.model.CartProductModel
import eramo.tahoon.presentation.adapters.recycleview.horizontal.ProductColorAdapter
import eramo.tahoon.presentation.adapters.recycleview.horizontal.ProductMoreAdapter
import eramo.tahoon.presentation.adapters.recycleview.horizontal.ProductSizeAdapter
import eramo.tahoon.presentation.ui.dialog.LoadingDialog
import eramo.tahoon.presentation.viewmodel.SharedViewModel
import eramo.tahoon.presentation.viewmodel.navbottom.extension.ProductDetailsViewModel
import eramo.tahoon.util.*
import eramo.tahoon.util.state.UiState
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

@AndroidEntryPoint
class ProductDetailsFragment : Fragment(R.layout.fragment_product_details),
    ProductMoreAdapter.OnItemClickListener {

    @Inject
    lateinit var productMoreAdapter: ProductMoreAdapter

    @Inject
    lateinit var productSizeAdapter: ProductSizeAdapter

    @Inject
    lateinit var productColorAdapter: ProductColorAdapter

    private lateinit var binding: FragmentProductDetailsBinding
    private val args by navArgs<ProductDetailsFragmentArgs>()
    private val productId get() = args.productId
    private val viewModel by viewModels<ProductDetailsViewModel>()
    private val viewModelShared: SharedViewModel by activityViewModels()
    private lateinit var productModel: ProductDetailsResponse
    private var isBuying = false
    private var hasSize = true
    private var hasColor = true
    private var isProductExtraTwoChecked = false
    private var isProductExtraThreeChecked = false
    private var qty = 1

    private var cartProductsModel: CartProductModel? = null

    override fun onStart() {
        super.onStart()
        viewModel.getProduct(productId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        LocalHelperUtil.loadLocal(requireActivity())
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProductDetailsBinding.bind(view)
        setupToolbar()
        setupPlusMinusQty()

        if (!UserUtil.isUserLogin()) {
            viewModel.cartData()
        }

        Log.e("productId", productId)

        productMoreAdapter.setListener(this)
        binding.apply {
            FDProductTvPriceBefore.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            FDProductTvCount.text = qty.toString()
            FDProductInBuyWith.tvAddToCart.isVisible = isProductExtraTwoChecked || isProductExtraThreeChecked

            FDProductIvImage.setOnClickListener {
                findNavController().navigate(
                    R.id.sliderZoomFragment, SliderZoomFragmentArgs(
                        FDProductIvImage.drawable.toBitmap()
                    ).toBundle()
                )
            }

            FDProductIvShare.setOnClickListener {
                setupShare()
            }

            FDProductIvFav.setOnClickListener {
                if (UserUtil.isUserLogin()) {
                    viewModel.addRemoveItemWishlist(productId)
                } else {
                    val myFavouriteEntity = MyFavouriteEntity(
                        productId = productModel.data?.get(0)?.id!!,
                        productName = productModel.data?.get(0)?.title!!,
                        categoryName = productModel.data?.get(0)?.category?.get(0)?.title,
                        imageUrl = productModel.data?.get(0)?.primaryImageUrl!!,
                        modelNumber = productModel.data?.get(0)?.modelNumber,
                        price = productModel.data?.get(0)?.realPrice!!.toFloat(),
                        fakePrice = productModel.data?.get(0)?.fakePrice!!.toFloat(),
                        isNew = productModel.data?.get(0)?.new,
                        profitPercent = productModel.data?.get(0)?.profitPercent
                    )
                    viewModel.addRemoveItemWishlistDB(myFavouriteEntity)
                }
            }

            FDProductTvAddToCart.setOnClickListener {
                // Default size & color id (-1)
                var size = -1
                var color = -1

                if (hasSize) {

                    // Check sizes list
                    if (!productModel.data?.get(0)?.sizes.isNullOrEmpty()) {

                        // Check size selection
                        if (productSizeAdapter.getSelectedSizeId().isEmpty()) {
                            showToast(getString(R.string.select_a_size))
                            return@setOnClickListener
                        } else {
                            size = productSizeAdapter.getSelectedSizeId().toInt()
                        }

                    } else {
                        size = -1
                    }
                }

                if (hasColor) {

                    // Check colors list
                    if (!productModel.data?.get(0)?.colors.isNullOrEmpty()) {

                        // Check color selection
                        if (productColorAdapter.getSelectedColorId().isEmpty()) {
                            showToast(getString(R.string.select_a_color))
                            return@setOnClickListener
                        } else {
                            color = productColorAdapter.getSelectedColorId().toInt()
                        }

                    } else {
                        color = -1
                    }
                }

                isBuying = false
                val cart = createAddToCartJsonObject(
                    productModel.data?.get(0)?.id!!,
                    qty,
                    getDetailsId(color, size), getVendorId(productModel.data?.get(0)?.vendorId)
                )

                val myCartDataEntity = MyCartDataEntity(
                    productId = productModel.data?.get(0)?.id!!,
                    productName = productModel.data?.get(0)?.title,
                    categoryName = productModel.data?.get(0)?.category?.get(0)?.title,
                    imageUrl = productModel.data?.get(0)?.primaryImageUrl,
                    modelNumber = productModel.data?.get(0)?.modelNumber,
                    price = removePriceComma(productModel.data?.get(0)?.realPrice.toString()).toFloat(),
                    limitation = productModel.data?.get(0)?.limitation,
                    productQty = qty,
                    sizeId = size,
                    colorId = color,
                    vendorId = productModel.data?.get(0)?.vendorId,
                    vendorName = productModel.data?.get(0)?.vendorName,

                )

                if (UserUtil.isUserLogin()) {
                 if ((productModel.data?.get(0)?.cart_qty!!)+(qty) <= (productModel.data?.get(0)?.limitation)!!){
                     viewModel.addToCart(cart,myCartDataEntity)
                     viewModel.getProduct(productId)
                 }else{
                     showToast(getString(R.string.you_cant_buy_more_than_limit))
                 }

                } else {
                    if (cartProductsModel?.productList!!.isEmpty()) {
                        if (qty <= productModel.data?.get(0)?.limitation!!.toInt()) {
                            viewModel.checkProductStock(
                                productModel.data?.get(0)?.id.toString(),
                                qty.toString(),
                                size.toString(),
                                color.toString(),
                                Constants.CLICKED_TV_ADD_TO_CART
                            )

                        } else {
                            showToast(getString(R.string.you_cant_buy_more_than_limit))
                        }
                    }
                    else {
                        for (item in cartProductsModel?.productList!!) {
                            if (item?.id == productModel.data?.get(0)?.id) {
                                if ((qty + item?.quantity!!) <= item.limitation!!) {

                                    viewModel.checkProductStock(
                                        productModel.data?.get(0)?.id.toString(),
//                                        qty.toString(),
                                        (qty + item.quantity).toString(),
                                        size.toString(),
                                        color.toString(),
                                        Constants.CLICKED_TV_ADD_TO_CART
                                    )

                                    break
                                } else {
                                    showToast(getString(R.string.you_cant_buy_more_than_limit))
                                }
                            }
                        }
                        val list = mutableListOf<Int>()

                        for (item in cartProductsModel?.productList!!) {
                            list.add(item?.id!!)
                        }

                        if (!list.contains(productModel.data?.get(0)?.id)) {
                            if (qty <= productModel.data?.get(0)?.limitation!!.toInt()) {
                                viewModel.checkProductStock(
                                    productModel.data?.get(0)?.id.toString(),
                                    qty.toString(),
                                    size.toString(),
                                    color.toString(),
                                    Constants.CLICKED_TV_ADD_TO_CART
                                )

                            } else {
                                showToast(getString(R.string.you_cant_buy_more_than_limit))
                            }
                        }
                    }
                }
            }

            FDProductBtnBuy.setOnClickListener {
                var size = -1
                var color = -1

                if (hasSize) {

                    if (!productModel.data?.get(0)?.sizes.isNullOrEmpty()) {
                        if (productSizeAdapter.getSelectedSizeId().isEmpty()) {
                            showToast(getString(R.string.select_a_size))
                            return@setOnClickListener
                        } else {
                            size = productSizeAdapter.getSelectedSizeId().toInt()
                        }
                    } else {
                        size = -1
                    }
                }

                if (hasColor) {
                    if (!productModel.data?.get(0)?.colors.isNullOrEmpty()) {
                        if (productColorAdapter.getSelectedColorId().isEmpty()) {
                            showToast(getString(R.string.select_a_color))
                            return@setOnClickListener
                        } else {
//                            color = productSizeAdapter.getSelectedSizeId().toInt()
                            color = productColorAdapter.getSelectedColorId().toInt()
                        }
                    } else {
                        color = -1
                    }
                }

                isBuying = true
                val cart = createAddToCartJsonObject(
                    productModel.data?.get(0)?.id!!,
                    qty,
                    getDetailsId(color, size), getVendorId(productModel.data?.get(0)?.vendorId)
                )

                val myCartDataEntity = MyCartDataEntity(
                    productId = productModel.data?.get(0)?.id!!,
                    productName = productModel.data?.get(0)?.title,
                    categoryName = productModel.data?.get(0)?.category?.get(0)?.title,
                    imageUrl = productModel.data?.get(0)?.primaryImageUrl,
                    modelNumber = productModel.data?.get(0)?.modelNumber,
                    price = removePriceComma(productModel.data?.get(0)?.realPrice.toString()).toFloat(),
                    limitation = productModel.data?.get(0)?.limitation,
                    productQty = qty,
                    sizeId = size,
                    colorId = color,
                    vendorId = productModel.data?.get(0)?.vendorId,
                    vendorName = productModel.data?.get(0)?.vendorName,

                )

                if (UserUtil.isUserLogin()) {
                    if ((productModel.data?.get(0)?.cart_qty!!)+(qty) <= (productModel.data?.get(0)?.limitation)!!){
                        viewModel.addToCart(cart,myCartDataEntity)
                        viewModel.getProduct(productId)
                    }else{
                        showToast(getString(R.string.you_cant_buy_more_than_limit))
                    }

                } else {
                    if (cartProductsModel?.productList!!.isEmpty()) {
                        if (qty <= productModel.data?.get(0)?.limitation!!.toInt()) {
                            viewModel.checkProductStock(
                                productModel.data?.get(0)?.id.toString(),
                                qty.toString(),
                                size.toString(),
                                color.toString(),
                                Constants.CLICKED_TV_ADD_TO_CART
                            )

                        } else {
                            showToast(getString(R.string.you_cant_buy_more_than_limit))
                        }
                    }
                    else {
                        for (item in cartProductsModel?.productList!!) {
                            if (item?.id == productModel.data?.get(0)?.id) {
                                if ((qty + item?.quantity!!) <= item.limitation!!) {

                                    viewModel.checkProductStock(
                                        productModel.data?.get(0)?.id.toString(),
//                                        qty.toString(),
                                        (qty + item.quantity).toString(),
                                        size.toString(),
                                        color.toString(),
                                        Constants.CLICKED_TV_ADD_TO_CART
                                    )

                                    break
                                } else {
                                    showToast(getString(R.string.you_cant_buy_more_than_limit))
                                }
                            }
                        }
                        val list = mutableListOf<Int>()

                        for (item in cartProductsModel?.productList!!) {
                            list.add(item?.id!!)
                        }

                        if (!list.contains(productModel.data?.get(0)?.id)) {
                            if (qty <= productModel.data?.get(0)?.limitation!!.toInt()) {
                                viewModel.checkProductStock(
                                    productModel.data?.get(0)?.id.toString(),
                                    qty.toString(),
                                    size.toString(),
                                    color.toString(),
                                    Constants.CLICKED_TV_ADD_TO_CART
                                )

                            } else {
                                showToast(getString(R.string.you_cant_buy_more_than_limit))
                            }
                        }
                    }
                }
            }
        }

        viewModelShared.getNotificationCount()


        fetchProductState()
//        fetchAddFavouriteState()
//        fetchRemoveFavouriteState()
        fetchAddToCartState()
        fetchCartCountState()

        fetchCartDataState()

        fetchCheckProductStockState()
        fetchNotificationCount()
        fetchAddRemoveFavouriteState()
        fetchAdRemoveItemWishlistDBState()
        fetchGetFavouriteListDBState()
        handleLoadingCancellation()

        Log.e("productId", productId.toString())

        this@ProductDetailsFragment.onBackPressed { findNavController().popBackStack() }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getCartCount()

        if (!UserUtil.isUserLogin()) {
            viewModel.cartData()
        }
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

    private fun setupPlusMinusQty() {
        binding.apply {
            FDProductIvPlus.setOnClickListener { FDProductTvCount.text = (++qty).toString() }
            FDProductIvMinus.setOnClickListener {
                if (qty > 1) FDProductTvCount.text = (--qty).toString()
            }
        }
    }

    private fun setupShare() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent
//            .putExtra(Intent.EXTRA_TEXT, "${EramoApi.DEEPLINK_URL}${productModel.productId}")
            .putExtra(
                Intent.EXTRA_TEXT,
                "${EramoApi.DEEPLINK_URL}${if (LocalHelperUtil.isEnglish()) Constants.API_HEADER_LANG_EN else Constants.API_HEADER_LANG_AR}/product/" +
                        "${productModel.data?.get(0)?.slug}"
            )
        shareIntent.type = "text/plain"
        requireActivity().startActivity(shareIntent)
    }

    private fun fetchProductState() {
        lifecycleScope.launchWhenStarted {
            viewModel.productState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()
                        binding.root.visibility = View.VISIBLE
                        productModel = state.data!!
                        setupBuyWithActions()
                        setupSwitchedData()
                        setupProductInfo()
                    }

                    is UiState.Error -> {
                        LoadingDialog.dismissDialog()
                        binding.root.visibility = View.VISIBLE

                        try {
                            showToast(parseErrorResponse(state.message!!.asString(requireContext())))
                        } catch (e: Exception) {
                            e.printStackTrace()
                            showToast(state.message!!.asString(requireContext()))
                        }
                    }

                    is UiState.Loading -> {
                        LoadingDialog.showDialog()
                        binding.root.visibility = View.GONE
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun setupBuyWithActions() {
        binding.FDProductInBuyWith.apply {

            // gone view if no extras
            if (productModel.data?.get(0)?.productsWith.isNullOrEmpty()) {
                binding.view10.visibility = View.GONE
                root.visibility = View.GONE
                return
            }

            // Main product in buyItWith layout
            tvNameOne.text = productModel.data?.get(0)?.title
            tvPriceOne.text = getString(R.string.s_egp, productModel.data?.get(0)?.realPrice.toString())
            Glide.with(requireContext())
                .load(productModel.data?.get(0)?.primaryImageUrl)
                .into(ivImgOne)

            //------//

            cbImgTwo.setOnCheckedChangeListener { _, isChecked ->
                isProductExtraTwoChecked = isChecked
                if (isChecked) ivImgTwo.setBackgroundResource(R.drawable.shape_yellow_low)
                else ivImgTwo.setBackgroundColor(Color.TRANSPARENT)
                tvAddToCart.isVisible = isProductExtraTwoChecked || isProductExtraThreeChecked
            }

            ivImgTwo.setOnClickListener {
                findNavController().navigate(
                    R.id.productDetailsFragment,
                    ProductDetailsFragmentArgs(productModel.data?.get(0)?.productsWith?.get(0)?.id.toString()).toBundle(),
                    navOptionsAnimation()
                )
            }

            tvNameTwo.text = productModel.data?.get(0)?.productsWith?.get(0)?.title
            tvPriceTwo.text = getString(R.string.s_egp, productModel.data?.get(0)?.productsWith?.get(0)?.realPrice.toString())
//            if (productModel.extraProducts!![0].allImageDtos.isNotEmpty()) {
//                productModel.extraProducts!![0].allImageDtos[0].image.let {
//                    Glide.with(requireContext())
//                        .load(EramoApi.IMAGE_URL_GENERAL + it)
//                        .into(ivImgTwo)
//                }
//            }
            Glide.with(requireContext())
                .load(productModel.data?.get(0)?.productsWith?.get(0)?.primaryImageUrl)
                .into(ivImgTwo)

            // adding extras to cart
            tvAddToCart.setOnClickListener {

                var size = -1
                var color = -1

                if (!productModel.data?.get(0)?.sizes.isNullOrEmpty()) {
                    if (productSizeAdapter.getSelectedSizeId().isEmpty()) {
                        showToast(getString(R.string.select_a_size))
                        return@setOnClickListener
                    } else {
                        size = productSizeAdapter.getSelectedSizeId().toInt()
                    }
                } else {
                    size = -1
                }


                if (!productModel.data?.get(0)?.colors.isNullOrEmpty()) {
                    if (productColorAdapter.getSelectedColorId().isEmpty()) {
                        showToast(getString(R.string.select_a_color))
                        return@setOnClickListener
                    } else {
                        color = productColorAdapter.getSelectedColorId().toInt()
                    }
                } else {
                    color = -1
                }


//                val cart = createJsonObject(
//                    productModel.data?.get(0)?.id!!,
//                    qty,
//                    color,
//                    size
//                )

                val cart = createAddToCartJsonObject(
                    productModel.data?.get(0)?.id!!,
                    qty,
                    getDetailsId(color, size), getVendorId(productModel.data?.get(0)?.vendorId)
                )

                val myCartDataEntity = MyCartDataEntity(
                    productId = productModel.data?.get(0)?.id!!,
                    productName = productModel.data?.get(0)?.title,
                    categoryName = productModel.data?.get(0)?.category?.get(0)?.title,
                    imageUrl = productModel.data?.get(0)?.primaryImageUrl,
                    modelNumber = productModel.data?.get(0)?.modelNumber,
//                    price = removePriceComma(productModel.data?.priceAfterTaxes.toString()).toFloat(),
                    price = removePriceComma(productModel.data?.get(0)?.realPrice.toString()).toFloat(),
                    limitation = productModel.data?.get(0)?.limitation,
                    productQty = qty,
                    sizeId = size,
                    colorId = color,
                    vendorId = productModel.data?.get(0)?.vendorId,
                    vendorName = productModel.data?.get(0)?.vendorName,
                )

                if (UserUtil.isUserLogin()) {

                    viewModel.addToCart(cart, myCartDataEntity)

                    if (isProductExtraTwoChecked) {
                        isProductExtraTwoChecked = false
//                    viewModel.addToCart(
//                        productModel.extraProducts!![0],
//                        productModel.extraProducts!![0].productId,
//                        "1",
//                        productModel.extraProducts!![0].displayPrice,
//                        "",
//                        ""
//                    )

//                        val cartExtraTwo = createJsonObject(
//                            productModel.data?.get(0)?.productsWith?.get(0)?.id!!,
//                            1,
//                            -1,
//                            -1
//                        )

                        val cartExtraTwoDetailsId = if (!productModel.data?.get(0)?.productsWith?.get(0)?.stocks.isNullOrEmpty()) {
                            productModel.data?.get(0)?.productsWith?.get(0)?.stocks?.get(0)?.id ?: 0
                        } else {
                            0
                        }

                        val cartExtraTwo = createAddToCartJsonObject(
                            productModel.data?.get(0)?.productsWith?.get(0)?.id!!,
                            1,
                            cartExtraTwoDetailsId, getVendorId(productModel.data?.get(0)?.productsWith?.get(0)?.vendorId)
                        )

                        val myCartDataEntity = MyCartDataEntity(
                            productId = productModel.data?.get(0)?.productsWith?.get(0)?.id!!,
                            productName = productModel.data?.get(0)?.productsWith?.get(0)?.title,
                            categoryName = productModel.data?.get(0)?.productsWith?.get(0)?.title,
                            imageUrl = productModel.data?.get(0)?.productsWith?.get(0)?.primaryImageUrl,
                            modelNumber = "",
                            price = removePriceComma(productModel.data?.get(0)?.productsWith?.get(0)?.realPrice.toString()).toFloat(),
                            limitation = productModel.data?.get(0)?.limitation,
                            productQty = 1,
                            sizeId = -1,
                            colorId = -1,
                            vendorId = productModel.data?.get(0)?.vendorId,
                            vendorName = productModel.data?.get(0)?.vendorName,
                        )
                        viewModel.addToCart(cartExtraTwo, myCartDataEntity)
                    }

                    if (isProductExtraThreeChecked) {
                        isProductExtraThreeChecked = false
//                    viewModel.addToCart(
//                        productModel.extraProducts!![1],
//                        productModel.extraProducts!![1].productId,
//                        "1",
//                        productModel.extraProducts!![1].displayPrice,
//                        "",
//                        ""
//                    )

//                        val cartExtraThree = createJsonObject(
//                            productModel.data?.get(0)?.productsWith?.get(1)?.id!!,
//                            1,
//                            -1,
//                            -1
//                        )

                        val cartExtraThreeDetailsId = if (!productModel.data?.get(0)?.productsWith?.get(1)?.stocks.isNullOrEmpty()) {
                            productModel.data?.get(0)?.productsWith?.get(1)?.stocks?.get(0)?.id ?: 0
                        } else {
                            0
                        }

                        val cartExtraThree = createAddToCartJsonObject(
                            productModel.data?.get(0)?.productsWith?.get(1)?.id!!,
                            1,
                            cartExtraThreeDetailsId, getVendorId(productModel.data?.get(0)?.productsWith?.get(1)?.vendorId)
                        )

                        val myCartDataEntity = MyCartDataEntity(
                            productId = productModel.data?.get(0)?.productsWith?.get(1)?.id!!,
                            productName = productModel.data?.get(0)?.productsWith?.get(1)?.title,
                            categoryName = productModel.data?.get(0)?.productsWith?.get(1)?.title,
                            imageUrl = productModel.data?.get(0)?.productsWith?.get(1)?.primaryImageUrl,
                            modelNumber = "",
                            price = removePriceComma(productModel.data?.get(0)?.productsWith?.get(1)?.realPrice.toString()).toFloat(),
                            limitation = productModel.data?.get(0)?.limitation,
                            productQty = 1,
                            sizeId = -1,
                            colorId = -1,
                            vendorId = productModel.data?.get(0)?.vendorId,
                            vendorName = productModel.data?.get(0)?.vendorName,
                        )
                        viewModel.addToCart(cartExtraThree, myCartDataEntity)
                    }

                } else {

                    viewModel.checkProductStock(
                        productModel.data?.get(0)?.id.toString(),
                        qty.toString(),
                        size.toString(),
                        color.toString(),
                        Constants.CLICKED_TV_ADD_TO_CART_EXTRA
                    )
                }


            }


            //----//

            // invisible product three if not exist
            if (productModel.data?.get(0)?.productsWith?.size!! < 2) {
                groupProductThree.visibility = View.INVISIBLE
                return
            }

            cbImgThree.setOnCheckedChangeListener { _, isChecked ->
                isProductExtraThreeChecked = isChecked
                if (isChecked) ivImgThree.setBackgroundResource(R.drawable.shape_yellow_low)
                else ivImgThree.setBackgroundColor(Color.TRANSPARENT)
                tvAddToCart.isVisible = isProductExtraTwoChecked || isProductExtraThreeChecked
            }

            ivImgThree.setOnClickListener {
                findNavController().navigate(
                    R.id.productDetailsFragment,
                    ProductDetailsFragmentArgs(productModel.data?.get(0)?.productsWith?.get(1)?.id.toString()).toBundle(),
                    navOptionsAnimation()
                )
            }

            tvNameThree.text = productModel.data?.get(0)?.productsWith?.get(1)?.title
            tvPriceThree.text = getString(R.string.s_egp, productModel.data?.get(0)?.productsWith?.get(1)?.realPrice.toString())
//            if (productModel.extraProducts!![1].allImageDtos.isNotEmpty()) {
//                productModel.extraProducts!![1].allImageDtos[0].image.let {
//                    Glide.with(requireContext())
//                        .load(EramoApi.IMAGE_URL_GENERAL + it)
//                        .into(ivImgThree)
//                }
//            }

            Glide.with(requireContext())
                .load(productModel.data?.get(0)?.productsWith?.get(1)?.primaryImageUrl)
                .into(ivImgThree)
        }
    }

    private fun setupSwitchedData() {
        binding.apply {

            val htmlDetails = productModel.data?.get(0)?.details?.trim()
            val htmlFeatures = productModel.data?.get(0)?.features?.trim()
            val htmlExtra = productModel.data?.get(0)?.extras?.trim()

            FDProductTvData.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(htmlDetails, Html.FROM_HTML_MODE_COMPACT)
            } else {
                Html.fromHtml(htmlDetails)
            }

            FDProductBtnFeatures.setOnClickListener {
                FDProductBtnFeatures.setBackgroundResource(R.color.gray_low)
                FDProductBtnExtra.setBackgroundColor(Color.TRANSPARENT)
                FDProductBtnInstruction.setBackgroundColor(Color.TRANSPARENT)
//                FDProductTvData.text = productModel.data?.features?.trim()
                FDProductTvData.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Html.fromHtml(htmlFeatures, Html.FROM_HTML_MODE_COMPACT)
                } else {
                    Html.fromHtml(htmlFeatures)
                }
            }

            FDProductBtnExtra.setOnClickListener {
                FDProductBtnFeatures.setBackgroundColor(Color.TRANSPARENT)
                FDProductBtnExtra.setBackgroundResource(R.color.gray_low)
                FDProductBtnInstruction.setBackgroundColor(Color.TRANSPARENT)
//                FDProductTvData.text = productModel.data?.extras?.trim()
                FDProductTvData.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Html.fromHtml(htmlExtra, Html.FROM_HTML_MODE_COMPACT)
                } else {
                    Html.fromHtml(htmlExtra)
                }

            }

            FDProductBtnInstruction.setOnClickListener {
                FDProductBtnFeatures.setBackgroundColor(Color.TRANSPARENT)
                FDProductBtnExtra.setBackgroundColor(Color.TRANSPARENT)
                FDProductBtnInstruction.setBackgroundResource(R.color.gray_low)
//                FDProductTvData.text = productModel.data?.instructions?.trim()

                FDProductTvData.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Html.fromHtml(htmlDetails, Html.FROM_HTML_MODE_COMPACT)
                } else {
                    Html.fromHtml(htmlDetails)
                }
            }
        }
    }

    private fun setupProductInfo() {
        binding.apply {
            FDProductTvName.text = productModel.data?.get(0)?.title

            FDProductTvCategory.text = productModel.data?.get(0)?.category?.get(0)?.title
//            FDProductTvManufacturerValue.text = productModel.data?.get(0)?.manufacturer?.name

//            if (!productModel.data?.get(0)?.stocks.isNullOrEmpty()) {
//                FDProductTvAvailabilityValue.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
//                FDProductTvAvailabilityValue.text = getString(R.string.in_stock_constant)
//            } else {
//                FDProductTvAvailabilityValue.setTextColor(ContextCompat.getColor(requireContext(), R.color.eramo_color))
//                FDProductTvAvailabilityValue.text = getString(R.string.out_of_stock_constant)
//            }
            if (productModel.data?.get(0)?.inStockQuantity ?: 0 > 0) {
                FDProductTvAvailabilityValue.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
                FDProductTvAvailabilityValue.text = getString(R.string.in_stock_constant)
            } else {
                FDProductTvAvailabilityValue.setTextColor(ContextCompat.getColor(requireContext(), R.color.eramo_color))
                FDProductTvAvailabilityValue.text = getString(R.string.out_of_stock_constant)
            }

            FDProductTvModelNumberValue.text = productModel.data?.get(0)?.modelNumber

//            FDProductTvInStockValue.text = productModel.data?.get(0)?.stocks?.size.toString()
            FDProductTvInStockValue.text = productModel.data?.get(0)?.inStockQuantity.toString()

            FDProductTvVendorNameValue.text = productModel.data?.get(0)?.vendorName

            FDProductTvShipping.text = productModel.data?.get(0)?.shipping

            FDProductTvPriceBefore.text = if (UserUtil.getCurrency() == Constants.CURRENCY_EGP) {
                getString(R.string.s_egp, formatPrice(removePriceComma(productModel.data?.get(0)?.fakePrice ?: "0.0")))
            } else {
                getString(R.string.s_usd, formatPrice(removePriceComma(productModel.data?.get(0)?.fakePrice ?: "0.0")))
            }

            FDProductTvPriceAfter.text = if (UserUtil.getCurrency() == Constants.CURRENCY_EGP) {
                getString(R.string.s_egp, formatPrice(removePriceComma(productModel.data?.get(0)?.realPrice ?: "0.0")))
            } else {
                getString(R.string.s_usd, formatPrice(removePriceComma(productModel.data?.get(0)?.realPrice ?: "0.0")))
            }


            if (removePriceComma(productModel.data?.get(0)?.realPrice.toString()) < removePriceComma(productModel.data?.get(0)?.fakePrice.toString()) && removePriceComma(
                    productModel.data?.get(0)?.fakePrice.toString()
                ) != 0.0
            ) {
                FDProductTvPriceBefore.visibility = View.VISIBLE
            } else {
                FDProductTvPriceBefore.visibility = View.GONE
            }

//            if (productModel.data?.stock == 1) {
//                val green = ContextCompat.getColor(requireContext(), R.color.green)
//                FDProductTvStockStatus.setTextColor(green)
//                FDProductGroupActions.visibility = View.VISIBLE
//            } else {
//                FDProductTvStockStatus.setTextColor(Color.RED)
////                FDProductGroupActions.visibility = View.INVISIBLE
//            }

            productMoreAdapter.submitList(getMoreImagesList(productModel))
            FDProductRvMore.adapter = productMoreAdapter

            productSizeAdapter.submitList(productModel.data?.get(0)?.sizes)
            FDProductRvSize.adapter = productSizeAdapter
            if (productModel.data?.get(0)?.sizes.isNullOrEmpty()) {
                hasSize = false
                FDProductTvProductSize.visibility = View.INVISIBLE
            }

            productColorAdapter.submitList(productModel.data?.get(0)?.colors)
            FDProductRvColor.adapter = productColorAdapter
            if (productModel.data?.get(0)?.colors.isNullOrEmpty()) {
                hasColor = false
                FDProductTvProductColor.visibility = View.INVISIBLE
            }

            Glide.with(requireContext())
                .load(productModel.data?.get(0)?.primaryImageUrl)
                .into(this.FDProductIvImage)

            if (UserUtil.isUserLogin()) {
                if (productModel.data?.get(0)?.isFav == 1) binding.FDProductIvFav.setImageResource(R.drawable.ic_heart_fill)
                else binding.FDProductIvFav.setImageResource(R.drawable.ic_heart)
            } else {
                viewModel.getFavouriteListDB()
            }

        }

    }

    private fun productTaxes(productModel: ProductDetailsResponse): String {
//        val realPrice = productModel.data?.realPrice?.toDouble()
//        val priceWithTaxes = productModel.data?.priceAfterTaxes?.toDouble()

        val realPrice = removePriceComma(productModel.data?.get(0)?.realPrice.toString())
        val priceWithTaxes = removePriceComma(productModel.data?.get(0)?.priceAfterTaxes.toString())


        return (priceWithTaxes - realPrice).toString()
    }

    @Throws(ParseException::class)
    private fun removePriceComma(value: String): Double {
        val newValue = value.replace(",", "")
        return newValue.toDouble()
    }

    private fun getMoreImagesList(productModel: ProductDetailsResponse): List<String> {
        val list = mutableListOf<String>()

        list.add(productModel.data?.get(0)?.primaryImageUrl!!)

        productModel.data.get(0)?.media?.forEach {
            it?.let { list.add(it.imageUrl.toString()) }
        }

        return list
    }

    private fun fetchAddToCartState() {
        lifecycleScope.launchWhenStarted {
            viewModel.addToCartState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()
                        showToast(getString(R.string.product_added_successfully))
                        binding.FDProductInBuyWith.ivImgTwo.setBackgroundColor(Color.TRANSPARENT)
                        binding.FDProductInBuyWith.ivImgThree.setBackgroundColor(Color.TRANSPARENT)
                        binding.FDProductInBuyWith.cbImgTwo.isChecked = false
                        binding.FDProductInBuyWith.cbImgThree.isChecked = false
                        binding.FDProductInBuyWith.tvAddToCart.isVisible = false

                        if (!UserUtil.isUserLogin()) {
                            viewModel.cartData()
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

    private fun fetchCartCountState() {
        lifecycleScope.launchWhenStarted {
            viewModel.cartCountState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()
                        viewModelShared.cartCount.value = state.data?.count
                        if (isBuying) {
                            isBuying = false

                            if (UserUtil.isUserLogin()) {
                                findNavController().navigate(
                                    R.id.checkoutStepOneFragment,
                                    null,
                                    navOptionsAnimation()
                                )
                            } else {
                                findNavController().navigate(
                                    R.id.cartFragment,
                                    null,
                                    navOptionsAnimation()
                                )
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

                        when (state.data?.clicked) {
                            Constants.CLICKED_TV_ADD_TO_CART -> {
                                var size = -1
                                var color = -1

                                if (hasSize) {

//                                    if (!productModel.data?.sizes.isNullOrEmpty()) {
//                                        if (productSizeAdapter.getSelectedSizeId().isEmpty()) {
//                                            showToast(getString(R.string.select_a_size))
//                                            return@collect
//                                        } else {
//                                            size = productSizeAdapter.getSelectedSizeId().toInt()
//                                        }
//                                    } else {
//                                        size = -1
//                                    }
                                }

                                if (hasColor) {
//                                    if (!productModel.data?.colors.isNullOrEmpty()) {
//                                        if (productColorAdapter.getSelectedColorId().isEmpty()) {
//                                            showToast(getString(R.string.select_a_color))
//                                            return@collect
//                                        } else {
////                            color = productSizeAdapter.getSelectedSizeId().toInt()
//                                            color = productColorAdapter.getSelectedColorId().toInt()
//                                        }
//                                    } else {
//                                        color = -1
//                                    }
                                }

                                isBuying = false
//                viewModel.addToCart(
//                    productModel,
//                    productModel.productId,
//                    qty.toString(),
//                    productModel.displayPrice,
//                    productSizeAdapter.getSelectedSizeId(),
//                    productColorAdapter.getSelectedColorId()
//                )

//                                val cart = createJsonObject(
//                                    productModel.data?.get(0)?.id!!,
//                                    qty,
//                                    color,
//                                    size
//                                )

                                val cart = createAddToCartJsonObject(
                                    productModel.data?.get(0)?.id!!,
                                    qty,
                                    getDetailsId(color, size), getVendorId(productModel.data?.get(0)?.vendorId)
                                )

                                val myCartDataEntity = MyCartDataEntity(
                                    productId = productModel.data?.get(0)?.id!!,
                                    productName = productModel.data?.get(0)?.title,
                                    categoryName = productModel.data?.get(0)?.category?.get(0)?.title,
                                    imageUrl = productModel.data?.get(0)?.primaryImageUrl,
                                    modelNumber = productModel.data?.get(0)?.modelNumber,
                                    price = removePriceComma(productModel.data?.get(0)?.realPrice.toString()).toFloat(),
                                    limitation = productModel.data?.get(0)?.limitation,
                                    productQty = qty,
                                    sizeId = size,
                                    colorId = color,
                                    vendorId = productModel.data?.get(0)?.vendorId,
                                    vendorName = productModel.data?.get(0)?.vendorName,
                                )

                                viewModel.addToCart(cart, myCartDataEntity)
                            }
                            //-------------------------------------------------------------------------------------------------------------//
                            Constants.CLICKED_BTN_BUY -> {
                                var size = -1
                                var color = -1

                                if (hasSize) {

//                                    if (!productModel.data?.sizes.isNullOrEmpty()) {
//                                        if (productSizeAdapter.getSelectedSizeId().isEmpty()) {
//                                            showToast(getString(R.string.select_a_size))
//                                            return@collect
//                                        } else {
//                                            size = productSizeAdapter.getSelectedSizeId().toInt()
//                                        }
//                                    } else {
//                                        size = -1
//                                    }
                                }

                                if (hasColor) {
//                                    if (!productModel.data?.colors.isNullOrEmpty()) {
//                                        if (productColorAdapter.getSelectedColorId().isEmpty()) {
//                                            showToast(getString(R.string.select_a_color))
//                                            return@collect
//                                        } else {
////                            color = productSizeAdapter.getSelectedSizeId().toInt()
//                                            color = productColorAdapter.getSelectedColorId().toInt()
//                                        }
//                                    } else {
//                                        color = -1
//                                    }
                                }

                                isBuying = true
                                val cart = createAddToCartJsonObject(
                                    productModel.data?.get(0)?.id!!,
                                    qty,
                                    getDetailsId(color, size), getVendorId(productModel.data?.get(0)?.vendorId)
                                )

                                val myCartDataEntity = MyCartDataEntity(
                                    productId = productModel.data?.get(0)?.id!!,
                                    productName = productModel.data?.get(0)?.title,
                                    categoryName = productModel.data?.get(0)?.category?.get(0)?.title,
                                    imageUrl = productModel.data?.get(0)?.primaryImageUrl,
                                    modelNumber = productModel.data?.get(0)?.modelNumber,
                                    price = removePriceComma(productModel.data?.get(0)?.realPrice.toString()).toFloat(),
                                    limitation = productModel.data?.get(0)?.limitation,
                                    productQty = qty,
                                    sizeId = size,
                                    colorId = color,
                                    vendorId = productModel.data?.get(0)?.vendorId,
                                    vendorName = productModel.data?.get(0)?.vendorName,
                                )

                                viewModel.addToCart(cart, myCartDataEntity)
                            }
                            //-------------------------------------------------------------------------------------------------------------//
                            Constants.CLICKED_TV_ADD_TO_CART_EXTRA -> {

                                var size = -1
                                var color = -1

//                                if (!productModel.data?.sizes.isNullOrEmpty()) {
//                                    if (productSizeAdapter.getSelectedSizeId().isEmpty()) {
//                                        showToast(getString(R.string.select_a_size))
//                                        return@collect
//                                    } else {
//                                        size = productSizeAdapter.getSelectedSizeId().toInt()
//                                    }
//                                } else {
//                                    size = -1
//                                }


//                                if (!productModel.data?.colors.isNullOrEmpty()) {
//                                    if (productColorAdapter.getSelectedColorId().isEmpty()) {
//                                        showToast(getString(R.string.select_a_color))
//                                        return@collect
//                                    } else {
//                                        color = productColorAdapter.getSelectedColorId().toInt()
//                                    }
//                                } else {
//                                    color = -1
//                                }


//                                val cart = createJsonObject(
//                                    productModel.data?.get(0)?.id!!,
//                                    qty,
//                                    color,
//                                    size
//                                )

                                val cart = createAddToCartJsonObject(
                                    productModel.data?.get(0)?.id!!,
                                    qty,
                                    getDetailsId(color, size), getVendorId(productModel.data?.get(0)?.vendorId)
                                )

                                val myCartDataEntity = MyCartDataEntity(
                                    productId = productModel.data?.get(0)?.id!!,
                                    productName = productModel.data?.get(0)?.title,
                                    categoryName = productModel.data?.get(0)?.category?.get(0)?.title,
                                    imageUrl = productModel.data?.get(0)?.primaryImageUrl,
                                    modelNumber = productModel.data?.get(0)?.modelNumber,
//                    price = removePriceComma(productModel.data?.priceAfterTaxes.toString()).toFloat(),
                                    price = removePriceComma(productModel.data?.get(0)?.realPrice.toString()).toFloat(),
                                    limitation = productModel.data?.get(0)?.limitation,
                                    productQty = qty,
                                    sizeId = size,
                                    colorId = color,
                                    vendorId = productModel.data?.get(0)?.vendorId,
                                    vendorName = productModel.data?.get(0)?.vendorName,
                                )

                                viewModel.addToCart(cart, myCartDataEntity)

                                if (isProductExtraTwoChecked) {
                                    isProductExtraTwoChecked = false
//                    viewModel.addToCart(
//                        productModel.extraProducts!![0],
//                        productModel.extraProducts!![0].productId,
//                        "1",
//                        productModel.extraProducts!![0].displayPrice,
//                        "",
//                        ""
//                    )

//                                    val cartExtraTwo = createJsonObject(
//                                        productModel.data?.get(0)?.productsWith?.get(0)?.id!!,
//                                        1,
//                                        -1,
//                                        -1
//                                    )

                                    val cartExtraTwoDetailsId =
                                        if (!productModel.data?.get(0)?.productsWith?.get(0)?.stocks.isNullOrEmpty()) {
                                            productModel.data?.get(0)?.productsWith?.get(0)?.stocks?.get(0)?.id ?: 0
                                        } else {
                                            0
                                        }

                                    val cartExtraTwo = createAddToCartJsonObject(
                                        productModel.data?.get(0)?.productsWith?.get(0)?.id!!,
                                        1,
                                        cartExtraTwoDetailsId, getVendorId(productModel.data?.get(0)?.productsWith?.get(0)?.vendorId)
                                    )


                                    val myCartDataEntity = MyCartDataEntity(
                                        productId = productModel.data?.get(0)?.productsWith?.get(0)?.id!!,
                                        productName = productModel.data?.get(0)?.productsWith?.get(0)?.title,
                                        categoryName = productModel.data?.get(0)?.productsWith?.get(0)?.title,
                                        imageUrl = productModel.data?.get(0)?.productsWith?.get(0)?.primaryImageUrl,
                                        modelNumber = "",
                                        price = removePriceComma(productModel.data?.get(0)?.productsWith?.get(0)?.realPrice.toString()).toFloat(),
                                        limitation = productModel.data?.get(0)?.limitation,
                                        productQty = 1,
                                        sizeId = -1,
                                        colorId = -1,
                                        vendorId = productModel.data?.get(0)?.vendorId,
                                        vendorName = productModel.data?.get(0)?.vendorName,
                                    )
                                    viewModel.addToCart(cartExtraTwo, myCartDataEntity)
                                }

                                if (isProductExtraThreeChecked) {
                                    isProductExtraThreeChecked = false
//                    viewModel.addToCart(
//                        productModel.extraProducts!![1],
//                        productModel.extraProducts!![1].productId,
//                        "1",
//                        productModel.extraProducts!![1].displayPrice,
//                        "",
//                        ""
//                    )

//                                    val cartExtraThree = createJsonObject(
//                                        productModel.data?.get(0)?.productsWith?.get(1)?.id!!,
//                                        1,
//                                        -1,
//                                        -1
//                                    )

                                    val cartExtraThreeDetailsId =
                                        if (!productModel.data?.get(0)?.productsWith?.get(1)?.stocks.isNullOrEmpty()) {
                                            productModel.data?.get(0)?.productsWith?.get(1)?.stocks?.get(0)?.id ?: 0
                                        } else {
                                            0
                                        }

                                    val cartExtraThree = createAddToCartJsonObject(
                                        productModel.data?.get(0)?.productsWith?.get(1)?.id!!,
                                        1,
                                        cartExtraThreeDetailsId, getVendorId(productModel.data?.get(0)?.productsWith?.get(1)?.vendorId)
                                    )

                                    val myCartDataEntity = MyCartDataEntity(
                                        productId = productModel.data?.get(0)?.productsWith?.get(1)?.id!!,
                                        productName = productModel.data?.get(0)?.productsWith?.get(1)?.title,
                                        categoryName = productModel.data?.get(0)?.productsWith?.get(1)?.title,
                                        imageUrl = productModel.data?.get(0)?.productsWith?.get(1)?.primaryImageUrl,
                                        modelNumber = "",
                                        price = removePriceComma(productModel.data?.get(0)?.productsWith?.get(1)?.realPrice.toString()).toFloat(),
                                        limitation = productModel.data?.get(0)?.limitation,
                                        productQty = 1,
                                        sizeId = -1,
                                        colorId = -1,
                                        vendorId = productModel.data?.get(0)?.vendorId,
                                        vendorName = productModel.data?.get(0)?.vendorName,

                                    )
                                    viewModel.addToCart(cartExtraThree, myCartDataEntity)
                                }
                            }
                            //-------------------------------------------------------------------------------------------------------------//
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

    private fun fetchGetFavouriteListDBState() {
        lifecycleScope.launchWhenStarted {
            viewModel.getFavouriteListDBState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()

                        for (i in state.data!!) {
                            if (i.productId.toString() == productId) {
                                binding.FDProductIvFav.setImageResource(R.drawable.ic_heart_fill)
                                return@collect
                            } else {
                                binding.FDProductIvFav.setImageResource(R.drawable.ic_heart)
//                                return@collect
                            }
                        }

                        if (state.data.isNullOrEmpty()) {
                            binding.FDProductIvFav.setImageResource(R.drawable.ic_heart)
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

    private fun fetchNotificationCount() {
        lifecycleScope.launchWhenStarted {
            viewModelShared.notificationCount.collect { state ->
                binding.inTbLayout.toolbarInNotification.tvCount.apply {
                    val notificationCounter = state.toString()
                    text = if (LocalHelperUtil.isEnglish()) {
                        notificationCounter
                    } else {
                        convertToArabicNumber(state)
                    }
                    visibility =
//                        if (notificationCounter.isNotEmpty() && notificationCounter != "0")
                        if (notificationCounter.isNotEmpty())
                            View.VISIBLE
                        else View.INVISIBLE
                }
            }
        }
    }


    private fun fetchCartDataState() {
        lifecycleScope.launchWhenStarted {
            viewModel.cartDataModelState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()
                        cartProductsModel = state.data
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
                    }
                }
            }
        }
    }


    //--------------------- Favourite ---------------------//

    private fun fetchAddRemoveFavouriteState() {
        lifecycleScope.launchWhenStarted {
            viewModel.addRemoveItemWishlistState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()
                        showToast(state.data?.message ?: getString(R.string.success))
                        viewModel.getProduct(productId)
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

    private fun fetchAdRemoveItemWishlistDBState() {
        lifecycleScope.launchWhenStarted {
            viewModel.addRemoveItemWishlistStateDB.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()
                        viewModel.getProduct(productId)
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

    override fun onProductMoreClick(url: String) {
        url.let {
            Glide.with(requireContext())
                .load(it)
                .into(binding.FDProductIvImage)
        }
    }

    private fun createJsonObject(product_id: Int, quantity: Int, color: Int, size: Int): String {
        val jsonArray = arrayListOf<JSONObject>()
        val jsonObject = JSONObject()

        jsonObject.put("product_id", product_id)
        jsonObject.put("quantity", quantity)
        jsonObject.put("extras", JSONArray())

        jsonArray.add(jsonObject)

        return jsonArray.toString()
    }

    private fun createAddToCartJsonObject(productId: Int, quantity: Int, detailsId: Int, vendorId: Int): String {
        val jsonArray = arrayListOf<JSONObject>()
        val jsonObject = JSONObject()

        jsonObject.put("product_id", productId)
        jsonObject.put("quantity", quantity)
        jsonObject.put("details_id", detailsId)
        jsonObject.put("vendor_id", vendorId)

        jsonArray.add(jsonObject)

        return jsonArray.toString()
    }

    private fun getDetailsId(colorId: Int, sizeId: Int): Int {
        var detailsId = 0
        for (i in productModel.data?.get(0)?.stocks!!) {
            if (i?.colorId == colorId && i.sizeId == sizeId) {
                detailsId = i.id!!
            }
        }
        return detailsId
    }

    private fun getVendorId(id: String?): Int {
        return if (id == "") {
            0
        } else id?.toInt() ?: 0
    }

    private fun getDetailsIdForExtras(colorId: Int, sizeId: Int): Int {
        var detailsId = 0
        for (i in productModel.data?.get(0)?.stocks!!) {
            if (i?.colorId == colorId && i.sizeId == sizeId) {
                detailsId = i.id!!
            }
        }
        return detailsId
    }

}