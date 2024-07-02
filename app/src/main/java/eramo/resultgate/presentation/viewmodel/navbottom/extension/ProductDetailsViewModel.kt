package eramo.resultgate.presentation.viewmodel.navbottom.extension

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eramo.resultgate.data.local.entity.MyCartDataEntity
import eramo.resultgate.data.local.entity.MyFavouriteEntity
import eramo.resultgate.data.remote.dto.cart.CheckProductStockResponse
import eramo.resultgate.data.remote.dto.products.FavouriteResponse
import eramo.resultgate.data.remote.dto.products.ProductDetailsResponse
import eramo.resultgate.data.remote.dto.products.orders.CartCountResponse
import eramo.resultgate.domain.model.CartProductModel
import eramo.resultgate.domain.model.ResultModel
import eramo.resultgate.domain.repository.CartRepository
import eramo.resultgate.domain.repository.ProductsRepository
import eramo.resultgate.domain.usecase.product.AddFavouriteUseCase
import eramo.resultgate.domain.usecase.product.GetProductByIdUseCase
import eramo.resultgate.domain.usecase.product.RemoveFavouriteUseCase
import eramo.resultgate.util.Constants
import eramo.resultgate.util.UserUtil
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
class ProductDetailsViewModel @Inject constructor(
    private val getProductByIdUseCase: GetProductByIdUseCase,
    private val addFavouriteUseCase: AddFavouriteUseCase,
    private val removeFavouriteUseCase: RemoveFavouriteUseCase,
    private val repository: CartRepository,
    private val productsRepository: ProductsRepository
) : ViewModel() {

    private val _productState = MutableStateFlow<UiState<ProductDetailsResponse>>(UiState.Empty())
    val productState: StateFlow<UiState<ProductDetailsResponse>> = _productState

    private val _addFavouriteState = MutableSharedFlow<UiState<ResultModel>>()
    val addFavouriteState: SharedFlow<UiState<ResultModel>> = _addFavouriteState

    private val _removeFavouriteState = MutableSharedFlow<UiState<ResultModel>>()
    val removeFavouriteState: SharedFlow<UiState<ResultModel>> = _removeFavouriteState

    private val _addToCartState = MutableSharedFlow<UiState<ResultModel>>()
    val addToCartState: SharedFlow<UiState<ResultModel>> = _addToCartState

    private val _cartCountState = MutableStateFlow<UiState<CartCountResponse>>(UiState.Empty())
    val cartCountState: StateFlow<UiState<CartCountResponse>> = _cartCountState

    private val _addRemoveItemWishlistState = MutableSharedFlow<UiState<FavouriteResponse>>()
    val addRemoveItemWishlistState: SharedFlow<UiState<FavouriteResponse>> = _addRemoveItemWishlistState

    private val _addRemoveItemWishlistStateDB = MutableSharedFlow<UiState<ResultModel>>()
    val addRemoveItemWishlistStateDB: SharedFlow<UiState<ResultModel>> = _addRemoveItemWishlistStateDB

    private val _checkProductStockState = MutableSharedFlow<UiState<CheckProductStockResponse>>()
    val checkProductStockState: SharedFlow<UiState<CheckProductStockResponse>> = _checkProductStockState

    private val _getFavouriteListDBState = MutableStateFlow<UiState<List<MyFavouriteEntity>>>(UiState.Empty())
    val getFavouriteListDBState: StateFlow<UiState<List<MyFavouriteEntity>>> = _getFavouriteListDBState

    private val _cartDataDtoState = MutableStateFlow<UiState<CartProductModel>>(UiState.Empty())
    val cartDataModelState: StateFlow<UiState<CartProductModel>> = _cartDataDtoState


    private var productJob: Job? = null
    private var addFavouriteJob: Job? = null
    private var removeFavouriteJob: Job? = null
    private var questJob: Job? = null
    private var addToCartJob: Job? = null
    private var cartCountJob: Job? = null
    private var addRemoveItemWishlistJob: Job? = null
    private var addRemoveItemWishlistJobDB: Job? = null
    private var checkProductStockJob: Job? = null
    private var getFavouriteListDBJob: Job? = null
    private var cartDataJob: Job? = null

    fun cancelRequest() {
        productJob?.cancel()
        addFavouriteJob?.cancel()
        removeFavouriteJob?.cancel()
        questJob?.cancel()
        addToCartJob?.cancel()
        cartCountJob?.cancel()
        addRemoveItemWishlistJob?.cancel()
        addRemoveItemWishlistJobDB?.cancel()
        checkProductStockJob?.cancel()
        getFavouriteListDBJob?.cancel()
        cartDataJob?.cancel()
    }

    fun getProduct(productId: String) {
        productJob?.cancel()
        productJob = viewModelScope.launch {
            withContext(coroutineContext) {
                delay(Constants.ANIMATION_DELAY)
                getProductByIdUseCase(productId).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let {
                                _productState.emit(UiState.Success(it))
                            } ?: run { _productState.emit(UiState.Empty()) }
                        }

                        is Resource.Error -> {
                            _productState.emit(
                                UiState.Error(result.message!!)
                            )
                        }

                        is Resource.Loading -> {
                            _productState.emit(UiState.Loading())
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

    fun addToCart(
//        productModel: ProductModel,
        cart: String,
        myCartDataEntity: MyCartDataEntity,
//        product_qty: String,
//        product_price: String,
//        productSize: String,
//        productColor: String
    ) {
        addToCartJob = viewModelScope.launch {
            withContext(coroutineContext) {
                val result =
                    if (UserUtil.isUserLogin()) {
                        repository.addToCartApi(cart)
                    } else {
                        repository.addToCartDB(myCartDataEntity)
                    }

//                    repository.addToCartApi(cart)

//                    if (UserUtil.isUserLogin()) {
//                    repository.addToCartApi(
//                        product_id,
//                        product_qty,
//                        product_price,
//                        productSize,
//                        productColor
//                    )
//                } else {
//                    Log.d(TAG, "addToCart: to database")
//                    repository.addToCartDB(productModel, product_qty, productSize, productColor)
//                }

                result.collect { it ->
                    when (it) {
                        is Resource.Success -> {
                            it.data?.let {
                                val resultModel = ResultModel(200, "Success")
                                _addToCartState.emit(UiState.Success(resultModel))
                            } ?: run { _addToCartState.emit(UiState.Empty()) }

                           getCartCount()
                        }

                        is Resource.Error -> {
                            _addToCartState.emit(
                                UiState.Error(it.message!!)
                            )
                        }

                        is Resource.Loading -> {
                            _addToCartState.emit(UiState.Loading())
                        }
                    }
                }
            }
        }
    }

    fun cartData() {
        cartDataJob?.cancel()
        cartDataJob = viewModelScope.launch {
            withContext(coroutineContext) {
                val result =
                    if (UserUtil.isUserLogin())
                        repository.getCartDataApi()
                    else
                        repository.getCartDataDB()

                result.collect {
                    when (it) {
                        is Resource.Success -> {
                            _cartDataDtoState.value = UiState.Success(it.data)
                            getCartCount()
                        }

                        is Resource.Error -> {
                            _cartDataDtoState.value = UiState.Error(it.message!!)
                        }

                        is Resource.Loading -> {
                            _cartDataDtoState.value = UiState.Loading()
                        }
                    }
                }
            }
        }
    }

    fun getCartCount() {
        cartCountJob?.cancel()
        cartCountJob = viewModelScope.launch {
            withContext(coroutineContext) {
                val result = if (UserUtil.isUserLogin())
                    repository.getCartCountApi()
                else
                    repository.getCartCountDB()

                result.collect { it ->
                    when (it) {
                        is Resource.Success -> {
                            it.data?.let {
                                _cartCountState.value = UiState.Success(it)
                            } ?: run { _cartCountState.value = UiState.Empty() }
                        }

                        is Resource.Error -> {
                            _cartCountState.value =
                                UiState.Error(it.message!!)
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

    fun checkProductStock(productId: String, quantity: String, sizeId: String, colorId: String, clicked: String) {
        checkProductStockJob?.cancel()
        checkProductStockJob = viewModelScope.launch {
            withContext(coroutineContext) {
                repository.checkProductStock(productId, quantity, sizeId, colorId).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let {
                                it.clicked = clicked
                                _checkProductStockState.emit(UiState.Success(it))
                            } ?: run { _checkProductStockState.emit(UiState.Empty()) }
                        }

                        is Resource.Error -> {
                            _checkProductStockState.emit(
                                UiState.Error(result.message!!)
                            )
                        }

                        is Resource.Loading -> {
                            _checkProductStockState.emit(UiState.Loading())
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


    init {
       getCartCount()
    }
}