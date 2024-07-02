package eramo.tahoon.presentation.ui.dialog

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import eramo.tahoon.R
import eramo.tahoon.databinding.DialogMyAddressesBinding
import eramo.tahoon.domain.model.auth.CitiesModel
import eramo.tahoon.domain.model.auth.CountriesModel
import eramo.tahoon.domain.model.auth.RegionsModel
import eramo.tahoon.domain.model.auth.SubRegionsModel
import eramo.tahoon.presentation.viewmodel.drawer.myaccount.MyAddressesViewModel
import eramo.tahoon.util.Constants
import eramo.tahoon.util.StringWithTag
import eramo.tahoon.util.navOptionsAnimation
import eramo.tahoon.util.showToast
import eramo.tahoon.util.state.UiState
import org.json.JSONObject

@AndroidEntryPoint
class MyAddressesDialog : DialogFragment(R.layout.dialog_my_addresses) {
    private lateinit var binding: DialogMyAddressesBinding

    private val viewModel by viewModels<MyAddressesViewModel>()

    private val args by navArgs<MyAddressesDialogArgs>()
    private val existCountryId get() = args.countryId
    private val existCityId get() = args.cityId
    private val existRegionId get() = args.regionId
    private val existSubRegionId get() = args.subRegionId
    private val existAddressType get() = args.addressType
    private val existAddress get() = args.address
    private val existAddressId get() = args.addressId
    private val destenation get() = args.destenation

    private val countryList = ArrayList<StringWithTag>()
    private val cityList = ArrayList<StringWithTag>()
    private val regionList = ArrayList<StringWithTag>()
    private val subRegionList = ArrayList<StringWithTag>()

    private var regionId = -1
    private var subRegionId = -1
    private var countryId = -1
    private var cityId = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setStyle(STYLE_NORMAL, R.style.DialogStyle)
        return super.onCreateView(inflater, container, savedInstanceState)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DialogMyAddressesBinding.bind(view)

        setupSpinners()

        fillFieldsOnAddressEdit()

        // Listener
        binding.apply {
            btnCancel.setOnClickListener { dismiss() }
            btnConfirm.setOnClickListener { validateAndAddAddressOrUpdateAddress() }
        }

        // Request
        viewModel.countries()

