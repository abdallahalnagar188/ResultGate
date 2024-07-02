package eramo.resultgate.presentation.viewmodel.navbottom.extension

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eramo.resultgate.domain.model.home.HomeBrandsModel
import eramo.resultgate.domain.repository.ProductsRepository
import eramo.resultgate.util.Constants
import eramo.resultgate.util.state.Resource
import eramo.resultgate.util.state.UiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AllStoresViewModel @Inject constructor(
    private val productsRepository: ProductsRepository,
) : ViewModel() {

    private val _brandsState = MutableStateFlow<UiState<List<HomeBrandsModel>>>(UiState.Empty())
    val brandsState: StateFlow<UiState<List<HomeBrandsModel>>> = _brandsState

    private var brandsJob: Job? = null

    fun cancelRequest() {
        brandsJob?.cancel()
    }

    fun getBrands() {
        brandsJob?.cancel()
        brandsJob = viewModelScope.launch {
            delay(Constants.ANIMATION_DELAY)
            withContext(coroutineContext) {
                productsRepository.homeGetBrands().collect() { result ->
                    when (result) {
                        is Resource.Success -> {
                            _brandsState.value = UiState.Success(result.data)
                        }

                        is Resource.Error -> {
                            _brandsState.value = UiState.Error(result.message!!)
                        }

                        is Resource.Loading -> {
                            _brandsState.value = UiState.Loading()
                        }
                    }
                }
            }
        }
    }
}