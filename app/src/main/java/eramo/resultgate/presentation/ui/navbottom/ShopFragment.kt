package eramo.resultgate.presentation.ui.navbottom

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.paging.PagingData
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import eramo.resultgate.R
import eramo.resultgate.data.local.entity.MyFavouriteEntity
import eramo.resultgate.databinding.FragmentShopBinding
import eramo.resultgate.domain.model.products.CategoryModel
import eramo.resultgate.domain.model.products.ShopProductModel
import eramo.resultgate.presentation.adapters.FooterLoadStateAdapter
import eramo.resultgate.presentation.adapters.recycleview.vertical.ProductShopPagingAdapter
import eramo.resultgate.presentation.ui.dialog.FilterSubCategoryProductsDialogArgs
import eramo.resultgate.presentation.ui.dialog.LoadingDialog
import eramo.resultgate.presentation.ui.navbottom.extension.ProductDetailsFragmentArgs
import eramo.resultgate.presentation.ui.navbottom.extension.SearchFragmentArgs
import eramo.resultgate.presentation.viewmodel.SharedViewModel
import eramo.resultgate.presentation.viewmodel.navbottom.ShopViewModel
import eramo.resultgate.util.LocalHelperUtil
import eramo.resultgate.util.MySingleton
import eramo.resultgate.util.UserUtil
import eramo.resultgate.util.convertToArabicNumber
import eramo.resultgate.util.navOptionsAnimation
import eramo.resultgate.util.onBackPressed
import eramo.resultgate.util.showToast
import eramo.resultgate.util.state.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class ShopFragment : Fragment(R.layout.fragment_shop),
    ProductShopPagingAdapter.OnItemClickListener {

    @Inject
    lateinit var productShopPagingAdapter: ProductShopPagingAdapter

    private lateinit var binding: FragmentShopBinding
    private val viewModel by viewModels<ShopViewModel>()
    private val viewModelShared: SharedViewModel by activityViewModels()
    private val args by navArgs<ShopFragmentArgs>()
    private val categoryId get() = args.categoryId
    private val brandId =UserUtil.getBrandId()
    private val filterSubCategoryProductsObject get() = args.FilterSubCategoryProducts
    private val titleList = ArrayList<String>()
    private val catIdsList = ArrayList<String>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        LocalHelperUtil.loadLocal(requireActivity())
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentShopBinding.bind(view)
        productShopPagingAdapter.setListener(this)
        setupViewWithPaging()
        setupToolbar()

        if (!UserUtil.isUserLogin()) {
            viewModel.getFavouriteListDB()
        }
        binding.apply {
            FShopRvProducts.adapter = productShopPagingAdapter

            FShopIvFilter.setOnClickListener {
                findNavController().navigate(R.id.filterFragment, null, navOptionsAnimation())
            }

            FShopIvFilterSubCategory.setOnClickListener {
                findNavController().navigate(
                    R.id.filterSubCategoryProductsDialog,
                    FilterSubCategoryProductsDialogArgs(categoryId.toString()).toBundle(),
                    navOptionsAnimation()
                )
            }

            FShopEtSearch.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val query = FShopEtSearch.text.toString().trim()
                    if (query.isEmpty()) showToast(getString(R.string.enter_a_query))
                    else findNavController().navigate(
                        R.id.searchFragment,
                        SearchFragmentArgs(searchModel = null, searchTitle = query).toBundle(),
                        navOptionsAnimation()
                    )
                }
                true
            }

            ivSearch.setOnClickListener {
                val query = FShopEtSearch.text.toString().trim()
                if (query.isEmpty()) showToast(getString(R.string.enter_a_query))
                else findNavController().navigate(
                    R.id.searchFragment,
                    SearchFragmentArgs(searchModel = null, searchTitle = query).toBundle(),
                    navOptionsAnimation()
                )
            }

        }

        if (categoryId != null) {
            viewModel.productsByCat(categoryId.toString(),brandId)
            binding.FShopIvFilter.visibility = View.GONE
            binding.FShopIvFilterSubCategory.visibility = View.VISIBLE
        } else if (brandId != null) {
            viewModel.productsByBrand(brandId.toString())
            binding.FShopIvFilter.visibility = View.GONE
            binding.FShopIvFilterSubCategory.visibility = View.VISIBLE

        } else {
            viewModel.allProducts()
            binding.FShopIvFilter.visibility = View.VISIBLE
            binding.FShopIvFilterSubCategory.visibility = View.GONE
        }

        filterSubCategoryProductsObject?.let {
            viewModel.filterSubCategoryProducts(
                filterSubCategoryProductsObject!!.subCategoryId,
                filterSubCategoryProductsObject!!.type,
                filterSubCategoryProductsObject!!.value,
                filterSubCategoryProductsObject!!.priceTo,
                filterSubCategoryProductsObject!!.priceFrom
            )
        }

        viewModelShared.getNotificationCount()

        fetchAllProductsState()
        fetchFilterSubCategoryProductsState()
        fetchAllCategories()
        fetchProductsByCatState()
        fetchProductsByBrandState()
        fetchAddFavouriteState()
        fetchRemoveFavouriteState()
        fetchCartCountState()
        fetchNotificationCount()
        fetchAddRemoveItemWishlistAndRefreshState()

        fetchAdRemoveItemWishlistDBState()
        fetchGetFavouriteListDBState()
        handleLoadingCancellation()

        this@ShopFragment.onBackPressed { findNavController().popBackStack() }
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

    private fun fetchAllProductsState() {
        lifecycleScope.launchWhenStarted {
            viewModel.allProductsState.collect { data ->

                data?.let {
                    productShopPagingAdapter.submitData(viewLifecycleOwner.lifecycle, it)
//                    binding.FShopRvProducts.scrollToPosition(0)
                }
            }
        }
    }

    private fun fetchFilterSubCategoryProductsState() {
        lifecycleScope.launchWhenStarted {
            viewModel.filterSubCategoryProductsState.collect { data ->

                data?.let {
                    productShopPagingAdapter.submitData(viewLifecycleOwner.lifecycle, it)
//                    binding.FShopRvProducts.scrollToPosition(0)
                }
            }
        }
    }

    private fun fetchAllCategories() {
        lifecycleScope.launchWhenStarted {
            viewModel.allCategoriesState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()
                        setupSpinner(state.data!!)
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

    private fun setupSpinner(body: List<CategoryModel>) {
        binding.apply {
            lifecycleScope.launchWhenStarted {

                titleList.clear()
                catIdsList.clear()
                titleList.add(0, getString(R.string.all))
                catIdsList.add(0, "")
                for (model in body) {
                    titleList.add(model.title)
                    catIdsList.add(model.manufacturerId)
                }

                binding.FShopSpinnerFilter.adapter =
                    ArrayAdapter(
                        requireContext(),
                        androidx.constraintlayout.widget.R.layout.support_simple_spinner_dropdown_item,
                        titleList
                    )
//                binding.FShopSpinnerFilter.setSelection(catIdsList.indexOf(manufacturerId))

                withContext(Dispatchers.Main) {
                    binding.FShopSpinnerFilter.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parentView: AdapterView<*>?,
                                selectedItemView: View,
                                position: Int,
                                id: Long
                            ) {

                                if (catIdsList[position].isEmpty())
                                    viewModel.allProducts()
                                else
                                    viewModel.productsByCat(catIdsList[position],brandId)
                            }

                            override fun onNothingSelected(parentView: AdapterView<*>?) {
                                // your code here
                            }
                        }
                }
            }
        }
    }

    private fun fetchProductsByCatState() {
        lifecycleScope.launchWhenStarted {
            viewModel.productsByCatState.collect { data ->
                data?.let {
                    productShopPagingAdapter.submitData(viewLifecycleOwner.lifecycle, it)

//                    binding.FShopRvProducts.scrollToPosition(0)
//                    binding.FShopRvProducts.layoutManager?.onRestoreInstanceState(recyclerViewState)
                }
            }
        }
    }

    private fun fetchProductsByBrandState() {
        lifecycleScope.launchWhenStarted {
            viewModel.productsByBrandState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()
                        Log.e("test", state.data.toString())

                        state?.let {
                            productShopPagingAdapter.submitData(viewLifecycleOwner.lifecycle, convertListToPagingData(it.data!!))
                            Log.e("test2", convertListToPagingData(it.data!!).toString())
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

    private fun convertListToPagingData(list: List<ShopProductModel>): PagingData<ShopProductModel> {
        return PagingData.from(list)
    }

    private fun fetchRemoveFavouriteState() {
        lifecycleScope.launchWhenStarted {
            viewModel.removeFavouriteState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()
                        showToast(state.data?.message ?: getString(R.string.success))
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

    private fun fetchAddFavouriteState() {
        lifecycleScope.launchWhenStarted {
            viewModel.addFavouriteState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()
                        showToast(state.data?.message ?: getString(R.string.success))
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

    private fun setupViewWithPaging() {
        binding.apply {
            FShopRvProducts.adapter =
                productShopPagingAdapter.withLoadStateHeaderAndFooter(
                    header = FooterLoadStateAdapter { productShopPagingAdapter.retry() },
                    footer = FooterLoadStateAdapter { productShopPagingAdapter.retry() }
                )

            FShopBtnRetry.setOnClickListener { viewModel.allProducts() }
            productShopPagingAdapter.addLoadStateListener { loadState ->
                if (loadState.source.refresh is LoadState.Loading) LoadingDialog.showDialog()
                else LoadingDialog.dismissDialog()
                FShopRvProducts.isVisible = loadState.source.refresh is LoadState.NotLoading
                FShopTvNoLoad.isVisible = loadState.source.refresh is LoadState.Error
                FShopBtnRetry.isVisible = loadState.source.refresh is LoadState.Error
                FShopTvResultCount.text = getString(
                    R.string.s_product,
                    productShopPagingAdapter.itemCount.toString()
                )

                val isEmptyView = loadState.source.refresh is LoadState.NotLoading &&
                        loadState.append.endOfPaginationReached &&
                        productShopPagingAdapter.itemCount < 1
                if (isEmptyView) {
                    FShopRvProducts.isVisible = false
//                    FShopTvNoResult.isVisible = true
                    lottieNoData.isVisible = true
                } else
//                    FShopTvNoResult.isVisible = false
                    lottieNoData.isVisible = false
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
                        binding.inTbLayout.toolbarInNotification.tvCount.apply {
                            val notificationCounter = state.data?.count.toString()
                            text = notificationCounter
                            visibility =
                                if (notificationCounter.isNotEmpty() && notificationCounter != "0")
                                    View.VISIBLE
                                else View.INVISIBLE
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

    private fun fetchAddRemoveItemWishlistAndRefreshState() {
        lifecycleScope.launchWhenStarted {
            viewModel.addRemoveItemWishlistState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()

//                        binding.FShopRvProducts.adapter?.notifyDataSetChanged()
                        if (categoryId != null) {
                            viewModel.productsByCat(categoryId.toString(),brandId)
                            binding.FShopIvFilter.visibility = View.GONE
                        } else {
                            viewModel.allProducts()
                            binding.FShopIvFilter.visibility = View.VISIBLE
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

    private fun fetchAdRemoveItemWishlistDBState() {
        lifecycleScope.launchWhenStarted {
            viewModel.addRemoveItemWishlistStateDB.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()
                        viewModel.getFavouriteListDB()
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
                        val list = mutableListOf<Int>()

                        for (i in state.data!!) {
                            list.add(i.productId!!)
                        }

                        MySingleton.favouriteDbIdsList = list

                        productShopPagingAdapter.notifyDataSetChanged()
//                        recallTheProducts()
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

    private fun callProductsApi() {
        if (categoryId != null) {
            viewModel.productsByCat(categoryId.toString(),brandId)
            binding.FShopIvFilter.visibility = View.GONE
            binding.FShopIvFilterSubCategory.visibility = View.VISIBLE
        } else {
            viewModel.allProducts()
            binding.FShopIvFilter.visibility = View.VISIBLE
            binding.FShopIvFilterSubCategory.visibility = View.GONE
        }
    }

    private fun recallTheProducts() {
        if (categoryId != null) {
            viewModel.productsByCat(categoryId.toString(),brandId)
            binding.FShopIvFilter.visibility = View.GONE
            binding.FShopIvFilterSubCategory.visibility = View.VISIBLE
        } else {
            viewModel.allProducts()
            binding.FShopIvFilter.visibility = View.VISIBLE
            binding.FShopIvFilterSubCategory.visibility = View.GONE
        }
        filterSubCategoryProductsObject?.let {
            viewModel.filterSubCategoryProducts(
                filterSubCategoryProductsObject!!.subCategoryId,
                filterSubCategoryProductsObject!!.type,
                filterSubCategoryProductsObject!!.value,
                filterSubCategoryProductsObject!!.priceTo,
                filterSubCategoryProductsObject!!.priceFrom
            )
        }
    }


    //___________________________,_________________________________________________________________//
    // adapters click

    override fun onProductShopClick(model: ShopProductModel) {
        findNavController().navigate(
            R.id.productDetailsFragment,
            ProductDetailsFragmentArgs(model.id.toString()).toBundle(),
            navOptionsAnimation()
        )
    }

    override fun onFavouriteClick(position: Int, model: ShopProductModel, isFav: Boolean) {
        super.onFavouriteClick(position, model, isFav)
        if (UserUtil.isUserLogin()) {

            viewModel.addRemoveItemWishlistDB(
                MyFavouriteEntity(
                    productId = model.id,
                    productName = model.title,
                    categoryName = model.category?.title,
                    imageUrl = model.primaryImageUrl,
                    modelNumber = "",
                    price = model.realPrice!!.toFloat(),
                    fakePrice = model.fakePrice!!.toFloat(),
                    isNew = model.new,
                    profitPercent = model.profitPercent
                )
            )
            viewModel.addRemoveItemWishlist(model.id.toString())
            if (isFav) viewModel.removeFavourite(model.id.toString())
            else viewModel.addFavourite(model.id.toString())
        }

        else {
            findNavController().navigate(R.id.loginDialog)

 //           recyclerViewState = binding.FShopRvProducts.layoutManager?.onSaveInstanceState()

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