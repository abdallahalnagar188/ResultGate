package eramo.tahoon.presentation.ui.drawer.myaccount

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.theartofdev.edmodo.cropper.CropImage
import dagger.hilt.android.AndroidEntryPoint
import eramo.tahoon.R
import eramo.tahoon.databinding.FragmentEditPersonalDetailsBinding
import eramo.tahoon.domain.model.auth.CitiesModel
import eramo.tahoon.domain.model.auth.CountriesModel
import eramo.tahoon.domain.model.auth.RegionsModel
import eramo.tahoon.presentation.ui.dialog.LoadingDialog
import eramo.tahoon.presentation.ui.dialog.WarningDialog
import eramo.tahoon.presentation.viewmodel.drawer.myaccount.EditPersonalDetailsViewModel
import eramo.tahoon.util.*
import eramo.tahoon.util.state.UiState
import org.json.JSONObject
import java.util.Calendar

@AndroidEntryPoint
class EditPersonalDetailsFragment : Fragment(R.layout.fragment_edit_personal_details) {

    private val viewModel by viewModels<EditPersonalDetailsViewModel>()
    private lateinit var binding: FragmentEditPersonalDetailsBinding
    private val args by navArgs<EditPersonalDetailsFragmentArgs>()
    private val memberModel get() = args.memberModel
    private var imageUri: Uri? = null
    private val countryList = ArrayList<StringWithTag>()
    private val cityList = ArrayList<StringWithTag>()
    private val regionList = ArrayList<StringWithTag>()
    private var isSettingCountry = true
    private var isSettingCity = true
    private var isSettingRegion = true
    private var selectedPosition = 0
    private var countryId = -1
    private var cityId = -1
    private var regionId = -1

    //    private val requestNotificationPermission =
//        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
//            if (it[Manifest.permission.MANAGE_EXTERNAL_STORAGE] == false) {
//                showToast("Access Deined")
//            }
//        }
    private val readMediaImagesPermission =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.READ_MEDIA_IMAGES else Manifest.permission.READ_EXTERNAL_STORAGE

    private val requestStoragePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // Permission granted
                // You can now perform the actions that require this permission
            } else {
                // Permission denied
                // Handle the scenario when the user denies the permission
            }
        }

    val activityResultContract = object : ActivityResultContract<Any, Uri?>() {
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
        binding = FragmentEditPersonalDetailsBinding.bind(view)
//        setupSpinners()

//        requestStoragePermission()

        val activityResultLauncher = registerForActivityResult(activityResultContract) {
            it?.let { uri ->
                imageUri = uri
                if (it.toString().isNotEmpty())
                    binding.FEditPersonalProfile.setImageURI(it)
            }
        }


        binding.apply {
            signupEtFirstName.setText(memberModel.firstName)
            signupEtLastName.setText(memberModel.lastName)

            FEditPersonalEtEmail.setText(memberModel.email)
            FEditPersonalEtPhone.setText(memberModel.phone)

            signupEtBirthDate.setText(memberModel.birthDate)
//            FEditPersonalEtAddress.setText(memberModel.userAddress)
//            FEditPersonalEtAddress.setText(UserUtil.getUserAddress())

            signupEtBirthDate.setOnClickListener { setupDatePickerDialog() }
            FEditPersonalBtnSave.setOnClickListener { setupSaveEdit() }
            FEditPersonalIvBack.setOnClickListener { findNavController().popBackStack() }

            Glide.with(requireContext())
                .load(memberModel.imageUrl)
                .into(binding.FEditPersonalProfile)
            Log.e("imageUri", memberModel.imageUrl.toString())

             val startForProfileImageResult =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                    val resultCode = result.resultCode
                    val data = result.data

                    if (resultCode == Activity.RESULT_OK) {
                        //Image Uri will not be null for RESULT_OK
                        val fileUri = data?.data!!

                        imageUri = fileUri
                        binding.FEditPersonalProfile.setImageURI(fileUri)
                    } else if (resultCode == ImagePicker.RESULT_ERROR) {
                        showToast( ImagePicker.getError(data))
                    } else {
                        showToast(getString(R.string.no_image_chose))
                    }
                }

            FEditPersonalCamera.setOnClickListener {
//                if (requestStoragePermissionAnd()) {
//                    activityResultLauncher.launch("image/*")
//                }
                ImagePicker.with(requireActivity())
                    .compress(1024)
                    .cropSquare()
                    .createIntent { intent ->
                        startForProfileImageResult.launch(intent)
                    }
            }
        }
        fetchEditProfileState()
