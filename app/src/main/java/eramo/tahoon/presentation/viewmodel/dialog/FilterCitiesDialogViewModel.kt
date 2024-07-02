package eramo.tahoon.presentation.viewmodel.dialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eramo.tahoon.domain.model.auth.CitiesWithRegionsModel
import eramo.tahoon.domain.model.auth.CountriesModel
import eramo.tahoon.domain.repository.AuthRepository
import eramo.tahoon.util.state.Resource
import eramo.tahoon.util.state.UiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class FilterCitiesDialogViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _countriesState = MutableStateFlow<UiState<List<CountriesModel>>>(UiState.Empty())
    val countriesState: StateFlow<UiState<List<CountriesModel>>> = _countriesState

    private val _cityState = MutableStateFlow<UiState<List<CitiesWithRegionsModel>>>(UiState.Empty())
    val cityState: StateFlow<UiState<List<CitiesWithRegionsModel>>> = _cityState

    private var countryJob: Job? = null
    private var cityJob: Job? = null

    fun cancelRequest() {
        countryJob?.cancel()
        cityJob?.cancel()
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
                authRepository.allCitiesWithRegions(countryId.toString()).collect {
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
}