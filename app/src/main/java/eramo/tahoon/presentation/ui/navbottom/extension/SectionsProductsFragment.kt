package eramo.tahoon.presentation.ui.navbottom.extension

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import eramo.tahoon.R
import eramo.tahoon.data.local.entity.MyFavouriteEntity
import eramo.tahoon.databinding.FragmentSectionsProductsBinding
import eramo.tahoon.domain.model.products.MyProductModel
import eramo.tahoon.presentation.adapters.recycleview.horizontal.ProductAdapter
import eramo.tahoon.presentation.ui.dialog.LoadingDialog
import eramo.tahoon.presentation.viewmodel.navbottom.HomeViewModel
import eramo.tahoon.util.MySingleton
import eramo.tahoon.util.UserUtil
import eramo.tahoon.util.navOptionsAnimation
import eramo.tahoon.util.onBackPressed
import eramo.tahoon.util.parseErrorResponse
import eramo.tahoon.util.showToast
import eramo.tahoon.util.state.UiState
import javax.inject.Inject

@AndroidEntryPoint
class SectionsProductsFragment : Fragment(R.layout.fragment_sections_products), ProductAdapter.OnItemClickListener {
    private lateinit var binding: FragmentSectionsProductsBinding
    private val viewModel by viewModels<HomeViewModel>()
    private val args by navArgs<SectionsProductsFragmentArgs>()
    private val sectionTitle get() = args.sectionTitle

    @Inject
    lateinit var productAdapter: ProductAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSectionsProductsBinding.bind(view)
        setupToolbar()

        productAdapter.setListener(this)
        binding.rvProducts.adapter = productAdapter

        callApi()
        fetchApi()

