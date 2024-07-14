package eramo.resultgate.presentation.ui.auth

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.theartofdev.edmodo.cropper.CropImage
import dagger.hilt.android.AndroidEntryPoint
import eramo.resultgate.R
import eramo.resultgate.databinding.FragmentSignupBinding
import eramo.resultgate.domain.model.auth.CitiesModel
import eramo.resultgate.domain.model.auth.CountriesModel
import eramo.resultgate.domain.model.auth.RegionsModel
import eramo.resultgate.domain.model.auth.SubRegionsModel
import eramo.resultgate.presentation.ui.dialog.LoadingDialog
import eramo.resultgate.presentation.ui.dialog.WarningDialog
import eramo.resultgate.presentation.viewmodel.auth.SignUpViewModel
import eramo.resultgate.util.Constants
import eramo.resultgate.util.LocalHelperUtil
import eramo.resultgate.util.StringWithTag
import eramo.resultgate.util.navOptionsAnimation
import eramo.resultgate.util.onBackPressed
import eramo.resultgate.util.showToast
import eramo.resultgate.util.state.UiState
import org.json.JSONObject
import java.util.*

@AndroidEntryPoint
class SignUpFragment : Fragment(R.layout.fragment_signup), View.OnClickListener {

    private lateinit var binding: FragmentSignupBinding
    private val viewModel by viewModels<SignUpViewModel>()
    private lateinit var activityResultLauncher: ActivityResultLauncher<Any>
    private var imageTaxUri: Uri? = null
    private var imageCommercialUri: Uri? = null
    private var profileUri: Uri? = null
    private val countryList = ArrayList<StringWithTag>()
    private val cityList = ArrayList<StringWithTag>()
    private val regionList = ArrayList<StringWithTag>()
    private val subRegionList = ArrayList<StringWithTag>()
    private var countryId = -1
    private var cityId = -1
    private var regionId = -1
    private var subRegionId = -1
    private var gender = ""

    private val activityResultContract = object : ActivityResultContract<Any, Uri?>() {
        override fun createIntent(context: Context, input: Any): Intent {
            return CropImage.activity()
                .setAspectRatio(1, 1)
                .setCropMenuCropButtonTitle(getString(R.string.select))
                .getIntent(requireActivity())
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            if (resultCode == AppCompatActivity.RESULT_OK)
                return CropImage.getActivityResult(intent).uri
            return Uri.parse("")
        }
    }

    val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            if (resultCode == Activity.RESULT_OK) {
                //Image Uri will not be null for RESULT_OK
                val fileUri = data?.data!!

                profileUri = fileUri
                binding.ivProfile.setImageURI(fileUri)
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                showToast(ImagePicker.getError(data))
            } else {
                showToast(getString(R.string.no_image_chose))
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        LocalHelperUtil.loadLocal(requireActivity())
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSignupBinding.bind(view)

        // Views
        setupSpinners()
        setupGenderRadioGroup()

        activityResultLauncher = registerForActivityResult(activityResultContract) {
            it?.let { uri ->
                profileUri = uri
                if (it.toString().isNotEmpty()) {
                    binding.ivProfile.setImageURI(it)
                }
            }
        }


        binding.apply {
            signUpTvLogin.setOnClickListener(this@SignUpFragment)
            signUpTvPolicy.setOnClickListener(this@SignUpFragment)
            signUpBtnSignUp.setOnClickListener(this@SignUpFragment)
            signupEtBirthDate.setOnClickListener(this@SignUpFragment)
            ivProfile.setOnClickListener(this@SignUpFragment)
        }

        // Request
        viewModel.countries()

        // Fetch
        fetchCountriesState()
        fetchCityState()
        fetchRegionState()
        fetchSubRegionState()

        fetchValidateState()
        fetchRegisterState()

        handleLoadingCancellation()

        this@SignUpFragment.onBackPressed { findNavController().popBackStack() }
    }

