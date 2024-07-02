package eramo.tahoon.presentation.viewmodel.navbottom

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import eramo.tahoon.data.local.entity.MyFavouriteEntity
import eramo.tahoon.data.remote.dto.products.FavouriteResponse
import eramo.tahoon.data.remote.dto.products.orders.CartCountResponse
import eramo.tahoon.domain.model.ResultModel
import eramo.tahoon.domain.model.products.CategoryModel
import eramo.tahoon.domain.model.products.ShopProductModel
import eramo.tahoon.domain.repository.CartRepository
import eramo.tahoon.domain.repository.ProductsRepository
import eramo.tahoon.domain.usecase.product.AddFavouriteUseCase
import eramo.tahoon.domain.usecase.product.AllCategorizationByUserIdUseCase
import eramo.tahoon.domain.usecase.product.AllProductsByUserIdUseCase
import eramo.tahoon.domain.usecase.product.HomeProductsManufacturerByUserIdUseCase
import eramo.tahoon.domain.usecase.product.RemoveFavouriteUseCase
import eramo.tahoon.util.Constants
import eramo.tahoon.util.UserUtil
import eramo.tahoon.util.state.Resource
import eramo.tahoon.util.state.UiState
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
class ShopViewModel @Inject constructor(
    private val productsRepository: ProductsRepository,
    private val homeProductsManufacturerByUserIdUseCase: HomeProductsManufacturerByUserIdUseCase,
    private val allProductsByUserIdUseCase: AllProductsByUserIdUseCase,
    private val allCategorizationByUserIdUseCase: AllCategorizationByUserIdUseCase,
    private val addFavouriteUseCase: AddFavouriteUseCase,
    private val removeFavouriteUseCase: RemoveFavouriteUseCase,
    private val cartRepository: CartRepository,
) : ViewModel() {

    private val _allProductsState = MutableStateFlow<PagingData<ShopProductModel>?>(null)
    val allProductsState: StateFlow<PagingData<ShopProductModel>?> = _allProductsState

    private val _filterSubCategoryProductsState = MutableStateFlow<PagingData<ShopProductModel>?>(null)
    val filterSubCategoryProductsState: StateFlow<PagingData<ShopProductModel>?> = _filterSubCategoryProductsState

    private val _productsByCatState = MutableStateFlow<PagingData<ShopProductModel>?>(null)
    val productsByCatState: StateFlow<PagingData<ShopProductModel>?> = _productsByCatState

    private val _productsByBrandState =  MutableStateFlow<UiState<List<ShopProductModel>>>(UiState.Empty())
    val productsByBrandState: StateFlow<UiState<List<ShopProductModel>>> = _productsByBrandState

    private val _allCategoriesState =
        MutableStateFlow<UiState<List<CategoryModel>>>(UiState.Empty())
    val allCategoriesState: StateFlow<UiState<List<CategoryModel>>> =
        _allCategoriesState

    private val _addFavouriteState = MutableSharedFlow<UiState<ResultModel>>()
    val addFavouriteState: SharedFlow<UiState<ResultModel>> = _addFavouriteState

    private val _removeFavouriteState = MutableSharedFlow<UiState<ResultModel>>()
    val removeFavouriteState: SharedFlow<UiState<ResultModel>> = _removeFavouriteState

    private val _cartCountState = MutableStateFlow<UiState<CartCountResponse>>(UiState.Empty())
    val cartCountState: StateFlow<UiState<CartCountResponse>> = _cartCountState

    private val _addRemoveItemWishlistState = MutableSharedFlow<UiState<FavouriteResponse>>()
    val addRemoveItemWishlistState: SharedFlow<UiState<FavouriteResponse>> = _addRemoveItemWishlistState

    private val _addRemoveItemWishlistStateDB = MutableSharedFlow<UiState<ResultModel>>()
    val addRemoveItemWishlistStateDB: SharedFlow<UiState<ResultModel>> = _addRemoveItemWishlistStateDB

    private val _getFavouriteListDBState = MutableStateFlow<UiState<List<MyFavouriteEntity>>>(UiState.Empty())
    val getFavouriteListDBState: StateFlow<UiState<List<MyFavouriteEntity>>> = _getFavouriteListDBState

    private var allProductsJob: Job? = null
    private var productsByCatJob: Job? = null
    private var productsByBrandJob: Job? = null
    private var allCategoriesJob: Job? = null
    private var addFavouriteJob: Job? = null
    private var removeFavouriteJob: Job? = null
    private var cartCountJob: Job? = null
    private var addRemoveItemWishlistJob: Job? = null
    private var filterSubCategoryProductsJob: Job? = null
    private var addRemoveItemWishlistJobDB: Job? = null
    private var getFavouriteListDBJob: Job? = null

    fun cancelRequest() {
        allProductsJob?.cancel()
        productsByCatJob?.cancel()
        productsByBrandJob?.cancel()
        allCategoriesJob?.cancel()
        addFavouriteJob?.cancel()
        removeFavouriteJob?.cancel()
        cartCountJob?.cancel()
        addRemoveItemWishlistJob?.cancel()
        filterSubCategoryProductsJob?.cancel()
        addRemoveItemWishlistJobDB?.cancel()
        getFavouriteListDBJob?.cancel()
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

    fun filterSubCategoryProducts(
        subCategoryId: String,
        type: String,
        value: String,
        max: String,
        min: String
    ) {

        filterSubCategoryProductsJob?.cancel()
        filterSubCategoryProductsJob = viewModelScope.launch {
            delay(Constants.ANIMATION_DELAY)
            withContext(coroutineContext) {
                allProductsByUserIdUseCase.filterSubCategoryProducts(subCategoryId,type, value, max, min)
                    .cachedIn(viewModelScope).collect {
                        _filterSubCategoryProductsState.value = it
                    }
            }
        }
    }

    fun productsByCat(catId: String,brandId: String) {
        productsByCatJob?.cancel()
        productsByCatJob = viewModelScope.launch {
            delay(Constants.ANIMATION_DELAY)
            withContext(coroutineContext) {
                allCategorizationByUserIdUseCase(catId, brandId = brandId).cachedIn(viewModelScope).collect {
                    _productsByCatState.value = it
                }
            }
        }
    }

    fun productsByBrand(brandId:String) {
        productsByBrandJob?.cancel()
        productsByBrandJob = viewModelScope.launch {
            delay(Constants.ANIMATION_DELAY)
            withContext(coroutineContext) {
                productsRepository.getProductsByBrand(brandId).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let { data ->
                                _productsByBrandState.value = UiState.Success(data)
                            } ?: run { _productsByBrandState.value = UiState.Empty() }
                            Log.e("productsByBrand",result.data.toString())
                        }

                        is Resource.Error -> {
                            _productsByBrandState.value =
                                UiState.Error(result.message!!)
                        }

                        is Resource.Loading -> {
                            _productsByBrandState.value = UiState.Loading()
                        }
                    }
                }
            }
        }
    }
    fun allCategories() {
        allCategoriesJob?.cancel()
        allCategoriesJob = viewModelScope.launch {
            delay(Constants.ANIMATION_DELAY)
            withContext(coroutineContext) {
                homeProductsManufacturerByUserIdUseCase().collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let { data ->
                                _allCategoriesState.value = UiState.Success(data)
                            } ?: run { _allCategoriesState.value = UiState.Empty() }
                        }

                        is Resource.Error -> {
                            _allCategoriesState.value =
                                UiState.Error(result.message!!)
                        }

                        is Resource.Loading -> {
                            _allCategoriesState.value = UiState.Loading()
                        }
                    }
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
                            allProducts()
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
                            allProducts()
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

    fun getCartCount() {
        cartCountJob?.cancel()
        cartCountJob = viewModelScope.launch {
            delay(Constants.ANIMATION_DELAY)
            withContext(coroutineContext) {
                val result = if (UserUtil.isUserLogin())
                    cartRepository.getCartCountApi()
                else
                    cartRepository.getCartCountDB()

                result.collect { it ->
                    when (it) {
                        is Resource.Success -> {
                            it.data?.let {
                                _cartCountState.value = UiState.Success(it)
                            } ?: run { _cartCountState.value = UiState.Empty() }
                        }

                        is Resource.Error -> {
                            _cartCountState.value = UiState.Error(it.message!!)
                        }

                        is Resource.Loading -> {
                            _cartCountState.value = UiState.Loading()
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