package eramo.resultgate.presentation.ui.drawer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import eramo.resultgate.R
import eramo.resultgate.data.local.entity.MyFavouriteEntity
import eramo.resultgate.databinding.FragmentFavouriteBinding
import eramo.resultgate.domain.model.products.MyProductModel
import eramo.resultgate.presentation.adapters.recycleview.vertical.ProductShopAdapter
import eramo.resultgate.presentation.ui.dialog.LoadingDialog
import eramo.resultgate.presentation.ui.navbottom.extension.ProductDetailsFragmentArgs
import eramo.resultgate.presentation.viewmodel.navbottom.extension.FavouriteViewModel
import eramo.resultgate.util.LocalHelperUtil
import eramo.resultgate.util.UserUtil
import eramo.resultgate.util.navOptionsAnimation
import eramo.resultgate.util.onBackPressed
import eramo.resultgate.util.showToast
import eramo.resultgate.util.state.UiState
import javax.inject.Inject

@AndroidEntryPoint
class FavouriteFragment : Fragment(R.layout.fragment_favourite),
    ProductShopAdapter.OnItemClickListener {

    @Inject
    lateinit var productShopAdapter: ProductShopAdapter
    private val viewModel by viewModels<FavouriteViewModel>()
    private lateinit var binding: FragmentFavouriteBinding

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
        binding = FragmentFavouriteBinding.bind(view)

        if (UserUtil.isUserLogin()){
            viewModel.userFav()
        }else{
            viewModel.getFavouriteListDB()
        }

        setupToolbar()
        productShopAdapter.setListener(this)
        binding.FFavouriteRvProducts.adapter = productShopAdapter

        fetchUserFavState()
        fetchGetFavouriteListDBState()
        fetchAdRemoveItemWishlistDBState()

        fetchRemoveFavouriteState()
        handleLoadingCancellation()

        this@FavouriteFragment.onBackPressed { findNavController().popBackStack() }

    }

    private fun setupToolbar() {
        binding.apply {
            FFavouriteToolbar.setNavigationIcon(R.drawable.ic_arrow_left_yellow)
            FFavouriteToolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun fetchUserFavState() {
        lifecycleScope.launchWhenStarted {
            viewModel.userFavState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()
                        productShopAdapter.submitList(state.data)
                        if (state.data!!.isEmpty()){
                            binding.lottieNoData.visibility = View.VISIBLE
                        } else{
                            binding.lottieNoData.visibility = View.GONE
                        }
                    //showToast(getString(R.string.no_favourite))
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

                        if (state.data.isNullOrEmpty()){
                            binding.lottieNoData.visibility = View.VISIBLE
                        } else{
                            binding.lottieNoData.visibility = View.GONE
                        }

                        val list = mutableListOf<MyProductModel>()

                        for (i in state.data!!){
                            list.add(
                                MyProductModel(
                                    i.productId,i.productName,i.fakePrice!!.toDouble(),i.price!!.toDouble(),-1,
                                    i.imageUrl,"",-1,"",1,i.isNew,i.imageUrl,i.profitPercent,i.price.toString(),
                                    -1,MyProductModel.Category(-1,i.categoryName,"","","")
                                )
                            )
                        }

                        productShopAdapter.submitList(list)
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


    private fun fetchRemoveFavouriteState() {
        lifecycleScope.launchWhenStarted {
            viewModel.removeFavouriteState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()
                        showToast(state.data?.message ?: getString(R.string.success))
                        viewModel.userFav()
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

    override fun onProductShopClick(model: MyProductModel) {
        findNavController().navigate(
            R.id.productDetailsFragment,
            ProductDetailsFragmentArgs(model.id.toString()).toBundle(),
            navOptionsAnimation()
        )
    }

    override fun onFavouriteClick(position: Int, model: MyProductModel, isFav: Boolean){
        super.onFavouriteClick(position, model, isFav)
        if (!UserUtil.isUserLogin()){
//            findNavController().navigate(R.id.loginDialog)
            viewModel.addRemoveItemWishlistDB(MyFavouriteEntity(
                productId = model.id, productName = model.title, categoryName = model.category?.title, imageUrl = model.primaryImageUrl,
                modelNumber = "", price = model.realPrice!!.toFloat(), fakePrice = model.fakePrice!!.toFloat(),
                isNew = model.new, profitPercent = model.profitPercent
            ))
        }else{
            viewModel.addRemoveItemWishlist(model.id.toString())
        }
//        viewModel.removeFavourite(model.id.toString())
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