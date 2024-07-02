package eramo.tahoon.presentation.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import eramo.tahoon.R
import eramo.tahoon.databinding.FragmentFilterCitiesNewDialogBinding
import eramo.tahoon.domain.model.auth.CitiesWithRegionsModel
import eramo.tahoon.domain.model.auth.CountriesModel
import eramo.tahoon.domain.model.auth.RegionsInCitiesModel
import eramo.tahoon.presentation.adapters.recycleview.vertical.RvSelectCityAdapter
import eramo.tahoon.presentation.adapters.recycleview.vertical.RvSelectCountryAdapter
import eramo.tahoon.presentation.viewmodel.SharedViewModel
import eramo.tahoon.presentation.viewmodel.dialog.FilterCitiesDialogViewModel
import eramo.tahoon.util.LocalHelperUtil
import eramo.tahoon.util.UserUtil
import eramo.tahoon.util.showToast
import eramo.tahoon.util.state.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class FilterCitiesDialogNewFragment : BottomSheetDialogFragment(),
    RvSelectCountryAdapter.OnItemClickListener,
    RvSelectCityAdapter.OnItemClickListener {

    private lateinit var binding: FragmentFilterCitiesNewDialogBinding
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val viewModel by viewModels<FilterCitiesDialogViewModel>()

    @Inject
    lateinit var rvSelectCountryAdapter: RvSelectCountryAdapter

    @Inject
    lateinit var rvSelectCityAdapter: RvSelectCityAdapter

    private var regionId = -1
    private var regionTitleEn = ""
    private var regionTitleAr = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        LocalHelperUtil.loadLocal(requireActivity())
        return inflater.inflate(R.layout.fragment_filter_cities_new_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dialog = dialog as BottomSheetDialog
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        binding = FragmentFilterCitiesNewDialogBinding.bind(view)

        setupViews()
        listeners()

        viewModel.countries()

        fetchCountriesState()
        fetchCityState()

//        Log.e()
    }

    private fun listeners() {
        binding?.btnContinue?.setOnClickListener {
            if (regionId != -1) {
                UserUtil.saveUserCityFiltrationId(regionId.toString())
                UserUtil.saveUserCityFiltrationTitleEn(regionTitleEn)
                UserUtil.saveUserCityFiltrationTitleAr(regionTitleAr)

                sharedViewModel.triggerFilterByCityEvent()

                dismiss()

                findNavController().navigate(R.id.homeFragment)
            } else {
                showToast(getString(R.string.select_region))
            }
        }
    }

    private fun setupViews() {

    }

    private fun fetchCountriesState() {
        lifecycleScope.launchWhenStarted {
            viewModel.countriesState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()
                        setupCountries(state.data ?: emptyList())
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

    private fun setupCountries(data: List<CountriesModel>) {
        rvSelectCountryAdapter.setListener(this@FilterCitiesDialogNewFragment)
        binding?.rvCountries?.adapter = rvSelectCountryAdapter
        rvSelectCountryAdapter.submitList(data)
        selectDefaultCountry()
    }

    private fun fetchCityState() {
        lifecycleScope.launchWhenStarted {
            viewModel.cityState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()
                        setupCities(state.data ?: emptyList())
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

    private fun setupCities(data: List<CitiesWithRegionsModel>) {
        rvSelectCityAdapter.setListener(this@FilterCitiesDialogNewFragment)
        binding?.rvCities?.adapter = rvSelectCityAdapter
        rvSelectCityAdapter.submitList(data)
//        selectDefaultCity()
    }

    private fun selectDefaultCountry() {
        viewLifecycleOwner.lifecycleScope.launch {
            delay(100)
            binding?.rvCountries?.findViewHolderForAdapterPosition(0)?.itemView?.performClick()
        }
    }

    private fun selectDefaultCity() {
        viewLifecycleOwner.lifecycleScope.launch {
            delay(100)
            binding?.rvCities?.findViewHolderForAdapterPosition(0)?.itemView?.performClick()
        }
    }

    override fun onCountryClick(model: CountriesModel) {
        viewModel.cities(model.id)
        rvSelectCityAdapter.setFlagImage(model.imageUrl)
    }

    override fun onSelectionDone(model: RegionsInCitiesModel, imageUrl: String) {
        binding?.apply {

            regionId = model.id
            regionTitleEn = model.titleEn
            regionTitleAr = model.titleAr

            btnTvCityName.text = model.title
            Glide.with(this@FilterCitiesDialogNewFragment).load(imageUrl).into(btnIvCountryFlag)

        }
    }
}