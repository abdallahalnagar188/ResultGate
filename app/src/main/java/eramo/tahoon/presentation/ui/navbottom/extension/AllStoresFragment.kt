package eramo.tahoon.presentation.ui.navbottom.extension

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import eramo.tahoon.R
import eramo.tahoon.databinding.FragmentAllStoresBinding
import eramo.tahoon.domain.model.home.HomeBrandsModel
import eramo.tahoon.presentation.adapters.recycleview.vertical.AllStoresRvAdapter
import eramo.tahoon.presentation.ui.dialog.LoadingDialog
import eramo.tahoon.presentation.viewmodel.navbottom.extension.AllStoresViewModel
import eramo.tahoon.util.LocalHelperUtil
import eramo.tahoon.util.parseErrorResponse
import eramo.tahoon.util.showToast
import eramo.tahoon.util.state.UiState
import javax.inject.Inject

@AndroidEntryPoint
class AllStoresFragment : Fragment(R.layout.fragment_all_stores), AllStoresRvAdapter.OnItemClickListener {
    private lateinit var binding: FragmentAllStoresBinding

    private val viewModel by viewModels<AllStoresViewModel>()

    @Inject
    lateinit var allStoresRvAdapter: AllStoresRvAdapter

    var currentList: List<HomeBrandsModel>? = emptyList()

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
        binding = FragmentAllStoresBinding.bind(view)

        binding.ivBack.setOnClickListener { findNavController().popBackStack() }

        viewModel.getBrands()
        fetchBrandsState()
    }

    private fun fetchBrandsState() {
        lifecycleScope.launchWhenStarted {
            viewModel.brandsState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()

                        // setup Rv
                        allStoresRvAdapter.setListener(this@AllStoresFragment)
                        binding.rvStores.adapter = allStoresRvAdapter
                        allStoresRvAdapter.submitList(state.data)

                        currentList = state.data
                        setupSearchFunctionality()
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

    private fun setupSearchFunctionality() {
        setupNoDataAnimation()

        binding.apply {
            FAllCategoryEtSearch.addTextChangedListener { text ->
                if (text.toString().isEmpty()) {
                    allStoresRvAdapter.submitList(null)
                    allStoresRvAdapter.submitList(currentList)
                } else {
                    val list = currentList?.filter {
                        it.name.lowercase()?.contains(text.toString().lowercase()) == true
                    }
                    allStoresRvAdapter.submitList(null)
                    allStoresRvAdapter.submitList(list)

                    setupNoDataAnimation()
                }
            }
        }
    }

    private fun setupNoDataAnimation() {
        if (currentList.isNullOrEmpty()) {
            binding.rvStores.visibility = View.INVISIBLE
            binding.lottieNoData.visibility = View.VISIBLE
        } else {
            binding.rvStores.visibility = View.VISIBLE
            binding.lottieNoData.visibility = View.INVISIBLE
        }
    }

    override fun onBrandClick(model: HomeBrandsModel) {
        findNavController().navigate(R.id.allCategoryFragment, AllCategoryFragmentArgs(model.id.toString()).toBundle())
    }
}