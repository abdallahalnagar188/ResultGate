package eramo.resultgate.presentation.viewmodel.navbottom.extension

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eramo.resultgate.domain.model.products.MyProductModel
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
class BrandProductsViewModel @Inject constructor(
    private val productsRepository: ProductsRepository,
) : ViewModel() {

    private val _brandProductsState = MutableStateFlow<UiState<List<MyProductModel>>>(UiState.Empty())
    val brandProductsState: StateFlow<UiState<List<MyProductModel>>> = _brandProductsState

    private var getBrandProductsJob: Job? = null

    fun cancelRequest() {
        getBrandProductsJob?.cancel()
    }

    fun getBrandProducts(brandId: String) {
        getBrandProductsJob?.cancel()
        getBrandProductsJob = viewModelScope.launch {
            delay(Constants.ANIMATION_DELAY)
            withContext(coroutineContext) {
                productsRepository.myWishList().collect() { result ->
                    when (result) {
                        is Resource.Success -> {
                            _brandProductsState.value = UiState.Success(result.data)
                        }

                        is Resource.Error -> {
                            _brandProductsState.value = UiState.Error(result.message!!)
                        }

                        is Resource.Loading -> {
                            _brandProductsState.value = UiState.Loading()
                        }
                    }
                }
            }
        }
    }
}