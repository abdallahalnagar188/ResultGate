package eramo.tahoon.presentation.viewmodel.auth

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eramo.tahoon.domain.model.auth.CitiesModel
import eramo.tahoon.domain.model.auth.CountriesModel
import eramo.tahoon.domain.model.auth.RegionsModel
import eramo.tahoon.domain.model.auth.SignUpModel
import eramo.tahoon.domain.model.auth.SubRegionsModel
import eramo.tahoon.domain.repository.AuthRepository
import eramo.tahoon.domain.validation.Validation
import eramo.tahoon.util.Constants
import eramo.tahoon.util.LocalHelperUtil
import eramo.tahoon.util.state.Resource
import eramo.tahoon.util.state.UiState
import eramo.tahoon.util.state.UiText
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val validation: Validation
) : ViewModel() {

    //-------------------------------------------------------------------------------------------------------//
    private val _countriesState = MutableStateFlow<UiState<List<CountriesModel>>>(UiState.Empty())
    val countriesState: StateFlow<UiState<List<CountriesModel>>> = _countriesState

    private val _cityState = MutableStateFlow<UiState<List<CitiesModel>>>(UiState.Empty())
    val cityState: StateFlow<UiState<List<CitiesModel>>> = _cityState

    private val _regionState = MutableStateFlow<UiState<List<RegionsModel>>>(UiState.Empty())
    val regionState: StateFlow<UiState<List<RegionsModel>>> = _regionState

    private val _subRegionState = MutableStateFlow<UiState<List<SubRegionsModel>>>(UiState.Empty())
    val subRegionState: StateFlow<UiState<List<SubRegionsModel>>> = _subRegionState
    //-------------------------------------------------------------------------------------------------------//

    private val _validateState = MutableSharedFlow<ValidateSignupForm>()
    val validateState: SharedFlow<ValidateSignupForm> = _validateState

    private val _registerState = MutableStateFlow<UiState<SignUpModel>>(UiState.Empty())
    val registerState: StateFlow<UiState<SignUpModel>> = _registerState

    private var registerJob: Job? = null
    private var countryJob: Job? = null
    private var cityJob: Job? = null
    private var regionJob: Job? = null
    private var subRegionJob: Job? = null

    fun cancelRequest() {
        registerJob?.cancel()
        countryJob?.cancel()
        cityJob?.cancel()
        regionJob?.cancel()
        subRegionJob?.cancel()
    }


    fun validateAndSignup(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        passwordConfirmation: String,
        phone: String,
        address: String,
        birthDate: String,
        gender: String,
        countryId: Int,
        cityId: Int,
        regionId: Int,
        subRegionId: Int,
        image: Uri?
    ) {
        val storeFirstResult = validation.validateField(firstName)
        val storeLastResult = validation.validateField(lastName)
        val storeEmailResult = validation.validateEmail(email)
        val storePhoneResult = validation.validatePhone(phone)
        val birthDateResult = validation.validateField(birthDate)
        val passwordResult = validation.validatePassword(password)
        val rePasswordResult = validation.validateRepeatPassword(password, passwordConfirmation)

//        val countryResult = validation.validateNotMinusOne(countryId, UiText.StringResource(R.string.txt_country))
        val countryResult = validation.validateNotMinusOne(countryId)
        val cityResult = validation.validateNotMinusOne(cityId)
        val regionResult = validation.validateNotMinusOne(regionId)
        val subRegionResult = validation.validateNotMinusOne(subRegionId)

        val addressResult = validation.validateField(address)
        val genderResult = validation.validateField(gender)

        val hasError = listOf(
            storeFirstResult,
            storeLastResult,
            storeEmailResult,
            storePhoneResult,
            birthDateResult,
            passwordResult,
            rePasswordResult,
            countryResult,
            cityResult,
            regionResult,
            subRegionResult,
            addressResult,
            genderResult
        ).any { !it.successful }

        if (hasError) {
            viewModelScope.launch {
                _validateState.emit(
                    ValidateSignupForm(
                        storeFirstError = storeFirstResult.errorMessage,
                        storeLastError = storeLastResult.errorMessage,
                        storeEmailError = storeEmailResult.errorMessage,
                        storePhoneError = storePhoneResult.errorMessage,
                        birthDateError = birthDateResult.errorMessage,
                        passwordError = passwordResult.errorMessage,
                        rePasswordError = rePasswordResult.errorMessage,

                        countryIdError = countryResult.errorMessage,
                        cityIdError = cityResult.errorMessage,
                        regionIdError = regionResult.errorMessage,
                        subRegionIdError = subRegionResult.errorMessage,

                        addressError = addressResult.errorMessage,
                        genderError = genderResult.errorMessage
                    )
                )
            }
            return
        }

//        registerApp(
//            convertToRequestBody(store_name),
//            convertToRequestBody(store_email),
//            convertToRequestBody(store_phone),
//            convertToRequestBody(password),
//            convertToRequestBody(countryId),
//            convertToRequestBody(cityId),
//            convertToRequestBody(regionId),
//            convertToRequestBody(address),
//            convertFileToMultipart(Constants.IMG_KEY_COMMERCIAL,commercial_img),
//            convertFileToMultipart(Constants.IMG_KEY_TAX,tax_card_img),
//            convertFileToMultipart(Constants.IMG_KEY_PROFILE,profile_img)
//        )

        registerApp(
            convertToRequestBody(firstName),
            convertToRequestBody(lastName),
            convertToRequestBody(email),
            convertToRequestBody(password),
            convertToRequestBody(passwordConfirmation),
            convertToRequestBody(phone),
            convertToRequestBody(address),
            convertToRequestBody(if (LocalHelperUtil.isEnglish()) Constants.DEFAULT_ADDRESS_TYPE_EN else Constants.DEFAULT_ADDRESS_TYPE_AR),
            convertToRequestBody(birthDate),
            convertToRequestBody(gender),
            convertToRequestBody(Constants.SIGN_UP_SIGN_FROM),
            convertToRequestBody(countryId.toString()),
            convertToRequestBody(cityId.toString()),
            convertToRequestBody(regionId.toString()),
            convertToRequestBody(subRegionId.toString()),
            convertFileToMultipart(Constants.IMG_KEY_PROFILE, image)
        )

    }

    private fun registerApp(
        firstName: RequestBody?,
        lastName: RequestBody?,
        email: RequestBody?,
        password: RequestBody?,
        passwordConfirmation: RequestBody?,
        phone: RequestBody?,
        address: RequestBody?,
        addressType: RequestBody?,
        birthDate: RequestBody?,
        gender: RequestBody?,
        signFrom: RequestBody?,
        countryId: RequestBody?,
        cityId: RequestBody?,
        regionId: RequestBody?,
        subRegionId: RequestBody?,
        image: MultipartBody.Part?
    ) {
        registerJob?.cancel()
        registerJob = viewModelScope.launch {
            withContext(coroutineContext) {
                authRepository.register(
                    firstName,
                    lastName,
                    email,
                    password,
                    passwordConfirmation,
                    phone,
                    address,
                    addressType,
                    birthDate,
                    gender,
                    signFrom,
                    countryId,
                    cityId,
                    regionId,
                    subRegionId,
                    image
                ).collect {
                    when (it) {
                        is Resource.Success -> {
                            _registerState.value = UiState.Success(it.data)
                        }

                        is Resource.Error -> {
                            _registerState.value = UiState.Error(it.message!!)
                        }

                        is Resource.Loading -> {
                            _registerState.value = UiState.Loading()
                        }
                    }
                }
            }
        }
    }


    fun countries() {
        countryJob?.cancel()
        countryJob = viewModelScope.launch {
            withContext(coroutineContext) {
                authRepository.allCountries().collect {
                    when (it) {
                        is Resource.Success -> {
                            _countriesState.value = UiState.Success(it.data)
                        }

                        is Resource.Error -> {
                            _countriesState.value = UiState.Error(it.message!!)
                        }

                        is Resource.Loading -> {
                            _countriesState.value = UiState.Loading()
                        }
                    }
                }
            }
        }
    }

    fun cities(countryId: Int) {
        cityJob?.cancel()
        cityJob = viewModelScope.launch {
            withContext(coroutineContext) {
                authRepository.allCities(countryId.toString()).collect {
                    when (it) {
                        is Resource.Success -> {
                            _cityState.value = UiState.Success(it.data)
                        }

                        is Resource.Error -> {
                            _cityState.value = UiState.Error(it.message!!)
                        }

                        is Resource.Loading -> {
                            _cityState.value = UiState.Loading()
                        }
                    }
                }
            }
        }
    }

    fun regions(cityId: Int) {
        regionJob?.cancel()
        regionJob = viewModelScope.launch {
            withContext(coroutineContext) {
                authRepository.allRegions(cityId.toString()).collect {
                    when (it) {
                        is Resource.Success -> {
                            _regionState.value = UiState.Success(it.data)
                        }

                        is Resource.Error -> {
                            _regionState.value = UiState.Error(it.message!!)
                        }

                        is Resource.Loading -> {
                            _regionState.value = UiState.Loading()
                        }
                    }
                }
            }
        }
    }

    fun subRegions(regionId: Int) {
        subRegionJob?.cancel()
        subRegionJob = viewModelScope.launch {
            withContext(coroutineContext) {
                authRepository.allSubRegions(regionId.toString()).collect {
                    when (it) {
                        is Resource.Success -> {
                            _subRegionState.value = UiState.Success(it.data)
                        }

                        is Resource.Error -> {
                            _subRegionState.value = UiState.Error(it.message!!)
                        }

                        is Resource.Loading -> {
                            _subRegionState.value = UiState.Loading()
                        }
                    }
                }
            }
        }
    }

    private fun convertToRequestBody(part: String): RequestBody? {
        return try {
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), part)
        } catch (e: Exception) {
            null
        }
    }

    private fun convertFileToMultipart(img_keyName: String, imageUri: Uri?): MultipartBody.Part? {
        return if (imageUri != null) {
            val file = File(imageUri.path!!)
            val requestBody = RequestBody.create("*/*".toMediaTypeOrNull(), file)
            MultipartBody.Part.createFormData(img_keyName, file.name, requestBody)
        } else null
    }

    data class ValidateSignupForm(
        val storeFirstError: UiText? = null,
        val storeLastError: UiText? = null,
        val storeEmailError: UiText? = null,
        val storePhoneError: UiText? = null,
        val birthDateError: UiText? = null,
        val passwordError: UiText? = null,
        val rePasswordError: UiText? = null,
        val countryIdError: UiText? = null,
        val cityIdError: UiText? = null,
        val regionIdError: UiText? = null,
        val subRegionIdError: UiText? = null,
        val addressError: UiText? = null,
        val genderError: UiText? = null
    )
}