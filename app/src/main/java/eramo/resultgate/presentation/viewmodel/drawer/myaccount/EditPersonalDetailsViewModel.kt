package eramo.resultgate.presentation.viewmodel.drawer.myaccount

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eramo.resultgate.data.remote.dto.auth.UpdateInformationResponse
import eramo.resultgate.domain.model.auth.CitiesModel
import eramo.resultgate.domain.model.auth.CountriesModel
import eramo.resultgate.domain.model.auth.RegionsModel
import eramo.resultgate.domain.repository.AuthRepository
import eramo.resultgate.domain.usecase.drawer.myaccount.EditProfileUseCase
import eramo.resultgate.util.Constants
import eramo.resultgate.util.UserUtil
import eramo.resultgate.util.state.Resource
import eramo.resultgate.util.state.UiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class EditPersonalDetailsViewModel @Inject constructor(
    private val editProfileUseCase: EditProfileUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _editProfileState = MutableStateFlow<UiState<UpdateInformationResponse>>(UiState.Empty())
    val editProfileState: StateFlow<UiState<UpdateInformationResponse>> = _editProfileState

    private val _countriesState = MutableStateFlow<UiState<List<CountriesModel>>>(UiState.Empty())
    val countriesState: StateFlow<UiState<List<CountriesModel>>> = _countriesState

    private val _cityState = MutableStateFlow<UiState<List<CitiesModel>>>(UiState.Empty())
    val cityState: StateFlow<UiState<List<CitiesModel>>> = _cityState

    private val _regionState = MutableStateFlow<UiState<List<RegionsModel>>>(UiState.Empty())
    val regionState: StateFlow<UiState<List<RegionsModel>>> = _regionState

    private var editProfileJob: Job? = null
    private var countryJob: Job? = null
    private var cityJob: Job? = null
    private var regionJob: Job? = null

    fun cancelRequest() {
        editProfileJob?.cancel()
        countryJob?.cancel()
        cityJob?.cancel()
        regionJob?.cancel()
    }

    fun editProfile(
        firstName: String,
        lastName: String,
        birthDate: String,

        image: Uri?
//        m_image: Uri?
    ) {
        editProfileJob?.cancel()
        editProfileJob = viewModelScope.launch {
            withContext(coroutineContext) {
                editProfileUseCase(
                    convertToRequestBody(firstName),
                    convertToRequestBody(lastName),
                    convertToRequestBody(birthDate),

                    convertFileToMultipart(Constants.IMG_KEY_IMAGE, image)
//                    convertFileToMultipart(Constants.IMG_KEY_IMAGE, m_image)
                ).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let {
                                _editProfileState.value = UiState.Success(it)
                            } ?: run { _editProfileState.value = UiState.Empty() }
                            saveUserInfo(
                                result.data?.data?.name ?: "",
                                UserUtil.getUserPass(),
                                "",
                                result.data?.data?.country?.id.toString(),
                                result.data?.data?.city?.id.toString(),
                                result.data?.data?.region?.id.toString(),
                                result.data?.data?.email ?: "",
                                result.data?.data?.phone ?: "",
                                result.data?.data?.imageUrl ?: ""
                            )
                        }

                        is Resource.Error -> {
                            _editProfileState.value =
                                UiState.Error(result.message!!)
                        }

                        is Resource.Loading -> {
                            _editProfileState.value = UiState.Loading()
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
        countryJob?.cancel()
        countryJob = viewModelScope.launch {
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
        countryJob?.cancel()
        countryJob = viewModelScope.launch {
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

    private fun saveUserInfo(
        user_name: String,
        user_pass: String,
        address: String,
        countryId: String,
        cityId: String,
        regionId: String,
        user_email: String,
        user_phone: String,
        m_image: String
    ) {
//        UserUtil.saveUserInfo(
//            UserUtil.isRememberUser(),
//            UserUtil.getUserId(),
//            UserUtil.getUserToken(),
//            user_name,
//            user_pass,
//            address,
//            countryId,
//            cityId,
//            regionId,
//            user_phone,
//            user_email,
//            m_image,
//        )
    }
}