//        fetchCountriesState()
//        fetchCityState()
//        fetchRegionState()
        handleLoadingCancellation()

        this@EditPersonalDetailsFragment.onBackPressed { findNavController().popBackStack() }
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun setupSpinners() {
        binding.apply {
            FEditPersonalInCountry.spinnerIcon.setImageResource(R.drawable.ic_location)
            FEditPersonalInCity.spinnerIcon.setImageResource(R.drawable.ic_location)
            FEditPersonalInRegion.spinnerIcon.setImageResource(R.drawable.ic_location)

            FEditPersonalInCity.spinner.adapter = Constants.createSpinnerAdapter(
                requireContext(),
                listOf(StringWithTag(getString(R.string.txt_city), 1))
            )

            FEditPersonalInRegion.spinner.adapter = Constants.createSpinnerAdapter(
                requireContext(),
                listOf(StringWithTag(getString(R.string.txt_region), 1))
            )

            FEditPersonalInCity.spinner.setOnTouchListener { _, _ ->
                if (countryId == -1)
                    WarningDialog.showDialog(
                        getString(R.string.txt_warning),
                        getString(R.string.select_country)
                    )
                false
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.countries()
    }

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
            countryList.clear()
            var item = StringWithTag(getString(R.string.txt_country), 0)
            countryList.add(item)
            for (model in list) {
                item = StringWithTag(model.title, model.id)
                countryList.add(item)
                if (isSettingCountry && model.id.toString() == memberModel.countryId.toString()) {
                    isSettingCountry = false
                    selectedPosition = countryList.indexOf(item)
                }
            }
            FEditPersonalInCountry.spinner.adapter =
                Constants.createSpinnerAdapter(requireContext(), countryList)

            FEditPersonalInCountry.spinner.setSelection(selectedPosition)
            selectedPosition = 0

            FEditPersonalInCountry.spinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parentView: AdapterView<*>?,
                        selectedItemView: View?,
                        position: Int,
                        id: Long
                    ) {
                        if (position == 0) cityId = -1
                        else {
                            (parentView?.getItemAtPosition(position) as StringWithTag).apply {
                                countryId = tag
                            }
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
            cityList.clear()
            var item = StringWithTag(getString(R.string.txt_city), 0)
            cityList.add(item)
            for (model in list) {
                item = StringWithTag(model.title, model.id)
                cityList.add(item)
                if (isSettingCity && model.id.toString() == memberModel.cityId.toString()) {
                    isSettingCity = false
                    selectedPosition = cityList.indexOf(item)
                }
            }
            FEditPersonalInCity.spinner.adapter =
                Constants.createSpinnerAdapter(requireContext(), cityList)

            FEditPersonalInCity.spinner.setSelection(selectedPosition)
            selectedPosition = 0

            FEditPersonalInCity.spinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parentView: AdapterView<*>?,
                        selectedItemView: View?,
                        position: Int,
                        id: Long
                    ) {
                        if (position == 0) regionId = -1
                        else {
                            (parentView?.getItemAtPosition(position) as StringWithTag).apply {
                                cityId = tag
                            }
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
            regionList.clear()
            var item = StringWithTag(getString(R.string.txt_region), 0)
            regionList.add(item)
            for (model in list) {
                item = StringWithTag(model.title, model.id)
                regionList.add(item)
                if (isSettingRegion && model.id.toString() == memberModel.regionId.toString()) {
                    isSettingRegion = false
                    selectedPosition = regionList.indexOf(item)
                }
            }
            FEditPersonalInRegion.spinner.adapter =
                Constants.createSpinnerAdapter(requireContext(), regionList)

            FEditPersonalInRegion.spinner.setSelection(selectedPosition)
            selectedPosition = 0

            FEditPersonalInRegion.spinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parentView: AdapterView<*>?,
                        selectedItemView: View?,
                        position: Int,
                        id: Long
                    ) {
                        if (position == 0) regionId = -1
                        else (parentView?.getItemAtPosition(position) as StringWithTag).apply {
                            regionId = tag
                        }
                    }

                    override fun onNothingSelected(parentView: AdapterView<*>?) {
                        // your code here
                    }
                }
        }
    }

    private fun setupSaveEdit() {
        binding.apply {
            val firstName = signupEtFirstName.text.toString().trim()
            val lastName = signupEtLastName.text.toString().trim()
//            val address = FEditPersonalEtAddress.text.toString().trim()
            val email = FEditPersonalEtEmail.text.toString().trim()
            val phone = FEditPersonalEtPhone.text.toString().trim()

            val birthDate = signupEtBirthDate.text.toString().trim()

            if (TextUtils.isEmpty(firstName)) {
                itlFirstName.error = getString(R.string.enter_your_first_name)
                itlFirstName.requestFocus()
                return
            } else itlFirstName.error = null

            if (TextUtils.isEmpty(lastName)) {
                itlLastName.error = getString(R.string.enter_your_last_name)
                itlLastName.requestFocus()
                return
            } else itlLastName.error = null

            if (TextUtils.isEmpty(birthDate)) {
                itlBirthDate.error = getString(R.string.enter_your_birth_date)
                itlBirthDate.requestFocus()
                return
            } else itlLastName.error = null

//            if (TextUtils.isEmpty(address)) {
//                itlAddress.error = getString(R.string.txt_address_is_required)
//                itlAddress.requestFocus()
//                return
//            } else itlAddress.error = null

//            val isAddressBlank = countryId == -1 || cityId == -1 || regionId == -1
//            if (isAddressBlank) {
//                showToast(getString(R.string.txt_address_is_required))
//                return
//            }

            if (TextUtils.isEmpty(email)) {
                itlEmail.error = getString(R.string.txt_email_is_required)
                return
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                itlEmail.error = getString(R.string.txt_please_enter_a_valid_email_address)
                return
            } else itlEmail.error = null

            if (TextUtils.isEmpty(phone)) {
                itlPhone.error = getString(R.string.txt_phone_is_required)
                return
            } else if (!PhoneNumberUtils.isGlobalPhoneNumber(phone)) {
                itlPhone.error = getString(R.string.txt_please_enter_a_valid_phone_number)
                return
            } else itlPhone.error = null

            viewModel.editProfile(
                firstName, lastName, birthDate, imageUri
            )
//            viewModel.editProfile(
//                memberModel.userId!!,
//                UserUtil.getUserPass(),
//                fullName,
//                address,
//                countryId.toString(),
//                cityId.toString(),
//                regionId.toString(),
//                email,
//                phone,
//                imageUri
//            )
        }
    }

    private fun fetchEditProfileState() {
        lifecycleScope.launchWhenCreated {
            viewModel.editProfileState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()
                        findNavController().popBackStack()
                        showToast(getString(R.string.account_updated_successfully))
                    }

                    is UiState.Error -> {
                        LoadingDialog.dismissDialog()
                        val string = state.message!!.asString(requireContext())

                        try {
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
            requireContext(), R.style.DatePickerDialogTheme,
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

            }, year, month, day
        )

        // 18 years ago and above as a max date
//        dpd.datePicker.maxDate = System.currentTimeMillis() - 568025136000L
        dpd.datePicker.maxDate = System.currentTimeMillis() - 568111593403L

        dpd.show()
    }

//    private fun requestStoragePermission() {
//            if (!isStoragePermissionGranted()) {
//                requestNotificationPermission.launch(arrayOf(Manifest.permission.MANAGE_EXTERNAL_STORAGE))
//            }
//    }
//
//
//    private fun isStoragePermissionGranted(): Boolean {
//        return ActivityCompat.checkSelfPermission(
//            requireContext(),
//            Manifest.permission.MANAGE_EXTERNAL_STORAGE
//        ) == PackageManager.PERMISSION_GRANTED
//    }

    private fun requestStoragePermissionAnd(): Boolean {
        return if (ContextCompat.checkSelfPermission(
                requireContext(),
                readMediaImagesPermission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestStoragePermissionLauncher.launch(readMediaImagesPermission)
            false
        } else {
            true
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

}