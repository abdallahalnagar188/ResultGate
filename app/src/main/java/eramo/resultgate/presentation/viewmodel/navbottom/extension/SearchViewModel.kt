package eramo.resultgate.presentation.viewmodel.navbottom.extension

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import eramo.resultgate.data.local.entity.MyFavouriteEntity
import eramo.resultgate.data.remote.dto.products.FavouriteResponse
import eramo.resultgate.domain.model.ResultModel
import eramo.resultgate.domain.model.SortSearchResultObject
import eramo.resultgate.domain.model.products.ShopProductModel
import eramo.resultgate.domain.model.request.SearchRequest
import eramo.resultgate.domain.repository.ProductsRepository
import eramo.resultgate.domain.usecase.product.AddFavouriteUseCase
import eramo.resultgate.domain.usecase.product.AllProductsByUserIdUseCase
import eramo.resultgate.domain.usecase.product.RemoveFavouriteUseCase
import eramo.resultgate.util.Constants
import eramo.resultgate.util.state.Resource
import eramo.resultgate.util.state.UiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val productsRepository: ProductsRepository,
    private val addFavouriteUseCase: AddFavouriteUseCase,
    private val removeFavouriteUseCase: RemoveFavouriteUseCase,
    private val allProductsByUserIdUseCase: AllProductsByUserIdUseCase
    ) : ViewModel() {

    private val _filterState = MutableStateFlow<PagingData<ShopProductModel>?>(null)
    val filterState: MutableStateFlow<PagingData<ShopProductModel>?> = _filterState

    private val _searchState = MutableStateFlow<PagingData<ShopProductModel>?>(null)
    val searchState: MutableStateFlow<PagingData<ShopProductModel>?> = _searchState

    private val _sortSearchState = MutableStateFlow<PagingData<ShopProductModel>?>(null)
    val sortSearchState: MutableStateFlow<PagingData<ShopProductModel>?> = _sortSearchState

    private val _addFavouriteState = MutableSharedFlow<UiState<ResultModel>>()
    val addFavouriteState: SharedFlow<UiState<ResultModel>> = _addFavouriteState

    private val _removeFavouriteState = MutableSharedFlow<UiState<ResultModel>>()
    val removeFavouriteState: SharedFlow<UiState<ResultModel>> = _removeFavouriteState

    private val _addRemoveItemWishlistState = MutableSharedFlow<UiState<FavouriteResponse>>()
    val addRemoveItemWishlistState: SharedFlow<UiState<FavouriteResponse>> = _addRemoveItemWishlistState

    private val _allProductsState = MutableStateFlow<PagingData<ShopProductModel>?>(null)
    val allProductsState: StateFlow<PagingData<ShopProductModel>?> = _allProductsState

    private val _addRemoveItemWishlistStateDB = MutableSharedFlow<UiState<ResultModel>>()
    val addRemoveItemWishlistStateDB: SharedFlow<UiState<ResultModel>> = _addRemoveItemWishlistStateDB

    private val _getFavouriteListDBState = MutableStateFlow<UiState<List<MyFavouriteEntity>>>(UiState.Empty())
    val getFavouriteListDBState: StateFlow<UiState<List<MyFavouriteEntity>>> = _getFavouriteListDBState

     var requestAllProducts = MutableStateFlow<Boolean>(false)

    private var filterJob: Job? = null
    private var searchJob: Job? = null
    private var addFavouriteJob: Job? = null
    private var removeFavouriteJob: Job? = null
    private var addRemoveItemWishlistJob: Job? = null
    private var allProductsJob: Job? = null
    private var addRemoveItemWishlistJobDB: Job? = null
    private var getFavouriteListDBJob: Job? = null
    private var sortSearchResultJob: Job? = null


    fun cancelRequest() {
        filterJob?.cancel()
        searchJob?.cancel()
        addFavouriteJob?.cancel()
        removeFavouriteJob?.cancel()
        addRemoveItemWishlistJob?.cancel()
        allProductsJob?.cancel()
        addRemoveItemWishlistJobDB?.cancel()
        getFavouriteListDBJob?.cancel()
        sortSearchResultJob?.cancel()
    }

    fun productFilter(filterRequest: SearchRequest) {
        filterJob = viewModelScope.launch {
            delay(Constants.ANIMATION_DELAY)
            withContext(coroutineContext) {
                productsRepository.productFilter(filterRequest).cachedIn(viewModelScope).collect {
                    _filterState.value = it
                }
            }
        }
    }

    fun productSearch(title: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(Constants.ANIMATION_DELAY)
            withContext(coroutineContext) {
                productsRepository.productSearch(title).cachedIn(viewModelScope).collect {
                    _searchState.value = it
                }
            }
        }
    }

    fun sortSearchResult(obj: SortSearchResultObject,categoryId: String?) {
        sortSearchResultJob?.cancel()

        sortSearchResultJob = viewModelScope.launch {
            delay(Constants.ANIMATION_DELAY)
            withContext(coroutineContext) {
                productsRepository.sortSearchResult(obj,categoryId).cachedIn(viewModelScope).collect {
                    _sortSearchState.value = it
                }
            }
        }
    }

    fun addFavourite(property_id: String) {
        addFavouriteJob?.cancel()
        addFavouriteJob = viewModelScope.launch {
            withContext(coroutineContext) {
                addFavouriteUseCase(property_id).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let {
                                _addFavouriteState.emit(UiState.Success(it))
                            } ?: run { _addFavouriteState.emit(UiState.Empty()) }
                        }

                        is Resource.Error -> {
                            _addFavouriteState.emit(
                                UiState.Error(result.message!!)
                            )
                        }

                        is Resource.Loading -> {
                            _addFavouriteState.emit(UiState.Loading())
                        }
                    }
                }
            }
        }
    }

    fun removeFavourite(property_id: String) {
        removeFavouriteJob?.cancel()
        removeFavouriteJob = viewModelScope.launch {
            withContext(coroutineContext) {
                removeFavouriteUseCase(property_id).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let {
                                _removeFavouriteState.emit(UiState.Success(it))
                            } ?: run { _removeFavouriteState.emit(UiState.Empty()) }
                        }

                        is Resource.Error -> {
                            _removeFavouriteState.emit(
                                UiState.Error(result.message!!)
                            )
                        }

                        is Resource.Loading -> {
                            _removeFavouriteState.emit(UiState.Loading())
                        }
                    }
                }
            }
        }
    }

    fun addRemoveItemWishlist(productId: String) {
        addRemoveItemWishlistJob?.cancel()
        addRemoveItemWishlistJob = viewModelScope.launch {
            withContext(coroutineContext) {
                addFavouriteUseCase.addRemoveItemWishlist(productId).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let {
                                _addRemoveItemWishlistState.emit(UiState.Success(it))
                            } ?: run { _addRemoveItemWishlistState.emit(UiState.Empty()) }

                        }

                        is Resource.Error -> {
                            _addRemoveItemWishlistState.emit(
                                UiState.Error(result.message!!)
                            )
                        }

                        is Resource.Loading -> {
                            _addRemoveItemWishlistState.emit(UiState.Loading())
                        }
                    }
                }
            }
        }
    }

    fun allProducts() {
        allProductsJob?.cancel()
        allProductsJob = viewModelScope.launch {
            delay(Constants.ANIMATION_DELAY)
            withContext(coroutineContext) {
                allProductsByUserIdUseCase().cachedIn(viewModelScope).collect {
                    _allProductsState.value = it
                }
            }
        }
    }

    fun addRemoveItemWishlistDB(myFavouriteEntity: MyFavouriteEntity) {
        addRemoveItemWishlistJobDB?.cancel()
        addRemoveItemWishlistJobDB = viewModelScope.launch {
            withContext(coroutineContext) {
                productsRepository.addRemoveFavouriteDB(myFavouriteEntity).collect { result ->

                    when (result) {
                        is Resource.Success -> {
                            result.data?.let {
                                _addRemoveItemWishlistStateDB.emit(UiState.Success(it))
                            } ?: run { _addRemoveItemWishlistStateDB.emit(UiState.Empty()) }
                        }

                        is Resource.Error -> {
                            _addRemoveItemWishlistStateDB.emit(
                                UiState.Error(result.message!!)
                            )
                        }

                        is Resource.Loading -> {
                            _addRemoveItemWishlistStateDB.emit(UiState.Loading())
                        }
                    }
                }
            }
        }
    }

    fun getFavouriteListDB() {
        getFavouriteListDBJob?.cancel()
        getFavouriteListDBJob = viewModelScope.launch {
            withContext(coroutineContext) {
                productsRepository.getFavouriteListDB().collect { result ->

                    when (result) {
                        is Resource.Success -> {
                            result.data?.let {
                                _getFavouriteListDBState.emit(UiState.Success(it))
                            } ?: run { _getFavouriteListDBState.emit(UiState.Empty()) }
                        }

                        is Resource.Error -> {
                            _getFavouriteListDBState.emit(
                                UiState.Error(result.message!!)
                            )
                        }

                        is Resource.Loading -> {
                            _getFavouriteListDBState.emit(UiState.Loading())
                        }
                    }
                }
            }
        }
    }
}