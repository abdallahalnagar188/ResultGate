package eramo.tahoon.presentation.viewmodel.drawer.myaccount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eramo.tahoon.data.remote.dto.drawer.myaccount.myaddresses.AddToMyAddressesResponse
import eramo.tahoon.data.remote.dto.drawer.myaccount.myaddresses.DeleteFromMyAddressesResponse
import eramo.tahoon.data.remote.dto.drawer.myaccount.myaddresses.GetMyAddressesResponse
import eramo.tahoon.data.remote.dto.drawer.myaccount.myaddresses.UpdateAddressResponse
import eramo.tahoon.domain.model.auth.CitiesModel
import eramo.tahoon.domain.model.auth.CountriesModel
import eramo.tahoon.domain.model.auth.RegionsModel
import eramo.tahoon.domain.model.auth.SubRegionsModel
import eramo.tahoon.domain.repository.AuthRepository
import eramo.tahoon.domain.usecase.drawer.myaccount.MyAddressesUseCase
import eramo.tahoon.util.Constants
import eramo.tahoon.util.state.Resource
import eramo.tahoon.util.state.UiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class MyAddressesViewModel @Inject constructor(
    private val myAddressesUseCase: MyAddressesUseCase, private val authRepository: AuthRepository
) : ViewModel(
) {

    private val _getMyAddressesState = MutableStateFlow<UiState<GetMyAddressesResponse>>(UiState.Empty())
    val getMyAddressesState: StateFlow<UiState<GetMyAddressesResponse>> = _getMyAddressesState

    private val _addToMyAddressesState = MutableStateFlow<UiState<AddToMyAddressesResponse>>(UiState.Empty())
    val addToMyAddressesState: StateFlow<UiState<AddToMyAddressesResponse>> = _addToMyAddressesState

    private val _deleteFromMyAddressesState = MutableStateFlow<UiState<DeleteFromMyAddressesResponse>>(UiState.Empty())
    val deleteFromMyAddressesState: StateFlow<UiState<DeleteFromMyAddressesResponse>> = _deleteFromMyAddressesState

    private val _updateAddressState = MutableStateFlow<UiState<UpdateAddressResponse>>(UiState.Empty())
    val updateAddressState: StateFlow<UiState<UpdateAddressResponse>> = _updateAddressState

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

    private var getMyAddressesJob: Job? = null
    private var addToMyAddressesJob: Job? = null
    private var deleteFromMyAddressesJob: Job? = null
    private var updateAddressJob: Job? = null
    private var countryJob: Job? = null
    private var cityJob: Job? = null
    private var regionJob: Job? = null
    private var subRegionJob: Job? = null

    fun cancelRequest() {
        getMyAddressesJob?.cancel()
        addToMyAddressesJob?.cancel()
        deleteFromMyAddressesJob?.cancel()
        updateAddressJob?.cancel()
        countryJob?.cancel()
        cityJob?.cancel()
        regionJob?.cancel()
        subRegionJob?.cancel()
    }

    fun getMyAddresses() {
        getMyAddressesJob?.cancel()
        getMyAddressesJob = viewModelScope.launch {
            delay(Constants.ANIMATION_DELAY)
            withContext(coroutineContext) {
                myAddressesUseCase.getMyAddresses().collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let {
                                _getMyAddressesState.value = UiState.Success(it)
                            } ?: run { _getMyAddressesState.value = UiState.Empty() }
                        }

                        is Resource.Error -> {
                            _getMyAddressesState.value = UiState.Error(result.message!!)
                        }

                        is Resource.Loading -> {
                            _getMyAddressesState.value = UiState.Loading()
                        }
                    }
                }
            }
        }
    }

    fun addToMyAddresses(
        addressType: String,
        address: String,
        countryId: String,
        cityId: String,
        regionId: String,
        subRegionId:String
    ) {
        getMyAddressesJob?.cancel()
        getMyAddressesJob = viewModelScope.launch {
            delay(Constants.ANIMATION_DELAY)
            withContext(coroutineContext) {
                myAddressesUseCase.addToMyAddresses(
                    addressType, address, countryId, cityId, regionId,subRegionId
                ).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let {
                                _addToMyAddressesState.value = UiState.Success(it)
                            } ?: run { _addToMyAddressesState.value = UiState.Empty() }
                        }

                        is Resource.Error -> {
                            _addToMyAddressesState.value = UiState.Error(result.message!!)
                        }

                        is Resource.Loading -> {
                            _addToMyAddressesState.value = UiState.Loading()
                        }
                    }
                }
            }
        }
    }

    fun deleteFromMyAddresses(
        addressId: String
    ) {
        deleteFromMyAddressesJob?.cancel()
        deleteFromMyAddressesJob = viewModelScope.launch {
            delay(Constants.ANIMATION_DELAY)
            withContext(coroutineContext) {
                myAddressesUseCase.deleteFromMyAddresses(
                    addressId
                ).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let {
                                _deleteFromMyAddressesState.value = UiState.Success(it)
                            } ?: run { _deleteFromMyAddressesState.value = UiState.Empty() }
                        }

                        is Resource.Error -> {
                            _deleteFromMyAddressesState.value = UiState.Error(result.message!!)
                        }

                        is Resource.Loading -> {
                            _deleteFromMyAddressesState.value = UiState.Loading()
                        }
                    }
                }
            }
        }
    }

    fun updateAddress(
        addressId: String,
        addressType: String,
        address: String,
        countryId: String,
        cityId: String,
        regionId: String,
        subRegionId: String
    ) {
        updateAddressJob?.cancel()
        updateAddressJob = viewModelScope.launch {
            delay(Constants.ANIMATION_DELAY)
            withContext(coroutineContext) {
                myAddressesUseCase.updateAddress(
                    addressId, addressType, address, countryId, cityId, regionId,subRegionId
                ).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let {
                                _updateAddressState.value = UiState.Success(it)
                            } ?: run { _updateAddressState.value = UiState.Empty() }
                        }

                        is Resource.Error -> {
                            _updateAddressState.value = UiState.Error(result.message!!)
                        }

                        is Resource.Loading -> {
                            _updateAddressState.value = UiState.Loading()
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
}