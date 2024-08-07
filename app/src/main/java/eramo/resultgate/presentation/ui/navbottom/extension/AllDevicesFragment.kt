package eramo.resultgate.presentation.ui.navbottom.extension

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.animation.Positioning
import dagger.hilt.android.AndroidEntryPoint
import eramo.resultgate.R
import eramo.resultgate.data.local.entity.MyFavouriteEntity
import eramo.resultgate.data.remote.dto.alldevices.AllDevicesResponse
import eramo.resultgate.data.remote.dto.alldevices.Data
import eramo.resultgate.data.remote.dto.alldevices.DataX
import eramo.resultgate.data.remote.dto.home.HomeCategoriesResponse
import eramo.resultgate.databinding.FragmentAllCategoryBinding
import eramo.resultgate.databinding.FragmentAllDevicesBinding
import eramo.resultgate.presentation.adapters.recycleview.vertical.AllCategoryAdapter
import eramo.resultgate.presentation.adapters.recycleview.vertical.AllDevicesAdapter
import eramo.resultgate.presentation.ui.dialog.LoadingDialog
import eramo.resultgate.presentation.viewmodel.SharedViewModel
import eramo.resultgate.presentation.viewmodel.navbottom.HomeViewModel
import eramo.resultgate.util.LocalHelperUtil
import eramo.resultgate.util.MySingleton
import eramo.resultgate.util.UserUtil
import eramo.resultgate.util.navOptionsAnimation
import eramo.resultgate.util.onBackPressed
import eramo.resultgate.util.parseErrorResponse
import eramo.resultgate.util.showToast
import eramo.resultgate.util.state.UiState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AllDevicesFragment : Fragment(R.layout.fragment_all_devices),
    AllDevicesAdapter.OnItemClickListener {

    private lateinit var binding: FragmentAllDevicesBinding

    private val viewModel by viewModels<HomeViewModel>()
    private val viewModelShared: SharedViewModel by activityViewModels()

    private val args by navArgs<AllDevicesFragmentArgs>()
    private val brandId get() = args.brandId

    private var isFavPressedProduct = false

    //private lateinit var currentList: List<AllDevicesResponse?>

    @Inject
    lateinit var allDevicesAdapter: AllDevicesAdapter

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
        binding = FragmentAllDevicesBinding.bind(view)
        setupToolbar()

        viewModel.getFavouriteListDB()
        viewModel.getAllDevices()
        lifecycleScope.launch {
            viewModel.allDevices.collect() {
                allDevicesAdapter.submitList(it?.data?.data)
            }
        }


        allDevicesAdapter.setListener(this)
        binding.apply {
            FAllCategoryRvManufacturer.adapter = allDevicesAdapter

//            FAllCategoryEtSearch.addTextChangedListener { text ->
//                if (text.toString().isEmpty()) {
//                    allCategoryAdapter.submitList(null)
//                    allCategoryAdapter.submitList(currentList)
//                } else {
//                    val list = currentList.filter {
//                        it?.title?.lowercase()?.contains(text.toString().lowercase()) == true
//                    }
//                    allCategoryAdapter.submitList(null)
//                    allCategoryAdapter.submitList(list)
//                }
//            }
        }

        fetchCategoriesState()
        fetchAddFavouriteState()
        fetchRemoveFavouriteState()
        fetchAdRemoveItemWishlistDBState()
        fetchGetFavouriteListDBState()
        fetchAddRemoveItemWishlistAndRefreshState()
        handleLoadingCancellation()

        this@AllDevicesFragment.onBackPressed { findNavController().popBackStack() }
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
    private fun fetchAddRemoveItemWishlistAndRefreshState() {
        lifecycleScope.launchWhenStarted {
            viewModel.addRemoveItemWishlistState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()
                    }

                    is UiState.Error -> {
                        LoadingDialog.dismissDialog()
                        showToast(state.message!!.asString(requireContext()))
                    }

                    is UiState.Loading -> {
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

                        allDevicesAdapter.notifyDataSetChanged()
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


    private fun fetchCategoriesState() {
        lifecycleScope.launchWhenStarted {
            viewModel.categoriesState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()
                        setupRvCategories(state.data!!)
                        setupNoDataAnimation(state.data)
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

    private fun setupRvCategories(categoriesObject: HomeCategoriesResponse) {
//        currentList = categoriesObject.data!!
//        allCategoryAdapter.submitList(currentList.reversed())
    }

    private fun setupNoDataAnimation(categoriesObject: HomeCategoriesResponse) {
        val list = categoriesObject.data!!

        if (list.isNullOrEmpty()) {
            binding.FAllCategoryRvManufacturer.visibility = View.INVISIBLE
            binding.lottieNoData.visibility = View.VISIBLE
        } else {
            binding.FAllCategoryRvManufacturer.visibility = View.VISIBLE
            binding.lottieNoData.visibility = View.INVISIBLE
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

    override fun onCategoryClick(model: DataX) {
        findNavController().navigate(
            R.id.productDetailsFragment,
            ProductDetailsFragmentArgs(model.id.toString()).toBundle(),
            navOptionsAnimation()
        )
//        findNavController().navigate(
//            R.id.shopFragment, ShopFragmentArgs(model.id.toString()).toBundle(),
//            navOptionsAnimation()
//        )
//        val subCategoriesList = model.subCatagories!!.map { it!!.toSubCategoryModel() }.toTypedArray()
//        findNavController().navigate(
//            R.id.subCategoriesFragment, SubCategoriesFragmentArgs(subCategoriesList).toBundle(),
//            navOptionsAnimation()
//        )
    }

    override fun onDeviceFavouriteClick(position: Int, model: DataX, isFav: Boolean) {
        if (UserUtil.isUserLogin()) {
            if (isFav) {
                viewModel.addRemoveItemWishlist(model.id.toString(), "latest products")
            } else {
                viewModel.addRemoveItemWishlist(model.id.toString(), "latest products")
            }
            viewModel.addRemoveItemWishlistDB(
                MyFavouriteEntity(
                    productId = model.id,
                    productName = model.title,
                    categoryName = model.category.toString(),
                    imageUrl = model.primaryImageUrl,
                    modelNumber = "",
                    price = model.realPrice!!.toFloat(),
                    fakePrice = model.fakePrice!!.toFloat(),
                    profitPercent = model.profitPercent
                )
            )
            isFavPressedProduct = true
            viewModel.addRemoveItemWishlist(model.id.toString(), "most sale products")
        } else {
            findNavController().navigate(R.id.loginDialog)
        }
    }

}