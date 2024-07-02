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
import dagger.hilt.android.AndroidEntryPoint
import eramo.resultgate.R
import eramo.resultgate.data.remote.dto.home.HomeCategoriesResponse
import eramo.resultgate.databinding.FragmentAllCategoryBinding
import eramo.resultgate.presentation.adapters.recycleview.vertical.AllCategoryAdapter
import eramo.resultgate.presentation.ui.dialog.LoadingDialog
import eramo.resultgate.presentation.viewmodel.SharedViewModel
import eramo.resultgate.presentation.viewmodel.navbottom.HomeViewModel
import eramo.resultgate.util.LocalHelperUtil
import eramo.resultgate.util.UserUtil
import eramo.resultgate.util.navOptionsAnimation
import eramo.resultgate.util.onBackPressed
import eramo.resultgate.util.parseErrorResponse
import eramo.resultgate.util.showToast
import eramo.resultgate.util.state.UiState
import javax.inject.Inject

@AndroidEntryPoint
class AllCategoryFragment : Fragment(R.layout.fragment_all_category),
    AllCategoryAdapter.OnItemClickListener {

    private lateinit var binding: FragmentAllCategoryBinding

    private val viewModel by viewModels<HomeViewModel>()
    private val viewModelShared: SharedViewModel by activityViewModels()

    private val args by navArgs<AllCategoryFragmentArgs>()
    private val brandId get() = args.brandId

    private lateinit var currentList: List<HomeCategoriesResponse.Data?>

    @Inject
    lateinit var allCategoryAdapter: AllCategoryAdapter

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
        binding = FragmentAllCategoryBinding.bind(view)
        setupToolbar()

        viewModel.getHomeCategories(brandId)

        allCategoryAdapter.setListener(this)
        binding.apply {
            FAllCategoryRvManufacturer.adapter = allCategoryAdapter

            FAllCategoryEtSearch.addTextChangedListener { text ->
                if (text.toString().isEmpty()) {
                    allCategoryAdapter.submitList(null)
                    allCategoryAdapter.submitList(currentList)
                } else {
                    val list = currentList.filter {
                        it?.title?.lowercase()?.contains(text.toString().lowercase()) == true
                    }
                    allCategoryAdapter.submitList(null)
                    allCategoryAdapter.submitList(list)
                }
            }
        }

        fetchCategoriesState()
        handleLoadingCancellation()

        this@AllCategoryFragment.onBackPressed { findNavController().popBackStack() }
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
        currentList = categoriesObject.data!!
        allCategoryAdapter.submitList(currentList.reversed())
    }

    private fun setupNoDataAnimation(categoriesObject: HomeCategoriesResponse){
        val list = categoriesObject.data!!

        if (list.isNullOrEmpty()){
            binding.FAllCategoryRvManufacturer.visibility = View.INVISIBLE
            binding.lottieNoData.visibility = View.VISIBLE
        }else{
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

    override fun onCategoryClick(model: HomeCategoriesResponse.Data) {
//        findNavController().navigate(
//            R.id.shopFragment, ShopFragmentArgs(model.id.toString()).toBundle(),
//            navOptionsAnimation()
//        )
        val subCategoriesList = model.subCatagories!!.map { it!!.toSubCategoryModel() }.toTypedArray()
        findNavController().navigate(
            R.id.subCategoriesFragment, SubCategoriesFragmentArgs(subCategoriesList).toBundle(),
            navOptionsAnimation()
        )
    }
}