    override fun onResume() {
        super.onResume()
        regionId = -1
        countryId = -1
        cityId = -1
        setupSpinners()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.signUp_tv_login -> findNavController().popBackStack()

            R.id.signUp_tv_policy -> findNavController().navigate(R.id.policyFragment)

            R.id.iv_profile -> {
                Constants.uploadType = Constants.TYPE_UPLOAD_PROFILE

                ImagePicker.with(requireActivity())
                    .compress(1024)
                    .cropSquare()
                    .createIntent { intent ->
                        startForProfileImageResult.launch(intent)
                    }
            }

            R.id.signUp_btn_signUp -> {
                binding.apply {
                    if (!signUpCbAgree.isChecked)
                        showToast(getString(R.string.txt_accept_privacy_first))
                    else {
                        viewModel.validateAndSignup(
                            signupEtFirstName.text.toString().trim(),
                            signupEtLastName.text.toString().trim(),
                            signupEtEmail.text.toString().trim(),
                            signupEtPassword.text.toString().trim(),
                            signupEtRePassword.text.toString().trim(),
                            signupEtPhone.text.toString().trim(),
                            signupEtAddress.text.toString().trim(),
                            signupEtBirthDate.text.toString().trim(),
                            gender,
                            countryId,
                            cityId,
                            regionId,
                            subRegionId,
                            profileUri,
                        )
                    }
                }
            }

            R.id.signup_et_birthDate -> {
                setupDatePickerDialog()
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
            signupInCountry.spinner.adapter = Constants.createSpinnerAdapter(requireContext(), countryList)

            // Listener
            signupInCountry.spinner.onItemSelectedListener =
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
            signupInCity.spinner.adapter = Constants.createSpinnerAdapter(requireContext(), cityList)

            // Listener
            signupInCity.spinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parentView: AdapterView<*>?,
                        selectedItemView: View?,
                        position: Int,
                        id: Long
                    ) {
                        if (position == 0) {
                            cityId = -1
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
            signupInRegion.spinner.adapter = Constants.createSpinnerAdapter(requireContext(), regionList)

            // Listener
            signupInRegion.spinner.onItemSelectedListener =
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
                            regionId = (parentView?.getItemAtPosition(position) as StringWithTag).tag
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
            signupInSubRegion.spinner.adapter = Constants.createSpinnerAdapter(requireContext(), subRegionList)

            // Listener
            signupInSubRegion.spinner.onItemSelectedListener =
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
                            subRegionId = (parentView?.getItemAtPosition(position) as StringWithTag).tag
                        }
                    }

                    override fun onNothingSelected(parentView: AdapterView<*>?) {
                        // your code here
                    }
                }
        }
    }

    //-------------------------------------------------------------------------------//

    private fun fetchValidateState() {
        lifecycleScope.launchWhenCreated {
            viewModel.validateState.collect { state ->
                binding.apply {

                    // remove errors if exist
                    itlFirstName.error = null
                    itlLastName.error = null
                    itlPhone.error = null
                    itlBirthDate.error = null
                    itlEmail.error = null
                    itlPassword.error = null
                    itlRePassword.error = null
                    signupInCountry.itlSpinner.error = null
                    signupInCity.itlSpinner.error = null
                    signupInRegion.itlSpinner.error = null
                    signupInSubRegion.itlSpinner.error = null
                    itlAddress.error = null
                    rbMale.error = null
                    rbFemale.error = null

                    itlFirstName.error = state.storeFirstError?.asString(requireContext())
                    itlLastName.error = state.storeLastError?.asString(requireContext())
                    itlPhone.error = state.storePhoneError?.asString(requireContext())
                    itlBirthDate.error = state.birthDateError?.asString(requireContext())
                    itlEmail.error = state.storeEmailError?.asString(requireContext())
                    itlPassword.error = state.passwordError?.asString(requireContext())
                    itlRePassword.error = state.rePasswordError?.asString(requireContext())
                    signupInCountry.itlSpinner.error =
                        state.countryIdError?.asString(requireContext())
                    signupInCity.itlSpinner.error = state.cityIdError?.asString(requireContext())
                    signupInRegion.itlSpinner.error = state.regionIdError?.asString(requireContext())
                    signupInSubRegion.itlSpinner.error = state.subRegionIdError?.asString(requireContext())
                    itlAddress.error = state.addressError?.asString(requireContext())
                    rbMale.error = state.genderError?.asString(requireContext())
                    rbFemale.error = state.genderError?.asString(requireContext())
                }
            }
        }
    }

    private fun fetchRegisterState() {
        lifecycleScope.launchWhenCreated {
            viewModel.registerState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()

                        val email = binding.signupEtEmail.text.toString().trim()
                        val phone = binding.signupEtPhone.text.toString().trim()
                        val password = binding.signupEtPassword.text.toString().trim()

                        showToast(getString(R.string.please_check_your_inbox_or_spam_mail))

                        findNavController().navigate(
                            R.id.signUpVerificationFragment, SignUpVerificationFragmentArgs(
                                email, phone, password, Constants.DESTINATION_FROM_SIGN_UP
                            ).toBundle(),
                            navOptionsAnimation()
                        )
                    }

                    is UiState.Error -> {
                        LoadingDialog.dismissDialog()
                        val string = state.message!!.asString(requireContext())

                        try {
//                            parseErrorResponse(string)
                            parseError(string)
                        } catch (e: Exception) {
                            showToast(string)
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

    @SuppressLint("ClickableViewAccessibility")
    private fun setupSpinners() {
        binding.apply {
            signupInCountry.spinnerIcon.setImageResource(R.drawable.ic_location)
            signupInCity.spinnerIcon.setImageResource(R.drawable.ic_location)
            signupInRegion.spinnerIcon.setImageResource(R.drawable.ic_location)
            signupInSubRegion.spinnerIcon.setImageResource(R.drawable.ic_location)

            signupInCity.spinner.adapter = Constants.createSpinnerAdapter(
                requireContext(),
                listOf(StringWithTag(getString(R.string.txt_city), 1))
            )

            signupInRegion.spinner.adapter = Constants.createSpinnerAdapter(
                requireContext(),
                listOf(StringWithTag(getString(R.string.txt_region), 1))
            )

            signupInSubRegion.spinner.adapter = Constants.createSpinnerAdapter(
                requireContext(),
                listOf(StringWithTag(getString(R.string.txt_subregion), 1))
            )

            signupInCity.spinner.setOnTouchListener { _, _ ->
                if (countryId == -1)
                    WarningDialog.showDialog(
                        getString(R.string.txt_warning),
                        getString(R.string.select_country)
                    )
                false
            }

            signupInRegion.spinner.setOnTouchListener { _, _ ->
                if (cityId == -1)
                    WarningDialog.showDialog(
                        getString(R.string.txt_warning),
                        getString(R.string.select_city)
                    )
                false
            }

            signupInSubRegion.spinner.setOnTouchListener { _, _ ->
                if (cityId == -1)
                    WarningDialog.showDialog(
                        getString(R.string.txt_warning),
                        getString(R.string.select_region)
                    )
                false
            }
        }
    }

    private fun setupGenderRadioGroup() {
        binding.radioGroup.setOnCheckedChangeListener { _, optionId ->
            gender = when (optionId) {
                R.id.rbMale -> {
                    "male"
                }

                R.id.rbFemale -> {
                    "female"
                }

                else -> {
                    ""
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

    private fun parseError(string: String) {
        try {
            parseErrorResponse(string)
        } catch (e: Exception) {
            parseServerErrorResponse(string)
        }
    }

    private fun parseErrorResponse(string: String) {
        val errorJson = JSONObject(string)
        val errorsKey = errorJson.getString("errors")

        // Key
        val errorsJson = JSONObject(errorsKey)
        val keys = errorsJson.keys()

        val errorMessage = errorsJson.getString(keys.next())
        val errorMessageSub = errorMessage.substring(2, (errorMessage.length - 2))

        showToast(errorMessageSub)
    }

    private fun parseServerErrorResponse(string: String) {
        val jsonErrorBody = JSONObject(string)
        val errorMessage = jsonErrorBody.getString("message")
        showToast(errorMessage)
    }

    private fun setupDatePickerDialog() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)


        val dpd = DatePickerDialog(
            requireContext(),
            R.style.DatePickerDialogTheme,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                val yyyy = year.toString()
                val mm = if ((monthOfYear + 1) < 10) {
                    "0${monthOfYear + 1}"
                } else {
                    "${monthOfYear + 1}"
                }
                val dd = if (dayOfMonth < 10) {
                    "0$dayOfMonth"
                } else {
                    "$dayOfMonth"
                }

                binding.signupEtBirthDate.setText("$yyyy-$mm-$dd")

            },
            year,
            month,
            day
        )

        // 18 years ago and above as a max date
        dpd.datePicker.maxDate = System.currentTimeMillis() - 568111593403L

        dpd.show()
    }
}