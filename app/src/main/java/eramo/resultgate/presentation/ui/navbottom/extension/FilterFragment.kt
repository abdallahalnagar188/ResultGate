package eramo.resultgate.presentation.ui.navbottom.extension

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.slider.LabelFormatter
import dagger.hilt.android.AndroidEntryPoint
import eramo.resultgate.R
import eramo.resultgate.databinding.FragmentFilterBinding
import eramo.resultgate.domain.model.request.SearchRequest
import eramo.resultgate.presentation.adapters.recycleview.vertical.FilterCategoryProductsAdapter
import eramo.resultgate.presentation.ui.dialog.LoadingDialog
import eramo.resultgate.presentation.viewmodel.navbottom.extension.FilterViewModel
import eramo.resultgate.util.LocalHelperUtil
import eramo.resultgate.util.navOptionsAnimation
import eramo.resultgate.util.showToast
import eramo.resultgate.util.state.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FilterFragment : Fragment(R.layout.fragment_filter) {

    @Inject
    lateinit var filterCategoryProductsAdapter: FilterCategoryProductsAdapter
    private val viewModel by viewModels<FilterViewModel>()
    private lateinit var binding: FragmentFilterBinding
    private var maxPrice = ""
    private var minPrice = ""

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
        binding = FragmentFilterBinding.bind(view)
        setupToolbar()

        binding.apply {
            FFilterRvManufacturers.adapter = filterCategoryProductsAdapter
            FFilterBtnFilter.setOnClickListener {

                val searchRequest = SearchRequest(
                    filterCategoryProductsAdapter.getSelectedManufacturers(),
                    binding.slider.values[0].toInt().toString(),
                    binding.slider.values[1].toInt().toString()
                )
                findNavController().navigate(
                    R.id.searchFragment,
                    SearchFragmentArgs(
                        searchModel = searchRequest,
                        searchTitle = ""
                        ,categoryId = if (filterCategoryProductsAdapter.getSelectedManufacturers().size < 1) null else filterCategoryProductsAdapter.getSelectedManufacturers().toString()
                    ).toBundle(),
                    navOptionsAnimation()
                )

                lifecycleScope.launch(Dispatchers.IO) {
                    delay(1000)
                    filterCategoryProductsAdapter.clearSelectedManufacturers()
                }
            }
        }
        fetchHomeCategoriesState()

//        fetchManufacturerState()
//        fetchMaxPriceState()
//        fetchMinPriceState()
        handleLoadingCancellation()
    }

    private fun setupToolbar() {
        binding.apply {
            FFilterToolbar.setNavigationIcon(R.drawable.ic_arrow_left_yellow)
            FFilterToolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.getHomeCategories()
//        viewModel.filterCategories()
//        viewModel.maxProductPrice()
//        viewModel.minProductPrice()
//        filterCategoryProductsAdapter.clearSelectedManufacturers()
    }

    private fun fetchHomeCategoriesState() {
        lifecycleScope.launchWhenCreated {
            viewModel.categoriesState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()

                        filterCategoryProductsAdapter.submitList(state.data?.data)

//                        setupSliderRange(state.data?.max.toString(),"0")
                        setupSliderRange("1000000", "0")
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

    private fun fetchManufacturerState() {
        lifecycleScope.launchWhenCreated {
            viewModel.filterCategoriesState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()
//                        filterCategoryProductsAdapter.submitList(state.data)
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

    private fun fetchMaxPriceState() {
        lifecycleScope.launchWhenCreated {
            viewModel.maxPriceState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()
                        maxPrice = state.data?.MaxPrice!!
                        setupSliderRange(maxPrice, minPrice)
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

    private fun fetchMinPriceState() {
        lifecycleScope.launchWhenCreated {
            viewModel.minPriceState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()
                        minPrice = state.data?.MinPrice!!
                        setupSliderRange(maxPrice, minPrice)
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

    private fun setupSliderRange(maxPrice: String, minPrice: String) {
        if (maxPrice.isNotEmpty() && minPrice.isNotEmpty()) {
            binding.slider.apply {
                valueFrom = if (minPrice.toFloat() >= maxPrice.toFloat()) 0.0f
                else minPrice.toFloat()
                valueTo = maxPrice.toFloat()
                setValues(valueFrom, valueTo)
                labelBehavior = LabelFormatter.LABEL_VISIBLE
            }

//            val list = mutableListOf<Float>()
//            list.add(0f)
//            list.add(2000f)
//            binding.slider.values = list
        }
    }
}