        // Fetch
        fetchAddToMyAddressState()
        fetchUpdateAddressState()
        fetchCountriesState()
        fetchCityState()
        fetchRegionState()
        fetchSubRegionState()
    }

    private fun validateAndAddAddressOrUpdateAddress() {
        binding.apply {
            if (countryId == -1) {
                spinnerCountry.itlSpinner.error = getString(R.string.select_country)
                return
            } else {
                spinnerCountry.itlSpinner.error = null
            }

            if (cityId == -1) {
                spinnerCity.itlSpinner.error = getString(R.string.select_city)
                return
            } else {
                spinnerCity.itlSpinner.error = null
            }

            if (regionId == -1) {
                spinnerRegion.itlSpinner.error = getString(R.string.select_region)
                return
            } else {
                spinnerRegion.itlSpinner.error = null
            }

            if (subRegionId == -1) {
                spinnerSubRegion.itlSpinner.error = getString(R.string.select_sub_region)
                return
            } else {
                spinnerSubRegion.itlSpinner.error = null
            }

            if (etAddressType.text.toString().isEmpty()) {
                itlAddressType.error = getString(R.string.enter_address_type)
                return
            }

            if (etAddressValue.text.toString().isEmpty()) {
                itlAddressType.error = getString(R.string.enter_address)
                return
            }

            if (existCountryId != null && existCityId != null && existRegionId != null && existAddressType != null && existAddress != null && existAddressId != null) {
                viewModel.updateAddress(
                    existAddressId!!,
                    etAddressType.text.toString().trim(),
                    etAddressValue.text.toString().trim(),
                    countryId.toString(),
                    cityId.toString(),
                    regionId.toString(),
                    subRegionId.toString()
                )
            } else {
                viewModel.addToMyAddresses(
                    etAddressType.text.toString().trim(),
                    etAddressValue.text.toString().trim(),
                    countryId.toString(),
                    cityId.toString(),
                    regionId.toString(),
                    subRegionId.toString()
                )
            }

        }
    }

    private fun fetchAddToMyAddressState() {
        lifecycleScope.launchWhenCreated {
            viewModel.addToMyAddressesState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()
                        showToast(state.data?.message ?: getString(R.string.success))

                        if (destenation == Constants.MY_ADDRESSES_FRAGMENT) {
                            findNavController().navigate(R.id.myAddressesFragment, null, navOptionsAnimation())
                        } else if (destenation == Constants.CHECKOUT_FRAGMENT) {
                            findNavController().navigate(R.id.checkoutStepOneFragment, null, navOptionsAnimation())
                        }
                    }

                    is UiState.Error -> {
                        LoadingDialog.dismissDialog()
                        val string = state.message!!.asString(requireContext())

                        try {
                            parseErrorResponse(string)
                            dismiss()
                        } catch (e: Exception) {
                            showToast(string)
                        }
                        showToast(string)
                        dismiss()
                    }

                    is UiState.Loading -> {
                        LoadingDialog.showDialog()
                    }

                    else -> Unit
                }

            }
        }
    }

    private fun fetchUpdateAddressState() {
        lifecycleScope.launchWhenCreated {
            viewModel.updateAddressState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()
                        showToast(state.data?.message ?: getString(R.string.success))
//                        findNavController().navigate(R.id.myAddressesFragment, null, navOptionsAnimation())

                        if (destenation == Constants.MY_ADDRESSES_FRAGMENT) {
                            findNavController().navigate(R.id.myAddressesFragment, null, navOptionsAnimation())
                        } else if (destenation == Constants.CHECKOUT_FRAGMENT) {
                            findNavController().navigate(R.id.checkoutStepOneFragment, null, navOptionsAnimation())
                        }
                    }

                    is UiState.Error -> {
                        LoadingDialog.dismissDialog()
                        val string = state.message!!.asString(requireContext())

                        try {
                            parseErrorResponse(string)
                            dismiss()
                        } catch (e: Exception) {
                            showToast(string)
                        }
                        showToast(string)
                        dismiss()
                    }

                    is UiState.Loading -> {
                        LoadingDialog.showDialog()
                    }

                    else -> Unit
                }

            }
        }
    }

    //-------------------------------------------------------------------------------//
    private fun fetchCountriesState() {
        lifecycleScope.launchWhenCreated {
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

    private fun setupCountries(list: List<CountriesModel>) {
        binding.apply {

            // Initial item
            countryList.clear()
            countryList.add(StringWithTag(getString(R.string.txt_country), 0))

            // Assign the countries
            for (model in list)
                countryList.add(StringWithTag(model.title, model.id))

            // Assign the spinner adapter
            spinnerCountry.spinner.adapter = Constants.createSpinnerAdapter(requireContext(), countryList)

            // Select user default
            for (i in countryList) {
                if (i.tag.toString() == existCountryId) {
                    spinnerCountry.spinner.setSelection(countryList.indexOf(i))
                }
            }

            // Listener
            spinnerCountry.spinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parentView: AdapterView<*>?,
                        selectedItemView: View?,
                        position: Int,
                        id: Long
                    ) {
                        if (position == 0) {
                            countryId = -1
                            //   viewModel.cities(-1)
                        } else {
                            countryId = (parentView?.getItemAtPosition(position) as StringWithTag).tag
                            viewModel.cities(countryId)
                        }
                    }

                    override fun onNothingSelected(parentView: AdapterView<*>?) {
                        // your code here
                    }
                }
        }
    }

    private fun fetchCityState() {
        lifecycleScope.launchWhenCreated {
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

    private fun setupCities(list: List<CitiesModel>) {
        binding.apply {

            // Initial item
            cityList.clear()
            cityList.add(StringWithTag(getString(R.string.txt_city), 0))

            // Assign the cities
            for (model in list)
                cityList.add(StringWithTag(model.title, model.id))

            // Assign the spinner adapter
            spinnerCity.spinner.adapter = Constants.createSpinnerAdapter(requireContext(), cityList)

            // Select user default
            for (i in cityList) {
                if (i.tag.toString() == existCityId) {
                    spinnerCity.spinner.setSelection(cityList.indexOf(i))
                }
            }

            // Listener
            spinnerCity.spinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parentView: AdapterView<*>?,
                        selectedItemView: View?,
                        position: Int,
                        id: Long
                    ) {
                        if (position == 0) {
                            cityId = -1
//                            viewModel.regions(-1)
                        } else {
                            cityId =
                                (parentView?.getItemAtPosition(position) as StringWithTag).tag
                            viewModel.regions(cityId)
                        }
                    }

                    override fun onNothingSelected(parentView: AdapterView<*>?) {
                        // your code here
                    }
                }
        }
    }

    private fun fetchRegionState() {
        lifecycleScope.launchWhenCreated {
            viewModel.regionState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()
                        setupRegions(state.data ?: emptyList())
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

    private fun setupRegions(list: List<RegionsModel>) {
        binding.apply {

            // Initial item
            regionList.clear()
            regionList.add(StringWithTag(getString(R.string.txt_region), 0))

            // Assign the regions
            for (model in list)
                regionList.add(StringWithTag(model.title, model.id))

            // Assign the spinner adapter
            spinnerRegion.spinner.adapter = Constants.createSpinnerAdapter(requireContext(), regionList)

            // Select user default
            for (i in regionList) {
                if (i.tag.toString() == existRegionId) {
                    spinnerRegion.spinner.setSelection(regionList.indexOf(i))
                }
            }

            // Listener
            spinnerRegion.spinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parentView: AdapterView<*>?,
                        selectedItemView: View?,
                        position: Int,
                        id: Long
                    ) {
                        if (position == 0) {
                            regionId = -1
                        } else {
                            regionId =  (parentView?.getItemAtPosition(position) as StringWithTag).tag
                            viewModel.subRegions(regionId)
                        }
                    }

                    override fun onNothingSelected(parentView: AdapterView<*>?) {
                        // your code here
                    }
                }
        }
    }

    private fun fetchSubRegionState() {
        lifecycleScope.launchWhenCreated {
            viewModel.subRegionState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()
                        setupSubRegions(state.data ?: emptyList())
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

    private fun setupSubRegions(list: List<SubRegionsModel>) {
        binding.apply {

            // Initial item
            subRegionList.clear()
            subRegionList.add(StringWithTag(getString(R.string.txt_subregion), 0))

            // Assign the regions
            for (model in list)
                subRegionList.add(StringWithTag(model.title, model.id))

            // Assign the spinner adapter
            spinnerSubRegion.spinner.adapter = Constants.createSpinnerAdapter(requireContext(), subRegionList)

            // Select user default
            for (i in subRegionList) {
                if (i.tag.toString() == existSubRegionId) {
                    spinnerSubRegion.spinner.setSelection(subRegionList.indexOf(i))
                }
            }

            // Listener
            spinnerSubRegion.spinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parentView: AdapterView<*>?,
                        selectedItemView: View?,
                        position: Int,
                        id: Long
                    ) {
                        if (position == 0) {
                            subRegionId = -1
                        } else {
                            subRegionId =  (parentView?.getItemAtPosition(position) as StringWithTag).tag
                        }
                    }

                    override fun onNothingSelected(parentView: AdapterView<*>?) {
                        // your code here
                    }
                }
        }
    }

    //-------------------------------------------------------------------------------//

    private fun fillFieldsOnAddressEdit() {
        if (existCountryId != null && existCityId != null && existRegionId != null && existAddressType != null && existAddress != null && existAddressId != null) {
            binding.apply {
                etAddressType.setText(existAddressType)
                etAddressValue.setText(existAddress)

                btnConfirm.text = getString(R.string.update)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupSpinners() {
        binding.apply {
            spinnerCountry.spinnerIcon.setImageResource(R.drawable.ic_flag)
            spinnerCity.spinnerIcon.setImageResource(R.drawable.ic_building_two)
            spinnerRegion.spinnerIcon.setImageResource(R.drawable.ic_building)
            spinnerSubRegion.spinnerIcon.setImageResource(R.drawable.ic_building)

            spinnerCity.spinner.adapter = Constants.createSpinnerAdapter(
                requireContext(),
                listOf(StringWithTag(getString(R.string.txt_city), 1))
            )

            spinnerRegion.spinner.adapter = Constants.createSpinnerAdapter(
                requireContext(),
                listOf(StringWithTag(getString(R.string.txt_region), 1))
            )

            spinnerSubRegion.spinner.adapter = Constants.createSpinnerAdapter(
                requireContext(),
                listOf(StringWithTag(getString(R.string.txt_subregion), 1))
            )

            spinnerCity.spinner.setOnTouchListener { _, _ ->
                if (countryId == -1)
                    WarningDialog.showDialog(
                        getString(R.string.txt_warning),
                        getString(R.string.select_country)
                    )
                false
            }

            spinnerRegion.spinner.setOnTouchListener { _, _ ->
                if (cityId == -1)
                    WarningDialog.showDialog(
                        getString(R.string.txt_warning),
                        getString(R.string.select_city)
                    )
                false
            }

            spinnerSubRegion.spinner.setOnTouchListener { _, _ ->
                if (regionId == -1)
                    WarningDialog.showDialog(
                        getString(R.string.txt_warning),
                        getString(R.string.select_region)
                    )
                false
            }
        }
    }

    private fun parseErrorResponse(string: String) {
        val jsonErrorBody = JSONObject(string)
        val errorMessage = jsonErrorBody.getString("errors")
        showToast(errorMessage)
    }
}