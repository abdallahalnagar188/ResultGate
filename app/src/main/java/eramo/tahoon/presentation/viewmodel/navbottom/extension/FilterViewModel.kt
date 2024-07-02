package eramo.tahoon.presentation.viewmodel.navbottom.extension

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eramo.tahoon.data.remote.dto.home.HomeCategoriesResponse
import eramo.tahoon.data.remote.dto.products.search.PriceResponse
import eramo.tahoon.domain.model.FilterCategoryModel
import eramo.tahoon.domain.model.home.HomeBrandsModel
import eramo.tahoon.domain.repository.ProductsRepository
import eramo.tahoon.domain.usecase.product.GetFilterCategoriesUseCase
import eramo.tahoon.domain.usecase.product.HomeGetCategoriesUseCase
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
class FilterViewModel @Inject constructor(
    private val productsRepository: ProductsRepository,
    private val getFilterCategoriesUseCase: GetFilterCategoriesUseCase,
    private val homeGetCategoriesUseCase: HomeGetCategoriesUseCase
    ) : ViewModel() {

    private val _filterCategoriesState =
        MutableStateFlow<UiState<List<FilterCategoryModel>>>(UiState.Empty())
    val filterCategoriesState: StateFlow<UiState<List<FilterCategoryModel>>> =
        _filterCategoriesState

    private val _brandsState = MutableStateFlow<UiState<List<HomeBrandsModel>>>(UiState.Empty())
    val brandsState: StateFlow<UiState<List<HomeBrandsModel>>> = _brandsState

    private val _maxPriceState = MutableStateFlow<UiState<PriceResponse>>(UiState.Empty())
    val maxPriceState: StateFlow<UiState<PriceResponse>> = _maxPriceState

    private val _minPriceState = MutableStateFlow<UiState<PriceResponse>>(UiState.Empty())
    val minPriceState: StateFlow<UiState<PriceResponse>> = _minPriceState

    private val _categoriesState =
        MutableStateFlow<UiState<HomeCategoriesResponse>>(UiState.Empty())
    val categoriesState: StateFlow<UiState<HomeCategoriesResponse>> =
        _categoriesState

    private var filterCategoriesJob: Job? = null
    private var brandsJob: Job? = null
    private var maxPriceJob: Job? = null
    private var minPriceJob: Job? = null
    private var allProductsManufacturerJob: Job? = null

    fun cancelRequest() {
        filterCategoriesJob?.cancel()
        brandsJob?.cancel()
        maxPriceJob?.cancel()
        minPriceJob?.cancel()
        allProductsManufacturerJob?.cancel()
    }

    fun getHomeCategories() {
        allProductsManufacturerJob?.cancel()
        allProductsManufacturerJob = viewModelScope.launch {
            delay(Constants.ANIMATION_DELAY)
            withContext(coroutineContext) {
                homeGetCategoriesUseCase.homeGetSubCategories().collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let { data ->
                                _categoriesState.value = UiState.Success(data)
                            } ?: run { _categoriesState.value = UiState.Empty() }
                        }

                        is Resource.Error -> {
                            _categoriesState.value =
                                UiState.Error(result.message!!)
                        }

                        is Resource.Loading -> {
                            _categoriesState.value = UiState.Loading()
                        }
                    }
                }
            }
        }
    }

    fun getHomeBrands() {
        brandsJob?.cancel()
        brandsJob = viewModelScope.launch {
            delay(Constants.ANIMATION_DELAY)
            withContext(coroutineContext) {
                productsRepository.homeGetBrands().collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let { data ->
                                _brandsState.value = UiState.Success(data)
                            } ?: run { _brandsState.value = UiState.Empty() }
                        }

                        is Resource.Error -> {
                            _brandsState.value =
                                UiState.Error(result.message!!)
                        }

                        is Resource.Loading -> {
                            _brandsState.value = UiState.Loading()
                        }
                    }
                }
            }
        }
    }

    fun filterCategories() {
        filterCategoriesJob?.cancel()
        filterCategoriesJob = viewModelScope.launch {
            delay(Constants.ANIMATION_DELAY)
            withContext(coroutineContext) {
                getFilterCategoriesUseCase().collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let {
                                _filterCategoriesState.value = UiState.Success(it)
                            } ?: run { _filterCategoriesState.value = UiState.Empty() }
                        }
                        is Resource.Error -> {
                            _filterCategoriesState.value =
                                UiState.Error(result.message!!)
                        }
                        is Resource.Loading -> {
                            _filterCategoriesState.value = UiState.Loading()
                        }
                    }
                }
            }
        }
    }

    fun maxProductPrice() {
        maxPriceJob?.cancel()
        maxPriceJob = viewModelScope.launch {
            delay(Constants.ANIMATION_DELAY)
            withContext(coroutineContext) {
                productsRepository.maxProductPrice().collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let {
                                _maxPriceState.value = UiState.Success(it)
                            } ?: run { _maxPriceState.value = UiState.Empty() }
                        }
                        is Resource.Error -> {
                            _maxPriceState.value =
                                UiState.Error(result.message!!)
                        }
                        is Resource.Loading -> {
                            _maxPriceState.value = UiState.Loading()
                        }
                    }
                }
            }
        }
    }

    fun minProductPrice() {
        minPriceJob?.cancel()
        minPriceJob = viewModelScope.launch {
            delay(Constants.ANIMATION_DELAY)
            withContext(coroutineContext) {
                productsRepository.minProductPrice().collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let {
                                _minPriceState.value = UiState.Success(it)
                            } ?: run { _minPriceState.value = UiState.Empty() }
                        }
                        is Resource.Error -> {
                            _minPriceState.value =
                                UiState.Error(result.message!!)
                        }
                        is Resource.Loading -> {
                            _minPriceState.value = UiState.Loading()
                        }
                    }
                }
            }
        }
    }
}