        this@SectionsProductsFragment.onBackPressed { findNavController().popBackStack() }
    }

    private fun callApi() {
        when (sectionTitle) {
            getString(R.string.latest_deals) -> {
                binding.tvTitle.text = getString(R.string.latest_deals)
                viewModel.latestDeals()
            }

            getString(R.string.latest_products) -> {
                binding.tvTitle.text = getString(R.string.latest_products)
                viewModel.getLatestProducts()
            }

            getString(R.string.most_viewed_products) -> {
                binding.tvTitle.text = getString(R.string.most_viewed_products)
                viewModel.mostViewed()
            }

            "first section" -> {
                fetchFirstBottomSectionState()
                viewModel.bottomSections()
            }

            "second section" -> {
                fetchSecondBottomSectionState()
                viewModel.bottomSections()
            }

            "third section" -> {
                fetchThirdBottomSectionState()
                viewModel.bottomSections()
            }

            "fourth section" -> {
                fetchFourthBottomSectionState()
                viewModel.bottomSections()
            }

            getString(R.string.most_sale_products) -> {
                binding.tvTitle.text = getString(R.string.most_sale_products)
                viewModel.mostSale()
            }
        }

    }

    private fun fetchApi() {
        fetchLatestDealsState()
        fetchLatestProductsState()
        fetchMostViewedState()
        fetchMostSaleState()

        fetchAdRemoveItemWishlistDBState()
        fetchGetFavouriteListDBState()
    }

    private fun fetchLatestDealsState() {
        lifecycleScope.launchWhenStarted {
            viewModel.latestDealsState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()
                        productAdapter.submitList(state.data)
                    }

                    is UiState.Error -> {
                        LoadingDialog.dismissDialog()
                        try {
                            showToast(parseErrorResponse(state.message!!.asString(requireContext())))
                        } catch (e: Exception) {
                            showToast(state.message!!.asString(requireContext()))
                        }
                    }

                    is UiState.Loading -> {
                        LoadingDialog.showDialog()
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun fetchLatestProductsState() {
        lifecycleScope.launchWhenStarted {
            viewModel.latestProductsState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()
                        productAdapter.submitList(state.data)
                    }

                    is UiState.Error -> {
                        LoadingDialog.dismissDialog()
                        try {
                            showToast(parseErrorResponse(state.message!!.asString(requireContext())))
                        } catch (e: Exception) {
                            showToast(state.message!!.asString(requireContext()))
                        }
                    }

                    is UiState.Loading -> {
                        LoadingDialog.showDialog()
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun fetchMostViewedState() {
        lifecycleScope.launchWhenStarted {
            viewModel.mostViewedState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()
                        productAdapter.submitList(state.data)
                    }

                    is UiState.Error -> {
                        LoadingDialog.dismissDialog()
                        try {
                            showToast(parseErrorResponse(state.message!!.asString(requireContext())))
                        } catch (e: Exception) {
                            showToast(state.message!!.asString(requireContext()))
                        }
                    }

                    is UiState.Loading -> {
                        LoadingDialog.showDialog()
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun fetchFirstBottomSectionState() {
        lifecycleScope.launchWhenStarted {
            viewModel.bottomSectionsState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()

                        binding.apply {
                            tvTitle.text = state.data?.data?.get(0)?.title
                            productAdapter.submitList(state.data?.data?.get(0)?.products?.map { it?.toMyProductModel() })
                        }

                    }

                    is UiState.Error -> {
                        LoadingDialog.dismissDialog()
                        try {
                            showToast(parseErrorResponse(state.message!!.asString(requireContext())))
                        } catch (e: Exception) {
                            showToast(state.message!!.asString(requireContext()))
                        }
                    }

                    is UiState.Loading -> {
                        LoadingDialog.showDialog()
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun fetchSecondBottomSectionState() {
        lifecycleScope.launchWhenStarted {
            viewModel.bottomSectionsState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()

                        binding.apply {
                            tvTitle.text = state.data?.data?.get(1)?.title
                            productAdapter.submitList(state.data?.data?.get(1)?.products?.map { it?.toMyProductModel() })
                        }

                    }

                    is UiState.Error -> {
                        LoadingDialog.dismissDialog()
                        try {
                            showToast(parseErrorResponse(state.message!!.asString(requireContext())))
                        } catch (e: Exception) {
                            showToast(state.message!!.asString(requireContext()))
                        }
                    }

                    is UiState.Loading -> {
                        LoadingDialog.showDialog()
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun fetchThirdBottomSectionState() {
        lifecycleScope.launchWhenStarted {
            viewModel.bottomSectionsState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()

                        binding.apply {
                            tvTitle.text = state.data?.data?.get(2)?.title
                            productAdapter.submitList(state.data?.data?.get(2)?.products?.map { it?.toMyProductModel() })
                        }

                    }

                    is UiState.Error -> {
                        LoadingDialog.dismissDialog()
                        try {
                            showToast(parseErrorResponse(state.message!!.asString(requireContext())))
                        } catch (e: Exception) {
                            showToast(state.message!!.asString(requireContext()))
                        }
                    }

                    is UiState.Loading -> {
                        LoadingDialog.showDialog()
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun fetchFourthBottomSectionState() {
        lifecycleScope.launchWhenStarted {
            viewModel.bottomSectionsState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()

                        binding.apply {
                            tvTitle.text = state.data?.data?.get(3)?.title
                            productAdapter.submitList(state.data?.data?.get(3)?.products?.map { it?.toMyProductModel() })
                        }

                    }

                    is UiState.Error -> {
                        LoadingDialog.dismissDialog()
                        try {
                            showToast(parseErrorResponse(state.message!!.asString(requireContext())))
                        } catch (e: Exception) {
                            showToast(state.message!!.asString(requireContext()))
                        }
                    }

                    is UiState.Loading -> {
                        LoadingDialog.showDialog()
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun fetchMostSaleState() {
        lifecycleScope.launchWhenStarted {
            viewModel.mostSaleState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()
                        productAdapter.submitList(state.data)
                    }

                    is UiState.Error -> {
                        LoadingDialog.dismissDialog()
                        try {
                            showToast(parseErrorResponse(state.message!!.asString(requireContext())))
                        } catch (e: Exception) {
                            showToast(state.message!!.asString(requireContext()))
                        }
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

                        productAdapter.notifyDataSetChanged()

                        if (UserUtil.isUserLogin() && list.isNotEmpty()) {
                            viewModel.clearFavouriteListDB()
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

    private fun setupToolbar() {
        binding.apply {
            FSearchToolbar.setNavigationIcon(R.drawable.ic_arrow_left_yellow)
            FSearchToolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    override fun onProductClick(model: MyProductModel) {
        findNavController().navigate(
            R.id.productDetailsFragment,
            ProductDetailsFragmentArgs(model.id.toString()).toBundle(),
            navOptionsAnimation()
        )
    }

    override fun onProductFavouriteClick(model: MyProductModel, isFav: Boolean) {
        if (!UserUtil.isUserLogin()) {
            viewModel.addRemoveItemWishlistDB(
                MyFavouriteEntity(
                    productId = model.id, productName = model.title, categoryName = model.category?.title, imageUrl = model.primaryImageUrl,
                    modelNumber = "", price = model.realPrice!!.toFloat(), fakePrice = model.fakePrice!!.toFloat(),
                    isNew = model.new, profitPercent = model.profitPercent
                )
            )
        } else {

            if (isFav) {
                viewModel.addRemoveItemWishlist(model.id.toString(), "latest products")
            } else {
                viewModel.addRemoveItemWishlist(model.id.toString(), "latest products")
            }
        }
    }

}