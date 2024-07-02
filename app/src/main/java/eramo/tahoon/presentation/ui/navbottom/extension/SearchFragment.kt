package eramo.tahoon.presentation.ui.navbottom.extension

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import dagger.hilt.android.AndroidEntryPoint
import eramo.tahoon.R
import eramo.tahoon.data.local.entity.MyFavouriteEntity
import eramo.tahoon.databinding.FragmentSearchBinding
import eramo.tahoon.domain.model.products.ShopProductModel
import eramo.tahoon.presentation.adapters.recycleview.vertical.ProductShopPagingAdapter
import eramo.tahoon.presentation.ui.dialog.LoadingDialog
import eramo.tahoon.presentation.ui.dialog.SortSearchResultDialogFragmentArgs
import eramo.tahoon.presentation.viewmodel.navbottom.extension.SearchViewModel
import eramo.tahoon.util.LocalHelperUtil
import eramo.tahoon.util.MySingleton
import eramo.tahoon.util.UserUtil
import eramo.tahoon.util.hideSoftKeyboard
import eramo.tahoon.util.navOptionsAnimation
import eramo.tahoon.util.showToast
import eramo.tahoon.util.state.UiState
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search),
    ProductShopPagingAdapter.OnItemClickListener {

    @Inject
    lateinit var productShopAdapter: ProductShopPagingAdapter

    //    lateinit var productShopAdapter: ProductShopAdapter
    private val viewModel by viewModels<SearchViewModel>()
    private lateinit var binding: FragmentSearchBinding
    private val args by navArgs<SearchFragmentArgs>()
    private val searchModel get() = args.searchModel
    private val searchTitle get() = args.searchTitle
    private val sortObject get() = args.sortObject

    //    private var categoryId get() = args.categoryId
    private var categoryId: String? = null

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
        binding = FragmentSearchBinding.bind(view)

        if (!UserUtil.isUserLogin()) {
            viewModel.getFavouriteListDB()
        }

        setupToolbar()
        productShopAdapter.setListener(this)
        binding.FSearchRvProducts.adapter = productShopAdapter

        requireActivity().hideSoftKeyboard()

        categoryId =args.categoryId

        if (sortObject != null) {
            viewModel.sortSearchResult(sortObject!!, categoryId)
//            categoryId = null
        } else if (searchTitle.isNotEmpty()) {
            viewModel.productSearch(searchTitle)
        } else {
            viewModel.productFilter(searchModel!!)
        }

        binding.ivSort.setOnClickListener {

            findNavController().navigate(
                R.id.sortSearchResultDialogFragment, SortSearchResultDialogFragmentArgs(searchTitle, categoryId).toBundle(),
                navOptionsAnimation()
            )
        }

        fetchFilterState()
        fetchSearchState()
        fetchSortSearchResultState()
        fetchRequestAllProductsState()
        fetchAllProductsState()
        fetchAddRemoveItemWishlistState()
        fetchAddFavouriteState()
        fetchRemoveFavouriteState()

        fetchAdRemoveItemWishlistDBState()
        fetchGetFavouriteListDBState()
        handleLoadingCancellation()
    }

    override fun onStart() {
        super.onStart()
//        requireActivity().hideSoftKeyboard()
//        if (searchTitle.isNotEmpty()) viewModel.productSearch(searchTitle)
//        else viewModel.productFilter(searchModel!!)
    }

    override fun onPause() {
        super.onPause()
        categoryId = null
    }

    private fun setupToolbar() {
        binding.apply {
            FSearchToolbar.setNavigationIcon(R.drawable.ic_arrow_left_yellow)
            FSearchToolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun fetchFilterState() {
//        lifecycleScope.launchWhenStarted {
//            viewModel.filterState.collect { state ->
//                when (state) {
//                    is UiState.Success -> {
//                        LoadingDialog.dismissDialog()
////                        productShopAdapter.submitList(state.data)
//                    }
//                    is UiState.Error -> {
//                        LoadingDialog.dismissDialog()
//                        showToast(state.message!!.asString(requireContext()))
//                    }
//                    is UiState.Loading -> {
//                        LoadingDialog.showDialog()
//                    }
//                    else -> Unit
//                }
//            }
//        }

        lifecycleScope.launchWhenStarted {
            viewModel.filterState.collect { data ->
                data?.let { it ->
//                    productShopAdapter.submitData( it)
                    productShopAdapter.submitData(viewLifecycleOwner.lifecycle, it)
//                    binding.FSearchRvProducts.scrollToPosition(0)

                    productShopAdapter.loadStateFlow.collect {
                        if (it.append is LoadState.NotLoading && it.append.endOfPaginationReached) {
                            if (productShopAdapter.itemCount < 1) {
                                viewModel.requestAllProducts.value = true
//                                binding.ivSort.visibility = View.INVISIBLE
                            } else {
//                                binding.ivSort.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            }
        }
    }

    private fun fetchSearchState() {
//        lifecycleScope.launchWhenStarted {
//            viewModel.searchState.collect { state ->
//                when (state) {
//                    is UiState.Success -> {
//                        LoadingDialog.dismissDialog()
//                        state.data?.let {
////                            productShopAdapter.submitList(state.data)
//                        }?: showToast(getString(R.string.no_results_found))
//                    }
//                    is UiState.Error -> {
//                        LoadingDialog.dismissDialog()
//                        showToast(state.message!!.asString(requireContext()))
//                    }
//                    is UiState.Loading -> {
//                        LoadingDialog.showDialog()
//                    }
//                    else -> Unit
//                }
//            }
//        }

        lifecycleScope.launchWhenStarted {
            viewModel.searchState.collect { data ->
                data?.let { it ->
                    productShopAdapter.submitData(viewLifecycleOwner.lifecycle, it)
//                    productShopAdapter.loadStateFlow.map { it.refresh }
//                        .distinctUntilChanged()
//                        .collect {
////                            if (it is LoadState.NotLoading) {
//                            if (it is LoadState.NotLoading) {
//                                viewModel.requestAllProducts.value = true
//                            }
//                        }

                    // works one
//                    productShopAdapter.loadStateFlow.collect {
//                        if (it.append is LoadState.NotLoading && it.append.endOfPaginationReached) {
//                            if (productShopAdapter.itemCount < 1) {
//                                viewModel.requestAllProducts.value = true
//                                binding.ivSort.visibility = View.INVISIBLE
//                            }
//                            else {
//                                binding.ivSort.visibility = View.VISIBLE
//                            }
//                        }
//                    }

                }
            }
        }
    }

    private fun fetchSortSearchResultState() {
        lifecycleScope.launchWhenStarted {
            viewModel.sortSearchState.collect { data ->
                data?.let { it ->
                    productShopAdapter.submitData(viewLifecycleOwner.lifecycle, it)


                    productShopAdapter.loadStateFlow.collect {
                        if (it.append is LoadState.NotLoading && it.append.endOfPaginationReached) {
                            if (productShopAdapter.itemCount < 1) {
//                                viewModel.requestAllProducts.value = true
                            }
                        }
                    }

                }
            }
        }
    }

    private fun fetchAllProductsState() {
        lifecycleScope.launchWhenStarted {
            viewModel.allProductsState.collect { data ->

                data?.let {
                    productShopAdapter.submitData(viewLifecycleOwner.lifecycle, it)
//                    binding.FShopRvProducts.scrollToPosition(0)
                }
            }
        }
    }

    private fun fetchRequestAllProductsState() {
        lifecycleScope.launchWhenStarted {
            viewModel.requestAllProducts.collect { data ->

                if (data){
                    viewModel.allProducts()
                    viewModel.requestAllProducts.value = false
                    categoryId = null
                }
            }
        }
    }

    private fun fetchRemoveFavouriteState() {
        lifecycleScope.launchWhenStarted {
            viewModel.removeFavouriteState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()
                        showToast(state.data?.message ?: getString(R.string.success))
                        viewModel.productFilter(searchModel!!)
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
                        viewModel.productFilter(searchModel!!)
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

    private fun fetchAddRemoveItemWishlistState() {
        lifecycleScope.launchWhenStarted {
            viewModel.addRemoveItemWishlistState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()

                        if (searchTitle.isNotEmpty()) viewModel.productSearch(searchTitle)
                        else viewModel.productFilter(searchModel!!)

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

//    override fun onProductShopClick(model: MyProductModel) {
//        findNavController().navigate(
//            R.id.productDetailsFragment,
//            ProductDetailsFragmentArgs(model.id.toString()).toBundle(),
//            navOptionsAnimation()
//        )
//    }

//    override fun onFavouriteClick(position: Int, model: MyProductModel, isFav: Boolean) {
//        super.onFavouriteClick(position, model, isFav)
//        if (!UserUtil.isUserLogin()) findNavController().navigate(R.id.loginDialog)
//        else {
//            if (isFav) viewModel.removeFavourite(model.id.toString())
//            else viewModel.addFavourite(model.id.toString())
//        }
//    }

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

                        productShopAdapter.notifyDataSetChanged()
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

    private fun handleLoadingCancellation() {
        LoadingDialog.cancelCurrentRequest.observe(viewLifecycleOwner) { isCancel ->
            if (isCancel) {
                viewModel.cancelRequest()
                LoadingDialog.dismissDialog()
                LoadingDialog.cancelCurrentRequest.value = false
            }
        }
    }

    override fun onProductShopClick(model: ShopProductModel) {
        findNavController().navigate(
            R.id.productDetailsFragment,
            ProductDetailsFragmentArgs(model.id.toString()).toBundle(),
            navOptionsAnimation()
        )
    }

    override fun onFavouriteClick(position: Int, model: ShopProductModel, isFav: Boolean) {
        super.onFavouriteClick(position, model, isFav)
        if (!UserUtil.isUserLogin()) {
            viewModel.addRemoveItemWishlistDB(
                MyFavouriteEntity(
                    productId = model.id, productName = model.title, categoryName = model.category?.title, imageUrl = model.primaryImageUrl,
                    modelNumber = "", price = model.realPrice!!.toFloat(), fakePrice = model.fakePrice!!.toFloat(),
                    isNew = model.new, profitPercent = model.profitPercent
                )
            )
        }
        //findNavController().navigate(R.id.loginDialog)
        else {
            viewModel.addRemoveItemWishlist(model.id.toString())
        }
    }
}