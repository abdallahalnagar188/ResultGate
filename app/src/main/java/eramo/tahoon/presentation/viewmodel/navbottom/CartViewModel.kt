package eramo.tahoon.presentation.viewmodel.navbottom

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eramo.tahoon.data.local.entity.MyCartDataEntity
import eramo.tahoon.data.remote.dto.cart.CheckProductStockResponse
import eramo.tahoon.data.remote.dto.cart.CheckPromoCodeResponse
import eramo.tahoon.data.remote.dto.drawer.myaccount.myaddresses.GetMyAddressesResponse
import eramo.tahoon.data.remote.dto.general.Member
import eramo.tahoon.data.remote.dto.products.orders.CartCountResponse
import eramo.tahoon.domain.model.CartProductModel
import eramo.tahoon.domain.model.ResultModel
import eramo.tahoon.domain.repository.CartRepository
import eramo.tahoon.domain.repository.ProductsRepository
import eramo.tahoon.domain.usecase.drawer.myaccount.GetProfileUseCase
import eramo.tahoon.domain.usecase.drawer.myaccount.MyAddressesUseCase
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
class CartViewModel @Inject constructor(
    private val repository: CartRepository,
    private val getProfileUseCase: GetProfileUseCase,
    private val myAddressesUseCase: MyAddressesUseCase,
    private val productsRepository: ProductsRepository
) : ViewModel() {

    private val _notificationCount = MutableStateFlow<Int>(0)
    val notificationCount: StateFlow<Int> get() = _notificationCount

    private val _cartDataDtoState = MutableStateFlow<UiState<CartProductModel>>(UiState.Empty())
    val cartDataModelState: StateFlow<UiState<CartProductModel>> = _cartDataDtoState

    private val _addToCartState = MutableSharedFlow<UiState<ResultModel>>()
    val addToCartState: SharedFlow<UiState<ResultModel>> = _addToCartState

    private val _updateCartState = MutableSharedFlow<UiState<ResultModel>>()
    val updateCartState: SharedFlow<UiState<ResultModel>> = _updateCartState

    private val _removeCartItemState = MutableSharedFlow<UiState<ResultModel>>()
    val removeCartItemState: SharedFlow<UiState<ResultModel>> = _removeCartItemState

    private val _cartCountState = MutableStateFlow<UiState<CartCountResponse>>(UiState.Empty())
    val cartCountState: StateFlow<UiState<CartCountResponse>> = _cartCountState

    private val _getProfileState = MutableStateFlow<UiState<Member>>(UiState.Empty())
    val getProfileState: StateFlow<UiState<Member>> = _getProfileState

    private val _getMyAddressesState = MutableStateFlow<UiState<GetMyAddressesResponse>>(UiState.Empty())
    val getMyAddressesState: StateFlow<UiState<GetMyAddressesResponse>> = _getMyAddressesState

    private val _checkPromoCodeState = MutableStateFlow<UiState<CheckPromoCodeResponse>>(UiState.Empty())
    val checkPromoCodeState: StateFlow<UiState<CheckPromoCodeResponse>> = _checkPromoCodeState

    private val _checkProductStockState = MutableSharedFlow<UiState<CheckProductStockResponse>>()
    val checkProductStockState: SharedFlow<UiState<CheckProductStockResponse>> = _checkProductStockState

    var checkoutTotal = MutableStateFlow(0.0f)

    private var cartDataJob: Job? = null
    private var addToCartJob: Job? = null
    private var updateCartJob: Job? = null
    private var removeCartItemJob: Job? = null
    private var cartCountJob: Job? = null
    private var getProfileJob: Job? = null
    private var getMyAddressesJob: Job? = null
    private var checkPromoCodeJob: Job? = null
    private var notificationCountJob: Job? = null
    private var checkProductStockJob: Job? = null

    fun cancelRequest() {
        cartDataJob?.cancel()
        updateCartJob?.cancel()
        removeCartItemJob?.cancel()
        cartCountJob?.cancel()
        getProfileJob?.cancel()
        getMyAddressesJob?.cancel()
        checkPromoCodeJob?.cancel()
        notificationCountJob?.cancel()
        checkProductStockJob?.cancel()
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

    fun checkProductStock(productId: String, quantity: String, sizeId: String, colorId: String) {
        checkProductStockJob?.cancel()
        checkProductStockJob = viewModelScope.launch {
            withContext(coroutineContext) {
                repository.checkProductStock(productId, quantity, sizeId, colorId).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let {
                                it.clicked = ""
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

    fun addToCart(
        cart: String
    ) {
        addToCartJob = viewModelScope.launch {
            withContext(coroutineContext) {
                val result =
                        repository.addToCartApi(cart)
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

    fun updateCartItem(
        productCartId: String,
        quantity: String,
        cartData: MyCartDataEntity
    ) {
        updateCartJob?.cancel()
        updateCartJob = viewModelScope.launch {
            withContext(coroutineContext) {
                val result =
                    if (UserUtil.isUserLogin())
                        repository.updateCartItemApi(productCartId, quantity)

                else
                        repository.updateCartItemQuantityDB(cartData)

//                    if (UserUtil.isUserLogin())
//                    repository.updateCartItemApi(
//                        main_id,
//                        product_id,
//                        product_qty,
//                        product_price,
//                        product_size,
//                        product_color
//                    )
//                else
//                    repository.updateCartItemDB(
//                        main_id,
//                        product_id,
//                        product_qty,
//                        product_price,
//                        product_size,
//                        product_color
//                    )

                result.collect {
                    when (it) {
                        is Resource.Success -> cartData()
                        is Resource.Error -> {
                            _updateCartState.emit(
                                UiState.Error(it.message!!)
                            )
                        }

                        is Resource.Loading -> {
                            _updateCartState.emit(UiState.Loading())
                        }
                    }
                }
            }
        }
    }

    fun removeCartItem(productCartId: String,id:String) {
        removeCartItemJob?.cancel()
        removeCartItemJob = viewModelScope.launch {
            withContext(coroutineContext) {
                val result =// repository.removeCartItemApi(id)
                    if (UserUtil.isUserLogin())
                    repository.removeCartItemApi(productCartId)
                else
                    repository.removeCartItemDB(id)

                result.collect {
                    when (it) {
                        is Resource.Success -> cartData()
                        is Resource.Error -> {
                            _removeCartItemState.emit(
                                UiState.Error(it.message!!)
                            )
                        }

                        is Resource.Loading -> {
                            _removeCartItemState.emit(UiState.Loading())
                        }
                    }
                }
            }
        }
    }

    private fun getCartCount() {
        cartCountJob?.cancel()
        cartCountJob = viewModelScope.launch {
            withContext(coroutineContext) {
                val result = // repository.getCartCountApi()
                    if (UserUtil.isUserLogin())
                    repository.getCartCountApi()
                else
                    repository.getCartCountDB()

                result.collect {
                    when (it) {
                        is Resource.Success -> _cartCountState.value = UiState.Success(it.data)
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

    fun getProfile() {
        getProfileJob?.cancel()
        getProfileJob = viewModelScope.launch {
            delay(Constants.ANIMATION_DELAY)
            withContext(coroutineContext) {
                getProfileUseCase().collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            _getProfileState.value = UiState.Success(result.data)
                        }

                        is Resource.Error -> {
                            _getProfileState.value =
                                UiState.Error(result.message!!)
                        }

                        is Resource.Loading -> {
                            _getProfileState.value = UiState.Loading()
                        }
                    }
                }
            }
        }
    }

    fun checkPromoCode(promoCode: String) {
        checkPromoCodeJob?.cancel()
        checkPromoCodeJob = viewModelScope.launch {
            delay(Constants.ANIMATION_DELAY)
            withContext(coroutineContext) {
                repository.checkPromoCode(promoCode).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            _checkPromoCodeState.value = UiState.Success(result.data)
                        }

                        is Resource.Error -> {
                            _checkPromoCodeState.value =
                                UiState.Error(result.message!!)
                        }

                        is Resource.Loading -> {
                            _checkPromoCodeState.value = UiState.Loading()
                        }
                    }
                }
            }
        }
    }

    fun getNotificationCount() {
        notificationCountJob?.cancel()
        notificationCountJob = viewModelScope.launch {
            delay(Constants.ANIMATION_DELAY)
            withContext(coroutineContext) {
                productsRepository.getUserNotifications().collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let { data ->
                                val unseenList = data.data!!.filter { it?.seen == 0 }
                                _notificationCount.value = unseenList.size
//                                _notificationCount.value = 0
                            } ?: run { _notificationCount.value = 0 }
                        }

                        is Resource.Error -> {
                            _notificationCount.value = 0
                        }

                        is Resource.Loading -> {
                            _notificationCount.value = 0
                        }
                    }
                }
            }
        }
    }
}